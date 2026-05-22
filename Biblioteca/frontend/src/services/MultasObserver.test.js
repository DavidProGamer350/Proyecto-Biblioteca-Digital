import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  MultasSubject,
  ReporteMultasObserver,
  NotificadorObserver,
} from './MultasObserver.js';

// Re-import the module internals for testing
// We need to test the classes directly, not the singleton instances
const TARIFA_POR_DIA = 1;

function crearPrestamo(overrides = {}) {
  return {
    id: 1,
    usuarioId: 1,
    libroId: 10,
    fechaPrestamo: '2026-04-01',
    fechaDevolucionEsperada: '2026-04-15',
    fechaDevolucionReal: null,
    estado: 'ACTIVO',
    ...overrides,
  };
}

const mockUsersMap = {
  1: { id: 1, name: 'Ana', email: 'ana@mail.com' },
  2: { id: 2, name: 'Luis', email: 'luis@mail.com' },
};

describe('MultasSubject (Subject del Observer)', () => {
  let subject;
  let observer;

  beforeEach(() => {
    subject = new MultasSubject();
    observer = { actualizar: vi.fn() };
  });

  it('agregarObserver — registra y notifica al observer', () => {
    subject.agregarObserver(observer);
    subject.notificarObservers({ tipo: 'test' });
    expect(observer.actualizar).toHaveBeenCalledWith({ tipo: 'test' });
  });

  it('eliminarObserver — elimina y ya no notifica', () => {
    subject.agregarObserver(observer);
    subject.eliminarObserver(observer);
    subject.notificarObservers({ tipo: 'test' });
    expect(observer.actualizar).not.toHaveBeenCalled();
  });

  it('notificarObservers — notifica a todos los observers registrados', () => {
    const obs2 = { actualizar: vi.fn() };
    subject.agregarObserver(observer);
    subject.agregarObserver(obs2);
    subject.notificarObservers({ tipo: 'test' });
    expect(observer.actualizar).toHaveBeenCalledTimes(1);
    expect(obs2.actualizar).toHaveBeenCalledTimes(1);
  });
});

describe('calcularMultas — detección de vencimientos', () => {
  let subject;

  beforeEach(() => {
    subject = new MultasSubject();
  });

  it('préstamo ACTIVO vencido genera evento VENCIMIENTO', () => {
    const prestamos = [
      crearPrestamo({
        fechaDevolucionEsperada: '2026-04-01',
        estado: 'ACTIVO',
      }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos.length).toBe(1);
    expect(eventos[0].tipoEvento).toBe('VENCIMIENTO');
    expect(eventos[0].diasVencido).toBeGreaterThan(0);
    expect(eventos[0].multaCalculada).toBe(eventos[0].diasVencido * TARIFA_POR_DIA);
  });

  it('devolución tardía genera evento DEVOLUCION_TARDIA', () => {
    const prestamos = [
      crearPrestamo({
        fechaDevolucionEsperada: '2026-04-01',
        fechaDevolucionReal: '2026-04-10',
        estado: 'DEVUELTO',
      }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos.length).toBe(1);
    expect(eventos[0].tipoEvento).toBe('DEVOLUCION_TARDIA');
    expect(eventos[0].diasVencido).toBe(9);
    expect(eventos[0].multaCalculada).toBe(9);
  });

  it('préstamo devuelto a tiempo no genera evento', () => {
    const prestamos = [
      crearPrestamo({
        fechaDevolucionEsperada: '2026-04-15',
        fechaDevolucionReal: '2026-04-10',
        estado: 'DEVUELTO',
      }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos.length).toBe(0);
  });

  it('préstamo ACTIVO no vencido no genera evento', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 30);
    const prestamos = [
      crearPrestamo({
        fechaDevolucionEsperada: futureDate.toISOString().split('T')[0],
        estado: 'ACTIVO',
      }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos.length).toBe(0);
  });

  it('múltiples préstamos generan múltiples eventos', () => {
    const prestamos = [
      crearPrestamo({ id: 1, fechaDevolucionEsperada: '2026-04-01', estado: 'ACTIVO' }),
      crearPrestamo({ id: 2, fechaDevolucionEsperada: '2026-03-01', estado: 'ACTIVO', usuarioId: 2 }),
      crearPrestamo({ id: 3, fechaDevolucionEsperada: '2026-04-01', fechaDevolucionReal: '2026-04-10', estado: 'DEVUELTO' }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos.length).toBe(3);
  });

  it('sin vencimientos retorna array vacío', () => {
    const prestamos = [
      crearPrestamo({
        id: 1,
        fechaDevolucionEsperada: '2099-01-01',
        estado: 'ACTIVO',
      }),
    ];
    const eventos = subject.calcularMultas(prestamos, []);
    expect(eventos).toEqual([]);
  });
});

describe('ReporteMultasObserver — agregación de multas', () => {
  let observer;

  beforeEach(() => {
    observer = new ReporteMultasObserver();
  });

  it('acumula multas del mismo usuario', () => {
    observer.actualizar({ usuarioId: 1, multaCalculada: 5 });
    observer.actualizar({ usuarioId: 1, multaCalculada: 3 });
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado.length).toBe(1);
    expect(resultado[0].totalMultas).toBe(8);
    expect(resultado[0].cantidadPrestamosMultados).toBe(2);
  });

  it('agrupa usuarios distintos por separado', () => {
    observer.actualizar({ usuarioId: 1, multaCalculada: 5 });
    observer.actualizar({ usuarioId: 2, multaCalculada: 3 });
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado.length).toBe(2);
  });

  it('obtenerMultas ordena por totalMultas descendente', () => {
    observer.actualizar({ usuarioId: 1, multaCalculada: 5 });
    observer.actualizar({ usuarioId: 2, multaCalculada: 10 });
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado[0].usuarioId).toBe(2);
    expect(resultado[1].usuarioId).toBe(1);
  });

  it('obtenerMultas asigna nombre y email desde el usersMap', () => {
    observer.actualizar({ usuarioId: 1, multaCalculada: 5 });
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado[0].nombre).toBe('Ana');
    expect(resultado[0].email).toBe('ana@mail.com');
  });

  it('obtenerMultas asigna "Desconocido" si no está en usersMap', () => {
    observer.actualizar({ usuarioId: 99, multaCalculada: 5 });
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado[0].nombre).toBe('Desconocido');
    expect(resultado[0].email).toBe('—');
  });

  it('limpiar resetea todas las multas acumuladas', () => {
    observer.actualizar({ usuarioId: 1, multaCalculada: 5 });
    observer.limpiar();
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado.length).toBe(0);
  });

  it('sin eventos retorna array vacío', () => {
    const resultado = observer.obtenerMultas(mockUsersMap);
    expect(resultado).toEqual([]);
  });
});

