import { render, screen, fireEvent } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import Layout from '../app/components/Layout';

// Mock react-router-dom
jest.mock('react-router-dom');

const theme = createTheme();

const MockedLayout = ({ children }: { children: React.ReactNode }) => (
  <ThemeProvider theme={theme}>
    <Layout>{children}</Layout>
  </ThemeProvider>
);

describe('Layout Component', () => {
  test('renders layout with title', () => {
    render(
      <MockedLayout>
        <div>Test Content</div>
      </MockedLayout>
    );

    expect(screen.getByText('Emiteaí - Cadastro de Pessoas')).toBeInTheDocument();
    expect(screen.getByText('Test Content')).toBeInTheDocument();
  });

  test('renders navigation buttons', () => {
    render(
      <MockedLayout>
        <div>Test Content</div>
      </MockedLayout>
    );

    expect(screen.getByText('Cadastro')).toBeInTheDocument();
    expect(screen.getByText('Listagem')).toBeInTheDocument();
    expect(screen.getByText('Download CSV')).toBeInTheDocument();
  });

  test('navigation buttons have correct links', () => {
    render(
      <MockedLayout>
        <div>Test Content</div>
      </MockedLayout>
    );

    const cadastroButton = screen.getByText('Cadastro').closest('a');
    const listagemButton = screen.getByText('Listagem').closest('a');
    const downloadButton = screen.getByText('Download CSV').closest('a');

    expect(cadastroButton).toHaveAttribute('href', '/cadastro-pessoa');
    expect(listagemButton).toHaveAttribute('href', '/listagem-pessoas');
    expect(downloadButton).toHaveAttribute('href', '/download-csv');
  });

  test('renders children content', () => {
    const testContent = 'This is test content';
    render(
      <MockedLayout>
        <div>{testContent}</div>
      </MockedLayout>
    );

    expect(screen.getByText(testContent)).toBeInTheDocument();
  });
});
