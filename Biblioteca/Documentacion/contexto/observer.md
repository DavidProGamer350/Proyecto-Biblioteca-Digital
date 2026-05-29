# Patrón Observer — Reporte Multas por Usuario

## 1. Problema

### Situación Actual

En la biblioteca digital, las multas se acumulan cuando un préstamo vence o se devuelve tarde. Actualmente no hay un mecanismo que **notifique automáticamente** cuando cambia el estado de un préstamo para actualizar el cálculo de multas. El reporte de multas solo refleja el estado al momento de la consulta, pero no reacciona a eventos en tiempo real.

```
Flujo actual:
┌──────────────┐     ┌──────────────────┐     ┌─────────────────┐
│ Préstamo     │     │ Usuario consulta │     │ Reporte muestra │
│ se vence     │ ──► │ reporte manual   │ ──► │ datos del       │
│              │     │                  │     │ momento         │
└──────────────┘     └──────────────────┘     └─────────────────┘
                     (sin notificación automática)
```

**Problemas identificados:**
- ❌ No hay actualización automática cuando un préstamo vence
- ❌ La lógica de cálculo de multas está dispersa
- ❌ No se pueden agregar fácilmente nuevos comportamientos al vencer un préstamo (ej: enviar email, registrar historial)
- ❌ Acoplamiento entre la detección de vencimientos y las acciones resultantes

---

## 2. Justificación del Patrón Observer

### ¿Qué es el Patrón Observer?

Observer es un patrón de **comportamiento** que define una dependencia **uno a muchos** entre objetos, de modo que cuando un objeto (Subject) cambia su estado, todos sus dependientes (Observers) son notificados y actualizados automáticamente.

```
Concepto:
┌─────────────┐       notifica       ┌──────────────┐
│  Subject    │ ──────────────────►  │  Observer    │
│             │                      │              │
│  - observers│                      │  +actualizar()│
└─────────────┘                      └──────────────┘
       │                                    │
       │                                    │
       ▼                                    ▼
┌─────────────┐                      ┌──────────────┐
│ Subject     │                      │ Observer     │
│ Concreto    │                      │ Concreto 1   │
└─────────────┘                      └──────────────┘
                                     ┌──────────────┐
                                     │ Observer     │
                                     │ Concreto 2   │
                                     └──────────────┘
```

### ¿Por qué Observer para Multas?

| Razón | Explicación |
|-------|-------------|
| **Eventos múltiples** | Un vencimiento puede disparar varias acciones: actualizar reporte, notificar usuario, registrar historial |
| **Desacoplamiento** | El `GestorMultas` no necesita saber qué hacen los observers |
| **Extensibilidad** | Agregar un nuevo observer no requiere modificar el subject |
| **Dinamismo** | Los observers pueden agregarse o eliminarse en tiempo de ejecución |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — Observer (MultasObserver.js)" {
  class MultasSubject {
    + constructor()
    + agregarObserver(observer)
    + eliminarObserver(observer)
    + notificarObservers(evento)
    + calcularMultas(loans, users)
  }

  class MultasObserver {
    + actualizar(evento)
  }

  class ReporteMultasObserver {
    - multasPorUsuario: Object
    + actualizar(evento)
    + obtenerMultas(usersMap): Array
    + limpiar()
  }

  class NotificadorObserver {
    + actualizar(evento)
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + loadAllData()
    + setMultas(data)
  }
}

MultasSubject o- MultasObserver : observers
MultasObserver <|.. ReporteMultasObserver : duck typing
MultasObserver <|.. NotificadorObserver : duck typing
ReportesPage ..> MultasSubject : usa gestorMultas
ReportesPage ..> ReporteMultasObserver : usa reporteMultasObserver

