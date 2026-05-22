import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { LoanService } from '../services/LoanService';
import { BookService } from '../services/BookService';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';
import { Navbar } from '../components/Navbar';

const TARIFA_POR_DIA = 1;

export const LoanListPage = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchText, setSearchText] = useState('');
  const [booksMap, setBooksMap] = useState({});
  const { user } = useContext(AuthContext);

  useEffect(() => {
    loadMyLoans();
  }, []);

  const loadMyLoans = async () => {
    try {
      setLoading(true);
      const [loansData, booksData] = await Promise.all([
        LoanService.getMyLoans(),
        BookService.getAll()
      ]);
      setLoans(loansData);

      const bMap = {};
      booksData.forEach(b => { bMap[b.id] = b.isbn || 'S/C'; });
      setBooksMap(bMap);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getIsbn = (libroId) => booksMap[libroId] || `ID: ${libroId}`;

  const calcularMulta = (loan) => {
    const base = loan.multasAcumuladas || 0;
    if (loan.estado === 'DEVUELTO') {
      return base;
    }
    const hoy = new Date();
    const esperada = new Date(loan.fechaDevolucionEsperada);
    if (esperada >= hoy) return base;
    const dias = Math.floor((hoy - esperada) / (1000 * 60 * 60 * 24)) + 1;
    return base + (dias * TARIFA_POR_DIA);
  };

  const filteredLoans = loans.filter(loan => {
    if (!searchText) return true;
    return getIsbn(loan.libroId).toLowerCase().includes(searchText.toLowerCase());
  });

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

  const handleRenew = async (id) => {
    if (!confirm('¿Renovar este préstamo por 7 días adicionales?')) return;
    try {
      await LoanService.renew(id);
      alert('Préstamo renovado exitosamente');
      loadMyLoans();
    } catch (err) {
      setError(err.message);
    }
  };

  const getHoyStr = () => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  };

  const getStatusBadge = (loan) => {
    if (loan.estado === 'DEVUELTO') {
      return <span className="rol-badge user">DEVUELTO</span>;
    }
    if (loan.fechaDevolucionEsperada < getHoyStr()) {
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

        <div style={{ marginBottom: '16px', display: 'flex', gap: '8px' }}>
          <input
            type="text"
            placeholder="Buscar por ISBN..."
            value={searchText}
            onChange={e => setSearchText(e.target.value)}
            style={{
              flex: 1,
              padding: '10px 14px',
              border: '1px solid var(--color-border)',
              borderRadius: 'var(--radius-md)',
              fontSize: '0.95rem'
            }}
          />
        </div>

        {loading ? (
          <div className="loading">Cargando préstamos...</div>
        ) : (
          <table className="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>ISBN</th>
                <th>Fecha Préstamo</th>
                <th>Devolución Esperada</th>
                <th>Renov.</th>
                <th>Estado</th>
                <th>Multa</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredLoans.map(loan => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{getIsbn(loan.libroId)}</td>
                  <td>{loan.fechaPrestamo}</td>
                  <td>{loan.fechaDevolucionEsperada}</td>
                  <td style={{ textAlign: 'center' }}>{loan.vecesRenovado || 0}</td>
                  <td>{getStatusBadge(loan)}</td>
                  <td>
                    {(() => {
                      const multa = calcularMulta(loan);
                      return multa > 0 ? (
                        <span style={{ color: 'red', fontWeight: 'bold' }}>${multa}</span>
                      ) : (
                        '-'
                      );
                    })()}
                  </td>
                  <td className="actions">
                    {loan.estado === 'ACTIVO' ? (
                      <>
                        <button
                          onClick={() => handleReturn(loan.id)}
                          className="btn-action"
                          style={{ marginRight: '5px' }}
                        >
                          Devolver
                        </button>
                        <button
                          onClick={() => handleRenew(loan.id)}
                          className="btn-action btn-action--renew"
                        >
                          Renovar
                        </button>
                      </>
                    ) : (
                      <span className="btn-action" style={{ opacity: 0.5, cursor: 'default' }}>Devuelto</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!loading && filteredLoans.length === 0 && (
          <div className="proximamente">
            <p>{searchText ? 'No se encontraron préstamos con ese ISBN.' : 'No tienes préstamos activos.'}</p>
            {!searchText && (
              <Link to="/catalogo" className="btn btn-primary" style={{ marginTop: '10px' }}>
                Ir al Catálogo
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  );
};