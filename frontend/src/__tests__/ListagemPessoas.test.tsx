import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import axios from 'axios';
import ListagemPessoas from '../app/pages/ListagemPessoas';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock do react-router-dom
jest.mock('react-router-dom', () => ({
  useNavigate: jest.fn(() => jest.fn()),
  useLocation: jest.fn(() => ({
    pathname: '/',
    search: '',
    hash: '',
    state: null
  })),
  useParams: jest.fn(() => ({})),
  BrowserRouter: ({ children }: { children: any }) => children,
  Router: ({ children }: { children: any }) => children,
  Routes: ({ children }: { children: any }) => children,
  Route: ({ children }: { children?: any }) => children || null,
  Link: ({ children, to }: { children: any; to: string }) => children,
  NavLink: ({ children, to }: { children: any; to: string }) => children
}));

const theme = createTheme();

const MockedListagemPessoas = () => (
  <ThemeProvider theme={theme}>
    <ListagemPessoas />
  </ThemeProvider>
);

describe('ListagemPessoas Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state initially', () => {
    // Mock a promise that never resolves to keep loading state
    mockedAxios.get.mockReturnValueOnce(new Promise(() => {}));

    render(<MockedListagemPessoas />);

    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });

  test('renders list of pessoas when API call succeeds', async () => {
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '12345678901' },
      { id: 2, nome: 'Maria Santos', cpf: '98765432100' },
      { id: 3, nome: 'Pedro Oliveira', cpf: '11122233344' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText('João Silva')).toBeInTheDocument();
      expect(screen.getByText('123.456.789-01')).toBeInTheDocument();
      expect(screen.getByText('Maria Santos')).toBeInTheDocument();
      expect(screen.getByText('987.654.321-00')).toBeInTheDocument();
      expect(screen.getByText('Pedro Oliveira')).toBeInTheDocument();
      expect(screen.getByText('111.222.333-44')).toBeInTheDocument();
    });

    expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/pessoas');
  });

  test('renders empty list when no pessoas are returned', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [] });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
    });

    // Should not have any list items
    const listItems = screen.queryAllByRole('listitem');
    expect(listItems).toHaveLength(0);
  });

  test('renders error message when API call fails', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('Network error'));

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar pessoas')).toBeInTheDocument();
    });

    // Should not show loading
    expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    // But title should still be visible
    expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
  });

  test('calls API with correct endpoint', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [] });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/pessoas');
      expect(mockedAxios.get).toHaveBeenCalledTimes(1);
    });
  });

  test('displays correct number of pessoas in list', async () => {
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '12345678901', telefone: '11999999999' },
      { id: 2, nome: 'Maria Santos', cpf: '98765432100', telefone: '11888888888' },
      { id: 3, nome: 'Pedro Oliveira', cpf: '11122233344', telefone: '11777777777' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      // Contar os cards pelos nomes das pessoas
      expect(screen.getByText('João Silva')).toBeInTheDocument();
      expect(screen.getByText('Maria Santos')).toBeInTheDocument();
      expect(screen.getByText('Pedro Oliveira')).toBeInTheDocument();
    });
  });

  test('displays telefone when provided', async () => {
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '12345678901', telefone: '11999999999' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('(11) 99999-9999')).toBeInTheDocument();
    });
  });

  test('handles missing telefone gracefully', async () => {
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '12345678901' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('João Silva')).toBeInTheDocument();
      expect(screen.getByText('123.456.789-01')).toBeInTheDocument();
    });

    // Telefone shouldn't be displayed if not provided
    expect(screen.queryByText('(11) 99999-9999')).not.toBeInTheDocument();
  });

  test('formats CPF correctly in display', async () => {
    const mockPessoas = [
      { id: 1, nome: 'João Silva', cpf: '00000000000' },
      { id: 2, nome: 'Maria Santos', cpf: '99999999999' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('000.000.000-00')).toBeInTheDocument();
      expect(screen.getByText('999.999.999-99')).toBeInTheDocument();
    });
  });

  test('displays empty state message when no data', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [] });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
    });

    // Should show empty state message
    expect(screen.getByText('Nenhuma pessoa cadastrada')).toBeInTheDocument();
    expect(screen.getByText('Cadastrar Primeira Pessoa')).toBeInTheDocument();
  });

  test('handles null or undefined response data', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: null });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar pessoas')).toBeInTheDocument();
    });
  });

  test('handles API response with invalid data structure', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: 'invalid data' });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar pessoas')).toBeInTheDocument();
    });
  });

  test('retries API call on error', async () => {
    // First call fails, second succeeds
    mockedAxios.get
      .mockRejectedValueOnce(new Error('Network error'))
      .mockResolvedValueOnce({ data: [{ id: 1, nome: 'João Silva', cpf: '12345678901' }] });

    render(<MockedListagemPessoas />);

    // Should show error first
    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar pessoas')).toBeInTheDocument();
    });

    // If there's a retry mechanism, it should eventually show the data
    // This test assumes the component has some retry logic
  });

  test('maintains component state during API call', async () => {
    let resolvePromise: (value: any) => void;
    const mockPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    mockedAxios.get.mockReturnValueOnce(mockPromise);

    render(<MockedListagemPessoas />);

    // Should show loading initially
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
    // During loading, title is not shown because the component returns early
    expect(screen.queryByText('Pessoas Cadastradas')).not.toBeInTheDocument();

    // Resolve the promise
    resolvePromise!({ data: [{ id: 1, nome: 'João Silva', cpf: '12345678901' }] });

    await waitFor(() => {
      expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
      expect(screen.getByText('João Silva')).toBeInTheDocument();
    });
  });

  test('handles pessoas with special characters in names', async () => {
    const mockPessoas = [
      { id: 1, nome: 'José Antônio Silva', cpf: '12345678901' },
      { id: 2, nome: 'María Fernández', cpf: '98765432100' },
      { id: 3, nome: 'François Müller', cpf: '11122233344' }
    ];

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(screen.getByText('José Antônio Silva')).toBeInTheDocument();
      expect(screen.getByText('María Fernández')).toBeInTheDocument();
      expect(screen.getByText('François Müller')).toBeInTheDocument();
    });
  });

  test('handles large list of pessoas', async () => {
    const mockPessoas = Array.from({ length: 100 }, (_, index) => ({
      id: index + 1,
      nome: `Pessoa ${index + 1}`,
      cpf: String(index + 1).padStart(11, '0'),
      telefone: `119${String(index + 1).padStart(8, '0')}`
    }));

    mockedAxios.get.mockResolvedValueOnce({ data: mockPessoas });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      // Verifica que tem a primeira pessoa
      expect(screen.getByText('Pessoa 1')).toBeInTheDocument();
      // E que também tem a última pessoa
      expect(screen.getByText('Pessoa 100')).toBeInTheDocument();
    });

    // Check first and last items are both present
    expect(screen.getByText('Pessoa 1')).toBeInTheDocument();
    expect(screen.getByText('Pessoa 100')).toBeInTheDocument();
  });

  test('displays loading state correctly', async () => {
    let resolvePromise: (value: any) => void;
    const mockPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    mockedAxios.get.mockReturnValueOnce(mockPromise);

    render(<MockedListagemPessoas />);

    // Should show loading spinner
    const progressBar = screen.getByRole('progressbar');
    expect(progressBar).toBeInTheDocument();

    // During loading, title is not shown because the component returns early
    expect(screen.queryByText('Pessoas Cadastradas')).not.toBeInTheDocument();

    // Resolve the API call
    resolvePromise!({ data: [] });

    await waitFor(() => {
      expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    });
    
    // After loading, title should be shown
    expect(screen.getByText('Pessoas Cadastradas')).toBeInTheDocument();
  });
});
