import React from 'react';
import { Button, Container, Typography } from '@mui/material';

export default function DownloadCsv() {
  const handleDownload = () => {
    window.location.href = '/api/relatorios/download';
  };

  return (
    <Container>
      <Typography variant="h4">Download de CSV</Typography>
      <Button variant="contained" onClick={handleDownload}>
        Baixar CSV
      </Button>
    </Container>
  );
}