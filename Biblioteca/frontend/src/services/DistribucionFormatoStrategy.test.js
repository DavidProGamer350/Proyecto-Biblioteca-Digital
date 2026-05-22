import { describe, it, expect } from 'vitest';
import {
  EstrategiaPorcentual,
  EstrategiaAbsoluta,
  ContextoDistribucion,
  crearStrategy,
  calcularDistribucion,
  agruparPorFormato,
} from './DistribucionFormatoStrategy';

const libros = [
  { id: 1, formato: 'PDF' },
  { id: 2, formato: 'EPUB' },
  { id: 3, formato: 'MOBI' },
  { id: 4, formato: 'PDF' },
];

describe('agruparPorFormato', () => {
  it('agrupa préstamos por formato del libro', () => {
    const prestamos = [
      { libroId: 1 }, { libroId: 2 }, { libroId: 3 }, { libroId: 4 },
    ];
    const resultado = agruparPorFormato(prestamos, libros);
    expect(resultado).toHaveLength(3);
    const pdf = resultado.find(r => r.formato === 'PDF');
    expect(pdf.cantidad).toBe(2);
  });

  it('préstamo sin libro conocido usa "Sin formato"', () => {
    const prestamos = [{ libroId: 999 }];
    const resultado = agruparPorFormato(prestamos, libros);
    expect(resultado[0].formato).toBe('Sin formato');
  });
});

describe('EstrategiaPorcentual', () => {
  it('calcula porcentajes correctos', () => {
    const prestamos = [
      { libroId: 1 }, { libroId: 4 }, { libroId: 2 }, { libroId: 2 },
    ];
    const r = new EstrategiaPorcentual().calcular(prestamos, libros);
    const epub = r.find(x => x.formato === 'EPUB');
    expect(epub.cantidad).toBe(2);
    expect(epub.porcentaje).toBe(50);
    const pdf = r.find(x => x.formato === 'PDF');
    expect(pdf.porcentaje).toBe(50);
  });

  it('un solo formato da 100%', () => {
    const r = new EstrategiaPorcentual().calcular([{ libroId: 1 }], libros);
    expect(r[0].porcentaje).toBe(100);
  });

  it('sin datos retorna array vacío', () => {
    expect(new EstrategiaPorcentual().calcular([], libros)).toEqual([]);
  });
});

describe('EstrategiaAbsoluta', () => {
  it('cuenta préstamos por formato', () => {
    const prestamos = [{ libroId: 1 }, { libroId: 2 }, { libroId: 1 }];
    const r = new EstrategiaAbsoluta().calcular(prestamos, libros);
    const pdf = r.find(x => x.formato === 'PDF');
    expect(pdf.cantidad).toBe(2);
  });
});

describe('ContextoDistribucion', () => {
  it('ejecuta estrategia correcta', () => {
    const ctx = new ContextoDistribucion(new EstrategiaPorcentual());
    expect(ctx.getNombre()).toBe('Porcentual');
    const r = ctx.ejecutar([{ libroId: 1 }], libros);
    expect(r[0].porcentaje).toBe(100);
  });

  it('cambiar estrategia cambia resultado', () => {
    const ctx = new ContextoDistribucion(new EstrategiaPorcentual());
    const r1 = ctx.ejecutar([{ libroId: 1 }, { libroId: 4 }], libros);
    expect(r1[0].porcentaje).toBe(100);
    ctx.setStrategy(new EstrategiaAbsoluta());
    const r2 = ctx.ejecutar([{ libroId: 1 }, { libroId: 4 }], libros);
    expect(r2[0].cantidad).toBe(2);
  });
});

describe('crearStrategy', () => {
  it('crea estrategia según tipo', () => {
    expect(crearStrategy('porcentual')).toBeInstanceOf(EstrategiaPorcentual);
    expect(crearStrategy('absoluta')).toBeInstanceOf(EstrategiaAbsoluta);
  });

  it('tipo inválido lanza error', () => {
    expect(() => crearStrategy('invalida')).toThrow('Estrategia desconocida');
  });
});

describe('calcularDistribucion — integración', () => {
  it('flujo completo orden descendente', () => {
    const prestamos = [
      { libroId: 1 }, { libroId: 4 }, { libroId: 2 }, { libroId: 3 },
    ];
    const r = calcularDistribucion(prestamos, libros, 'absoluta');
    expect(r[0].formato).toBe('PDF');
    expect(r[0].cantidad).toBe(2);
  });
});
