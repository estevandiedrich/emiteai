import React, { useState } from 'react';
import { TextField, Button, Container, Typography, Box, CircularProgress, Alert } from '@mui/material';
import axios from 'axios';

export default function CadastroPessoa() {
  const [form, setForm] = useState({ nome: '', telefone: '', cpf: '', cep: '', numero: '', complemento: '' });
  const [endereco, setEndereco] = useState({ bairro: '', municipio: '', estado: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const buscarCep = async () => {
    try {
      const response = await axios.get(`/api/cep/${form.cep}`);
      setEndereco(response.data);
    } catch {
      setError("Erro ao buscar o CEP");
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await axios.post('/api/pessoas', { ...form, endereco });
      setSuccess(true);
    } catch {
      setError("Erro ao salvar pessoa");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Typography variant="h4">Cadastro de Pessoa</Typography>
      {error && <Alert severity="error">{error}</Alert>}
      {success && <Alert severity="success">Pessoa cadastrada com sucesso!</Alert>}

      <Box display="flex" flexDirection="column" gap={2}>
        <TextField name="nome" label="Nome" value={form.nome} onChange={handleChange} fullWidth />
        <TextField name="telefone" label="Telefone" value={form.telefone} onChange={handleChange} fullWidth />
        <TextField name="cpf" label="CPF" value={form.cpf} onChange={handleChange} fullWidth />
        <TextField name="cep" label="CEP" value={form.cep} onChange={handleChange} onBlur={buscarCep} fullWidth />
        <TextField name="numero" label="Número" value={form.numero} onChange={handleChange} fullWidth />
        <TextField name="complemento" label="Complemento" value={form.complemento} onChange={handleChange} fullWidth />
        <TextField label="Bairro" value={endereco.bairro} disabled fullWidth />
        <TextField label="Município" value={endereco.municipio} disabled fullWidth />
        <TextField label="Estado" value={endereco.estado} disabled fullWidth />
        <Button variant="contained" onClick={handleSubmit} disabled={loading}>
          {loading ? <CircularProgress size={24} /> : "Salvar"}
        </Button>
      </Box>
    </Container>
  );
}