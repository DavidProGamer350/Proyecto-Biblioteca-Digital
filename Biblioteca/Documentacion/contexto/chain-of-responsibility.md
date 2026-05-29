# Patrón Chain of Responsibility — Gestión de Préstamos

## 1. Problema

### Situación Actual

La página de Gestión de Préstamos muestra 4 tarjetas resumen en la parte superior: Activos, Vencidos, Devueltos y Multas Totales. Cada una se calcula recorriendo el arreglo de préstamos con lógica directa en el componente.

```
Sin Chain of Responsibility:
const activeLoans = loans.filter(l => !esVencido(l) && l.estado !== 'DEVUELTO').length;
const overdueLoans = loans.filter(l => esVencido(l)).length;
const returnedLoans = loans.filter(l => l.estado === 'DEVUELTO').length;
const totalFines = loans.reduce((sum, l) => sum + calcularMulta(l), 0);
// ❌ Lógica de cálculo mezclada con el componente
// ❌ Cada nueva métrica requiere modificar el componente
// ❌ Difícil reutilizar o testear cada métrica por separado
```

**Problemas identificados:**
- ❌ El componente tiene múltiples responsabilidades (renderizar y calcular métricas)
- ❌ Agregar una nueva tarjeta implica modificar el componente
- ❌ No hay separación entre la lógica de cada métrica
- ❌ Difícil probar cada cálculo de forma aislada

---

## 2. Justificación del Patrón Chain of Responsibility

### ¿Qué es el Patrón Chain of Responsibility?

Chain of Responsibility es un patrón de **comportamiento** que permite pasar una solicitud a través de una cadena de handlers. Cada handler decide si procesa la solicitud o la pasa al siguiente. Esto desacopla el emisor de los receptores.

```
Concepto:
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ Cliente  │───►│ Handler  │───►│ Handler  │───►│ Handler  │
│          │    │    1     │    │    2     │    │    3     │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                     │               │               │
                     ▼               ▼               ▼
                 Resultado A      Resultado B     Resultado C
```

### ¿Por qué Chain of Responsibility para Gestión de Préstamos?

| Razón | Explicación |
|-------|-------------|
| **Separación** | Cada handler calcula una única métrica de forma independiente |
| **Extensibilidad** | Nueva tarjeta se agrega creando un handler sin modificar los existentes |
| **Testeabilidad** | Cada handler se prueba de forma aislada |
| **Composición flexible** | Se puede reordenar, agregar o quitar handlers sin afectar al componente |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — CoR (CadenaPrestamosHandler.js)" {
  class BaseHandler {
    # next: BaseHandler
    + setNext(handler): BaseHandler
    + manejar(loans, contexto): Object
  }

  class ActivosHandler {
    + manejar(loans, contexto): Object
  }

  class VencidosHandler {
    + manejar(loans, contexto): Object
  }

  class DevueltosHandler {
    + manejar(loans, contexto): Object
  }

  class MultasTotalesHandler {
    + manejar(loans, contexto): Object
  }
}

package "Contexto (AdminLoansPage.jsx)" {
  class AdminLoansPage {
    + loadAllData()
    + render()
  }
}

BaseHandler <|-- ActivosHandler : duck typing
BaseHandler <|-- VencidosHandler : duck typing
BaseHandler <|-- DevueltosHandler : duck typing
BaseHandler <|-- MultasTotalesHandler : duck typing
AdminLoansPage ..> BaseHandler : construye cadena y ejecuta

@enduml
```

### Descripción de la Jerarquía

| Clase | Archivo | Rol |
|-------|---------|-----|
| `BaseHandler` | `CadenaPrestamosHandler.js` | Clase base con `setNext()` y `manejar()` que delega al siguiente handler |
| `ActivosHandler` | `CadenaPrestamosHandler.js` | Calcula cantidad de préstamos activos no vencidos |
| `VencidosHandler` | `CadenaPrestamosHandler.js` | Calcula cantidad de préstamos vencidos |
| `DevueltosHandler` | `CadenaPrestamosHandler.js` | Calcula cantidad de préstamos devueltos |
| `MultasTotalesHandler` | `CadenaPrestamosHandler.js` | Calcula el total de multas acumuladas |

### Métodos del Handler

| Método | Descripción |
|--------|-------------|
| `setNext(handler)` | Enlaza el siguiente handler en la cadena, retorna el handler para encadenamiento |
| `manejar(loans, contexto)` | Procesa la solicitud y la pasa al siguiente, retorna el contexto acumulado |

---

## 4. Flujo de Datos

```
AdminLoansPage — loadAllData()
         │
         ▼
