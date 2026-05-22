import { describe, it, expect } from 'vitest';
import {
  SinRenovacion,
  PocaRenovacion,
  MuchaRenovacion,
  StateFactory,
  ContextoRenovacion,
  agruparPorLibro,
} from './LibrosRenovadosState';

describe('StateFactory — creación de estados', () => {
  it('0 renovaciones crea SinRenovacion', () => {
    const state = StateFactory.crearState(0);
    expect(state).toBeInstanceOf(SinRenovacion);
  });

  it('1 renovación crea PocaRenovacion', () => {
    const state = StateFactory.crearState(1);
    expect(state).toBeInstanceOf(PocaRenovacion);
  });

  it('2 renovaciones crea PocaRenovacion', () => {
    const state = StateFactory.crearState(2);
    expect(state).toBeInstanceOf(PocaRenovacion);
  });

  it('3 renovaciones crea MuchaRenovacion', () => {
    const state = StateFactory.crearState(3);
    expect(state).toBeInstanceOf(MuchaRenovacion);
  });

  it('5 renovaciones crea MuchaRenovacion', () => {
    const state = StateFactory.crearState(5);
    expect(state).toBeInstanceOf(MuchaRenovacion);
  });
});

describe('Estados — propiedades específicas', () => {
  it('SinRenovacion badge gris y orden 3', () => {
    const s = new SinRenovacion();
    expect(s.getNombreEstado()).toBe('Sin renovación');
    expect(s.getBadgeClass()).toBe('state-badge--baja');
    expect(s.getOrdenPrioridad()).toBe(3);
    expect(s.getRecomendacion()).toBe('');
  });

  it('PocaRenovacion badge azul, orden 2 y recomendación', () => {
    const s = new PocaRenovacion();
    expect(s.getNombreEstado()).toBe('Poca renovación');
    expect(s.getBadgeClass()).toBe('state-badge--media');
    expect(s.getOrdenPrioridad()).toBe(2);
    expect(s.getRecomendacion()).toBeTruthy();
  });

  it('MuchaRenovacion badge rojo, orden 1 y recomendación', () => {
    const s = new MuchaRenovacion();
    expect(s.getNombreEstado()).toBe('Mucha renovación');
    expect(s.getBadgeClass()).toBe('state-badge--alta');
    expect(s.getOrdenPrioridad()).toBe(1);
    expect(s.getRecomendacion()).toBeTruthy();
  });
});

describe('ContextoRenovacion — delegación', () => {
  it('contexto con 0 renovaciones delega SinRenovacion', () => {
    const ctx = new ContextoRenovacion(0);
    expect(ctx.getNombreEstado()).toBe('Sin renovación');
    expect(ctx.getBadgeClass()).toBe('state-badge--baja');
    expect(ctx.getOrdenPrioridad()).toBe(3);
    expect(ctx.getRecomendacion()).toBe('');
  });

  it('contexto con 2 renovaciones delega PocaRenovacion', () => {
    const ctx = new ContextoRenovacion(2);
    expect(ctx.getNombreEstado()).toBe('Poca renovación');
    expect(ctx.getBadgeClass()).toBe('state-badge--media');
    expect(ctx.getOrdenPrioridad()).toBe(2);
    expect(ctx.getRecomendacion()).toBeTruthy();
  });

  it('contexto con 4 renovaciones delega MuchaRenovacion', () => {
    const ctx = new ContextoRenovacion(4);
    expect(ctx.getNombreEstado()).toBe('Mucha renovación');
    expect(ctx.getBadgeClass()).toBe('state-badge--alta');
    expect(ctx.getOrdenPrioridad()).toBe(1);
    expect(ctx.getRecomendacion()).toBeTruthy();
  });
});

describe('agruparPorLibro — integración', () => {
  it('suma renovaciones del mismo libro', () => {
    const prestamos = [
      { libroId: 1, vecesRenovado: 1 },
      { libroId: 1, vecesRenovado: 2 },
      { libroId: 2, vecesRenovado: 0 },
    ];
    const libros = [
      { id: 1, titulo: 'Libro A', autor: 'Autor A', isbn: 'ABC-123', formato: 'PDF' },
      { id: 2, titulo: 'Libro B', autor: 'Autor B', isbn: 'DEF-456', formato: 'EPUB' },
    ];
    const resultado = agruparPorLibro(prestamos, libros);
    const libroA = resultado.find(r => r.libroId === 1);
    expect(libroA.totalRenovaciones).toBe(3);
    expect(libroA.titulo).toBe('Libro A');
    expect(libroA.isbn).toBe('ABC-123');
  });

  it('ordena por prioridad descendente', () => {
    const prestamos = [
      { libroId: 1, vecesRenovado: 0 },
      { libroId: 2, vecesRenovado: 5 },
      { libroId: 3, vecesRenovado: 1 },
    ];
    const resultado = agruparPorLibro(prestamos, []);
    expect(resultado[0].libroId).toBe(2); // MuchaRenovacion (orden 1)
    expect(resultado[1].libroId).toBe(3); // PocaRenovacion (orden 2)
    expect(resultado[2].libroId).toBe(1); // SinRenovacion (orden 3)
  });

  it('respeta desempate por total renovaciones', () => {
    const prestamos = [
      { libroId: 1, vecesRenovado: 4 },
      { libroId: 2, vecesRenovado: 3 },
    ];
    const resultado = agruparPorLibro(prestamos, []);
    expect(resultado[0].libroId).toBe(1);
    expect(resultado[1].libroId).toBe(2);
  });

  it('libro sin datos en catálogo usa valores por defecto', () => {
    const prestamos = [{ libroId: 99, vecesRenovado: 1 }];
    const resultado = agruparPorLibro(prestamos, []);
    expect(resultado[0].titulo).toBe('Libro #99');
    expect(resultado[0].autor).toBe('—');
    expect(resultado[0].isbn).toBe('—');
    expect(resultado[0].formato).toBe('—');
  });

  it('retorna array vacío sin préstamos', () => {
    expect(agruparPorLibro([], [])).toEqual([]);
  });
});