@enduml
```

### Descripción de la Jerarquía

| Clase / Instancia | Archivo | Rol |
|-------------------|---------|-----|
| `MultasSubject` | `MultasObserver.js` | Define los métodos para gestionar observers y notificar eventos de multa |
| `MultasObserver` (conceptual) | — | Contrato (duck typing) que deben cumplir los objetos que quieran recibir notificaciones |
| `gestorMultas` (instancia de `MultasSubject`) | `MultasObserver.js:112` | Subject singleton que itera préstamos, detecta vencimientos y notifica a todos los observers |
| `reporteMultasObserver` (instancia de `ReporteMultasObserver`) | `MultasObserver.js:113` | Observer que acumula multas por usuario en un mapa interno y genera el ranking final |
| `notificadorObserver` (instancia de `NotificadorObserver`) | `MultasObserver.js:114` | Observer que registra en consola cada multa detectada (simula notificación) |
| `ReportesPage.jsx` | `pages/ReportesPage.jsx` | Contexto React que instancia los datos, ejecuta `gestorMultas.calcularMultas()` y renderiza la tabla |

---

## 4. Flujo de Datos
```

USUARIO visita /reportes
         │
         ▼
┌─────────────────────────────────────┐
│  ReportesPage.jsx                   │
│  loadAllData() — Promise.all([     │
│    LoanService.getAll(),            │
│    UserService.getAll(),            │
│    BookService.getAll()             │
│  ])                                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  gestorMultas.calcularMultas()      │
│     │                               │
│     ├─► Itera préstamos            │
│     │                               │
│     ├─► Filtra vencidos            │
│     │   (ACTIVO + fecha vencida)    │
│     │                               │
│     ├─► Filtra devoluciones tardías│
│     │   (DEVUELTO + fecha real >    │
│     │    fecha esperada)            │
│     │                               │
│     └─► Crea evento por cada       │
│          multa detectada            │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  gestorMultas.notificarObservers()  │
│         │                           │
│         ▼                           │
│  ┌─────────────────────────────┐    │
│  │ reporteMultasObserver       │    │
│  │ .actualizar(evento)         │    │
│  │ └── acumula en mapa         │    │
│  │     multasPorUsuario[uid]   │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ notificadorObserver         │    │
│  │ .actualizar(evento)         │    │
│  │ └── console.log()           │    │
│  └─────────────────────────────┘    │
└──────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  reporteMultasObserver               │
│  .obtenerMultas(usersMap)            │
│  → Array ordenado por total desc     │
└──────────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│  setMultas(multasData)               │
│  → React re-renderiza la tabla       │
└──────────────────────────────────────┘
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── MultasObserver.js                            ← Implementación frontend Observer

frontend/src/pages/
└── ReportesPage.jsx                             ← Consume el patrón Observer
```

### Endpoints REST

No se requieren endpoints adicionales. El patrón Observer se ejecuta completamente en el frontend a partir de los datos obtenidos de los endpoints existentes (`GET /prestamos`, `GET /users`, `GET /books`).

---

## 6. Criterios de Notificación

| Evento | Disparador | Datos del MultaEvento |
|--------|-----------|----------------------|
| Vencimiento | `estado = ACTIVO` y `fechaDevolucionEsperada < hoy` | prestamoId, usuarioId, libroId, diasVencido, multaCalculada |
| Devolución tardía | `estado = DEVUELTO` y `fechaDevolucionReal > fechaDevolucionEsperada` | prestamoId, usuarioId, libroId, diasVencido, multaCalculada |

### Cálculo de multa

```
TARIFA_POR_DIA = 1 (constante definida en frontend)

