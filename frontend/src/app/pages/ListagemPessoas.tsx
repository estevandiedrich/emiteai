import React, { useEffect, useState } from 'react';
import { Container, Typography, List, ListItem, CircularProgress, Alert } from '@mui/material';
import axios from 'axios';

export default function ListagemPessoas() {
  const [pessoas, setPessoas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    axios.get('/api/pessoas')
      .then(response => setPessoas(response.data))
      .catch(() => setError("Erro ao carregar pessoas"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container>
      <Typography variant="h4">Pessoas Cadastradas</Typography>
      <List>
        {pessoas.map(p => (
          <ListItem key={p.id}>{p.nome} - {p.cpf}</ListItem>
        ))}
      </List>
    </Container>
  );
}