import React from 'react';

// Mock do react-router-dom
export const BrowserRouter = ({ children }: { children: React.ReactNode }) => (
  <div data-testid="browser-router">{children}</div>
);

export const Router = ({ children }: { children: React.ReactNode }) => (
  <div data-testid="router">{children}</div>
);

export const Routes = ({ children }: { children: React.ReactNode }) => (
  <div data-testid="routes">{children}</div>
);

export const Route = ({ children }: { children?: React.ReactNode }) => (
  <div data-testid="route">{children}</div>
);

export const Link = ({ children, to, ...props }: { children: React.ReactNode; to: string; [key: string]: any }) => (
  <a href={to} data-testid="link" {...props}>{children}</a>
);

export const NavLink = ({ children, to, ...props }: { children: React.ReactNode; to: string; [key: string]: any }) => (
  <a href={to} data-testid="navlink" {...props}>{children}</a>
);

// Mock hooks with proper default return values
export const useLocation = () => ({
  pathname: '/',
  search: '',
  hash: '',
  state: null
});

export const useParams = () => ({});

export const useNavigate = () => jest.fn();

export const useHistory = jest.fn().mockReturnValue({
  push: jest.fn(),
  replace: jest.fn(),
  goBack: jest.fn(),
  goForward: jest.fn()
});

export const useMatch = jest.fn().mockReturnValue(null);

export const useResolvedPath = jest.fn().mockReturnValue({ pathname: '/', search: '', hash: '' });

export default {
  BrowserRouter,
  Router,
  Routes,
  Route,
  Link,
  NavLink,
  useLocation,
  useParams,
  useNavigate,
  useHistory,
  useMatch,
  useResolvedPath
};