multaCalculada = diasVencido × TARIFA_POR_DIA
```

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Composite** | Los reportes existentes usan Composite para estructura de árbol; Observer agrega reactividad al cálculo de multas dentro del frontend |
| **State** | El State (próximo reporte) define el comportamiento visual según nivel de renovación; Observer reacciona a cambios en los datos de préstamos |
| **Strategy** | El Strategy (próximo reporte) define algoritmos intercambiables de distribución; Observer notifica cuando los datos base cambian |

---

## 8. Pruebas Unitarias

### Tests Implementados (19 pruebas)

Archivo: `frontend/src/services/MultasObserver.test.js`

Framework: **Vitest** (integración nativa con Vite)

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `agregarObserver_recibeNotificaciones` | Subject | Observer registrado recibe el evento |
| 2 | `eliminarObserver_yaNoRecibeNotificaciones` | Subject | Observer eliminado deja de recibir eventos |
| 3 | `notificarObservers_notificaATodos` | Subject | Todos los observers registrados son notificados |
| 4 | `prestamoVencido_generaEventoVENCIMIENTO` | Cálculo | Préstamo ACTIVO con fecha vencida → evento VENCIMIENTO |
| 5 | `devolucionTardia_generaEventoDEVOLUCION_TARDIA` | Cálculo | Préstamo DEVUELTO después de fecha esperada → evento tardío |
| 6 | `prestamoDevueltoATiempo_noGeneraEvento` | Cálculo | Devolución antes del vencimiento → sin evento |
| 7 | `prestamoActivoNoVencido_noGeneraEvento` | Cálculo | Préstamo ACTIVO con fecha futura → sin evento |
| 8 | `multiplesPrestamos_generanMultiplesEventos` | Cálculo | N préstamos vencidos → N eventos |
| 9 | `sinVencimientos_retornaArrayVacio` | Cálculo | Sin vencimientos → array vacío |
| 10 | `acumulaMultasMismoUsuario` | Agregación | Múltiples eventos del mismo usuario → suma total |
| 11 | `agrupaUsuariosDistintos` | Agregación | Eventos de distintos usuarios → entradas separadas |
| 12 | `obtenerMultas_ordenDescendente` | Agregación | Ranking ordenado por totalMultas descendente |
| 13 | `obtenerMultas_asignaNombreEmail` | Agregación | Nombres resueltos desde el usersMap |
| 14 | `obtenerMultas_usuarioNoEnMapa` | Agregación | Usuario sin datos → "Desconocido" y "—" |
| 15 | `limpiar_reseteaMultas` | Agregación | limpiar() vacía el mapa interno |
| 16 | `sinEventos_retornaArrayVacio` | Agregación | Observer sin datos → array vacío |
| 17 | `notificadorObserver_llamaConsoleLog` | Notificador | actualizar() ejecuta console.log con formato |
| 18 | `subject_notificaATodosEnCalcularMultas` | Integración | calcularMultas() dispara notificación a observers |
| 19 | `flujoCompleto_calcularYAcumular` | Integración | Calcular → observer acumula → obtener ranking correcto |

**Resultado**: ✅ 19/19 tests pasando

### Mock de datos

```js
const mockUsersMap = {
  1: { id: 1, name: 'Ana', email: 'ana@mail.com' },
  2: { id: 2, name: 'Luis', email: 'luis@mail.com' },
};

function crearPrestamo(overrides) {
  return {
    id: 1, usuarioId: 1, libroId: 10,
    fechaPrestamo: '2026-04-01',
    fechaDevolucionEsperada: '2026-04-15',
    fechaDevolucionReal: null,
    estado: 'ACTIVO',
    ...overrides,
  };
}
```

### Ejecución

```bash
# Una vez
npm test

# Modo watch (desarrollo)
npm run test:watch
```

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | `MultasSubject` solo gestiona observers y detecta vencimientos; cada observer tiene una única responsabilidad |
| **OCP** | Nuevos observers se agregan sin modificar `MultasSubject` — basta crear una clase que implemente `actualizar()` |
| **LSP** | Todos los observers (`ReporteMultasObserver`, `NotificadorObserver`) comparten la misma interfaz (duck typing) y son intercambiables |
| **ISP** | Cada observer implementa únicamente el método `actualizar(evento)` |
| **DIP** | `MultasSubject` depende de la abstracción `MultasObserver`, no de implementaciones concretas |
