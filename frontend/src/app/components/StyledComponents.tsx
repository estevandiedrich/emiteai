import styled from 'styled-components';
import { Container, Typography, Box } from '@mui/material';

// Styled components para demonstrar o uso conforme requisitos
export const StyledContainer = styled(Container)`
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const StyledTitle = styled(Typography)`
  color: white;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
  margin-bottom: 2rem;
  text-align: center;
`;

export const StyledCard = styled(Box)`
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  max-width: 600px;
  width: 100%;
  margin-bottom: 2rem;
`;

export const StyledButton = styled.button`
  background: linear-gradient(45deg, #2196F3 30%, #21CBF3 90%);
  border: none;
  border-radius: 8px;
  box-shadow: 0 3px 5px 2px rgba(33, 203, 243, .3);
  color: white;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  padding: 0 30px;
  transition: all 0.3s ease;

  &:hover {
    background: linear-gradient(45deg, #1976D2 30%, #1E88E5 90%);
    transform: translateY(-2px);
    box-shadow: 0 5px 15px 2px rgba(33, 203, 243, .4);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background: #ccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
`;

export const StyledInput = styled.input`
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  margin-bottom: 1rem;

  &:focus {
    outline: none;
    border-color: #2196F3;
    box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
  }

  &:hover {
    border-color: #bdbdbd;
  }

  &::placeholder {
    color: #999;
  }
`;

export const StyledFormGroup = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 1.5rem;
`;

export const StyledLabel = styled.label`
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 0.5rem;
`;

export const StyledErrorMessage = styled.div`
  background: #ffebee;
  color: #c62828;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #f44336;
  margin-bottom: 1rem;
  font-size: 14px;
`;

export const StyledSuccessMessage = styled.div`
  background: #e8f5e8;
  color: #2e7d32;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #4caf50;
  margin-bottom: 1rem;
  font-size: 14px;
`;

export const StyledLoadingSpinner = styled.div`
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2196F3;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto;

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

export const StyledGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
`;

export const StyledPerson = styled.div`
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #2196F3;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
`;

export const StyledPersonName = styled.h3`
  color: #333;
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
`;

export const StyledPersonInfo = styled.p`
  color: #666;
  margin: 0.25rem 0;
  font-size: 0.9rem;
`;

export const StyledFloatingButton = styled.button`
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(45deg, #FF6B6B, #FF8E53);
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
  transition: all 0.3s ease;
  z-index: 1000;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 20px rgba(255, 107, 107, 0.6);
  }
`;

export const StyledNavigation = styled.nav`
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 2rem;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  justify-content: center;
`;

export const StyledNavLink = styled.a<{ $active?: boolean }>`
  color: ${props => props.$active ? '#fff' : 'rgba(255, 255, 255, 0.8)'};
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background: ${props => props.$active ? 'rgba(255, 255, 255, 0.2)' : 'transparent'};
  transition: all 0.3s ease;
  font-weight: ${props => props.$active ? '600' : '400'};

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
  }
`;
