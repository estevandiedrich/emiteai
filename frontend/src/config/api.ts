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
