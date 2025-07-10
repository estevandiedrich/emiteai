import React, { useEffect, useState } from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  Card, 
  CardContent, 
  CardActions,
  Button,
  CircularProgress, 
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Grid,
  Chip,
  Divider,
  IconButton,
  Tooltip
} from '@mui/material';
import { Edit, Delete, Phone, LocationOn, Person } from '@mui/icons-material';
import axios from 'axios';
import { buildApiUrl, API_CONFIG } from '../../config/api';
import { useNavigate } from 'react-router-dom';

interface Endereco {
  id: number;
  cep: string;
  numero: string;
  complemento: string;
  bairro: string;
  municipio: string;
  estado: string;
}

interface Pessoa {
  id: number;
  nome: string;
  telefone: string;
  cpf: string;
  endereco?: Endereco;
}

export default function ListagemPessoas() {
  const [pessoas, setPessoas] = useState<Pessoa[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [pessoaToDelete, setPessoaToDelete] = useState<Pessoa | null>(null);
  const [deleting, setDeleting] = useState(false);
  const navigate = useNavigate();

  const loadPessoas = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await axios.get(buildApiUrl(API_CONFIG.ENDPOINTS.PESSOAS));
      setPessoas(response.data);
    } catch (err) {
      console.error('Erro ao carregar pessoas:', err);
      setError("Erro ao carregar pessoas");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPessoas();
  }, []);

  const handleEdit = (pessoa: Pessoa) => {
    // Navegar para a página de cadastro com o ID da pessoa para edição
    navigate(`/cadastro-pessoa/${pessoa.id}`);
  };

  const handleDeleteClick = (pessoa: Pessoa) => {
    setPessoaToDelete(pessoa);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!pessoaToDelete) return;

    setDeleting(true);
    setError(null);

    try {
      await axios.delete(buildApiUrl(`${API_CONFIG.ENDPOINTS.PESSOAS}/${pessoaToDelete.id}`));
      setSuccess(`Pessoa ${pessoaToDelete.nome} excluída com sucesso!`);
      setDeleteDialogOpen(false);
      setPessoaToDelete(null);
      
      // Recarregar a lista
      await loadPessoas();
      
      // Limpar mensagem de sucesso após 5 segundos
      setTimeout(() => setSuccess(null), 5000);
    } catch (err) {
      console.error('Erro ao excluir pessoa:', err);
      setError("Erro ao excluir pessoa");
    } finally {
      setDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setPessoaToDelete(null);
  };

  const formatCep = (cep: string) => {
    return cep.replace(/(\d{5})(\d)/, '$1-$2');
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Typography variant="h4" gutterBottom>
        Pessoas Cadastradas
      </Typography>
      
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

      {pessoas.length === 0 ? (
        <Box textAlign="center" mt={4}>
          <Typography variant="h6" color="textSecondary">
            Nenhuma pessoa cadastrada
          </Typography>
          <Button 
            variant="contained" 
            onClick={() => navigate('/cadastro-pessoa')}
            sx={{ mt: 2 }}
          >
            Cadastrar Primeira Pessoa
          </Button>
        </Box>
      ) : (
        <Box display="flex" flexDirection="column" gap={2}>
          {pessoas.map((pessoa, index) => (
            <Card 
              key={pessoa.id} 
              elevation={2}
              sx={{
                backgroundColor: index % 2 === 0 ? '#ffffff' : '#f5f5f5',
                '&:hover': {
                  elevation: 4,
                  transform: 'translateY(-2px)',
                  transition: 'all 0.3s ease'
                }
              }}
            >
              <CardContent>
                <Grid container spacing={2}>
                  <Grid item xs={12} md={8}>
                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <Person color="primary" />
                      <Typography variant="h6" component="h2">
                        {pessoa.nome}
                      </Typography>
                    </Box>
                    
                    <Box display="flex" flexDirection="column" gap={1}>
                      <Box display="flex" alignItems="center" gap={1}>
                        <Chip 
                          label={pessoa.cpf} 
                          variant="outlined" 
                          size="small"
                          color="primary"
                        />
                        {pessoa.telefone && (
                          <Box display="flex" alignItems="center" gap={0.5}>
                            <Phone fontSize="small" color="action" />
                            <Typography variant="body2" color="textSecondary">
                              {pessoa.telefone}
                            </Typography>
                          </Box>
                        )}
                      </Box>
                      
                      {pessoa.endereco && (
                        <Box display="flex" alignItems="center" gap={0.5}>
                          <LocationOn fontSize="small" color="action" />
                          <Typography variant="body2" color="textSecondary">
                            {pessoa.endereco.numero && `${pessoa.endereco.numero}, `}
                            {pessoa.endereco.bairro && `${pessoa.endereco.bairro}, `}
                            {pessoa.endereco.municipio && `${pessoa.endereco.municipio}, `}
                            {pessoa.endereco.estado && `${pessoa.endereco.estado} `}
                            {pessoa.endereco.cep && `- ${formatCep(pessoa.endereco.cep)}`}
                          </Typography>
                        </Box>
                      )}
                    </Box>
                  </Grid>
                  
                  <Grid item xs={12} md={4}>
                    <Box display="flex" gap={1} justifyContent="flex-end" height="100%" alignItems="center">
                      <Tooltip title="Editar pessoa">
                        <IconButton 
                          color="primary" 
                          onClick={() => handleEdit(pessoa)}
                          size="small"
                        >
                          <Edit />
                        </IconButton>
                      </Tooltip>
                      
                      <Tooltip title="Excluir pessoa">
                        <IconButton 
                          color="error" 
                          onClick={() => handleDeleteClick(pessoa)}
                          size="small"
                        >
                          <Delete />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          ))}
        </Box>
      )}

      <Box mt={3} display="flex" justifyContent="center">
        <Button 
          variant="contained" 
          onClick={() => navigate('/cadastro-pessoa')}
          size="large"
        >
          Cadastrar Nova Pessoa
        </Button>
      </Box>

      {/* Dialog de confirmação de exclusão */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteCancel}
        aria-labelledby="delete-dialog-title"
        aria-describedby="delete-dialog-description"
      >
        <DialogTitle id="delete-dialog-title">
          Confirmar Exclusão
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="delete-dialog-description">
            Tem certeza que deseja excluir a pessoa <strong>{pessoaToDelete?.nome}</strong>?
            Esta ação não pode ser desfeita.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} disabled={deleting}>
            Cancelar
          </Button>
          <Button 
            onClick={handleDeleteConfirm} 
            color="error" 
            disabled={deleting}
            autoFocus
          >
            {deleting ? <CircularProgress size={20} /> : 'Excluir'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}