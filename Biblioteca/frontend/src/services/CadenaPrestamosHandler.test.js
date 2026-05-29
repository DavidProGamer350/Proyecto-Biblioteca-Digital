import { describe, it, expect } from 'vitest';
import {
  BaseHandler,
  ActivosHandler,
  VencidosHandler,
  DevueltosHandler,
  MultasTotalesHandler,
  construirCadena,
  ejecutarCadena,
} from './CadenaPrestamosHandler';

const loans = [
  { id: 1, estado: 'ACTIVO', fechaDevolucionEsperada: '2099-01-01', multasAcumuladas: 0 },
  { id: 2, estado: 'ACTIVO', fechaDevolucionEsperada: '2099-01-01', multasAcumuladas: 5 },
  { id: 3, estado: 'ACTIVO', fechaDevolucionEsperada: '2020-01-01', multasAcumuladas: 0 },
  { id: 4, estado: 'ACTIVO', fechaDevolucionEsperada: '2020-01-01', multasAcumuladas: 10 },
  { id: 5, estado: 'DEVUELTO', fechaDevolucionEsperada: '2020-01-01', multasAcumuladas: 0 },
  { id: 6, estado: 'DEVUELTO', fechaDevolucionEsperada: '2099-01-01', multasAcumuladas: 3 },
];

describe('ActivosHandler', () => {
  it('cuenta activos no vencidos', () => {
    const h = new ActivosHandler();
    const r = h.manejar(loans, {});
    expect(r.activos).toBe(2);
  });
});

describe('VencidosHandler', () => {
  it('cuenta vencidos', () => {
    const h = new VencidosHandler();
    const r = h.manejar(loans, {});
    expect(r.vencidos).toBe(2);
  });
});

describe('DevueltosHandler', () => {
  it('cuenta devueltos', () => {
    const h = new DevueltosHandler();
    const r = h.manejar(loans, {});
    expect(r.devuelto).toBe(2);
  });
});

describe('MultasTotalesHandler', () => {
  it('suma multas incluyendo calculadas por vencimiento', () => {
    const h = new MultasTotalesHandler();
    const r = h.manejar(loans, {});
    expect(r.multasTotales).toBeGreaterThanOrEqual(18);
  });
});

describe('cadena completa', () => {
  it('ejecuta todos los handlers', () => {
    const r = ejecutarCadena(loans);
    expect(r).toHaveProperty('activos');
    expect(r).toHaveProperty('vencidos');
    expect(r).toHaveProperty('devuelto');
    expect(r).toHaveProperty('multasTotales');
  });
});

describe('BaseHandler', () => {
  it('setNext retorna el handler para encadenar', () => {
    const h1 = new BaseHandler();
    const h2 = new BaseHandler();
    const ret = h1.setNext(h2);
    expect(ret).toBe(h2);
  });

  it('sin siguiente retorna contexto intacto', () => {
    const h = new BaseHandler();
    const r = h.manejar(loans, { existente: true });
    expect(r.existente).toBe(true);
  });
});
