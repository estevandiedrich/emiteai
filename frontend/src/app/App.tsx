import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import CadastroPessoa from './pages/CadastroPessoa';
import ListagemPessoas from './pages/ListagemPessoas';
import DownloadCsv from './pages/DownloadCsv';

export default function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<CadastroPessoa />} />
          <Route path="/cadastro-pessoa" element={<CadastroPessoa />} />
          <Route path="/cadastro-pessoa/:id" element={<CadastroPessoa />} />
          <Route path="/listagem-pessoas" element={<ListagemPessoas />} />
          <Route path="/download-csv" element={<DownloadCsv />} />
        </Routes>
      </Layout>
    </Router>
  );
}