describe('NotificadorObserver — registro en consola', () => {
  it('actualizar llama a console.log con el mensaje de multa', () => {
    const spy = vi.spyOn(console, 'log').mockImplementation(() => {});
    const notifier = new NotificadorObserver();
    notifier.actualizar({
      usuarioId: 1,
      multaCalculada: 150,
      diasVencido: 15,
      prestamoId: 5,
      tipoEvento: 'VENCIMIENTO',
    });
    expect(spy).toHaveBeenCalledWith(
      '[NOTIFICACIÓN] Usuario 1 — Multa de $150 por 15 día(s) vencido'
    );
    spy.mockRestore();
  });
});

describe('Integración Subject + Observers', () => {
  it('calcularMultas notifica a todos los observers registrados', () => {
    const subject = new MultasSubject();
    const spyObserver = { actualizar: vi.fn() };
    subject.agregarObserver(spyObserver);

    subject.calcularMultas(
      [crearPrestamo({ fechaDevolucionEsperada: '2026-04-01', estado: 'ACTIVO' })],
      []
    );

    expect(spyObserver.actualizar).toHaveBeenCalled();
    const evento = spyObserver.actualizar.mock.calls[0][0];
    expect(evento.tipoEvento).toBe('VENCIMIENTO');
    expect(evento.multaCalculada).toBeGreaterThan(0);
  });

  it('flujo completo: calcular → observer acumula → obtener ranking', () => {
    const subject = new MultasSubject();
    const reportObserver = new ReporteMultasObserver();
    subject.agregarObserver(reportObserver);

    const prestamos = [
      crearPrestamo({ id: 1, usuarioId: 1, fechaDevolucionEsperada: '2026-04-01', estado: 'ACTIVO' }),
      crearPrestamo({ id: 2, usuarioId: 2, fechaDevolucionEsperada: '2026-03-15', estado: 'ACTIVO' }),
      crearPrestamo({ id: 3, usuarioId: 1, fechaDevolucionEsperada: '2026-04-01', fechaDevolucionReal: '2026-04-10', estado: 'DEVUELTO' }),
    ];
    subject.calcularMultas(prestamos, []);
    const ranking = reportObserver.obtenerMultas(mockUsersMap);

    expect(ranking.length).toBe(2);
    // Usuario 1 tiene 2 préstamos multados → debe tener más multa acumulada
    const u1 = ranking.find(r => r.usuarioId === 1);
    const u2 = ranking.find(r => r.usuarioId === 2);
    expect(u1.cantidadPrestamosMultados).toBe(2);
    expect(u2.cantidadPrestamosMultados).toBe(1);
  });
});
