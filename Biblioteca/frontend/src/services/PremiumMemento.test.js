import { describe, it, expect } from 'vitest';
import { PremiumMemento, PremiumOriginator, PremiumCaretaker } from './PremiumMemento';

const users = [
  { id: 1, name: 'Ana', suscripcionActiva: true },
  { id: 2, name: 'Luis', suscripcionActiva: false },
  { id: 3, name: 'Sofia', suscripcionActiva: true },
  { id: 4, name: 'Pedro', suscripcionActiva: false },
  { id: 5, name: 'Maria', suscripcionActiva: false },
];

describe('PremiumOriginator', () => {
  it('calcular con usuarios mixtos', () => {
    const o = new PremiumOriginator();
    o.calcular(users);
    expect(o.getPremiumUsers()).toBe(2);
    expect(o.getFreeUsers()).toBe(3);
    expect(o.getPctPremium()).toBe(40);
    expect(o.getIngresoPremium()).toBe(30000);
  });

  it('calcular todos premium', () => {
    const o = new PremiumOriginator();
    o.calcular(users.slice(0, 1).map(u => ({ ...u, suscripcionActiva: true })));
    expect(o.getPctPremium()).toBe(100);
  });

  it('calcular todos free', () => {
    const o = new PremiumOriginator();
    o.calcular(users.slice(0, 1).map(u => ({ ...u, suscripcionActiva: false })));
    expect(o.getPctPremium()).toBe(0);
  });

  it('calcular sin usuarios', () => {
    const o = new PremiumOriginator();
    o.calcular([]);
    expect(o.getPremiumUsers()).toBe(0);
    expect(o.getFreeUsers()).toBe(0);
    expect(o.getPctPremium()).toBe(0);
    expect(o.getIngresoPremium()).toBe(0);
  });
});

describe('PremiumMemento', () => {
  it('guarda y restaura estado', () => {
    const o = new PremiumOriginator();
    o.calcular(users);
    const memento = o.guardarMemento();

    o.calcular([{ id: 99, name: 'Nuevo', suscripcionActiva: false }]);
    expect(o.getPremiumUsers()).toBe(0);

    o.restaurarMemento(memento);
    expect(o.getPremiumUsers()).toBe(2);
    expect(o.getFreeUsers()).toBe(3);
    expect(o.getPctPremium()).toBe(40);
    expect(o.getIngresoPremium()).toBe(30000);
  });
});

describe('PremiumCaretaker', () => {
  it('guarda y deshace', () => {
    const c = new PremiumCaretaker();
    const o = new PremiumOriginator();

    o.calcular(users);
    c.guardar(o.guardarMemento());

    o.calcular(users.slice(0, 1));
    c.guardar(o.guardarMemento());

    const m1 = c.deshacer();
    o.restaurarMemento(m1);
    expect(o.getPremiumUsers()).toBe(1);

    const m2 = c.deshacer();
    o.restaurarMemento(m2);
    expect(o.getPremiumUsers()).toBe(2);
  });

  it('deshacer sin mementos retorna null', () => {
    const c = new PremiumCaretaker();
    expect(c.deshacer()).toBeNull();
  });

  it('limpiar vacia historial', () => {
    const c = new PremiumCaretaker();
    const o = new PremiumOriginator();
    o.calcular(users);
    c.guardar(o.guardarMemento());
    c.limpiar();
    expect(c.deshacer()).toBeNull();
  });
});
