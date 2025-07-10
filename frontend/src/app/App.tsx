import React from 'react';
import Layout from './components/Layout';
import CadastroPessoa from './pages/CadastroPessoa';
import ListagemPessoas from './pages/ListagemPessoas';
import DownloadCsv from './pages/DownloadCsv';

export default function App() {
  const [page, setPage] = React.useState<'cadastro' | 'listagem' | 'download'>('cadastro');

  return (
    <Layout onNavigate={setPage}>
      {page === 'cadastro' && <CadastroPessoa/>}
      {page === 'listagem' && <ListagemPessoas />}
      {page === 'download' && <DownloadCsv />}
    </Layout>
  );
}
