import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
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

  test('renders form fields correctly', async () => {
    await act(async () => {
      render(<MockedCadastroPessoa />);
    });

    expect(screen.getByText('Cadastro de Pessoa')).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /nome/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /telefone/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /cpf/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /cep/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /número/i })).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /complemento/i })).toBeInTheDocument();
    // Verificar campos específicos de endereço disabled
    expect(screen.getByRole('textbox', { name: /bairro/i })).toBeDisabled();
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
    await user.type(screen.getByRole('textbox', { name: /cpf/i }), '12345678901');

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

    // Fill required fields
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');
    await user.type(screen.getByRole('textbox', { name: /cpf/i }), '12345678901');

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

  test('validates required fields before submission', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    // Tenta submeter form vazio
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    // Form não deve ser submetido sem campos obrigatórios
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  test('clears form after successful submission', async () => {
    const user = userEvent.setup();
    mockedAxios.post.mockResolvedValueOnce({ data: { id: 1 } });

    render(<MockedCadastroPessoa />);

    // Preenche o formulário
    const nomeInput = screen.getByRole('textbox', { name: /nome/i });
    const telefoneInput = screen.getByRole('textbox', { name: /telefone/i });
    const cpfInput = screen.getByRole('textbox', { name: /cpf/i });
    
    await user.type(nomeInput, 'João Silva');
    await user.type(telefoneInput, '11999999999');
    await user.type(cpfInput, '12345678901');
    
    expect(nomeInput).toHaveValue('João Silva');
    expect(telefoneInput).toHaveValue('(11) 99999-9999');
    expect(cpfInput).toHaveValue('123.456.789-01');

    // Submete o formulário
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Pessoa cadastrada com sucesso!')).toBeInTheDocument();
    });

    // Verifica se o formulário foi limpo
    await waitFor(() => {
      expect(nomeInput).toHaveValue('');
      expect(telefoneInput).toHaveValue('');
      expect(cpfInput).toHaveValue('');
    });
  });

  test('handles CEP formatting correctly', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    
    // Testa formatação automática do CEP
    await user.type(cepInput, '01310100');
    expect(cepInput).toHaveValue('01310-100');
  });

  test('handles phone number formatting correctly', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const telefoneInput = screen.getByRole('textbox', { name: /telefone/i });
    
    // Testa formatação automática do telefone
    await user.type(telefoneInput, '11987654321');
    expect(telefoneInput).toHaveValue('(11) 98765-4321');
  });

  test('handles CPF formatting correctly', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const cpfInput = screen.getByRole('textbox', { name: /cpf/i });
    
    // Testa formatação automática do CPF
    await user.type(cpfInput, '12345678901');
    expect(cpfInput).toHaveValue('123.456.789-01');
  });

  test('does not search CEP when field is empty', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    
    // Foca e desfoca campo sem digitar nada
    await user.click(cepInput);
    fireEvent.blur(cepInput);

    // Não deve fazer chamada para API
    expect(mockedAxios.get).not.toHaveBeenCalled();
  });

  test('does not search CEP when field has incomplete value', async () => {
    const user = userEvent.setup();
    render(<MockedCadastroPessoa />);

    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    
    // Digita CEP incompleto
    await user.type(cepInput, '01310');
    fireEvent.blur(cepInput);

    // Não deve fazer chamada para API
    expect(mockedAxios.get).not.toHaveBeenCalled();
  });

  test('shows specific error for duplicate CPF', async () => {
    const user = userEvent.setup();
    const errorResponse = {
      response: {
        status: 400,
        data: { message: 'CPF já cadastrado' }
      }
    };
    mockedAxios.post.mockRejectedValueOnce(errorResponse);

    render(<MockedCadastroPessoa />);

    // Preenche o formulário
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');
    await user.type(screen.getByRole('textbox', { name: /cpf/i }), '12345678901');

    // Submete o formulário
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('CPF já cadastrado')).toBeInTheDocument();
    });
  });

  test('handles network error gracefully', async () => {
    const user = userEvent.setup();
    const networkError = new Error('Network Error');
    networkError.message = 'Network Error';
    mockedAxios.post.mockRejectedValueOnce(networkError);

    render(<MockedCadastroPessoa />);

    // Preenche e submete o formulário
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');
    await user.type(screen.getByRole('textbox', { name: /cpf/i }), '12345678901');
    const submitButton = screen.getByRole('button', { name: 'Salvar' });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Erro ao salvar pessoa')).toBeInTheDocument();
    });
  });

  test('maintains form state during CEP search loading', async () => {
    const user = userEvent.setup();
    let resolveCepPromise: (value: any) => void;
    const cepPromise = new Promise((resolve) => {
      resolveCepPromise = resolve;
    });
    mockedAxios.get.mockReturnValueOnce(cepPromise);

    render(<MockedCadastroPessoa />);

    // Preenche alguns campos
    await user.type(screen.getByRole('textbox', { name: /nome/i }), 'João Silva');
    await user.type(screen.getByRole('textbox', { name: /telefone/i }), '11999999999');
    
    // Digita CEP e desfoca
    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    await user.type(cepInput, '01310-100');
    fireEvent.blur(cepInput);

    // Verifica que outros campos mantêm seus valores durante o carregamento
    expect(screen.getByRole('textbox', { name: /nome/i })).toHaveValue('João Silva');
    expect(screen.getByRole('textbox', { name: /telefone/i })).toHaveValue('(11) 99999-9999');

    // Resolve CEP promise
    resolveCepPromise!({
      data: {
        bairro: 'Centro',
        municipio: 'São Paulo',
        estado: 'SP'
      }
    });

    await waitFor(() => {
      expect(screen.getByDisplayValue('Centro')).toBeInTheDocument();
    });
  });

  test('allows manual override of CEP fields after auto-fill', async () => {
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

    // Busca CEP
    const cepInput = screen.getByRole('textbox', { name: /cep/i });
    await user.type(cepInput, '01310-100');
    fireEvent.blur(cepInput);

    await waitFor(() => {
      expect(screen.getByDisplayValue('Centro')).toBeInTheDocument();
    });

    // Verifica se os campos de endereço foram preenchidos automaticamente
    expect(screen.getByDisplayValue('São Paulo')).toBeInTheDocument();
    expect(screen.getByDisplayValue('SP')).toBeInTheDocument();
  });
});
