import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export const PrivateRoute = ({ children, requiredRol }) => {
  const { user, loading } = useContext(AuthContext);
  const navigate = useNavigate();

  if (loading) {
    return <div className="container">Cargando...</div>;
  }

  if (!user) {
    navigate('/login');
    return null;
  }

  if (requiredRol && user.rol !== requiredRol) {
    return (
      <div className="container">
        <div className="private-message">
          <h2>Acceso Denegado</h2>
          <p>No tienes permisos para acceder a esta página.</p>
        </div>
      </div>
    );
  }

  return children;
};