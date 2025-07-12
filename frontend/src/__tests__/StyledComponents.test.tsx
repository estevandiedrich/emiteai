import React from 'react';
import { render, screen } from '@testing-library/react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { StyledContainer } from '../app/components/StyledComponents';

const theme = createTheme();

const WithTheme = ({ children }: { children: React.ReactNode }) => (
  <ThemeProvider theme={theme}>
    {children}
  </ThemeProvider>
);

describe('StyledComponents', () => {
  test('StyledContainer renders correctly', () => {
    render(
      <WithTheme>
        <StyledContainer>
          <div>Test content</div>
        </StyledContainer>
      </WithTheme>
    );

    expect(screen.getByText('Test content')).toBeInTheDocument();
  });
});
