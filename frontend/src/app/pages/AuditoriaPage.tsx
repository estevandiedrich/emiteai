import React, { useEffect, useState } from 'react';
import { 
  Typography, 
  Alert, 
  CircularProgress, 
  Card, 
  CardContent, 
  Grid, 
  Chip,
  TableContainer,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Paper,
  TextField,
  Button,
  Box
} from '@mui/material';
import axios from 'axios';
import { StyledContainer, StyledTitle, StyledCard } from '../components/StyledComponents';

interface AuditoriaData {
  id: number;
  timestampRequisicao: string;
  metodoHttp: string;
  endpoint: string;
  ipOrigem: string;
  statusResposta: number;
  tempoProcessamento: number;
  erro?: string;
}

interface EstatisticasData {
  totalRequisicoes: number;
  distribuicaoStatus: { [key: string]: number };
  periodoHoras: number;
  timestampConsulta: string;
}

export default function AuditoriaPage() {
  const [auditorias, setAuditorias] = useState<AuditoriaData[]>([]);
  const [estatisticas, setEstatisticas] = useState<EstatisticasData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroEndpoint, setFiltroEndpoint] = useState('');
  const [horasFiltro, setHorasFiltro] = useState(24);

  useEffect(() => {
    carregarDados();
  }, [horasFiltro]);

  const carregarDados = async () => {
    try {
      setLoading(true);
      setError(null);

      // Carregar auditorias recentes
      const auditoriaResponse = await axios.get(`/api/auditoria/recentes?horas=${horasFiltro}`);
      setAuditorias(auditoriaResponse.data);

      // Carregar estatísticas
      const estatisticasResponse = await axios.get(`/api/auditoria/estatisticas?horas=${horasFiltro}`);
      setEstatisticas(estatisticasResponse.data);

    } catch (err) {
      setError('Erro ao carregar dados de auditoria');
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  const filtrarPorEndpoint = async () => {
    if (!filtroEndpoint.trim()) {
      carregarDados();
      return;
    }

    try {
      setLoading(true);
      const response = await axios.get(`/api/auditoria/endpoint?endpoint=${encodeURIComponent(filtroEndpoint)}`);
      setAuditorias(response.data);
    } catch (err) {
      setError('Erro ao filtrar auditorias');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: number) => {
    if (status >= 200 && status < 300) return 'success';
    if (status >= 300 && status < 400) return 'info';
    if (status >= 400 && status < 500) return 'warning';
    return 'error';
  };

  const formatarTempo = (milissegundos: number) => {
    if (milissegundos < 1000) return `${milissegundos}ms`;
    return `${(milissegundos / 1000).toFixed(2)}s`;
  };

  const formatarDataHora = (timestamp: string) => {
    return new Date(timestamp).toLocaleString('pt-BR');
  };

  if (loading && auditorias.length === 0) {
    return (
      <StyledContainer>
        <CircularProgress />
        <Typography sx={{ mt: 2 }}>Carregando dados de auditoria...</Typography>
      </StyledContainer>
    );
  }

  return (
    <StyledContainer>
      <StyledTitle variant="h3">
        Auditoria do Sistema
      </StyledTitle>

      {error && (
        <Alert severity="error" sx={{ mb: 2, maxWidth: 800 }}>
          {error}
        </Alert>
      )}

      {/* Estatísticas */}
      {estatisticas && (
        <StyledCard>
          <Typography variant="h5" gutterBottom>
            Estatísticas (últimas {estatisticas.periodoHoras} horas)
          </Typography>
          
          <Grid container spacing={3}>
            <Grid item xs={12} md={4}>
              <Card>
                <CardContent>
                  <Typography variant="h6" color="primary">
                    Total de Requisições
                  </Typography>
                  <Typography variant="h4">
                    {estatisticas.totalRequisicoes}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>

            <Grid item xs={12} md={8}>
              <Card>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Distribuição por Status HTTP
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {Object.entries(estatisticas.distribuicaoStatus).map(([status, count]) => (
                      <Chip
                        key={status}
                        label={`${status}: ${count}`}
                        color={getStatusColor(parseInt(status))}
                        variant="outlined"
                      />
                    ))}
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </StyledCard>
      )}

      {/* Filtros */}
      <StyledCard>
        <Typography variant="h6" gutterBottom>
          Filtros de Auditoria
        </Typography>
        
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Filtrar por Endpoint"
              value={filtroEndpoint}
              onChange={(e) => setFiltroEndpoint(e.target.value)}
              placeholder="/api/pessoas"
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <TextField
              fullWidth
              label="Últimas Horas"
              type="number"
              value={horasFiltro}
              onChange={(e) => setHorasFiltro(parseInt(e.target.value) || 24)}
              inputProps={{ min: 1, max: 168 }}
            />
          </Grid>
          <Grid item xs={12} md={2}>
            <Button 
              variant="contained" 
              onClick={filtrarPorEndpoint}
              fullWidth
            >
              Filtrar
            </Button>
          </Grid>
          <Grid item xs={12} md={2}>
            <Button 
              variant="outlined" 
              onClick={carregarDados}
              fullWidth
            >
              Limpar
            </Button>
          </Grid>
        </Grid>
      </StyledCard>

      {/* Tabela de Auditorias */}
      <StyledCard>
        <Typography variant="h6" gutterBottom>
          Registro de Auditorias ({auditorias.length} registros)
        </Typography>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
            <CircularProgress />
          </Box>
        ) : (
          <TableContainer component={Paper} sx={{ maxHeight: 600 }}>
            <Table stickyHeader>
              <TableHead>
                <TableRow>
                  <TableCell>Data/Hora</TableCell>
                  <TableCell>Método</TableCell>
                  <TableCell>Endpoint</TableCell>
                  <TableCell>IP</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Tempo</TableCell>
                  <TableCell>Erro</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {auditorias.map((auditoria) => (
                  <TableRow key={auditoria.id} hover>
                    <TableCell>
                      {formatarDataHora(auditoria.timestampRequisicao)}
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={auditoria.metodoHttp} 
                        size="small"
                        color={auditoria.metodoHttp === 'GET' ? 'info' : 
                               auditoria.metodoHttp === 'POST' ? 'success' :
                               auditoria.metodoHttp === 'PUT' ? 'warning' : 'error'}
                      />
                    </TableCell>
                    <TableCell>{auditoria.endpoint}</TableCell>
                    <TableCell>{auditoria.ipOrigem}</TableCell>
                    <TableCell>
                      <Chip 
                        label={auditoria.statusResposta} 
                        size="small"
                        color={getStatusColor(auditoria.statusResposta)}
                      />
                    </TableCell>
                    <TableCell>{formatarTempo(auditoria.tempoProcessamento)}</TableCell>
                    <TableCell>
                      {auditoria.erro ? (
                        <Chip label="Erro" color="error" size="small" />
                      ) : (
                        <Chip label="OK" color="success" size="small" />
                      )}
                    </TableCell>
                  </TableRow>
                ))}
                {auditorias.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      <Typography color="textSecondary">
                        Nenhum registro de auditoria encontrado
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </StyledCard>
    </StyledContainer>
  );
}
