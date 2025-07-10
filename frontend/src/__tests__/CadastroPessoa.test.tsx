import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import CadastroPessoa from '../app/pages/CadastroPessoa';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

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
    expect(screen.getByLabelText('Nome')).toBeInTheDocument();
    expect(screen.getByLabelText('Telefone')).toBeInTheDocument();
    expect(screen.getByLabelText('CPF')).toBeInTheDocument();
    expect(screen.getByLabelText('CEP')).toBeInTheDocument();
    expect(screen.getByLabelText('Número')).toBeInTheDocument();
    expect(screen.getByLabelText('Complemento')).toBeInTheDocument();
    expect(screen.getByLabelText('Bairro')).toBeInTheDocument();
    expect(screen.getByLabelText('Município')).toBeInTheDocument();
    expect(screen.getByLabelText('Estado')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Salvar' })).toBeInTheDocument();
  });

  test('updates form fields when user types', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const nomeInput = screen.getByLabelText('Nome');
    const telefoneInput = screen.getByLabelText('Telefone');
    const cpfInput = screen.getByLabelText('CPF');

    await user.type(nomeInput, 'João Silva');
    await user.type(telefoneInput, '11999999999');
    await user.type(cpfInput, '12345678901');

    expect(nomeInput).toHaveValue('João Silva');
    expect(telefoneInput).toHaveValue('11999999999');
    expect(cpfInput).toHaveValue('12345678901');
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

    const cepInput = screen.getByLabelText('CEP');
    await user.type(cepInput, '01310-100');
    fireEvent.blur(cepInput);

    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/cep/01310-100');
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

    const cepInput = screen.getByLabelText('CEP');
    await user.type(cepInput, '00000-000');
    fireEvent.blur(cepInput);

    await waitFor(() => {
      expect(screen.getByText('Erro ao buscar o CEP')).toBeInTheDocument();
    });
  });

  test('submits form successfully', async () => {
    const user = userEvent.setup();
    mockedAxios.post.mockResolvedValueOnce({ data: { id: 1 } });

    render(<MockedCadastroPessoa />);

    // Fill form
    await user.type(screen.getByLabelText('Nome'), 'João Silva');
    await user.type(screen.getByLabelText('Telefone'), '11999999999');
    await user.type(screen.getByLabelText('CPF'), '12345678901');
    await user.type(screen.getByLabelText('CEP'), '01310-100');
    await user.type(screen.getByLabelText('Número'), '123');
    await user.type(screen.getByLabelText('Complemento'), 'Apt 1');

    // Submit
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockedAxios.post).toHaveBeenCalledWith('/api/pessoas', {
        nome: 'João Silva',
        telefone: '11999999999',
        cpf: '12345678901',
        cep: '01310-100',
        numero: '123',
        complemento: 'Apt 1',
        endereco: { bairro: '', municipio: '', estado: '' }
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
    await user.type(screen.getByLabelText('Nome'), 'João Silva');

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
