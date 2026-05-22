// Strategy pattern — Distribución de préstamos por formato

class EstrategiaPorcentual {
  getNombre() { return 'Porcentual'; }

  calcular(prestamos, libros) {
    const formatos = agruparPorFormato(prestamos, libros);
    const total = formatos.reduce((s, f) => s + f.cantidad, 0);
    return formatos.map(f => ({
      ...f,
      porcentaje: total > 0 ? Math.round((f.cantidad / total) * 100) : 0,
      _unidad: '%',
    }));
  }
}

class EstrategiaAbsoluta {
  getNombre() { return 'Absoluta'; }

  calcular(prestamos, libros) {
    const formatos = agruparPorFormato(prestamos, libros);
    return formatos.map(f => ({
      ...f,
      _unidad: 'préstamos',
    }));
  }
}

function agruparPorFormato(prestamos, libros) {
  const librosMap = {};
  if (libros) {
    Object.values(libros).forEach(l => { librosMap[l.id] = l; });
  }

  const conteo = {};
  prestamos.forEach(p => {
    const libro = librosMap[p.libroId];
    const formato = (libro && libro.formato) || 'Sin formato';
    if (!conteo[formato]) conteo[formato] = 0;
    conteo[formato]++;
  });

  const FORMATOS_ORDEN = { PDF: 1, EPUB: 2, MOBI: 3, FISICO: 4 };
  return Object.entries(conteo)
    .map(([formato, cantidad]) => ({
      formato,
      cantidad,
      orden: FORMATOS_ORDEN[formato] || 99,
    }))
    .sort((a, b) => a.orden - b.orden);
}

class ContextoDistribucion {
  constructor(strategy) {
    this.strategy = strategy;
  }

  setStrategy(strategy) {
    this.strategy = strategy;
  }

  getNombre() {
    return this.strategy.getNombre();
  }

  ejecutar(prestamos, libros) {
    return this.strategy.calcular(prestamos, libros);
  }
}

const ESTRATEGIAS = {
  porcentual: EstrategiaPorcentual,
  absoluta: EstrategiaAbsoluta,
};

function crearStrategy(tipo) {
  const Clase = ESTRATEGIAS[tipo];
  if (!Clase) throw new Error(`Estrategia desconocida: "${tipo}"`);
  return new Clase();
}

function calcularDistribucion(prestamos, libros, tipo = 'porcentual') {
  const strategy = crearStrategy(tipo);
  const ctx = new ContextoDistribucion(strategy);
  return ctx.ejecutar(prestamos, libros);
}

export {
  EstrategiaPorcentual,
  EstrategiaAbsoluta,
  ContextoDistribucion,
  crearStrategy,
  calcularDistribucion,
  agruparPorFormato,
};
