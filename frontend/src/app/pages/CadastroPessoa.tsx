import React, { useState, useEffect } from 'react';
import { TextField, Button, Container, Typography, Box, CircularProgress, Alert } from '@mui/material';
import axios from 'axios';
import { buildApiUrl, API_CONFIG } from '../../config/api';
import { useParams, useNavigate } from 'react-router-dom';

interface Endereco {
  id?: number;
  cep: string;
  numero: string;
  complemento: string;
  bairro: string;
  municipio: string;
  estado: string;
}

interface Pessoa {
  id?: number;
  nome: string;
  telefone: string;
  cpf: string;
  endereco?: Endereco;
}

export default function CadastroPessoa() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEditing = !!id;
  
  const [form, setForm] = useState({ nome: '', telefone: '', cpf: '', cep: '', numero: '', complemento: '' });
  const [endereco, setEndereco] = useState({ bairro: '', municipio: '', estado: '' });
  const [loading, setLoading] = useState(false);
  const [loadingCep, setLoadingCep] = useState(false);
  const [loadingPessoa, setLoadingPessoa] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const formatCep = (cep: string) => {
    return cep.replace(/\D/g, '');
  };

  // Função para validar CEP (8 dígitos)
  const isValidCep = (cep: string) => {
    const cleanCep = formatCep(cep);
    return cleanCep.length === 8;
  };

  // Função para formatar CPF (000.000.000-00)
  const formatCpf = (cpf: string) => {
    return cpf
      .replace(/\D/g, '') // Remove tudo que não é dígito
      .replace(/(\d{3})(\d)/, '$1.$2') // Coloca um ponto entre o terceiro e quarto dígitos
      .replace(/(\d{3})(\d)/, '$1.$2') // Coloca um ponto entre o terceiro e quarto dígitos
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2'); // Coloca um hífen entre o terceiro e quarto dígitos
  };

  // Função para formatar telefone (00) 0000-0000 ou (00) 00000-0000
  const formatTelefone = (telefone: string) => {
    const numbers = telefone.replace(/\D/g, '');
    
    if (numbers.length <= 2) {
      return numbers;
    } else if (numbers.length <= 6) {
      return numbers.replace(/(\d{2})(\d+)/, '($1) $2');
    } else if (numbers.length <= 10) {
      return numbers.replace(/(\d{2})(\d{4})(\d+)/, '($1) $2-$3');
    } else {
      return numbers.replace(/(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    if (name === 'cep') {
      const formattedCep = value.replace(/\D/g, '').replace(/(\d{5})(\d)/, '$1-$2');
      setForm({ ...form, [name]: formattedCep });
    } else if (name === 'cpf') {
      const formattedCpf = formatCpf(value);
      setForm({ ...form, [name]: formattedCpf });
    } else if (name === 'telefone') {
      const formattedTelefone = formatTelefone(value);
      setForm({ ...form, [name]: formattedTelefone });
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
      
      setEndereco({
        bairro: data.bairro || '',
        municipio: data.localidade || data.municipio || '',
        estado: data.uf || data.estado || ''
      });
      
      setError(null);
    } catch (err) {
      setError("CEP não encontrado ou inválido");
      setEndereco({ bairro: '', municipio: '', estado: '' });
    } finally {
      setLoadingCep(false);
    }
  };

  useEffect(() => {
    if (isValidCep(form.cep)) {
      buscarCep(form.cep);
    }
  }, [form.cep]); // eslint-disable-line react-hooks/exhaustive-deps

  const loadPessoa = async (pessoaId: string) => {
    setLoadingPessoa(true);
    setError(null);
    
    try {
      const response = await axios.get(buildApiUrl(`${API_CONFIG.ENDPOINTS.PESSOAS}/${pessoaId}`));
      const pessoa: Pessoa = response.data;
      
      setForm({
        nome: pessoa.nome || '',
        telefone: pessoa.telefone ? formatTelefone(pessoa.telefone) : '',
        cpf: pessoa.cpf ? formatCpf(pessoa.cpf) : '',
        cep: pessoa.endereco?.cep ? pessoa.endereco.cep.replace(/(\d{5})(\d)/, '$1-$2') : '',
        numero: pessoa.endereco?.numero || '',
        complemento: pessoa.endereco?.complemento || ''
      });
      
      if (pessoa.endereco) {
        setEndereco({
          bairro: pessoa.endereco.bairro || '',
          municipio: pessoa.endereco.municipio || '',
          estado: pessoa.endereco.estado || ''
        });
      }
    } catch (err) {
      setError("Erro ao carregar dados da pessoa");
    } finally {
      setLoadingPessoa(false);
    }
  };

  useEffect(() => {
    if (isEditing && id) {
      loadPessoa(id);
    }
  }, [isEditing, id]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    
    await new Promise(resolve => setTimeout(resolve, 100));
    
    if (!form.nome.trim()) {
      setError("Nome é obrigatório");
      setLoading(false);
      return;
    }
    
    if (!form.cpf.trim()) {
      setError("CPF é obrigatório");
      setLoading(false);
      return;
    }
    
    try {
      const pessoaData = {
        nome: form.nome,
        telefone: form.telefone.replace(/\D/g, ''),
        cpf: form.cpf.replace(/\D/g, ''),
        endereco: {
          cep: formatCep(form.cep),
          numero: form.numero,
          complemento: form.complemento,
          bairro: endereco.bairro,
          municipio: endereco.municipio,
          estado: endereco.estado
        }
      };
      
      if (isEditing && id) {
        await axios.put(buildApiUrl(`${API_CONFIG.ENDPOINTS.PESSOAS}/${id}`), pessoaData);
        setSuccess(true);
        
        setTimeout(() => {
          navigate('/listagem-pessoas');
        }, 2000);
      } else {
        await axios.post(buildApiUrl(API_CONFIG.ENDPOINTS.PESSOAS), pessoaData);
        setSuccess(true);
        
        setForm({ nome: '', telefone: '', cpf: '', cep: '', numero: '', complemento: '' });
        setEndereco({ bairro: '', municipio: '', estado: '' });
      }
    } catch (err: any) {
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Erro ao salvar pessoa");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom>
        {isEditing ? 'Editar Pessoa' : 'Cadastro de Pessoa'}
      </Typography>
      
      {loadingPessoa && (
        <Box display="flex" justifyContent="center" mb={2}>
          <CircularProgress />
        </Box>
      )}
      
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {isEditing ? 'Pessoa atualizada com sucesso!' : 'Pessoa cadastrada com sucesso!'}
        </Alert>
      )}

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
          placeholder="(00) 0000-0000 ou (00) 00000-0000"
          helperText="Telefone fixo ou celular"
        />
        
        <TextField 
          name="cpf" 
          label="CPF" 
          value={form.cpf} 
          onChange={handleChange} 
          fullWidth 
          required
          placeholder="000.000.000-00"
          helperText="Formato: 000.000.000-00"
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
          disabled={loading || loadingCep || loadingPessoa}
          size="large"
        >
          {loading ? (
            <CircularProgress size={24} />
          ) : (
            isEditing ? "Atualizar" : "Salvar"
          )}
        </Button>
      </Box>
    </Container>
  );
}