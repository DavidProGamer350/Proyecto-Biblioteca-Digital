const PRECIO_PREMIUM = 15000;

class PremiumMemento {
  constructor(state) {
    this._state = { ...state };
  }

  getEstado() {
    return { ...this._state };
  }
}

class PremiumOriginator {
  calcular(users) {
    this.premiumUsers = users.filter(u => u.suscripcionActiva).length;
    this.freeUsers = users.filter(u => !u.suscripcionActiva).length;
    this.totalUsers = users.length;
    this.pctPremium = this.totalUsers > 0 ? Math.round((this.premiumUsers / this.totalUsers) * 100) : 0;
    this.ingresoPremium = this.premiumUsers * PRECIO_PREMIUM;
  }

  guardarMemento() {
    return new PremiumMemento({
      premiumUsers: this.premiumUsers,
      freeUsers: this.freeUsers,
      totalUsers: this.totalUsers,
      pctPremium: this.pctPremium,
      ingresoPremium: this.ingresoPremium,
    });
  }

  restaurarMemento(memento) {
    const s = memento.getEstado();
    this.premiumUsers = s.premiumUsers;
    this.freeUsers = s.freeUsers;
    this.totalUsers = s.totalUsers;
    this.pctPremium = s.pctPremium;
    this.ingresoPremium = s.ingresoPremium;
  }

  getPremiumUsers() { return this.premiumUsers; }
  getFreeUsers() { return this.freeUsers; }
  getPctPremium() { return this.pctPremium; }
  getIngresoPremium() { return this.ingresoPremium; }
}

class PremiumCaretaker {
  constructor() {
    this.mementos = [];
  }

  guardar(memento) {
    this.mementos.push(memento);
  }

  deshacer() {
    return this.mementos.pop() || null;
  }

  limpiar() {
    this.mementos = [];
  }
}

export { PremiumMemento, PremiumOriginator, PremiumCaretaker };
