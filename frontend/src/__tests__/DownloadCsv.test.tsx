import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import DownloadCsv from '../app/pages/DownloadCsv';

const theme = createTheme();

const MockedDownloadCsv = () => (
  <ThemeProvider theme={theme}>
    <DownloadCsv />
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

describe('DownloadCsv Component', () => {
  beforeEach(() => {
    mockLocation.href = '';
  });

  test('renders download page correctly', () => {
    render(<MockedDownloadCsv />);

    expect(screen.getByText('Download de CSV')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Baixar CSV' })).toBeInTheDocument();
  });

  test('initiates download when button is clicked', async () => {
    const user = userEvent.setup();
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    expect(mockLocation.href).toBe('/api/relatorios/download');
  });

  test('button is clickable and not disabled', () => {
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    expect(downloadButton).toBeEnabled();
  });

  test('handles multiple clicks correctly', async () => {
    const user = userEvent.setup();
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    
    await user.click(downloadButton);
    expect(mockLocation.href).toBe('/api/relatorios/download');

    await user.click(downloadButton);
    expect(mockLocation.href).toBe('/api/relatorios/download');
  });
});
