import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { ProfilePage } from './pages/ProfilePage';
import { BookListPage } from './pages/BookListPage';
import { BookCreatePage } from './pages/BookCreatePage';
import { BookEditPage } from './pages/BookEditPage';
import { CatalogPage } from './pages/CatalogPage';
import { LoanListPage } from './pages/LoanListPage';
import { AdminLoansPage } from './pages/AdminLoansPage';
import { ReportesPage } from './pages/ReportesPage';
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
      <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/catalogo" element={<CatalogPage />} />
          <Route path="/dashboard" element={
            <PrivateRouteWrapper>
              <DashboardPage />
            </PrivateRouteWrapper>
          } />
          <Route path="/libros" element={
            <AdminRouteWrapper>
              <BookListPage />
            </AdminRouteWrapper>
          } />
          <Route path="/libros/nuevo" element={
            <AdminRouteWrapper>
              <BookCreatePage />
            </AdminRouteWrapper>
          } />
          <Route path="/libros/editar/:id" element={
            <AdminRouteWrapper>
              <BookEditPage />
            </AdminRouteWrapper>
          } />
          <Route path="/mis-prestamos" element={
            <PrivateRouteWrapper>
              <LoanListPage />
            </PrivateRouteWrapper>
          } />
          <Route path="/prestamos" element={
            <PrivateRouteWrapper>
              <LoanListPage />
            </PrivateRouteWrapper>
          } />
          <Route path="/admin/prestamos" element={
            <AdminRouteWrapper>
              <AdminLoansPage />
            </AdminRouteWrapper>
          } />
          <Route path="/reportes" element={
            <AdminRouteWrapper>
              <ReportesPage />
            </AdminRouteWrapper>
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
          <Route path="/" element={<Navigate to="/catalogo" replace />} />
          <Route path="*" element={<Navigate to="/catalogo" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;