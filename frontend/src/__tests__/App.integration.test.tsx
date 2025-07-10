import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import App from '../app/App';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

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

  test('complete user flow: navigate, fill form, submit, and check list', async () => {
    const user = userEvent.setup();

    // Mock successful form submission
    mockedAxios.post.mockResolvedValueOnce({ data: { id: 1 } });
    
    // Mock CEP search
    const mockCepResponse = {
      data: {
        bairro: 'Centro',
        municipio: 'São Paulo',
        estado: 'SP'
      }
    };
    mockedAxios.get.mockResolvedValueOnce(mockCepResponse);

    // Mock people list
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '12345678901' }
    ];
    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedApp />);

    // Verify we start on cadastro page
    expect(screen.getByText('Cadastro de Pessoa')).toBeInTheDocument();

    // Fill the form
    await user.type(screen.getByLabelText('Nome'), 'João Silva');
    await user.type(screen.getByLabelText('Telefone'), '11999999999');
    await user.type(screen.getByLabelText('CPF'), '12345678901');
    await user.type(screen.getByLabelText('CEP'), '01310-100');
    fireEvent.blur(screen.getByLabelText('CEP'));

    // Wait for CEP to be populated
    await waitFor(() => {
      expect(screen.getByDisplayValue('Centro')).toBeInTheDocument();
    });

    await user.type(screen.getByLabelText('Número'), '123');
    await user.type(screen.getByLabelText('Complemento'), 'Apt 1');

    // Submit the form
    await user.click(screen.getByRole('button', { name: 'Salvar' }));

    // Wait for success message
    await waitFor(() => {
      expect(screen.getByText('Pessoa cadastrada com sucesso!')).toBeInTheDocument();
    });

    // Navigate to listagem
    await user.click(screen.getByText('Listagem'));

    // Verify we're on listagem page and data is loaded
    await waitFor(() => {
      expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
      expect(screen.getByText('João Silva - 12345678901')).toBeInTheDocument();
    });

    // Navigate to download page
    await user.click(screen.getByText('Download CSV'));
    expect(screen.getByText('Download de CSV')).toBeInTheDocument();

    // Test download functionality
    await user.click(screen.getByRole('button', { name: 'Baixar CSV' }));
    expect(mockLocation.href).toBe('/api/relatorios/download');
  });

  test('navigation between all pages works correctly', async () => {
    const user = userEvent.setup();
    
    // Mock empty list for listagem
    mockedAxios.get.mockResolvedValueOnce({ data: [] });

    render(<MockedApp />);

    // Start on cadastro
    expect(screen.getByText('Cadastro de Pessoa')).toBeInTheDocument();

    // Go to listagem
    await user.click(screen.getByText('Listagem'));
    await waitFor(() => {
      expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
    });

    // Go to download
    await user.click(screen.getByText('Download CSV'));
    expect(screen.getByText('Download de CSV')).toBeInTheDocument();

    // Back to cadastro
    await user.click(screen.getByText('Cadastro'));
    expect(screen.getByText('Cadastro de Pessoa')).toBeInTheDocument();

    // Verify pages are exclusive (only one shown at a time)
    expect(screen.queryByText('Pessoas Cadastradas')).not.toBeInTheDocument();
    expect(screen.queryByText('Download de CSV')).not.toBeInTheDocument();
  });

  test('error handling across different pages', async () => {
    const user = userEvent.setup();

    render(<MockedApp />);

    // Test error on cadastro page
    mockedAxios.post.mockRejectedValueOnce(new Error('Server error'));
    await user.click(screen.getByRole('button', { name: 'Salvar' }));

    await waitFor(() => {
      expect(screen.getByText('Erro ao salvar pessoa')).toBeInTheDocument();
    });

    // Navigate to listagem and test error
    mockedAxios.get.mockRejectedValueOnce(new Error('Network error'));
    await user.click(screen.getByText('Listagem'));

    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar pessoas')).toBeInTheDocument();
    });
  });
});
