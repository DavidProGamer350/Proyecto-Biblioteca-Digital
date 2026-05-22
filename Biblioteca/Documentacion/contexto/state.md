# Patrón State — Reporte Libros Más Renovados

## 1. Problema

### Situación Actual

En la biblioteca digital, cada préstamo puede renovarse múltiples veces. El reporte de "Libros Más Renovados" debe clasificar los libros según su nivel de renovación para identificar cuáles requieren atención. Sin un patrón de diseño, la lógica de clasificación se dispersa en condicionales.

```
Sin State:
if (veces == 0) { /* badge gris */ }
else if (veces <= 2) { /* badge azul */ }
else { /* badge rojo */ }
// ❌ Lógica mezclada con la presentación
// ❌ Difícil agregar nuevos niveles
// ❌ Violación de Open/Closed
```

**Problemas identificados:**
- ❌ Lógica de estados mezclada con la presentación
- ❌ Agregar nuevo estado implica modificar condicionales existentes
- ❌ Cada estado no encapsula su propio comportamiento (badge, orden, recomendación)
- ❌ Difícil de testear cada estado por separado

---

## 2. Justificación del Patrón State

### ¿Qué es el Patrón State?

State es un patrón de **comportamiento** que permite a un objeto alterar su comportamiento cuando su estado interno cambia. Cada estado se encapsula en una clase independiente con su propia lógica.

```
Concepto:
┌─────────────────────┐
│     Contexto        │
│  - state: State     │
│  + cambiarEstado(s) │
│  + comportarse() ───┼──── delega en state
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│   State (interface) │
│  + getColor()       │
│  + getOrden()       │
│  + getRecomendacion()│
└─────────┬───────────┘
          │
    ┌─────┼─────┐
    ▼     ▼     ▼
┌──────┐ ┌────┐ ┌──────┐
│State1│ │State│ │State3│
│      │ │2   │ │      │
└──────┘ └────┘ └──────┘
```

### ¿Por qué State para Libros Renovados?

| Razón | Explicación |
|-------|-------------|
| **Comportamiento por nivel** | Cada nivel de renovación tiene badge, orden y recomendación distintos |
| **Encapsulación** | Cada estado contiene toda la lógica que le corresponde |
| **Extensibilidad** | Nuevo estado (ej: "Renovación Extrema" para 5+) se agrega sin modificar otros |
| **Testeabilidad** | Cada estado se prueba de forma independiente |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — State (LibrosRenovadosState.js)" {
  class ContextoRenovacion {
    - state: RenovacionState
    + ContextoRenovacion(vecesRenovado)
    + getNombreEstado(): String
    + getBadgeClass(): String
    + getOrdenPrioridad(): Number
    + getRecomendacion(): String
  }

  interface RenovacionState {
    + getNombreEstado(): String
    + getBadgeClass(): String
    + getOrdenPrioridad(): Number
    + getRecomendacion(): String
  }

  class SinRenovacion implements RenovacionState {
    + getNombreEstado(): "Sin renovación"
    + getBadgeClass(): "state-badge--baja"
    + getOrdenPrioridad(): 3
    + getRecomendacion(): ""
  }

  class PocaRenovacion implements RenovacionState {
    + getNombreEstado(): "Poca renovación"
    + getBadgeClass(): "state-badge--media"
    + getOrdenPrioridad(): 2
    + getRecomendacion(): "Considerar extender plazo"
  }

  class MuchaRenovacion implements RenovacionState {
    + getNombreEstado(): "Mucha renovación"
    + getBadgeClass(): "state-badge--alta"
    + getOrdenPrioridad(): 1
    + getRecomendacion(): "Evaluar ampliar periodo de préstamo"
  }

  class StateFactory {
    + crearState(vecesRenovado): RenovacionState
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + agruparPorLibro(loans)
    + renderSection()
  }
}

ContextoRenovacion o-> RenovacionState : state
StateFactory ..> SinRenovacion : crea
StateFactory ..> PocaRenovacion : crea
StateFactory ..> MuchaRenovacion : crea
ReportesPage ..> ContextoRenovacion : usa
ReportesPage ..> StateFactory : usa

@enduml
```

### Descripción de la Jerarquía

| Clase / Función | Archivo | Rol |
|-----------------|---------|-----|
| `RenovacionState` | `LibrosRenovadosState.js` | Define el contrato: `getNombreEstado()`, `getBadgeClass()`, `getOrdenPrioridad()`, `getRecomendacion()` |
| `SinRenovacion` | `LibrosRenovadosState.js` | Estado concreto: 0 renovaciones, badge gris, orden 3 |
| `PocaRenovacion` | `LibrosRenovadosState.js` | Estado concreto: 1-2 renovaciones, badge azul, orden 2, sugiere extender plazo |
| `MuchaRenovacion` | `LibrosRenovadosState.js` | Estado concreto: 3+ renovaciones, badge rojo, orden 1, sugiere ampliar período |
| `StateFactory` | `LibrosRenovadosState.js` | Crea el estado correcto según el número de renovaciones |
| `ContextoRenovacion` | `LibrosRenovadosState.js` | Contexto que mantiene el estado actual y delega el comportamiento |

---

## 4. Flujo de Datos

```
ReportesPage.jsx — loadAllData()
         │
         ▼
