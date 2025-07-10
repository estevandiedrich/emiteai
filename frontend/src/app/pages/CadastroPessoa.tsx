import React, { useState, useEffect } from 'react';
import { TextField, Button, Container, Typography, Box, CircularProgress, Alert } from '@mui/material';
import axios from 'axios';
import { buildApiUrl, API_CONFIG } from '../../config/api';

export default function CadastroPessoa() {
  const [form, setForm] = useState({ nome: '', telefone: '', cpf: '', cep: '', numero: '', complemento: '' });
  const [endereco, setEndereco] = useState({ bairro: '', municipio: '', estado: '' });
  const [loading, setLoading] = useState(false);
  const [loadingCep, setLoadingCep] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Função para limpar caracteres não numéricos do CEP
  const formatCep = (cep: string) => {
    return cep.replace(/\D/g, '');
  };

  // Função para validar CEP (8 dígitos)
  const isValidCep = (cep: string) => {
    const cleanCep = formatCep(cep);
    return cleanCep.length === 8;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    // Se for o campo CEP, aplica formatação
    if (name === 'cep') {
      const formattedCep = value.replace(/\D/g, '').replace(/(\d{5})(\d)/, '$1-$2');
      setForm({ ...form, [name]: formattedCep });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  const buscarCep = async (cep: string) => {
    if (!isValidCep(cep)) {
      return;
    }

    setLoadingCep(true);
    setError(null);
    
    try {
      const cleanCep = formatCep(cep);
      const response = await axios.get(buildApiUrl(`${API_CONFIG.ENDPOINTS.CEP}/${cleanCep}`));
      const data = response.data;
      
      // Mapear os campos corretamente
      setEndereco({
        bairro: data.bairro || '',
        municipio: data.localidade || data.municipio || '',
        estado: data.uf || data.estado || ''
      });
      
      setError(null);
    } catch (err) {
      console.error('Erro ao buscar CEP:', err);
      setError("CEP não encontrado ou inválido");
      setEndereco({ bairro: '', municipio: '', estado: '' });
    } finally {
      setLoadingCep(false);
    }
  };

  // Effect para buscar CEP automaticamente quando estiver completo
  useEffect(() => {
    if (isValidCep(form.cep)) {
      buscarCep(form.cep);
    }
  }, [form.cep]);

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    
    try {
      const pessoaData = {
        nome: form.nome,
        telefone: form.telefone,
        cpf: form.cpf,
        endereco: {
          cep: formatCep(form.cep),
          numero: form.numero,
          complemento: form.complemento,
          bairro: endereco.bairro,
          municipio: endereco.municipio,
          estado: endereco.estado
        }
      };
      
      await axios.post(buildApiUrl(API_CONFIG.ENDPOINTS.PESSOAS), pessoaData);
      setSuccess(true);
      
      // Limpar formulário após sucesso
      setForm({ nome: '', telefone: '', cpf: '', cep: '', numero: '', complemento: '' });
      setEndereco({ bairro: '', municipio: '', estado: '' });
    } catch (err) {
      console.error('Erro ao salvar pessoa:', err);
      setError("Erro ao salvar pessoa");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom>Cadastro de Pessoa</Typography>
      
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>Pessoa cadastrada com sucesso!</Alert>}

      <Box display="flex" flexDirection="column" gap={2}>
        <TextField 
          name="nome" 
          label="Nome" 
          value={form.nome} 
          onChange={handleChange} 
          fullWidth 
          required
        />
        
        <TextField 
          name="telefone" 
          label="Telefone" 
          value={form.telefone} 
          onChange={handleChange} 
          fullWidth 
        />
        
        <TextField 
          name="cpf" 
          label="CPF" 
          value={form.cpf} 
          onChange={handleChange} 
          fullWidth 
          required
        />
        
        <Box position="relative">
          <TextField 
            name="cep" 
            label="CEP" 
            value={form.cep} 
            onChange={handleChange} 
            fullWidth 
            placeholder="00000-000"
            helperText="Digite o CEP para buscar o endereço automaticamente"
          />
          {loadingCep && (
            <CircularProgress 
              size={20} 
              sx={{ 
                position: 'absolute', 
                right: 10, 
                top: '50%', 
                transform: 'translateY(-50%)' 
              }} 
            />
          )}
        </Box>
        
        <TextField 
          name="numero" 
          label="Número" 
          value={form.numero} 
          onChange={handleChange} 
          fullWidth 
        />
        
        <TextField 
          name="complemento" 
          label="Complemento" 
          value={form.complemento} 
          onChange={handleChange} 
          fullWidth 
        />
        
        <TextField 
          label="Bairro" 
          value={endereco.bairro} 
          disabled 
          fullWidth 
          helperText="Preenchido automaticamente via CEP"
        />
        
        <TextField 
          label="Município" 
          value={endereco.municipio} 
          disabled 
          fullWidth 
          helperText="Preenchido automaticamente via CEP"
        />
        
        <TextField 
          label="Estado" 
          value={endereco.estado} 
          disabled 
          fullWidth 
          helperText="Preenchido automaticamente via CEP"
        />
        
        <Button 
          variant="contained" 
          onClick={handleSubmit} 
          disabled={loading || loadingCep}
          size="large"
        >
          {loading ? <CircularProgress size={24} /> : "Salvar"}
        </Button>
      </Box>
    </Container>
  );
}