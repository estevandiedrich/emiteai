import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import AuditoriaPage from '../app/pages/AuditoriaPage';

// Mock do axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

// Mock react-router-dom
jest.mock('react-router-dom');

const theme = createTheme();

const MockedAuditoriaPage = () => (
  <ThemeProvider theme={theme}>
    <AuditoriaPage />
  </ThemeProvider>
);

const mockAuditoriaData = [
  {
    id: 1,
    timestampRequisicao: '2023-07-12T10:30:00',
    metodoHttp: 'GET',
    endpoint: '/api/pessoas',
    ipOrigem: '127.0.0.1',
    statusResposta: 200,
    tempoProcessamento: 150,
    erro: null
  },
  {
    id: 2,
    timestampRequisicao: '2023-07-12T11:30:00',
    metodoHttp: 'POST',
    endpoint: '/api/pessoas',
    ipOrigem: '192.168.1.1',
    statusResposta: 201,
    tempoProcessamento: 300,
    erro: null
  },
  {
    id: 3,
    timestampRequisicao: '2023-07-12T12:30:00',
    metodoHttp: 'DELETE',
    endpoint: '/api/pessoas/1',
    ipOrigem: '192.168.1.2',
    statusResposta: 500,
    tempoProcessamento: 1200,
    erro: 'Internal Server Error'
  }
];

const mockEstatisticasData = {
  totalRequisicoes: 100,
  distribuicaoStatus: {
    '200': 80,
    '201': 15,
    '404': 3,
    '500': 2
  },
  periodoHoras: 24,
  timestampConsulta: '2023-07-12T15:30:00'
};

describe('AuditoriaPage Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock das chamadas para API
    mockedAxios.get.mockImplementation((url) => {
      if (url.includes('/recentes')) {
        return Promise.resolve({ data: mockAuditoriaData });
      }
      if (url.includes('/estatisticas')) {
        return Promise.resolve({ data: mockEstatisticasData });
      }
      if (url.includes('/endpoint')) {
        return Promise.resolve({ data: mockAuditoriaData.filter(a => a.endpoint.includes('pessoas')) });
      }
      return Promise.reject(new Error('URL não mockada'));
    });
  });

  test('renders page title correctly', async () => {
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      expect(screen.getByText('Auditoria do Sistema')).toBeInTheDocument();
    });
  });

  test('loads and displays audit data on mount', async () => {
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    // Aguarda carregamento dos dados
    await waitFor(() => {
      expect(screen.getByText('Registro de Auditorias (3 registros)')).toBeInTheDocument();
    });

    // Verifica se os dados foram carregados
    const apiPessoasElements = screen.getAllByText('/api/pessoas');
    expect(apiPessoasElements.length).toBeGreaterThan(0);
    expect(screen.getByText('GET')).toBeInTheDocument();
    expect(screen.getByText('POST')).toBeInTheDocument();
    expect(screen.getByText('DELETE')).toBeInTheDocument();
    expect(screen.getByText('127.0.0.1')).toBeInTheDocument();
  });

  test('displays statistics correctly', async () => {
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      expect(screen.getByText('Estatísticas (últimas 24 horas)')).toBeInTheDocument();
    });

    // Verifica estatísticas
    expect(screen.getByText('Total de Requisições')).toBeInTheDocument();
    expect(screen.getByText('100')).toBeInTheDocument();
    expect(screen.getByText('Distribuição por Status HTTP')).toBeInTheDocument();
  });

  test('filters audit data by endpoint', async () => {
    const user = userEvent.setup();
    
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      expect(screen.getByText('Registro de Auditorias (3 registros)')).toBeInTheDocument();
    });

    // Encontra e preenche o campo de filtro
    const filtroInput = screen.getByLabelText('Filtrar por Endpoint');
    await user.type(filtroInput, '/api/pessoas');

    // Clica no botão filtrar
    const filtrarButton = screen.getByRole('button', { name: 'Filtrar' });
    await user.click(filtrarButton);

    // Verifica se a API foi chamada com o filtro
    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('/endpoint?endpoint=%2Fapi%2Fpessoas')
      );
    });
  });

  test('displays error message when API call fails', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('API Error'));

    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      expect(screen.getByText('Erro ao carregar dados de auditoria')).toBeInTheDocument();
    });
  });

  test('displays correct status colors and method chips', async () => {
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      // Verifica se os chips de método estão presentes
      expect(screen.getByText('GET')).toBeInTheDocument();
      expect(screen.getByText('POST')).toBeInTheDocument();
      expect(screen.getByText('DELETE')).toBeInTheDocument();

      // Verifica se os status codes estão presentes
      expect(screen.getByText('200')).toBeInTheDocument();
      expect(screen.getByText('201')).toBeInTheDocument();
      expect(screen.getByText('500')).toBeInTheDocument();
    });
  });

  test('handles empty endpoint filter correctly', async () => {
    const user = userEvent.setup();
    
    await act(async () => {
      render(<MockedAuditoriaPage />);
    });

    await waitFor(() => {
      expect(screen.getByText('Registro de Auditorias (3 registros)')).toBeInTheDocument();
    });

    // Deixa o filtro vazio e clica em filtrar
    const filtrarButton = screen.getByRole('button', { name: 'Filtrar' });
    await user.click(filtrarButton);

    // Deve chamar carregarDados em vez de filtrar
    await waitFor(() => {
      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('/recentes?horas=24')
      );
    });
  });
});
