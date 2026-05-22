import { describe, it, expect } from 'vitest';
import { GenerarTopLectoresCommand, ComandoHistorial } from './TopLectoresCommand';

const usersMap = {
  1: { id: 1, name: 'Ana', email: 'ana@mail.com', suscripcionActiva: true },
  2: { id: 2, name: 'Luis', email: 'luis@mail.com', suscripcionActiva: false },
  3: { id: 3, name: 'Sofia', email: 'sofia@mail.com', suscripcionActiva: true },
};

const prestamos = [
  { usuarioId: 1, fechaPrestamo: '2026-05-01' },
  { usuarioId: 1, fechaPrestamo: '2026-05-10' },
  { usuarioId: 1, fechaPrestamo: '2026-05-15' },
  { usuarioId: 2, fechaPrestamo: '2026-05-05' },
  { usuarioId: 2, fechaPrestamo: '2026-05-12' },
  { usuarioId: 3, fechaPrestamo: '2026-05-08' },
];

describe('GenerarTopLectoresCommand', () => {
  it('ejecutar retorna ranking ordenado descendente', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    const resultado = cmd.ejecutar();
    expect(resultado).toHaveLength(3);
    expect(resultado[0].nombre).toBe('Ana');
    expect(resultado[0].totalPrestamos).toBe(3);
    expect(resultado[1].nombre).toBe('Luis');
    expect(resultado[1].totalPrestamos).toBe(2);
    expect(resultado[2].nombre).toBe('Sofia');
    expect(resultado[2].totalPrestamos).toBe(1);
  });

  it('filtra por rango de fechas', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap, '2026-05-10', '2026-05-15');
    const resultado = cmd.ejecutar();
    expect(resultado).toHaveLength(2);
    expect(resultado[0].nombre).toBe('Ana');
    expect(resultado[0].totalPrestamos).toBe(2);
  });

  it('sin préstamos retorna array vacío', () => {
    const cmd = new GenerarTopLectoresCommand([], usersMap);
    expect(cmd.ejecutar()).toEqual([]);
  });

  it('resuelve nombre y email desde usersMap', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    const r = cmd.ejecutar();
    expect(r[0].nombre).toBe('Ana');
    expect(r[0].email).toBe('ana@mail.com');
  });

  it('usuario sin datos en mapa usa valores por defecto', () => {
    const prestamosExtra = [...prestamos, { usuarioId: 99, fechaPrestamo: '2026-05-20' }];
    const cmd = new GenerarTopLectoresCommand(prestamosExtra, usersMap);
    const r = cmd.ejecutar();
    const desconocido = r.find(x => x.usuarioId === 99);
    expect(desconocido.nombre).toBe('Desconocido');
    expect(desconocido.email).toBe('—');
  });

  it('marca esPremium según suscripcionActiva', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    const r = cmd.ejecutar();
    expect(r.find(x => x.nombre === 'Ana').esPremium).toBe(true);
    expect(r.find(x => x.nombre === 'Luis').esPremium).toBe(false);
  });

  it('getNombre retorna "Top Lectores"', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    expect(cmd.getNombre()).toBe('Top Lectores');
  });

  it('getTimestamp retorna fecha ISO', () => {
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    expect(cmd.getTimestamp()).toMatch(/^\d{4}-\d{2}-\d{2}T/);
  });
});

describe('ComandoHistorial', () => {
  it('ejecutar guarda comando en historial', () => {
    const hist = new ComandoHistorial();
    const cmd = new GenerarTopLectoresCommand(prestamos, usersMap);
    hist.ejecutar(cmd);
    expect(hist.getHistorial()).toHaveLength(1);
    expect(hist.getHistorial()[0].nombre).toBe('Top Lectores');
  });

  it('múltiples ejecuciones se acumulan', () => {
    const hist = new ComandoHistorial();
    hist.ejecutar(new GenerarTopLectoresCommand(prestamos, usersMap));
    hist.ejecutar(new GenerarTopLectoresCommand(prestamos, usersMap, '2026-05-01', '2026-05-10'));
    expect(hist.getHistorial()).toHaveLength(2);
  });

  it('limpiar vacía el historial', () => {
    const hist = new ComandoHistorial();
    hist.ejecutar(new GenerarTopLectoresCommand(prestamos, usersMap));
    hist.limpiar();
    expect(hist.getHistorial()).toEqual([]);
  });

  it('historial recién creado está vacío', () => {
    const hist = new ComandoHistorial();
    expect(hist.getHistorial()).toEqual([]);
  });
});
