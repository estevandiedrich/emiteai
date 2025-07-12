import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
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
});
