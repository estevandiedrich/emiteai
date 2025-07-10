import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import App from '../app/App';

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
  test('renders app with layout and default cadastro page', () => {
    render(<MockedApp />);

    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    expect(screen.getByTestId('cadastro-page')).toBeInTheDocument();
    expect(screen.queryByTestId('listagem-page')).not.toBeInTheDocument();
    expect(screen.queryByTestId('download-page')).not.toBeInTheDocument();
  });

  test('navigates to listagem page when listagem button is clicked', () => {
    render(<MockedApp />);

    fireEvent.click(screen.getByText('Listagem'));

    expect(screen.queryByTestId('cadastro-page')).not.toBeInTheDocument();
    expect(screen.getByTestId('listagem-page')).toBeInTheDocument();
    expect(screen.queryByTestId('download-page')).not.toBeInTheDocument();
  });

  test('navigates to download page when download button is clicked', () => {
    render(<MockedApp />);

    fireEvent.click(screen.getByText('Download CSV'));

    expect(screen.queryByTestId('cadastro-page')).not.toBeInTheDocument();
    expect(screen.queryByTestId('listagem-page')).not.toBeInTheDocument();
    expect(screen.getByTestId('download-page')).toBeInTheDocument();
  });

  test('navigates back to cadastro page when cadastro button is clicked', () => {
    render(<MockedApp />);

    // Navigate to another page first
    fireEvent.click(screen.getByText('Listagem'));
    expect(screen.getByTestId('listagem-page')).toBeInTheDocument();

    // Navigate back to cadastro
    fireEvent.click(screen.getByText('Cadastro'));
    expect(screen.getByTestId('cadastro-page')).toBeInTheDocument();
    expect(screen.queryByTestId('listagem-page')).not.toBeInTheDocument();
  });
});
