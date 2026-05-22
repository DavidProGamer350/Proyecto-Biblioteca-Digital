const TARIFA_POR_DIA = 1;

class MultasSubject {
  constructor() {
    this.observers = [];
  }

  agregarObserver(observer) {
    this.observers.push(observer);
  }

  eliminarObserver(observer) {
    this.observers = this.observers.filter(o => o !== observer);
  }

  notificarObservers(evento) {
    this.observers.forEach(o => o.actualizar(evento));
  }

  calcularMultas(loans, users) {
    const hoy = new Date();
    const eventos = [];

    loans.forEach(p => {
      if (!p.fechaDevolucionEsperada) return;

      if (p.estado === 'ACTIVO' && new Date(p.fechaDevolucionEsperada) < hoy) {
        const esperada = new Date(p.fechaDevolucionEsperada);
        const dias = Math.floor((hoy - esperada) / (1000 * 60 * 60 * 24));
        const multa = dias * TARIFA_POR_DIA;
        const evento = {
          prestamoId: p.id,
          usuarioId: p.usuarioId,
          libroId: p.libroId,
          diasVencido: dias,
          multaCalculada: multa,
          tipoEvento: 'VENCIMIENTO',
          fechaEvento: hoy.toISOString().split('T')[0]
        };
        eventos.push(evento);
        this.notificarObservers(evento);
      }

      if (p.estado === 'DEVUELTO' && p.fechaDevolucionReal && p.fechaDevolucionEsperada) {
        const real = new Date(p.fechaDevolucionReal);
        const esperada = new Date(p.fechaDevolucionEsperada);
        if (real > esperada) {
          const dias = Math.floor((real - esperada) / (1000 * 60 * 60 * 24));
          const multa = dias * TARIFA_POR_DIA;
          const evento = {
            prestamoId: p.id,
            usuarioId: p.usuarioId,
            libroId: p.libroId,
            diasVencido: dias,
            multaCalculada: multa,
            tipoEvento: 'DEVOLUCION_TARDIA',
            fechaEvento: p.fechaDevolucionReal
          };
          eventos.push(evento);
          this.notificarObservers(evento);
        }
      }
    });

    return eventos;
  }
}

class ReporteMultasObserver {
  constructor() {
    this.multasPorUsuario = {};
  }

  actualizar(evento) {
    const uid = evento.usuarioId;
    if (!this.multasPorUsuario[uid]) {
      this.multasPorUsuario[uid] = {
        usuarioId: uid,
        nombre: '',
        email: '',
        totalMultas: 0,
        cantidadPrestamosMultados: 0
      };
    }
    this.multasPorUsuario[uid].totalMultas += evento.multaCalculada;
    this.multasPorUsuario[uid].cantidadPrestamosMultados += 1;
  }

  obtenerMultas(usersMap) {
    return Object.values(this.multasPorUsuario)
      .map(m => ({
        ...m,
        nombre: usersMap[m.usuarioId]?.name || 'Desconocido',
        email: usersMap[m.usuarioId]?.email || '—'
      }))
      .sort((a, b) => b.totalMultas - a.totalMultas);
  }

  limpiar() {
    this.multasPorUsuario = {};
  }
}

class NotificadorObserver {
  actualizar(evento) {
    console.log(
      `[NOTIFICACIÓN] Usuario ${evento.usuarioId} — Multa de $${evento.multaCalculada} por ${evento.diasVencido} día(s) vencido`
    );
  }
}

export const gestorMultas = new MultasSubject();
export const reporteMultasObserver = new ReporteMultasObserver();
export const notificadorObserver = new NotificadorObserver();

gestorMultas.agregarObserver(reporteMultasObserver);
gestorMultas.agregarObserver(notificadorObserver);

// Export classes for testing
export { MultasSubject, ReporteMultasObserver, NotificadorObserver };
