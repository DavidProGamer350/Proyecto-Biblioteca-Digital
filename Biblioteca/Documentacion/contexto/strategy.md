# Patrón Strategy — Reporte Distribución por Formato

## 1. Problema

### Situación Actual

La biblioteca digital tiene libros en múltiples formatos (PDF, EPUB, MOBI, FISICO). El reporte de "Distribución por Formato" debe mostrar cómo se distribuyen los préstamos entre estos formatos, pero existen **múltiples formas de calcularlo**: porcentajes, valores absolutos, o comparativas históricas. Sin Strategy, cada variante agrega condicionales.

```
Sin Strategy:
if (tipo === "porcentual") { /* fórmula A */ }
else if (tipo === "absoluto") { /* fórmula B */ }
else if (tipo === "comparativo") { /* fórmula C */ }
// ❌ Violación de Open/Closed
// ❌ Lógica acoplada al presentador
```

**Problemas identificados:**
- ❌ Agregar nueva variante requiere modificar código existente
- ❌ Cada algoritmo está acoplado al reporte
- ❌ No se pueden testear algoritmos por separado

---

## 2. Justificación del Patrón Strategy

### ¿Qué es el Patrón Strategy?

Strategy es un patrón de **comportamiento** que define una familia de algoritmos, los encapsula y los hace intercambiables. Permite que el algoritmo varíe independientemente de los clientes que lo usan.

```
Concepto:
┌───────────────────────────┐
│        Contexto           │
│  - strategy: Strategy     │
│  + setStrategy(s)         │
│  + calcular(): Resultado  │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│   Strategy (interface)    │
│  + getNombre(): String    │
│  + calcular(): Resultado  │
└───────────┬───────────────┘
            │
    ┌───────┼───────┐
    ▼       ▼       ▼
┌────────┐ ┌────┐ ┌──────────┐
│Strategy│ │Stra│ │ Strategy │
│   A    │ │tegy│ │    C     │
│        │ │ B  │ │          │
└────────┘ └────┘ └──────────┘
```

### ¿Por qué Strategy para Distribución por Formato?

| Razón | Explicación |
|-------|-------------|
| **Algoritmos intercambiables** | Porcentual, absoluto y comparativo son variantes del mismo cálculo |
| **Open/Closed** | Nueva estrategia se agrega sin modificar existentes |
| **Testeabilidad** | Cada estrategia se prueba de forma independiente |
| **Selector en tiempo real** | El usuario puede cambiar de estrategia en la UI sin recargar |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — Strategy (DistribucionFormatoStrategy.js)" {
  interface DistribucionStrategy {
    + getNombre(): String
    + calcular(prestamos, libros): FormatoData[]
  }

  class EstrategiaPorcentual implements DistribucionStrategy {
    + getNombre(): "Porcentual"
    + calcular(): FormatoData[] con %
  }

  class EstrategiaAbsoluta implements DistribucionStrategy {
    getNombre(): "Absoluta"
    calcular(): FormatoData[] con cantidades
  }

  class ContextoDistribucion {
    - strategy: DistribucionStrategy
    + ContextoDistribucion(strategy)
    + setStrategy(strategy)
    + getNombre(): String
    + ejecutar(prestamos, libros): FormatoData[]
  }

  class FormatoData {
    + formato: String
    + cantidad: Number
    + porcentaje: Number
    + variacion: Number
  }

  class StrategyFactory {
    + crearStrategy(tipo): DistribucionStrategy
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + calcularDistribucion()
    + renderSection()
  }
}

ContextoDistribucion o-> DistribucionStrategy : strategy
ReportesPage ..> ContextoDistribucion : usa
ReportesPage ..> StrategyFactory : usa
StrategyFactory ..> EstrategiaPorcentual : crea
StrategyFactory ..> EstrategiaAbsoluta : crea
distribucionFormatoStrategy ..> FormatoData : contiene

