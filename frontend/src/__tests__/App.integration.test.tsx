import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import App from '../app/App';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock react-router-dom
jest.mock('react-router-dom');

const theme = createTheme();

const MockedApp = () => (
  <ThemeProvider theme={theme}>
    <App />
  </ThemeProvider>
);

// Mock window.location
const mockLocation = {
  href: ''
};

Object.defineProperty(window, 'location', {
  value: mockLocation,
  writable: true
});

describe('App Integration Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockLocation.href = '';
  });

  test('renders app with layout and navigation structure', async () => {
    const user = userEvent.setup();
    
    render(<MockedApp />);

    // Verify basic layout renders
    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    
    // Verify navigation is present
    expect(screen.getByText('Cadastro')).toBeInTheDocument();
    expect(screen.getByText('Listagem')).toBeInTheDocument();
    expect(screen.getByText('Download CSV')).toBeInTheDocument();
    
    // Verify router structure is present
    expect(screen.getByTestId('browser-router')).toBeInTheDocument();
    expect(screen.getByTestId('routes')).toBeInTheDocument();
  });

  test('navigation links are clickable and have correct attributes', async () => {
    const user = userEvent.setup();
    
    render(<MockedApp />);

    // Test navigation links
    const cadastroLink = screen.getByText('Cadastro').closest('a');
    const listagemLink = screen.getByText('Listagem').closest('a');
    const downloadLink = screen.getByText('Download CSV').closest('a');

    expect(cadastroLink).toHaveAttribute('href', '/cadastro-pessoa');
    expect(listagemLink).toHaveAttribute('href', '/listagem-pessoas');
    expect(downloadLink).toHaveAttribute('href', '/download-csv');

    // Test that links are clickable (even if they don't navigate in the mock)
    await user.click(screen.getByText('Cadastro'));
    await user.click(screen.getByText('Listagem'));
    await user.click(screen.getByText('Download CSV'));
    
    // Layout should still be present after clicks
    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
  });

  test('app maintains consistent layout structure', () => {
    render(<MockedApp />);

    // Verify consistent structure elements
    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    expect(screen.getByTestId('browser-router')).toBeInTheDocument();
    expect(screen.getByTestId('routes')).toBeInTheDocument();
    
    // Verify all navigation options are available
    const cadastroLink = screen.getByText('Cadastro');
    const listagemLink = screen.getByText('Listagem');
    const downloadLink = screen.getByText('Download CSV');
    
    expect(cadastroLink).toBeInTheDocument();
    expect(listagemLink).toBeInTheDocument();
    expect(downloadLink).toBeInTheDocument();
    
    // Verify routes are defined
    const routes = screen.getAllByTestId('route');
    expect(routes.length).toBeGreaterThan(0);
  });
});