Cadena:
  const handler1 = new ActivosHandler();
  const handler2 = new VencidosHandler();
  const handler3 = new DevueltosHandler();
  const handler4 = new MultasTotalesHandler();
  
  handler1.setNext(handler2).setNext(handler3).setNext(handler4);
  
  const resultado = handler1.manejar(loans, {});
         │
         ▼
┌─────────────────────────────────────────────┐
│  ActivosHandler.manejar(loans, {})           │
│    → contexto.activos = contar(activos)     │
│    → pasa al VencidosHandler                │
├─────────────────────────────────────────────┤
│  VencidosHandler.manejar(loans, contexto)    │
│    → contexto.vencidos = contar(vencidos)   │
│    → pasa al DevueltosHandler               │
├─────────────────────────────────────────────┤
│  DevueltosHandler.manejar(loans, contexto)   │
│    → contexto.devuelto = contar(devueltos)  │
│    → pasa al MultasTotalesHandler           │
├─────────────────────────────────────────────┤
│  MultasTotalesHandler.manejar(loans, ctx)   │
│    → contexto.multasTotales = sumar(multas) │
│    → retorna contexto final                 │
└─────────────────────────────────────────────┘
         │
         ▼
  const { activos, vencidos, devueltos, multasTotales } = resultado;
         │
         ▼
Render: 4 tarjetas con los valores calculados
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── CadenaPrestamosHandler.js              ← Implementación Chain of Responsibility

frontend/src/pages/
└── AdminLoansPage.jsx                     ← Consume el patrón

frontend/src/services/
└── CadenaPrestamosHandler.test.js         ← Tests
```

### Endpoints REST

No se requieren endpoints adicionales. El patrón opera sobre los datos de `GET /prestamos`.

---

## 6. Handlers de la Cadena

| Handler | Métrica | Fórmula |
|---------|---------|---------|
| `ActivosHandler` | `activos` | Préstamos no vencidos y no devueltos |
| `VencidosHandler` | `vencidos` | Préstamos con fecha vencida y no devueltos |
| `DevueltosHandler` | `devueltos` | Préstamos con estado `DEVUELTO` |
| `MultasTotalesHandler` | `multasTotales` | Suma de `multasAcumuladas` + multas por días vencidos |

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Template Method** | Template Method define el esqueleto de un algoritmo; CoR pasa una solicitud por una cadena de handlers |
| **Command** | Command encapsula una solicitud como objeto; CoR encadena múltiples handlers que procesan la misma solicitud |
| **Composite** | CoR suele implementarse con una estructura similar a Composite (cada handler puede tener un "next") |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/CadenaPrestamosHandler.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `ActivosHandler_cuenta_activos_no_vencidos` | Handler | Préstamos activos sin vencer |
| 2 | `VencidosHandler_cuenta_vencidos` | Handler | Préstamos con fecha vencida |
| 3 | `DevueltosHandler_cuenta_devueltos` | Handler | Préstamos con estado DEVUELTO |
| 4 | `MultasTotalesHandler_suma_multas` | Handler | Suma correcta de multas |
| 5 | `cadena_completa_ejecuta_todos` | Cadena | La cadena completa calcula todas las métricas |
| 6 | `cadena_con_handler_único` | Cadena | Cadena con un solo handler funciona |
| 7 | `setNext_retorna_handler_para_encadenar` | Base | setNext permite encadenamiento fluido |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | Cada handler tiene una única responsabilidad: calcular una métrica específica |
| **OCP** | Nuevo handler se agrega sin modificar handlers existentes ni el componente |
| **LSP** | Todos los handlers implementan la misma interfaz y son intercambiables |
| **ISP** | Cada handler solo implementa `manejar()` |
| **DIP** | Los handlers dependen de la abstracción `BaseHandler` |
