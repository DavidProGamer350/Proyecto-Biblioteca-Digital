import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { ProfilePage } from './pages/ProfilePage';
import { PrivateRoute } from './components/PrivateRoute';
import { ProximamentePage } from './components/ProximamentePage';

const PrivateRouteWrapper = ({ children }) => {
  return <PrivateRoute>{children}</PrivateRoute>;
};

const AdminRouteWrapper = ({ children }) => {
  return <PrivateRoute requiredRol="ADMIN">{children}</PrivateRoute>;
};

const LogoutHandler = () => {
  localStorage.clear();
  window.location.href = '/login';
  return null;
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
          <Route path="/prestamos" element={
            <PrivateRouteWrapper>
              <ProximamentePage titulo="Préstamos" />
            </PrivateRouteWrapper>
          } />
          <Route path="/libros" element={
            <PrivateRouteWrapper>
              <ProximamentePage titulo="Libros" />
            </PrivateRouteWrapper>
          } />
          <Route path="/catalogo" element={
            <PrivateRouteWrapper>
              <ProximamentePage titulo="Catálogo" />
            </PrivateRouteWrapper>
          } />
          <Route path="/recomendaciones" element={
            <PrivateRouteWrapper>
              <ProximamentePage titulo="Recomendaciones" />
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

export default App;