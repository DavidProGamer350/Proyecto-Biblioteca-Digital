import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { LoanService } from '../services/LoanService';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';
import { Navbar } from '../components/Navbar';

export const AdminLoansPage = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useContext(AuthContext);

  useEffect(() => {
    loadLoans();
  }, []);

  const loadLoans = async () => {
    try {
      setLoading(true);
      const data = await LoanService.getAll();
      setLoans(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleReturn = async (id) => {
    if (!confirm('¿Estás seguro de marcar como devuelto?')) return;
    try {
      await LoanService.returnLoan(id);
      alert('Préstamo devuelto exitosamente');
      loadLoans();
    } catch (err) {
      setError(err.message);
    }
  };

  const getStatusBadge = (loan) => {
    if (loan.estado === 'DEVUELTO') {
      return <span className="rol-badge user">DEVUELTO</span>;
    }

    const today = new Date();
    const expectedReturn = new Date(loan.fechaDevolucionEsperada);
    
    if (expectedReturn < today) {
      return <span className="rol-badge admin">VENCIDO</span>;
    }

    return <span className="rol-badge active">ACTIVO</span>;
  };

  const activeLoans = loans.filter(l => l.estado === 'ACTIVO').length;
  const overdueLoans = loans.filter(l => {
    if (l.estado !== 'ACTIVO') return false;
    const today = new Date();
    const expected = new Date(l.fechaDevolucionEsperada);
    return expected < today;
  }).length;
  const returnedLoans = loans.filter(l => l.estado === 'DEVUELTO').length;
  const totalFines = loans.reduce((sum, l) => sum + (l.multasAcumuladas || 0), 0);

  return (
    <div>
      <Navbar />

      <div className="users-container">
        <div className="users-header">
          <h1>Gestión de Préstamos</h1>
        </div>

        <div className="dashboard-grid" style={{ gridTemplateColumns: 'repeat(4, 1fr)', marginBottom: '20px' }}>
          <div className="dashboard-card">
            <h3>Activos</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--color-success)' }}>{activeLoans}</p>
          </div>
          <div className="dashboard-card">
            <h3>Vencidos</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--color-error)' }}>{overdueLoans}</p>
          </div>
          <div className="dashboard-card">
            <h3>Devueltos</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold' }}>{returnedLoans}</p>
          </div>
          <div className="dashboard-card">
            <h3>Multas Totales</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--color-accent)' }}>${totalFines}</p>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Cargando préstamos...</div>
        ) : (
          <table className="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Usuario ID</th>
                <th>Libro ID</th>
                <th>Fecha Préstamo</th>
                <th>Devolución Esperada</th>
                <th>Estado</th>
                <th>Multas</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loans.map(loan => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{loan.usuarioId}</td>
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
                        Marcar Devuelto
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};