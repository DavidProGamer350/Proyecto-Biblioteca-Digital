// State pattern — Clasificación de libros por renovaciones

class SinRenovacion {
  getNombreEstado() { return 'Sin renovación'; }
  getBadgeClass() { return 'state-badge--baja'; }
  getOrdenPrioridad() { return 3; }
  getRecomendacion() { return ''; }
}

class PocaRenovacion {
  getNombreEstado() { return 'Poca renovación'; }
  getBadgeClass() { return 'state-badge--media'; }
  getOrdenPrioridad() { return 2; }
  getRecomendacion() { return 'Considerar extender el plazo de préstamo'; }
}

class MuchaRenovacion {
  getNombreEstado() { return 'Mucha renovación'; }
  getBadgeClass() { return 'state-badge--alta'; }
  getOrdenPrioridad() { return 1; }
  getRecomendacion() { return 'Evaluar ampliar periodo de préstamo estándar'; }
}

class StateFactory {
  static crearState(vecesRenovado) {
    if (vecesRenovado >= 3) return new MuchaRenovacion();
    if (vecesRenovado >= 1) return new PocaRenovacion();
    return new SinRenovacion();
  }
}

class ContextoRenovacion {
  constructor(vecesRenovado) {
    this.state = StateFactory.crearState(vecesRenovado);
  }

  getNombreEstado() { return this.state.getNombreEstado(); }
  getBadgeClass() { return this.state.getBadgeClass(); }
  getOrdenPrioridad() { return this.state.getOrdenPrioridad(); }
  getRecomendacion() { return this.state.getRecomendacion(); }
}

function agruparPorLibro(prestamos, libros) {
  const renovaciones = {};
  prestamos.forEach(p => {
    const id = p.libroId;
    if (!renovaciones[id]) {
      renovaciones[id] = {
        libroId: id,
        totalRenovaciones: 0,
        cantidadPrestamos: 0,
      };
    }
    renovaciones[id].totalRenovaciones += (p.vecesRenovado || 0);
    renovaciones[id].cantidadPrestamos += 1;
  });

  const librosMap = {};
  if (libros) {
    Object.values(libros).forEach(l => { librosMap[l.id] = l; });
  }

  return Object.values(renovaciones).map(r => {
    const info = librosMap[r.libroId] || {};
    const ctx = new ContextoRenovacion(r.totalRenovaciones);
    return {
      ...r,
      titulo: info.titulo || `Libro #${r.libroId}`,
      autor: info.autor || '—',
      isbn: info.isbn || '—',
      formato: info.formato || '—',
      estado: ctx.getNombreEstado(),
      badgeClass: ctx.getBadgeClass(),
      ordenPrioridad: ctx.getOrdenPrioridad(),
      recomendacion: ctx.getRecomendacion(),
    };
  }).sort((a, b) => a.ordenPrioridad - b.ordenPrioridad || b.totalRenovaciones - a.totalRenovaciones);
}

export {
  SinRenovacion,
  PocaRenovacion,
  MuchaRenovacion,
  StateFactory,
  ContextoRenovacion,
  agruparPorLibro,
};