@enduml
```

### Descripción de la Jerarquía

| Clase / Función | Archivo | Rol |
|-----------------|---------|-----|
| `DistribucionStrategy` | `DistribucionFormatoStrategy.js` | Define el contrato: `getNombre()`, `calcular(prestamos, libros)` |
| `EstrategiaPorcentual` | `DistribucionFormatoStrategy.js` | Calcula % de cada formato sobre el total |
| `EstrategiaAbsoluta` | `DistribucionFormatoStrategy.js` | Cuenta número bruto de préstamos por formato |
| `ContextoDistribucion` | `DistribucionFormatoStrategy.js` | Mantiene la estrategia activa y delega el cálculo |
| `crearStrategy(tipo)` | `DistribucionFormatoStrategy.js` | Factory: crea estrategia según tipo |

---

## 4. Flujo de Datos

```
ReportesPage.jsx — loadAllData()
         │
         ▼
Guarda loans[] y books[] en estado
         │
         ▼
Usuario selecciona estrategia (pestañas)
  "Porcentual" | "Absoluta"
         │
         ▼
ContextoDistribucion.setStrategy(factory.crearStrategy(tipo))
         │
         ▼
ContextoDistribucion.ejecutar(prestamos, libros)
         │
         ├── Filtra préstamos con libro existente
         ├── Agrupa por formato (book.formato)
         │
         ├── Porcentual:
         │     formatos.map(f => ({
         │       formato, cantidad,
         │       porcentaje: (cantidad / total) * 100
          │     }))
          │
          └── Absoluta:
                formatos.map(f => ({ formato, cantidad }))
         │
         ▼
Renderiza tabla + barras de distribución
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── DistribucionFormatoStrategy.js        ← Implementación Strategy

frontend/src/pages/
└── ReportesPage.jsx                      ← Consume el patrón Strategy

frontend/src/services/
└── DistribucionFormatoStrategy.test.js   ← Tests
```

### Datos fuente

```
GET /prestamos → [{ libroId, ... }]
GET /books     → [{ id, formato: "PDF" | "EPUB" | "MOBI" | "FISICO", ... }]

Cálculo: se cruza préstamo → libro → formato
```

---

## 6. Estrategias

| Estrategia | Columnas | Descripción | Fórmula |
|------------|----------|-------------|---------|
| **Porcentual** | Formato, Cantidad, % | % de cada formato sobre el total | `(cantidad / total) * 100` |
| **Absoluta** | Formato, Cantidad | Conteo bruto de préstamos | `sum(préstamos por formato)` |

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **State** | State clasifica libros por renovación; Strategy calcula distribución con algoritmo intercambiable |
| **Observer** | Observer notifica cambios en préstamos; Strategy define cómo se agrupan esos datos |
| **Factory** | StrategyFactory es un ejemplo de Simple Factory |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/DistribucionFormatoStrategy.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `agrupa_préstamos_por_formato` | Agrupación | 4 préstamos, 3 formatos distintos → PDF con 2 |
| 2 | `préstamo_sin_libro_Sin formato` | Agrupación | libroId 999 → "Sin formato" |
| 3 | `Porcentual_calcula_porcentajes` | Estrategia | 50% PDF, 50% EPUB |
| 4 | `Porcentual_un_solo_formato` | Estrategia | 100% para el único formato |
| 5 | `Porcentual_sin_datos` | Estrategia | Array vacío |
| 6 | `Absoluta_cuenta_prestamos` | Estrategia | PDF con 2, EPUB con 1 |
| 7 | `Contexto_ejecuta_estrategia_correcta` | Contexto | getNombre() y resultado correcto |
| 8 | `Contexto_cambiar_estrategia` | Contexto | Cambio de Porcentual a Absoluta cambia resultado |
| 9 | `factory_crea_según_tipo` | Factory | "porcentual" → Porcentual, "absoluta" → Absoluta |
| 10 | `factory_tipo_inválido_lanza_error` | Factory | Error esperado |
| 11 | `flujo_completo_orden_descendente` | Integración | PDF primero, cantidad correcta |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | Cada estrategia tiene una única responsabilidad: calcular distribución de una forma |
| **OCP** | Nueva estrategia se agrega creando una clase que implemente `DistribucionStrategy` — no se modifica código existente |
| **LSP** | Todas las estrategias implementan `DistribucionStrategy` y son intercambiables |
| **ISP** | La interfaz tiene métodos cohesivos y específicos |
| **DIP** | `ContextoDistribucion` depende de la abstracción `DistribucionStrategy`, no de concretas |
