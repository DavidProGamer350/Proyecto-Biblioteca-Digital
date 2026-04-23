import { useContext, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export const Navbar = () => {
  const { user, logout } = useContext(AuthContext);

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  if (!user) return null;

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="navbar-brand">Biblioteca Digital</Link>
      <div className="navbar-menu">
        <Link to="/catalogo" className="navbar-link">Catálogo</Link>
        <Link to="/prestamos" className="navbar-link">Préstamos</Link>
        <Link to="/recomendaciones" className="navbar-link">Recomendaciones</Link>
        <span className="navbar-user">{user.name} ({user.rol})</span>
        <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '5px 10px' }}>
          Cerrar Sesión
        </button>
      </div>
    </nav>
  );
};