import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import CadastroPessoa from '../app/pages/CadastroPessoa';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock react-router-dom
jest.mock('react-router-dom');

const theme = createTheme();

const MockedCadastroPessoa = () => (
  <ThemeProvider theme={theme}>
    <CadastroPessoa />
  </ThemeProvider>
);

describe('CadastroPessoa Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders form fields correctly', () => {
    render(<MockedCadastroPessoa />);

    expect(screen.getByText('Cadastro de Pessoa')).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /nome/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /telefone/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /cpf/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /cep/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /número/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /complemento/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /bairro/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /município/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /estado/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Salvar' })).toBeInTheDocument();
  });

  test('updates form fields when user types', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const nomeInput = screen.getByRole('textbox', { name: /nome/i });
    const telefoneInput = screen.getByRole('textbox', { name: /telefone/i });
    const cpfInput = screen.getByRole('textbox', { name: /cpf/i });

    await user.type(nomeInput, 'João Silva');
    await user.type(telefoneInput, '11999999999');
    await user.type(cpfInput, '12345678901');

    expect(nomeInput).toHaveValue('João Silva');
    expect(telefoneInput).toHaveValue('(11) 99999-9999');
    expect(cpfInput).toHaveValue('123.456.789-01');
  });

  test('searches for CEP when user leaves CEP field', async () => {
    const user = userEvent.setup();
    const mockCepResponse = {
      data: {
        bairro: 'Centro',
        municipio: 'São Paulo',
        estado: 'SP'
      }
    };

    mockedAxios.get.mockResolvedValueOnce(mockCepResponse);

    render(<MockedCadastroPessoa />);

    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    await user.type(cepInput, '01310-100');
    fireEvent.blur(cepInput);

    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/cep/01310100');
    });

    await waitFor(() => {
      expect(screen.getByDisplayValue('Centro')).toBeInTheDocument();
      expect(screen.getByDisplayValue('São Paulo')).toBeInTheDocument();
      expect(screen.getByDisplayValue('SP')).toBeInTheDocument();
    });
  });

  test('shows error when CEP search fails', async () => {
    const user = userEvent.setup();
    mockedAxios.get.mockRejectedValueOnce(new Error('CEP not found'));

    render(<MockedCadastroPessoa />);

    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    await user.type(cepInput, '00000-000');
    fireEvent.blur(cepInput);

    await waitFor(() => {
      expect(screen.getByText('CEP não encontrado ou inválido')).toBeInTheDocument();
    });
  });

  test('submits form successfully', async () => {
    const user = userEvent.setup();
    mockedAxios.post.mockResolvedValueOnce({ data: { id: 1 } });

    render(<MockedCadastroPessoa />);

    // Fill form
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');
    await user.type(screen.getByRole('textbox', { name: /telefone/i }), '11999999999');
    await user.type(screen.getByRole('textbox', { name: /cpf/i }), '12345678901');
    await user.type(screen.getByRole('textbox', { name: /cep/i }), '01310-100');
    await user.type(screen.getByRole('textbox', { name: /número/i }), '123');
    await user.type(screen.getByRole('textbox', { name: /complemento/i }), 'Apt 1');

    // Submit
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockedAxios.post).toHaveBeenCalledWith('http://localhost:8080/api/pessoas', {
        nome: 'João Silva',
        telefone: '11999999999',
        cpf: '12345678901',
        endereco: { 
          bairro: '', 
          municipio: '', 
          estado: '',
          cep: '01310100',
          numero: '123',
          complemento: 'Apt 1'
        }
      });
    });

    await waitFor(() => {
      expect(screen.getByText('Pessoa cadastrada com sucesso!')).toBeInTheDocument();
    });
  });

  test('shows error when form submission fails', async () => {
    const user = userEvent.setup();
    mockedAxios.post.mockRejectedValueOnce(new Error('Server error'));

    render(<MockedCadastroPessoa />);

    // Fill form
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');

    // Submit
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Erro ao salvar pessoa')).toBeInTheDocument();
    });
  });

  test('shows loading state during form submission', async () => {
    const user = userEvent.setup();
    let resolvePromise: (value: any) => void;
    const mockPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    mockedAxios.post.mockReturnValueOnce(mockPromise);

    render(<MockedCadastroPessoa />);

    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    // Check loading state
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    expect(submitButton).toBeDisabled();

    // Resolve the promise
    resolvePromise!({ data: { id: 1 } });

    await waitFor(() => {
      expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    });
  });
});
