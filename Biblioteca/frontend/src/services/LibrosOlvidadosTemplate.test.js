import { describe, it, expect } from 'vitest';
import { ReporteLibrosBase, LibrosNuncaPrestados } from './LibrosOlvidadosTemplate';

const libros = [
  { id: 1, titulo: 'Cien Años de Soledad', autor: 'Gabriel García Márquez', isbn: '978-3-16-148410-0', formato: 'PDF' },
  { id: 2, titulo: 'El Quijote', autor: 'Miguel de Cervantes', isbn: '978-0-14-044909-9', formato: 'FISICO' },
  { id: 3, titulo: 'Rayuela', autor: 'Julio Cortázar', isbn: '978-84-376-0494-7', formato: 'EPUB' },
  { id: 4, titulo: '1984', autor: 'George Orwell', isbn: '978-0-452-28423-4', formato: 'PDF' },
  { id: 5, titulo: 'Ficciones', autor: 'Jorge Luis Borges', isbn: '978-84-339-3026-2', formato: 'MOBI' },
];

const prestamos = [
  { libroId: 1, usuarioId: 1 },
  { libroId: 1, usuarioId: 2 },
  { libroId: 3, usuarioId: 1 },
  { libroId: 4, usuarioId: 3 },
];

describe('LibrosNuncaPrestados', () => {
  it('libro sin prestamos aparece en resultado', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    const ids = r.map(x => x.id);
    expect(ids).toContain(2);
  });

  it('libro con prestamos no aparece', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    const ids = r.map(x => x.id);
    expect(ids).not.toContain(1);
    expect(ids).not.toContain(3);
    expect(ids).not.toContain(4);
  });

  it('mezcla correcta libros con y sin prestamos', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    expect(r).toHaveLength(2);
    expect(r.map(x => x.id).sort()).toEqual([2, 5]);
  });

  it('getNombre retorna Nunca prestados', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    expect(t.getNombre()).toBe('Nunca prestados');
  });

  it('resultados ordenados por titulo A-Z', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    expect(r[0].titulo).toBe('El Quijote');
    expect(r[1].titulo).toBe('Ficciones');
  });

  it('catalogo vacio retorna array vacio', () => {
    const t = new LibrosNuncaPrestados([], prestamos);
    const r = t.generar();
    expect(r).toHaveLength(0);
  });

  it('sin prestamos todos los libros aparecen', () => {
    const t = new LibrosNuncaPrestados(libros, []);
    const r = t.generar();
    expect(r).toHaveLength(5);
  });

  it('cada resultado incluye formato del libro', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    r.forEach(item => {
      expect(item).toHaveProperty('formato');
    });
  });

  it('resultados incluyen indice secuencial', () => {
    const t = new LibrosNuncaPrestados(libros, prestamos);
    const r = t.generar();
    r.forEach((item, i) => {
      expect(item.indice).toBe(i + 1);
    });
  });
});

describe('ReporteLibrosBase', () => {
  it('filtrarLibros abstracto lanza error', () => {
    const t = new ReporteLibrosBase(libros, prestamos);
    expect(() => t.filtrarLibros(libros, prestamos)).toThrow('Método abstracto');
  });

  it('obtenerLibros retorna copia de libros', () => {
    const t = new ReporteLibrosBase(libros, prestamos);
    const r = t.obtenerLibros();
    expect(r).toEqual(libros);
    expect(r).not.toBe(libros);
  });

  it('obtenerPrestamos retorna copia de prestamos', () => {
    const t = new ReporteLibrosBase(libros, prestamos);
    const r = t.obtenerPrestamos();
    expect(r).toEqual(prestamos);
    expect(r).not.toBe(prestamos);
  });
});
