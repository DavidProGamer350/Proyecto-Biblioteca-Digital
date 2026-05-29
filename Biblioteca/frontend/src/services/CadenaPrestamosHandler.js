const TARIFA_POR_DIA = 1;

class BaseHandler {
  constructor() {
    this._next = null;
  }

  setNext(handler) {
    this._next = handler;
    return handler;
  }

  manejar(loans, contexto) {
    if (this._next) {
      return this._next.manejar(loans, contexto);
    }
    return contexto;
  }
}

function esVencido(loan, hoyStr) {
  if (loan.estado === 'DEVUELTO') return false;
  return loan.fechaDevolucionEsperada < hoyStr;
}

function getHoyStr() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

class ActivosHandler extends BaseHandler {
  manejar(loans, contexto) {
    const hoy = getHoyStr();
    contexto.activos = loans.filter(l => !esVencido(l, hoy) && l.estado !== 'DEVUELTO').length;
    return super.manejar(loans, contexto);
  }
}

class VencidosHandler extends BaseHandler {
  manejar(loans, contexto) {
    const hoy = getHoyStr();
    contexto.vencidos = loans.filter(l => esVencido(l, hoy)).length;
    return super.manejar(loans, contexto);
  }
}

class DevueltosHandler extends BaseHandler {
  manejar(loans, contexto) {
    contexto.devuelto = loans.filter(l => l.estado === 'DEVUELTO').length;
    return super.manejar(loans, contexto);
  }
}

class MultasTotalesHandler extends BaseHandler {
  calcularMulta(loan) {
    const base = loan.multasAcumuladas || 0;
    if (loan.estado === 'DEVUELTO') return base;
    const hoy = new Date();
    const esperada = new Date(loan.fechaDevolucionEsperada);
    if (esperada >= hoy) return base;
    const dias = Math.floor((hoy - esperada) / (1000 * 60 * 60 * 24)) + 1;
    return base + (dias * TARIFA_POR_DIA);
  }

  manejar(loans, contexto) {
    contexto.multasTotales = loans.reduce((sum, l) => sum + this.calcularMulta(l), 0);
    return super.manejar(loans, contexto);
  }
}

function construirCadena() {
  const h1 = new ActivosHandler();
  const h2 = new VencidosHandler();
  const h3 = new DevueltosHandler();
  const h4 = new MultasTotalesHandler();
  h1.setNext(h2).setNext(h3).setNext(h4);
  return h1;
}

function ejecutarCadena(loans) {
  const cadena = construirCadena();
  return cadena.manejar(loans, {});
}

export {
  BaseHandler,
  ActivosHandler,
  VencidosHandler,
  DevueltosHandler,
  MultasTotalesHandler,
  construirCadena,
  ejecutarCadena,
};
