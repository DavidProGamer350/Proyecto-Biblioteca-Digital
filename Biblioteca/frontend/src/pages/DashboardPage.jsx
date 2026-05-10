import { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Navbar } from '../components/Navbar';

export const DashboardPage = () => {
  const { user } = useContext(AuthContext);

  return (
    <div>
      <Navbar />

      <div className="dashboard">
        <h1 className="dashboard-title">Panel de Control</h1>
        
        <div className="dashboard-grid">
          <Link to="/catalogo" className="dashboard-card">
            <h3>Catálogo de Libros</h3>
            <p>Explora y descarga libros en formato PDF, EPUB y MOBI</p>
          </Link>

          <Link to="/mis-prestamos" className="dashboard-card">
            <h3>Mis Préstamos</h3>
            <p>Consulta tus préstamos activos y historial</p>
          </Link>

          <Link to="/recomendaciones" className="dashboard-card">
            <h3>Recomendaciones</h3>
            <p>Recibe sugerencias personalizadas de lectura</p>
          </Link>
        </div>
      </div>
    </div>
  );
};