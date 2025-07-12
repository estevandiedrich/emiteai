import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
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

// Mock fetch global
global.fetch = jest.fn();

describe('DownloadCsv Component', () => {
  beforeEach(() => {
    mockLocation.href = '';
    jest.clearAllMocks();
  });

  test('renders download page correctly', () => {
    render(<MockedDownloadCsv />);

    expect(screen.getByText('Relatório de Pessoas')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Baixar CSV' })).toBeInTheDocument();
  });

  test('initiates download when button is clicked', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      status: 200
    });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    expect(mockLocation.href).toBe('http://localhost:8080/api/relatorios/download');
  });

  test('button is clickable and not disabled', () => {
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    expect(downloadButton).toBeEnabled();
  });

  test('handles multiple clicks correctly', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200
    });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    
    await user.click(downloadButton);
    expect(mockLocation.href).toBe('http://localhost:8080/api/relatorios/download');

    await user.click(downloadButton);
    expect(mockLocation.href).toBe('http://localhost:8080/api/relatorios/download');
  });

  test('displays loading state during download process', async () => {
    const user = userEvent.setup();
    let resolvePromise: (value: any) => void;
    const mockPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    (global.fetch as jest.Mock).mockReturnValueOnce(mockPromise);
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Should show loading state
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    expect(downloadButton).toBeDisabled();

    // Resolve the promise
    resolvePromise!({ ok: true, status: 200 });

    // Wait for loading to complete - the button text should change back
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Baixar CSV' })).toBeInTheDocument();
    });
    
    expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    expect(downloadButton).toBeEnabled();
  });

  test('shows error message when download fails', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockRejectedValueOnce(new Error('Network error'));
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Should show error message
    await screen.findByText('Erro ao baixar relatório. Tente novamente.');
  });

  test('handles 404 error from server', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: false,
      status: 404,
      statusText: 'Not Found'
    });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Should show specific error for 404
    await screen.findByText('Arquivo não encontrado. Gere o relatório primeiro.');
  });

  test('handles 500 error from server', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: false,
      status: 500,
      statusText: 'Internal Server Error'
    });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Should show generic error message
    await screen.findByText('Erro no servidor. Tente novamente mais tarde.');
  });

  test('shows generate CSV button when no file exists', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: false,
      status: 404
    });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Should show generate button after 404
    await screen.findByRole('button', { name: 'Gerar Relatório' });
  });

  test('generates CSV when generate button is clicked', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock)
      .mockResolvedValueOnce({ ok: false, status: 404 }) // First call fails
      .mockResolvedValueOnce({ ok: true, status: 202 }); // Generate call succeeds
    
    render(<MockedDownloadCsv />);

    // First try to download (fails)
    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Click generate button
    const generateButton = await screen.findByRole('button', { name: 'Gerar Relatório' });
    await user.click(generateButton);

    // Should call generate endpoint
    expect(global.fetch).toHaveBeenLastCalledWith(
      'http://localhost:8080/api/relatorios/csv',
      { method: 'POST' }
    );
  });

  test('shows success message after generating CSV', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock)
      .mockResolvedValueOnce({ ok: false, status: 404 })
      .mockResolvedValueOnce({ ok: true, status: 202 });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    const generateButton = await screen.findByRole('button', { name: 'Gerar Relatório' });
    await user.click(generateButton);

    // Should show success message
    await screen.findByText('Relatório sendo gerado. Aguarde alguns instantes e tente baixar novamente.');
  });

  test('handles generation error gracefully', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock)
      .mockResolvedValueOnce({ ok: false, status: 404 })
      .mockRejectedValueOnce(new Error('Generation failed'));
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    const generateButton = await screen.findByRole('button', { name: 'Gerar Relatório' });
    await user.click(generateButton);

    // Should show error message
    await screen.findByText('Erro ao gerar relatório. Tente novamente.');
  });

  test('disables buttons during loading states', async () => {
    const user = userEvent.setup();
    let resolvePromise: (value: any) => void;
    const mockPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    (global.fetch as jest.Mock).mockReturnValueOnce(mockPromise);
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    await user.click(downloadButton);

    // Button should be disabled during loading
    expect(downloadButton).toBeDisabled();

    resolvePromise!({ ok: true, status: 200 });

    // Wait for completion - the button text should change back
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Baixar CSV' })).toBeInTheDocument();
    });
    
    expect(downloadButton).toBeEnabled();
  });

  test('clears error messages on new download attempt', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock)
      .mockRejectedValueOnce(new Error('First error'))
      .mockResolvedValueOnce({ ok: true, status: 200 });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    
    // First click - should show error
    await user.click(downloadButton);
    await screen.findByText('Erro ao baixar relatório. Tente novamente.');

    // Second click - error should be cleared
    await user.click(downloadButton);
    
    // Error message should not be visible during new attempt
    expect(screen.queryByText('Erro ao baixar relatório. Tente novamente.')).not.toBeInTheDocument();
  });

  test('shows informative description about CSV content', () => {
    render(<MockedDownloadCsv />);

    expect(screen.getByText(/Este relatório contém/i)).toBeInTheDocument();
    expect(screen.getByText(/todas as pessoas cadastradas/i)).toBeInTheDocument();
  });

  test('handles keyboard navigation correctly', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValueOnce({ ok: true, status: 200 });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    
    // Tab twice to get to download button (first tab goes to "Gerar Relatório")
    await user.tab();
    await user.tab();
    expect(downloadButton).toHaveFocus();
    
    await user.keyboard('{Enter}');
    
    expect(mockLocation.href).toBe('http://localhost:8080/api/relatorios/download');
  });

  test('shows file format information', () => {
    render(<MockedDownloadCsv />);

    expect(screen.getByText(/formato CSV/i)).toBeInTheDocument();
    expect(screen.getByText(/compatível com Excel/i)).toBeInTheDocument();
  });

  test('handles rapid multiple clicks gracefully', async () => {
    const user = userEvent.setup();
    (global.fetch as jest.Mock).mockResolvedValue({ ok: true, status: 200 });
    
    render(<MockedDownloadCsv />);

    const downloadButton = screen.getByRole('button', { name: 'Baixar CSV' });
    
    // Rapid clicks
    await user.click(downloadButton);
    await user.click(downloadButton);
    await user.click(downloadButton);

    // Should not cause issues
    expect(mockLocation.href).toBe('http://localhost:8080/api/relatorios/download');
  });
});
