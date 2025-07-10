import React from 'react';
import { Container, Typography, Box, Button, AppBar, Toolbar } from '@mui/material';

type LayoutProps = {
  children: React.ReactNode;
  onNavigate: (page: 'cadastro' | 'listagem' | 'download') => void;
};

export default function Layout({ children, onNavigate }: LayoutProps) {
  return (
    <Container>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Emiteaí - Cadastro de Pessoas
          </Typography>
          <Button color="inherit" onClick={() => onNavigate('cadastro')}>Cadastro</Button>
          <Button color="inherit" onClick={() => onNavigate('listagem')}>Listagem</Button>
          <Button color="inherit" onClick={() => onNavigate('download')}>Download CSV</Button>
        </Toolbar>
      </AppBar>

      <Box mt={4}>
        {children}
      </Box>
    </Container>
  );
}
