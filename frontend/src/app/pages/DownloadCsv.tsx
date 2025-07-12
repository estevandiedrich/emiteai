import React, { useState } from 'react';
import { Button, Container, Typography, Alert, CircularProgress, Box } from '@mui/material';
import { buildApiUrl, API_CONFIG } from '../../config/api';
import axios from 'axios';

export default function DownloadCsv() {
  const [loading, setLoading] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleGenerateReport = async () => {
    setGenerating(true);
    setMessage(null);
    setError(null);

    try {
      const response = await fetch(buildApiUrl(`${API_CONFIG.ENDPOINTS.RELATORIOS}/csv`), {
        method: 'POST'
      });
      
      if (response.ok) {
        setMessage("Relatório sendo gerado. Aguarde alguns instantes e tente baixar novamente.");
      } else {
        throw new Error('Erro ao gerar relatório');
      }
      setTimeout(() => setGenerating(false), 2000); // Simula tempo de processamento
    } catch (err) {
      setError("Erro ao gerar relatório. Tente novamente.");
      setGenerating(false);
    }
  };

  const handleDownload = async () => {
    setLoading(true);
    setError(null);
    setMessage(null);

    try {
      const downloadUrl = buildApiUrl(`${API_CONFIG.ENDPOINTS.RELATORIOS}/download`);
      
      // Verifica se o arquivo está disponível
      const response = await fetch(downloadUrl, { method: 'HEAD' });
      
      if (response.status === 404) {
        setError("Arquivo não encontrado. Gere o relatório primeiro.");
        return;
      }

      if (response.status === 500) {
        setError("Erro no servidor. Tente novamente mais tarde.");
        return;
      }

      if (!response.ok) {
        throw new Error('Erro ao baixar arquivo');
      }
      
      // Faz o download do arquivo
      window.location.href = downloadUrl;
      
    } catch (err) {
      setError("Erro ao baixar relatório. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md">
      <Typography variant="h4" gutterBottom>
        Relatório de Pessoas
      </Typography>
      
      <Typography variant="body1" gutterBottom sx={{ mb: 3 }}>
        Este relatório contém todas as pessoas cadastradas no formato CSV, compatível com Excel.
      </Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {message && <Alert severity="success" sx={{ mb: 2 }}>{message}</Alert>}

      <Box display="flex" gap={2} flexDirection="column" maxWidth="300px">
        <Button 
          variant="contained" 
          onClick={handleGenerateReport}
          disabled={generating}
          color="primary"
          size="large"
        >
          {generating ? (
            <>
              <CircularProgress size={20} sx={{ mr: 1 }} />
              Gerando Relatório...
            </>
          ) : (
            "Gerar Relatório"
          )}
        </Button>

        <Button 
          variant="outlined" 
          onClick={handleDownload}
          disabled={loading}
          color="secondary"
          size="large"
        >
          {loading ? (
            <>
              <CircularProgress size={20} sx={{ mr: 1 }} />
              Baixando...
            </>
          ) : (
            "Baixar CSV"
          )}
        </Button>
      </Box>

      <Box sx={{ mt: 3 }}>
        <Typography variant="h6" gutterBottom>
          Como funciona:
        </Typography>
        <Typography variant="body2" component="ul" sx={{ pl: 2 }}>
          <li>1. Clique em "Gerar Relatório" para solicitar a criação do arquivo CSV</li>
          <li>2. O sistema processa a solicitação de forma assíncrona usando RabbitMQ</li>
          <li>3. Aguarde alguns segundos para o processamento ser concluído</li>
          <li>4. Clique em "Baixar CSV" para fazer o download do arquivo gerado</li>
        </Typography>
      </Box>
    </Container>
  );
}