Agrupa préstamos por libroId
(sumando vecesRenovado de cada préstamo)
         │
         ▼
Para cada libro:
  │
  ├─► StateFactory.crearState(vecesRenovado)
  │       │
  │       ├─► 0       → new SinRenovacion()
  │       ├─► 1-2     → new PocaRenovacion()
  │       └─► 3+      → new MuchaRenovacion()
  │
  ├─► new ContextoRenovacion(state)
  │
  └─► Obtener propiedades:
        ├─► .getNombreEstado()     → "Sin renovación"
        ├─► .getBadgeClass()       → "state-badge--baja"
        ├─► .getOrdenPrioridad()   → 3
        └─► .getRecomendacion()    → "Considerar extender plazo"
         │
         ▼
Ordena por getOrdenPrioridad()
         │
         ▼
Renderiza tabla con badge + recomendación
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── LibrosRenovadosState.js                 ← Implementación State

frontend/src/pages/
└── ReportesPage.jsx                        ← Consume el patrón State

frontend/src/services/
└── LibrosRenovadosState.test.js            ← Tests
```

### Cálculo de renovaciones por libro

```
Datos fuente: GET /prestamos → cada préstamo tiene "vecesRenovado"

Agrupación frontend:
  const renovacionesPorLibro = {};
  loans.forEach(p => {
    const id = p.libroId;
    if (!renovacionesPorLibro[id]) {
      renovacionesPorLibro[id] = { libroId: id, totalRenovaciones: 0, cantidadPrestamos: 0 };
    }
    renovacionesPorLibro[id].totalRenovaciones += (p.vecesRenovado || 0);
    renovacionesPorLibro[id].cantidadPrestamos += 1;
  });
```

### Endpoints REST

No se requieren endpoints adicionales. El patrón State se ejecuta completamente en el frontend.

---

## 6. Criterios de Estado

| Rango `vecesRenovado` | Estado | Badge CSS | Orden | Recomendación |
|-----------------------|--------|-----------|-------|---------------|
| 0 | `SinRenovacion` | `state-badge--baja` (gris) | 3 | — |
| 1 — 2 | `PocaRenovacion` | `state-badge--media` (azul) | 2 | Considerar extender plazo |
| 3+ | `MuchaRenovacion` | `state-badge--alta` (rojo) | 1 | Evaluar ampliar periodo de préstamo |

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Observer** | Observer reacciona a cambios en los préstamos; State clasifica esos préstamos por nivel de renovación |
| **Strategy** | Strategy (próximo reporte) define algoritmos intercambiables; State define comportamientos fijos por nivel |
| **Composite** | Los reportes existentes usan Composite para estructura de árbol; State determina el display de cada hoja |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/LibrosRenovadosState.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `SinRenovacion_0_renovaciones` | State | veces=0 → estado SinRenovacion |
| 2 | `PocaRenovacion_1_renovacion` | State | veces=1 → estado PocaRenovacion |
| 3 | `PocaRenovacion_2_renovaciones` | State | veces=2 → estado PocaRenovacion |
| 4 | `MuchaRenovacion_3_renovaciones` | State | veces=3 → estado MuchaRenovacion |
| 5 | `MuchaRenovacion_5_renovaciones` | State | veces=5 → estado MuchaRenovacion |
| 6 | `SinRenovacion_propiedades` | Comportamiento | Badge "state-badge--baja", orden 3, nombre "Sin renovación" |
| 7 | `PocaRenovacion_propiedades` | Comportamiento | Badge "state-badge--media", orden 2, recomendación |
| 8 | `MuchaRenovacion_propiedades` | Comportamiento | Badge "state-badge--alta", orden 1, recomendación |
| 9 | `ContextoRenovacion_delegaEnState` | Contexto | Contexto devuelve lo mismo que el state interno |
| 10 | `factory_creaEstadoCorrecto` | Factory | StateFactory.crearState(n) retorna la clase adecuada |
| 11 | `agruparPorLibro_sumaRenovaciones` | Integración | Múltiples préstamos del mismo libro se agrupan sumando |
| 12 | `flujoCompleto_agruparYclasificar` | Integración | Agrupar → crear estado → ordenar por prioridad |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | Cada estado tiene una única responsabilidad: definir su comportamiento |
| **OCP** | Nuevos estados se agregan sin modificar estados existentes |
| **LSP** | Todos los estados implementan `RenovacionState` y son intercambiables |
| **ISP** | La interfaz `RenovacionState` tiene métodos cohesivos y específicos |
| **DIP** | `ContextoRenovacion` depende de la abstracción `RenovacionState`, no de concretas |
