import { useContext, useState } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const [showGestion, setShowGestion] = useState(false);

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  if (!user) return null;

  const isAdmin = user.rol === 'ADMIN';

  return (
    <nav className="navbar">
      <Link to="/catalogo" className="navbar-brand">Biblioteca Digital</Link>
      <div className="navbar-menu">
        <Link to="/catalogo" className="navbar-link">Catálogo</Link>
        <Link to="/mis-prestamos" className="navbar-link">Mis Préstamos</Link>
        {isAdmin && (
          <div className="navbar-dropdown">
            <button
              className="navbar-link"
              onClick={() => setShowGestion(!showGestion)}
              style={{ background: 'none', border: 'none', cursor: 'pointer' }}
            >
              Gestión ▼
            </button>
            {showGestion && (
              <div className="dropdown-menu">
                <Link to="/libros" className="dropdown-item" onClick={() => setShowGestion(false)}>
                  Gestionar Libros
                </Link>
                <Link to="/admin/users" className="dropdown-item" onClick={() => setShowGestion(false)}>
                  Gestionar Usuarios
                </Link>
                <Link to="/admin/prestamos" className="dropdown-item" onClick={() => setShowGestion(false)}>
                  Gestionar Préstamos
                </Link>
              </div>
            )}
          </div>
        )}
        <Link to="/recomendaciones" className="navbar-link">Recomendaciones</Link>
        <span className="navbar-user">{user.name} ({user.rol})</span>
        <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '5px 10px' }}>
          Cerrar Sesión
        </button>
      </div>
    </nav>
  );
};