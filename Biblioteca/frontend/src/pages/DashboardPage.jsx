import { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export const DashboardPage = () => {
  const { user } = useContext(AuthContext);

  return (
    <div>
      <nav className="navbar">
        <Link to="/dashboard" className="navbar-brand">Biblioteca Digital</Link>
        <div className="navbar-menu">
          <Link to="/catalogo" className="navbar-link">Catálogo</Link>
          <Link to="/profile" className="navbar-link">Mi Perfil</Link>
          <span className="navbar-user">Bienvenido, {user?.name}</span>
          <Link to="/logout" className="navbar-link">Cerrar Sesión</Link>
        </div>
      </nav>

      <div className="dashboard">
        <h1 className="dashboard-title">Panel de Control</h1>
        
        <div className="dashboard-grid">
          <Link to="/catalogo" className="dashboard-card">
            <h3>Catálogo de Libros</h3>
            <p>Explora y descarga libros en formato PDF, EPUB y MOBI</p>
          </Link>

          <Link to="/prestamos" className="dashboard-card">
            <h3>Mis Préstamos</h3>
            <p>Consulta tus préstamos activos y history</p>
          </Link>

          <Link to="/recomendaciones" className="dashboard-card">
            <h3>Recomendaciones</h3>
            <p>Recibe sugerencias personalizadas de lectura</p>
          </Link>

          {user?.rol === 'ADMIN' && (
            <>
              <Link to="/admin/users" className="dashboard-card">
                <h3>Gestión de Usuarios</h3>
                <p>Administra los usuarios del sistema</p>
              </Link>

              <Link to="/libros" className="dashboard-card">
                <h3>Gestión de Libros</h3>
                <p>Agrega, modifica o elimina libros</p>
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
};