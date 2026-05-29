import { useState, useEffect } from 'react';
import { LoanService } from '../services/LoanService';
import { UserService } from '../services/UserService';
import { BookService } from '../services/BookService';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';
import { Navbar } from '../components/Navbar';
import { ejecutarCadena } from '../services/CadenaPrestamosHandler';

export const AdminLoansPage = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingLoan, setEditingLoan] = useState(null);
  const [editForm, setEditForm] = useState({
    observaciones: '',
    multasAcumuladas: 0,
    fechaDevolucionEsperada: '',
    estado: 'ACTIVO'
  });
  const [saving, setSaving] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [usersMap, setUsersMap] = useState({});
  const [booksMap, setBooksMap] = useState({});
  const { user } = useContext(AuthContext);

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    try {
      setLoading(true);
      const [loansData, usersData, booksData] = await Promise.all([
        LoanService.getAll(),
        UserService.getAll(),
        BookService.getAll()
      ]);
      setLoans(loansData);

      const uMap = {};
      usersData.forEach(u => { uMap[u.id] = u.email || u.correo || 'S/C'; });
      setUsersMap(uMap);

      const bMap = {};
      booksData.forEach(b => { bMap[b.id] = b.isbn || 'S/C'; });
      setBooksMap(bMap);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getEmail = (usuarioId) => usersMap[usuarioId] || `ID: ${usuarioId}`;
  const getIsbn = (libroId) => booksMap[libroId] || `ID: ${libroId}`;

  const { activeLoans, vencidos, devueltos, multasTotales } = (() => {
    if (loans.length === 0) return { activeLoans: 0, vencidos: 0, devueltos: 0, multasTotales: 0 };
    const r = ejecutarCadena(loans);
    return { activeLoans: r.activos, vencidos: r.vencidos, devueltos: r.devuelto, multasTotales: r.multasTotales };
  })();

  const filteredLoans = loans.filter(loan => {
    if (!searchText) return true;
    const q = searchText.toLowerCase();
    return getEmail(loan.usuarioId).toLowerCase().includes(q) ||
           getIsbn(loan.libroId).toLowerCase().includes(q);
  });

  const handleReturn = async (id) => {
    if (!confirm('¿Estás seguro de marcar como devuelto?')) return;
    try {
      await LoanService.returnLoan(id);
      alert('Préstamo devuelto exitosamente');
      loadAllData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRenew = async (id) => {
    if (!confirm('¿Renovar este préstamo por 7 días adicionales?')) return;
    try {
      await LoanService.renew(id);
      alert('Préstamo renovado exitosamente');
      loadAllData();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEditClick = (loan) => {
    setEditingLoan(loan);
    setEditForm({
      observaciones: loan.observaciones || '',
      multasAcumuladas: loan.multasAcumuladas || 0,
      fechaDevolucionEsperada: loan.fechaDevolucionEsperada || '',
      estado: loan.estado || 'ACTIVO'
    });
    setShowEditModal(true);
  };

  const handleEditChange = (e) => {
    setEditForm({
      ...editForm,
      [e.target.name]: e.target.value
    });
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    if (!editingLoan) return;
    setSaving(true);
    try {
      await LoanService.update(editingLoan.id, editForm);
      alert('Préstamo actualizado exitosamente');
      setShowEditModal(false);
      setEditingLoan(null);
      loadAllData();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
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

  const esVencido = (loan) => {
    if (loan.estado === 'DEVUELTO') return false;
    return loan.fechaDevolucionEsperada < getHoyStr();
  };

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
            <p style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--color-error)' }}>{vencidos}</p>
          </div>
          <div className="dashboard-card">
            <h3>Devueltos</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold' }}>{devueltos}</p>
          </div>
          <div className="dashboard-card">
            <h3>Multas Totales</h3>
            <p style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--color-accent)' }}>${multasTotales}</p>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div style={{ marginBottom: '16px', display: 'flex', gap: '8px' }}>
          <input
            type="text"
            placeholder="Buscar por email o ISBN..."
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
                <th>Email</th>
                <th>ISBN</th>
                <th>Fecha Préstamo</th>
                <th>Devolución Esperada</th>
                <th>Renov.</th>
                <th>Estado</th>
                <th>Multas</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredLoans.map(loan => (
                <tr key={loan.id}>
                  <td>{loan.id}</td>
                  <td>{getEmail(loan.usuarioId)}</td>
                  <td>{getIsbn(loan.libroId)}</td>
                  <td>{loan.fechaPrestamo}</td>
                  <td>{loan.fechaDevolucionEsperada}</td>
                  <td style={{ textAlign: 'center' }}>{loan.vecesRenovado || 0}</td>
                  <td>{getStatusBadge(loan)}</td>
                  <td>
                    {(() => {
                      const base = loan.multasAcumuladas || 0;
                      let multa = base;
                      if (loan.estado !== 'DEVUELTO') {
                        const hoy = new Date();
                        const esperada = new Date(loan.fechaDevolucionEsperada);
                        if (esperada < hoy) {
                          const dias = Math.floor((hoy - esperada) / (1000 * 60 * 60 * 24)) + 1;
                          multa = base + dias;
                        }
                      }
                      return multa > 0 ? (
                        <span style={{ color: 'red', fontWeight: 'bold' }}>${multa}</span>
                      ) : (
                        '-'
                      );
                    })()}
                  </td>
                  <td className="actions">
                    <button
                      onClick={() => handleEditClick(loan)}
                      className="btn-action"
                      style={{ marginRight: '5px' }}
                    >
                      Editar
                    </button>
                    {loan.estado === 'ACTIVO' && (
                      <>
                        <button
                          onClick={() => handleReturn(loan.id)}
                          className="btn-action"
                          style={{ marginRight: '5px' }}
                        >
                          Devuelto
                        </button>
                        <button
                          onClick={() => handleRenew(loan.id)}
                          className="btn-action btn-action--renew"
                        >
                          Renovar
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!loading && filteredLoans.length === 0 && (
          <div className="proximamente">
            <p>{searchText ? 'No se encontraron préstamos con ese criterio.' : 'No hay préstamos registrados.'}</p>
          </div>
        )}

        {showEditModal && editingLoan && (
          <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
              <div className="modal-header">
                <h2>Editar Préstamo #{editingLoan.id}</h2>
                <button className="modal-close" onClick={() => setShowEditModal(false)}>&times;</button>
              </div>
              <form onSubmit={handleEditSubmit}>
                <div className="modal-body">
                  <div className="form-group">
                    <label>Observaciones</label>
                    <textarea
                      name="observaciones"
                      value={editForm.observaciones}
                      onChange={handleEditChange}
                      rows={3}
                    />
                  </div>
                  <div className="form-group">
                    <label>Multas Acumuladas</label>
                    <input
                      type="number"
                      name="multasAcumuladas"
                      value={editForm.multasAcumuladas}
                      onChange={handleEditChange}
                      min={0}
                    />
                  </div>
                  <div className="form-group">
                    <label>Fecha Devolución Esperada</label>
                    <input
                      type="date"
                      name="fechaDevolucionEsperada"
                      value={editForm.fechaDevolucionEsperada}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Estado</label>
                    <select
                      name="estado"
                      value={editForm.estado}
                      onChange={handleEditChange}
                    >
                      <option value="ACTIVO">ACTIVO</option>
                      <option value="VENCIDO">VENCIDO</option>
                      <option value="DEVUELTO">DEVUELTO</option>
                    </select>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>
                    Cancelar
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? 'Guardando...' : 'Guardar Cambios'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};