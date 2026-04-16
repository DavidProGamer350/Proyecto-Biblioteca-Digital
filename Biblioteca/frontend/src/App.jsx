import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { ProfilePage } from './pages/ProfilePage';
import { PrivateRoute } from './components/PrivateRoute';

const PrivateRouteWrapper = ({ children }) => {
  return <PrivateRoute>{children}</PrivateRoute>;
};

const AdminRouteWrapper = ({ children }) => {
  return <PrivateRoute requiredRol="ADMIN">{children}</PrivateRoute>;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={
            <PrivateRouteWrapper>
              <DashboardPage />
            </PrivateRouteWrapper>
          } />
          <Route path="/books" element={
            <PrivateRouteWrapper>
              <BooksPage />
            </PrivateRouteWrapper>
          } />
          <Route path="/profile" element={
            <PrivateRouteWrapper>
              <ProfilePage />
            </PrivateRouteWrapper>
          } />
          <Route path="/admin/users" element={
            <AdminRouteWrapper>
              <AdminUsersPage />
            </AdminRouteWrapper>
          } />
          <Route path="/logout" element={<LogoutHandler />} />
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

const BooksPage = () => {
  return (
    <div>
      <nav className="navbar">
        <a href="/dashboard" className="navbar-brand">Biblioteca Digital</a>
        <div className="navbar-menu">
          <a href="/dashboard" className="navbar-link">Panel</a>
          <a href="/books" className="navbar-link">Catálogo</a>
          <a href="/profile" className="navbar-link">Mi Perfil</a>
          <a href="/logout" className="navbar-link">Cerrar Sesión</a>
        </div>
      </nav>
      <div className="container">
        <h1>Catálogo de Libros</h1>
        <p>Próximamente: Lista de libros disponibles</p>
      </div>
    </div>
  );
};

const LogoutHandler = () => {
  localStorage.clear();
  window.location.href = '/login';
  return null;
};

export default App;