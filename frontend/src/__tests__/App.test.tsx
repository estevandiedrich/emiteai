import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import App from '../app/App';

// Mock react-router-dom
jest.mock('react-router-dom');

// Mock dos componentes de páginas
jest.mock('../app/pages/CadastroPessoa', () => {
  return function CadastroPessoa() {
    return <div data-testid="cadastro-page">Cadastro Page</div>;
  };
});

jest.mock('../app/pages/ListagemPessoas', () => {
  return function ListagemPessoas() {
    return <div data-testid="listagem-page">Listagem Page</div>;
  };
});

jest.mock('../app/pages/DownloadCsv', () => {
  return function DownloadCsv() {
    return <div data-testid="download-page">Download Page</div>;
  };
});

const theme = createTheme();

const MockedApp = () => (
  <ThemeProvider theme={theme}>
    <App />
  </ThemeProvider>
);

describe('App Component', () => {
  test('renders app with layout and navigation', () => {
    render(<MockedApp />);

    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    expect(screen.getByText('Cadastro')).toBeInTheDocument();
    expect(screen.getByText('Listagem')).toBeInTheDocument();
    expect(screen.getByText('Download CSV')).toBeInTheDocument();
  });

  test('navigation links have correct hrefs', () => {
    render(<MockedApp />);

    const cadastroLink = screen.getByText('Cadastro').closest('a');
    const listagemLink = screen.getByText('Listagem').closest('a');
    const downloadLink = screen.getByText('Download CSV').closest('a');

    expect(cadastroLink).toHaveAttribute('href', '/cadastro-pessoa');
    expect(listagemLink).toHaveAttribute('href', '/listagem-pessoas');
    expect(downloadLink).toHaveAttribute('href', '/download-csv');
  });

  test('renders router structure', () => {
    render(<MockedApp />);

    expect(screen.getByTestId('browser-router')).toBeInTheDocument();
    expect(screen.getByTestId('routes')).toBeInTheDocument();
    
    // Router should contain route definitions
    const routes = screen.getAllByTestId('route');
    expect(routes).toHaveLength(5); // Index, cadastro, listagem, download, edit routes
  });

  test('app layout structure is correct', () => {
    render(<MockedApp />);

    // Should have the main container
    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    
    // Should have navigation structure
    expect(screen.getByTestId('browser-router')).toBeInTheDocument();
    
    // Router structure should be present
    expect(screen.getByTestId('routes')).toBeInTheDocument();
  });
});
