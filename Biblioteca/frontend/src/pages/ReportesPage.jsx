import { useState, useEffect } from 'react';
import { LoanService } from '../services/LoanService';
import { UserService } from '../services/UserService';
import { BookService } from '../services/BookService';
import { Navbar } from '../components/Navbar';
import { gestorMultas, reporteMultasObserver } from '../services/MultasObserver';

const PRECIO_PREMIUM = 15000;
const NOMBRES_MESES = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];

export const ReportesPage = () => {
  const [loans, setLoans] = useState([]);
  const [users, setUsers] = useState([]);
  const [booksMap, setBooksMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [topDesde, setTopDesde] = useState('');
  const [topHasta, setTopHasta] = useState('');
  const [actDesde, setActDesde] = useState('');
  const [actHasta, setActHasta] = useState('');
  const [multas, setMultas] = useState([]);
  const [searchMultas, setSearchMultas] = useState('');

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
      setUsers(usersData);
      const bMap = {};
      booksData.forEach(b => { bMap[b.id] = b; });
      setBooksMap(bMap);

      // Observer: calcular multas a partir de los datos cargados
      reporteMultasObserver.limpiar();
      gestorMultas.calcularMultas(loansData, usersData);
      const usersMap = {};
      usersData.forEach(u => { usersMap[u.id] = u; });
      setMultas(reporteMultasObserver.obtenerMultas(usersMap));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // ─── Premium ───────────────────────────────────────────────
  const premiumUsers = users.filter(u => u.suscripcionActiva);
  const freeUsers = users.filter(u => !u.suscripcionActiva);
  const totalUsers = users.length;
  const pctPremium = totalUsers > 0 ? Math.round((premiumUsers.length / totalUsers) * 100) : 0;
  const ingresoPremium = premiumUsers.length * PRECIO_PREMIUM;

  // ─── Top libros ────────────────────────────────────────────
  const loansTopFiltrados = topDesde || topHasta
    ? loans.filter(l => {
        if (!l.fechaPrestamo) return false;
        if (topDesde && l.fechaPrestamo < topDesde) return false;
        if (topHasta && l.fechaPrestamo > topHasta) return false;
        return true;
      })
    : loans;

  const countByLibro = {};
  loansTopFiltrados.forEach(l => {
    countByLibro[l.libroId] = (countByLibro[l.libroId] || 0) + 1;
  });
  const topLibros = Object.entries(countByLibro)
    .map(([id, total]) => {
      const libro = booksMap[Number(id)] || {};
      return { libroId: Number(id), total, titulo: libro.titulo || 'S/T', autor: libro.autor || 'S/A', isbn: libro.isbn || 'S/I' };
    })
    .sort((a, b) => b.total - a.total);
  const maxPrestamos = topLibros.length > 0 ? topLibros[0].total : 1;

  // ─── Tiempo devolución ─────────────────────────────────────
  const devueltos = loans.filter(l => l.estado === 'DEVUELTO' && l.fechaDevolucionReal);
  const vencidos = loans.filter(l => l.estado === 'ACTIVO' && new Date(l.fechaDevolucionEsperada) < new Date());

  let sumaDias = 0;
  let aTiempo = 0;
  devueltos.forEach(l => {
    const prestamo = new Date(l.fechaPrestamo);
    const real = new Date(l.fechaDevolucionReal);
    const dias = Math.floor((real - prestamo) / (1000 * 60 * 60 * 24));
    sumaDias += dias;
    if (real <= new Date(l.fechaDevolucionEsperada)) aTiempo++;
  });

  vencidos.forEach(l => {
    const esperada = new Date(l.fechaDevolucionEsperada);
    const dias = Math.floor((new Date() - esperada) / (1000 * 60 * 60 * 24)) + 1;
    sumaDias += dias;
  });

  const totalAnalizados = devueltos.length + vencidos.length;
  const promedioDias = totalAnalizados > 0 ? Math.round(sumaDias / totalAnalizados) : 0;
  const pctATiempo = devueltos.length > 0 ? Math.round((aTiempo / devueltos.length) * 100) : 0;
  const pctTarde = 100 - pctATiempo;

  // ─── Actividad mensual ─────────────────────────────────────
  const loansActFiltrados = actDesde || actHasta
    ? loans.filter(l => {
        if (!l.fechaPrestamo) return false;
        if (actDesde && l.fechaPrestamo < actDesde) return false;
        if (actHasta && l.fechaPrestamo > actHasta) return false;
        return true;
      })
    : loans;

  const meses = {};
  loansActFiltrados.forEach(l => {
    if (!l.fechaPrestamo) return;
    const [anio, mes] = l.fechaPrestamo.split('-');
    const key = `${anio}-${mes}`;
    if (!meses[key]) meses[key] = { anio: Number(anio), mes: Number(mes), total: 0 };
    meses[key].total++;
  });

  const mesesArray = Object.values(meses)
    .sort((a, b) => b.anio - a.anio || b.mes - a.mes)
    .slice(0, 12)
    .reverse();
  const maxMensual = mesesArray.length > 0 ? Math.max(...mesesArray.map(m => m.total)) : 1;

  // ─── Resumen ejecutivo ─────────────────────────────────────
  const librosPrestados = Object.keys(countByLibro).length;
  const mesActualKey = (() => {
    const hoy = new Date();
    return `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}`;
  })();
  const prestamosEsteMes = meses[mesActualKey]?.total || 0;

  const multasFiltradas = searchMultas
    ? multas.filter(m => m.email.toLowerCase().includes(searchMultas.toLowerCase()))
    : multas;

  if (loading) return <div><Navbar /><div className="loading" style={{ marginTop: '40px' }}>Cargando reportes...</div></div>;

  return (
    <div>
      <Navbar />
      <div className="reportes-container">
        <h1 className="reportes-title">Reportes</h1>

        {error && <div className="error-message">{error}</div>}

        {/* ── Resumen Ejecutivo ── */}
        <div className="reportes-resumen">
          <div className="resumen-card">
            <div className="resumen-label">Usuarios Premium</div>
            <div className="resumen-number">{premiumUsers.length}</div>
            <div className="resumen-sub">Ingreso: ${ingresoPremium.toLocaleString()}</div>
          </div>
          <div className="resumen-card">
            <div className="resumen-label">Libros Prestados</div>
            <div className="resumen-number">{librosPrestados}</div>
            <div className="resumen-sub">Total préstamos: {loans.length}</div>
          </div>
          <div className="resumen-card">
            <div className="resumen-label">Promedio Devolución</div>
            <div className="resumen-number">{promedioDias} días</div>
            <div className="resumen-sub">{pctATiempo}% a tiempo</div>
          </div>
          <div className="resumen-card">
            <div className="resumen-label">Préstamos Este Mes</div>
            <div className="resumen-number">{prestamosEsteMes}</div>
            <div className="resumen-sub">{mesesArray.length > 0 ? `${NOMBRES_MESES[mesesArray[mesesArray.length - 1]?.mes - 1] || ''}` : ''}</div>
          </div>
        </div>

        {/* ── Sección 1: Premium vs Gratuitos ── */}
        <section className="reportes-section">
          <h2 className="reportes-section-title">Usuarios Premium vs Gratuitos</h2>
          <div className="premium-cards">
            <div className="premium-card premium-card--premium">
              <div className="premium-card-number">{premiumUsers.length}</div>
              <div className="premium-card-label">Premium</div>
              <div className="premium-card-ingreso">${ingresoPremium.toLocaleString()}/mes</div>
            </div>
            <div className="premium-card premium-card--free">
              <div className="premium-card-number">{freeUsers.length}</div>
              <div className="premium-card-label">Gratuitos</div>
            </div>
          </div>
          <div className="progress-bar">
            <div className="progress-fill progress-fill--premium" style={{ width: `${pctPremium}%` }} />
          </div>
          <div className="progress-labels">
            <span style={{ color: 'var(--color-secondary)' }}>Premium {pctPremium}%</span>
            <span style={{ color: 'var(--color-text-muted)' }}>{100 - pctPremium}% Gratuito</span>
          </div>
        </section>

        {/* ── Sección 2: Top Libros más prestados ── */}
        <section className="reportes-section">
          <h2 className="reportes-section-title">Top Libros Más Prestados</h2>

          <div className="filtro-fecha">
            <label className="filtro-fecha-label">
              Desde:
              <input type="date" className="filtro-fecha-input" value={topDesde}
                onChange={e => setTopDesde(e.target.value)} />
            </label>
            <label className="filtro-fecha-label">
              Hasta:
              <input type="date" className="filtro-fecha-input" value={topHasta}
                onChange={e => setTopHasta(e.target.value)} />
            </label>
            {(topDesde || topHasta) && (
              <button className="btn btn-secondary btn-sm" onClick={() => { setTopDesde(''); setTopHasta(''); }}>
                Limpiar
              </button>
            )}
            <span className="filtro-fecha-total">{loansTopFiltrados.length} préstamo(s)</span>
          </div>

          {topLibros.length === 0 ? (
            <p className="reportes-empty">No hay préstamos registrados en este período.</p>
          ) : (
            <table className="users-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Título</th>
                  <th>Autor</th>
                  <th>ISBN</th>
                  <th>Préstamos</th>
                  <th>Popularidad</th>
                </tr>
              </thead>
              <tbody>
                {topLibros.map((libro, i) => (
                  <tr key={libro.libroId}>
                    <td>{i + 1}</td>
                    <td>{libro.titulo}</td>
                    <td>{libro.autor}</td>
                    <td>{libro.isbn}</td>
                    <td><strong>{libro.total}</strong></td>
                    <td>
                      <div className="popularity-bar-track">
                        <div className="popularity-bar-fill" style={{ width: `${(libro.total / maxPrestamos) * 100}%` }} />
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>

        {/* ── Sección 3: Tiempo promedio de devolución ── */}
        <section className="reportes-section">
          <h2 className="reportes-section-title">Tiempo de Devolución</h2>
          <div className="tiempo-grid">
            <div className="tiempo-card">
              <div className="tiempo-card-number">{promedioDias}</div>
              <div className="tiempo-card-label">Días promedio</div>
              <div className="tiempo-card-sub">({totalAnalizados} préstamos analizados)</div>
            </div>
            <div className="tiempo-card">
              <div className="tiempo-card-number" style={{ color: 'var(--color-success)' }}>{pctATiempo}%</div>
              <div className="tiempo-card-label">Devueltos a tiempo</div>
              <div className="tiempo-card-sub">({aTiempo} préstamos)</div>
            </div>
            <div className="tiempo-card">
              <div className="tiempo-card-number" style={{ color: 'var(--color-error)' }}>{pctTarde}%</div>
              <div className="tiempo-card-label">Devueltos tarde</div>
              <div className="tiempo-card-sub">({devueltos.length - aTiempo} préstamos)</div>
            </div>
          </div>
          {vencidos.length > 0 && (
            <p className="reportes-nota">
              ⚠ {vencidos.length} préstamo(s) vencido(s) sin devolver incluidos en el promedio.
            </p>
          )}
        </section>

        {/* ── Sección 4: Actividad Mensual ── */}
        <section className="reportes-section">
          <h2 className="reportes-section-title">Actividad Mensual</h2>

          <div className="filtro-fecha">
            <label className="filtro-fecha-label">
              Desde:
              <input type="date" className="filtro-fecha-input" value={actDesde}
                onChange={e => setActDesde(e.target.value)} />
            </label>
            <label className="filtro-fecha-label">
              Hasta:
              <input type="date" className="filtro-fecha-input" value={actHasta}
                onChange={e => setActHasta(e.target.value)} />
            </label>
            {(actDesde || actHasta) && (
              <button className="btn btn-secondary btn-sm" onClick={() => { setActDesde(''); setActHasta(''); }}>
                Limpiar
              </button>
            )}
            <span className="filtro-fecha-total">{loansActFiltrados.length} préstamo(s)</span>
          </div>

          {mesesArray.length === 0 ? (
            <p className="reportes-empty">No hay datos de actividad en este período.</p>
          ) : (
            <div className="chart-container">
              <div className="chart-bars">
                {mesesArray.map(m => (
                  <div key={`${m.anio}-${m.mes}`} className="chart-column">
                    <div className="chart-bar" style={{ height: `${(m.total / maxMensual) * 180}px` }}>
                      <span className="chart-bar-value">{m.total}</span>
                    </div>
                    <div className="chart-label">{NOMBRES_MESES[m.mes - 1]}<br /><small>{m.anio}</small></div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </section>

        {/* ── Sección 5: Multas por Usuario (Observer) ── */}
        <section className="reportes-section">
          <h2 className="reportes-section-title">Multas por Usuario</h2>
          <div className="filtro-fecha">
            <input type="text" className="filtro-fecha-input" placeholder="Buscar por email..."
              value={searchMultas} onChange={e => setSearchMultas(e.target.value)} />
            <span className="filtro-fecha-total">{multasFiltradas.length} usuario(s) con multas</span>
          </div>
          {multasFiltradas.length === 0 ? (
            <p className="reportes-empty">{searchMultas ? 'No hay usuarios con ese email.' : 'No hay multas registradas.'}</p>
          ) : (
            <table className="users-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Nombre</th>
                  <th>Email</th>
                  <th>Total Multas</th>
                  <th>Préstamos Multados</th>
                </tr>
              </thead>
              <tbody>
                {multasFiltradas.map((m, i) => (
                  <tr key={m.usuarioId}>
                    <td>{i + 1}</td>
                    <td>{m.nombre}</td>
                    <td>{m.email}</td>
                    <td><strong className="multa-monto">${m.totalMultas.toLocaleString()}</strong></td>
                    <td>{m.cantidadPrestamosMultados}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </div>
    </div>
  );
};
