import React from 'react';
import { Container, Typography, Box, Button, AppBar, Toolbar } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';

type LayoutProps = {
  children: React.ReactNode;
};

export default function Layout({ children }: LayoutProps) {
  const location = useLocation();

  return (
    <Container>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Emiteaí - Cadastro de Pessoas
          </Typography>
          <Button 
            color="inherit" 
            component={Link} 
            to="/cadastro-pessoa"
            sx={{ 
              backgroundColor: location.pathname === '/cadastro-pessoa' || location.pathname === '/' ? 'rgba(255,255,255,0.1)' : 'transparent' 
            }}
          >
            Cadastro
          </Button>
          <Button 
            color="inherit" 
            component={Link} 
            to="/listagem-pessoas"
            sx={{ 
              backgroundColor: location.pathname === '/listagem-pessoas' ? 'rgba(255,255,255,0.1)' : 'transparent' 
            }}
          >
            Listagem
          </Button>
          <Button 
            color="inherit" 
            component={Link} 
            to="/download-csv"
            sx={{ 
              backgroundColor: location.pathname === '/download-csv' ? 'rgba(255,255,255,0.1)' : 'transparent' 
            }}
          >
            Download CSV
          </Button>
        </Toolbar>
      </AppBar>

      <Box mt={4}>
        {children}
      </Box>
    </Container>
  );
}
