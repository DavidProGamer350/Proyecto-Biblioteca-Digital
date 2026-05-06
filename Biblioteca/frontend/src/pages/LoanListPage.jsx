import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { LoanService } from '../services/LoanService';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';
import { Navbar } from '../components/Navbar';

export const LoanListPage = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useContext(AuthContext);

  useEffect(() => {
    loadMyLoans();
  }, []);

  const loadMyLoans = async () => {
    try {
      setLoading(true);
      const data = await LoanService.getMyLoans();
      setLoans(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleReturn = async (id) => {
    if (!confirm('¿Estás seguro de devolver este préstamo?')) return;
    try {
      await LoanService.returnLoan(id);
      alert('Préstamo devuelto exitosamente');
      loadMyLoans();
    } catch (err) {
      setError(err.message);
    }
  };

  const getStatusBadge = (loan) => {
    const today = new Date();
    const expectedReturn = new Date(loan.fechaDevolucionEsperada);
    
    if (loan.estado === 'DEVUELTO') {
      return <span className="rol-badge user">DEVUELTO</span>;
    }
    
    if (expectedReturn < today) {
      return <span className="rol-badge admin">VENCIDO</span>;
    }
    
    return <span className="rol-badge active">ACTIVO</span>;
  };

  return (
    <div>
      <Navbar />

      <div className="users-container">
        <div className="users-header">
          <h1>Mis Préstamos</h1>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Cargando préstamos...</div>
        ) : (
          <table className="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Libro ID</th>
                <th>Fecha Préstamo</th>
                <th>Devolución Esperada</th>
                <th>Estado</th>
                <th>Multa</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loans.map(loan => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{loan.libroId}</td>
                  <td>{loan.fechaPrestamo}</td>
                  <td>{loan.fechaDevolucionEsperada}</td>
                  <td>{getStatusBadge(loan)}</td>
                  <td>
                    {loan.multasAcumuladas > 0 ? (
                      <span style={{ color: 'red' }}>${loan.multasAcumuladas}</span>
                    ) : (
                      '-'
                    )}
                  </td>
                  <td className="actions">
                    {loan.estado === 'ACTIVO' && (
                      <button
                        onClick={() => handleReturn(loan.id)}
                        className="btn-action"
                      >
                        Devolver
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!loading && loans.length === 0 && (
          <div className="proximamente">
            <p>No tienes préstamos activos.</p>
            <Link to="/catalogo" className="btn btn-primary" style={{ marginTop: '10px' }}>
              Ir al Catálogo
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};