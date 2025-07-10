import { render, screen, waitFor } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import axios from 'axios';
import ListagemPessoas from '../app/pages/ListagemPessoas';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

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
      expect(screen.getByText('João Silva - 12345678901')).toBeInTheDocument();
      expect(screen.getByText('Maria Santos - 98765432100')).toBeInTheDocument();
      expect(screen.getByText('Pedro Oliveira - 11122233344')).toBeInTheDocument();
    });

    expect(mockedAxios.get).toHaveBeenCalledWith('/api/pessoas');
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

    // Should not show loading or the title
    expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    expect(screen.queryByText('Pessoas Cadastradas')).not.toBeInTheDocument();
  });

  test('calls API with correct endpoint', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: [] });

    render(<MockedListagemPessoas />);

    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/pessoas');
      expect(mockedAxios.get).toHaveBeenCalledTimes(1);
    });
  });
});
