// Command pattern — Ranking de Top Lectores

class GenerarTopLectoresCommand {
  constructor(prestamos, usersMap, desde = '', hasta = '') {
    this._prestamos = prestamos;
    this._usersMap = usersMap || {};
    this._desde = desde;
    this._hasta = hasta;
    this._timestamp = new Date().toISOString();
  }

  getNombre() { return 'Top Lectores'; }

  getTimestamp() { return this._timestamp; }

  ejecutar() {
    const filtrados = this._prestamos.filter(p => {
      if (!p.fechaPrestamo) return false;
      if (this._desde && p.fechaPrestamo < this._desde) return false;
      if (this._hasta && p.fechaPrestamo > this._hasta) return false;
      return true;
    });

    const conteo = {};
    filtrados.forEach(p => {
      const uid = p.usuarioId;
      conteo[uid] = (conteo[uid] || 0) + 1;
    });

    return Object.entries(conteo)
      .map(([uid, total]) => {
        const user = this._usersMap[uid] || {};
        return {
          usuarioId: Number(uid),
          nombre: user.name || 'Desconocido',
          email: user.email || '—',
          totalPrestamos: total,
          esPremium: user.suscripcionActiva || false,
        };
      })
      .sort((a, b) => b.totalPrestamos - a.totalPrestamos || a.nombre.localeCompare(b.nombre));
  }
}

class ComandoHistorial {
  constructor() {
    this._historial = [];
  }

  ejecutar(command) {
    const resultado = command.ejecutar();
    this._historial.push({
      nombre: command.getNombre(),
      timestamp: command.getTimestamp(),
    });
    return resultado;
  }

  getHistorial() {
    return [...this._historial];
  }

  limpiar() {
    this._historial = [];
  }
}

export { GenerarTopLectoresCommand, ComandoHistorial };
