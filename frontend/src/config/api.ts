// Configuração da API
export const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  ENDPOINTS: {
    PESSOAS: '/api/pessoas',
    CEP: '/api/cep',
    AUDITORIA: '/api/auditoria',
    RELATORIOS: '/api/relatorios'
  }
};

// Helper para construir URLs completas
export const buildApiUrl = (endpoint: string) => {
  return `${API_CONFIG.BASE_URL}${endpoint}`;
};

// Helper para configurar headers com CORS
export const getApiHeaders = () => {
  return {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
    'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept, Authorization'
  };
};
