class ReporteLibrosBase {
  constructor(libros, prestamos) {
    this._libros = libros || [];
    this._prestamos = prestamos || [];
  }

  generar() {
    const libros = this.obtenerLibros();
    const prestamos = this.obtenerPrestamos();
    const filtrados = this.filtrarLibros(libros, prestamos);
    const ordenados = this.ordenarLibros(filtrados);
    return this.enriquecerLibros(ordenados);
  }

  obtenerLibros() {
    return [...this._libros];
  }

  obtenerPrestamos() {
    return [...this._prestamos];
  }

  filtrarLibros(libros, prestamos) {
    throw new Error('Método abstracto — debe implementarse en subclase');
  }

  ordenarLibros(libros) {
    return [...libros].sort((a, b) => {
      const tA = (a.titulo || '').toLowerCase();
      const tB = (b.titulo || '').toLowerCase();
      return tA.localeCompare(tB);
    });
  }

  enriquecerLibros(libros) {
    return libros.map((l, i) => ({
      ...l,
      indice: i + 1,
    }));
  }
}

class LibrosNuncaPrestados extends ReporteLibrosBase {
  getNombre() { return 'Nunca prestados'; }

  filtrarLibros(libros, prestamos) {
    const idsPrestados = new Set(prestamos.map(p => p.libroId));
    return libros.filter(l => !idsPrestados.has(l.id));
  }
}

export { ReporteLibrosBase, LibrosNuncaPrestados };
