import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import Layout from '../app/components/Layout';

const theme = createTheme();

const MockedLayout = ({ children, onNavigate }: { children: React.ReactNode, onNavigate: jest.Mock }) => (
  <ThemeProvider theme={theme}>
    <Layout onNavigate={onNavigate}>{children}</Layout>
  </ThemeProvider>
);

describe('Layout Component', () => {
  let mockOnNavigate: jest.Mock;

  beforeEach(() => {
    mockOnNavigate = jest.fn();
  });

  test('renders layout with title', () => {
    render(
      <MockedLayout onNavigate={mockOnNavigate}>
        <div>Test Content</div>
      </MockedLayout>
    );

    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    expect(screen.getByText('Test Content')).toBeInTheDocument();
  });

  test('renders navigation buttons', () => {
    render(
      <MockedLayout onNavigate={mockOnNavigate}>
        <div>Test Content</div>
      </MockedLayout>
    );

    expect(screen.getByText('Cadastro')).toBeInTheDocument();
    expect(screen.getByText('Listagem')).toBeInTheDocument();
    expect(screen.getByText('Download CSV')).toBeInTheDocument();
  });

  test('calls onNavigate when cadastro button is clicked', () => {
    render(
      <MockedLayout onNavigate={mockOnNavigate}>
        <div>Test Content</div>
      </MockedLayout>
    );

    fireEvent.click(screen.getByText('Cadastro'));
    expect(mockOnNavigate).toHaveBeenCalledWith('cadastro');
  });

  test('calls onNavigate when listagem button is clicked', () => {
    render(
      <MockedLayout onNavigate={mockOnNavigate}>
        <div>Test Content</div>
      </MockedLayout>
    );

    fireEvent.click(screen.getByText('Listagem'));
    expect(mockOnNavigate).toHaveBeenCalledWith('listagem');
  });

  test('calls onNavigate when download button is clicked', () => {
    render(
      <MockedLayout onNavigate={mockOnNavigate}>
        <div>Test Content</div>
      </MockedLayout>
    );

    fireEvent.click(screen.getByText('Download CSV'));
    expect(mockOnNavigate).toHaveBeenCalledWith('download');
  });
});
