import { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { RecomendacionService } from '../services/RecomendacionService';
import { BookService } from '../services/BookService';
import { Navbar } from '../components/Navbar';

export const RecomendacionesPage = () => {
  const { user } = useContext(AuthContext);
  const [recomendaciones, setRecomendaciones] = useState([]);
  const [generadas, setGeneradas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [bookMap, setBookMap] = useState({});

  useEffect(() => {
    if (!user) return;
    loadRecomendaciones();
  }, [user]);

  useEffect(() => {
    BookService.getAll().then(books => {
      const map = {};
      books.forEach(b => { map[b.id] = b; });
      setBookMap(map);
    }).catch(() => {});
  }, []);

  const loadRecomendaciones = async () => {
    try {
      const data = await RecomendacionService.getByUsuarioId(user.id);
      const activas = data.filter(r => r.activa);
      setRecomendaciones(activas);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerar = async () => {
    setGenerating(true);
    setGeneradas([]);
    try {
      const data = await RecomendacionService.generar(user.id);
      setGeneradas(data);
      await loadRecomendaciones();
    } catch (err) {
      alert('Error al generar recomendaciones: ' + err.message);
    } finally {
      setGenerating(false);
    }
  };

  const handleEliminar = async (id) => {
    try {
      await RecomendacionService.delete(id);
      setRecomendaciones(prev => prev.filter(r => r.id !== id));
    } catch (err) {
      alert('Error al eliminar recomendación: ' + err.message);
    }
  };

  const prioridadBadge = (p) => {
    if (p === 'alta') return 'badge badge-danger';
    if (p === 'media') return 'badge badge-warning';
    return 'badge badge-info';
  };

  if (loading) return <div className="container"><p className="loading-text">Cargando recomendaciones...</p></div>;

  return (
    <div>
      <Navbar />
      <div className="recomendaciones-container">
      <h1 className="recomendaciones-title">Recomendaciones</h1>

      {/* Botón generar */}
      <div className="recomendaciones-actions">
        <button className="btn btn-primary" onClick={handleGenerar} disabled={generating}>
          {generating ? 'Generando...' : 'Generar recomendaciones'}
        </button>
      </div>

      {/* Recomendaciones recién generadas */}
      {generadas.length > 0 && (
        <section className="recomendaciones-section">
          <h2 className="recomendaciones-section-title">Recomendaciones generadas</h2>
          <div className="recomendaciones-grid">
            {generadas.map(rec => (
              <div key={rec.id} className="recomendacion-card">
                {bookMap[rec.libroId] && (
                  <div className="recomendacion-card-header">
                    <h3 className="recomendacion-libro-titulo">{bookMap[rec.libroId].titulo}</h3>
                    <p className="recomendacion-libro-autor">{bookMap[rec.libroId].autor}</p>
                  </div>
                )}
                <span className={prioridadBadge(rec.prioridad)}>
                  {rec.prioridad.charAt(0).toUpperCase() + rec.prioridad.slice(1)}
                </span>
                <p className="recomendacion-motivo">{rec.motivo}</p>
                {bookMap[rec.libroId] && (
                  <span className="recomendacion-formato">{bookMap[rec.libroId].formato}</span>
                )}
              </div>
            ))}
          </div>
        </section>
      )}

      {/* Sin recomendaciones existentes */}
      {recomendaciones.length === 0 && generadas.length === 0 && (
        <div className="recomendaciones-empty">
          <p>Aún no tienes recomendaciones. Presiona "Generar recomendaciones" para obtener sugerencias personalizadas.</p>
        </div>
      )}

      {/* Recomendaciones guardadas */}
      {recomendaciones.length > 0 && (
        <section className="recomendaciones-section">
          <h2 className="recomendaciones-section-title">Tus recomendaciones guardadas</h2>
          <div className="recomendaciones-list">
            <table className="table">
              <thead>
                <tr>
                  <th>Libro</th>
                  <th>Autor</th>
                  <th>Motivo</th>
                  <th>Prioridad</th>
                  <th>Formato</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {recomendaciones.map(rec => (
                  <tr key={rec.id}>
                    <td>{bookMap[rec.libroId]?.titulo || '—'}</td>
                    <td>{bookMap[rec.libroId]?.autor || '—'}</td>
                    <td className="recomendacion-motivo-cell">{rec.motivo}</td>
                    <td><span className={prioridadBadge(rec.prioridad)}>{rec.prioridad}</span></td>
                    <td>{bookMap[rec.libroId]?.formato || '—'}</td>
                    <td>
                      <button className="btn-action btn-action--delete" onClick={() => handleEliminar(rec.id)}>
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </div>
    </div>
  );
};
