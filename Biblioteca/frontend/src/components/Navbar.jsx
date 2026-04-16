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
        <Link to="/books" className="navbar-link">Catálogo</Link>
        <span className="navbar-user">{user.name} ({user.rol})</span>
        <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '5px 10px' }}>
          Cerrar Sesión
        </button>
      </div>
    </nav>
  );
};