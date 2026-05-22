# Patrones de Comportamiento - Nuevos Reportes

## 1. Introducción

Este documento describe la implementación de **4 patrones de comportamiento** del catálogo GoF para los nuevos reportes del sistema de biblioteca digital. Cada patrón se asigna a un reporte específico según la naturaleza del problema que resuelve.

### Mapeo Patrón → Reporte

| Patrón | Reporte | Problema que resuelve |
|--------|---------|-----------------------|
| **Observer** | Multas por Usuario | Reaccionar a cambios en el estado de préstamos |
| **State** | Libros Más Renovados | Comportamiento variable según nivel de renovación |
| **Strategy** | Distribución por Formato | Algoritmos intercambiables de cálculo |
| **Command** | Top Lectores | Encapsular solicitudes como objetos |

### Ubicación en el proyecto

```
src/main/java/com/biblioteca/digital/
├── domain/
│   ├── model/
│   │   ├── reporte/
│   │   │   ├── comportamiento/
│   │   │   │   ├── observer/        ← Observer (Multas)
│   │   │   │   ├── state/           ← State (Renovados)
│   │   │   │   ├── strategy/        ← Strategy (Formato)
│   │   │   │   └── command/         ← Command (Top Lectores)
```

---

## 2. Observer — Reporte Multas por Usuario

### Problema

Las multas se acumulan dinámicamente cuando un préstamo vence o se devuelve tarde. El reporte de multas por usuario debe **reflejar el estado actual** en tiempo real. Sin un mecanismo de notificación, el reporte quedaría desactualizado hasta que alguien lo solicite explícitamente.

```
Flujo actual (sin Observer):
┌──────────────┐     ┌──────────────────┐     ┌─────────────────┐
│ Préstamo     │     │ Usuario consulta │     │ Reporte muestra │
│ se vence     │ ──► │ reporte manual   │ ──► │ datos viejos    │
└──────────────┘     └──────────────────┘     └─────────────────┘
                     (sin notificación automática)
```

### Solución: Patrón Observer

El préstamo (o un gestor de multas) actúa como **Subject** que notifica a los **Observers** cuando ocurren eventos relevantes (vencimiento, devolución tardía). El reporte se actualiza automáticamente.

```
Diagrama:
┌───────────────────────────────────────────────┐
│              MultasSubject (interface)         │
├───────────────────────────────────────────────┤
│ + agregarObserver(MultasObserver)             │
│ + eliminarObserver(MultasObserver)            │
│ + notificarObservers()                        │
└──────────────────────┬────────────────────────┘
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
┌─────────────────────┐   ┌─────────────────────────┐
│ GestorMultas        │   │   MultasObserver        │
│   (Subject concreto)│   │     (interface)         │
├─────────────────────┤   ├─────────────────────────┤
│ - observers: List   │   │ + actualizar(multa)     │
│ + agregarObserver() │   └──────────┬──────────────┘
│ + notificar()       │              │
│ + registrarVencimto()│             │
└─────────────────────┘              │
                                     │
                    ┌────────────────┼────────────────┐
                    ▼                ▼                 ▼
          ┌─────────────────┐ ┌──────────────┐ ┌──────────────────┐
          │ ReporteMultas   │ │ Notificador  │ │ HistorialMultas  │
          │ - actualiza     │ │ - envía      │ │ - registra       │
          │   el reporte    │ │   alerta     │ │   en BD          │
          └─────────────────┘ └──────────────┘ └──────────────────┘
```

### Estructura de clases

| Clase | Rol | Descripción |
|-------|-----|-------------|
| `MultasSubject` | Interfaz Subject | Define métodos para gestionar observers |
| `GestorMultas` | Subject concreto | Detecta vencimientos y devoluciones, notifica a observers |
| `MultasObserver` | Interfaz Observer | Contrato que deben implementar los observadores |
| `ReporteMultasObserver` | Observer concreto | Actualiza el reporte de multas por usuario |
| `NotificadorMultasObserver` | Observer concreto | Envía notificación al usuario moroso |
| `HistorialMultasObserver` | Observer concreto | Persiste el evento de multa en el historial |

### Flujo de datos

```
1. Préstamo vence (fechaDevolucionEsperada < hoy y estado = ACTIVO)
         │
         ▼
2. GestorMultas.registrarVencimiento(prestamo)
         │
         ▼
3. GestorMultas.notificarObservers(multa)
         │
         ├──► ReporteMultasObserver.actualizar(multa)
         │       └── recalcula totales por usuario
         │
         ├──► NotificadorMultasObserver.actualizar(multa)
         │       └── envía alerta al usuario
         │
         └──► HistorialMultasObserver.actualizar(multa)
                 └── guarda en BD
```

### Criterios de notificación

| Evento | Disparador | Datos notificados |
|--------|-----------|-------------------|
| Vencimiento | `fechaDevolucionEsperada < hoy` | prestamoId, usuarioId, diasVencido |
| Devolución tardía | `fechaDevolucionReal > fechaDevolucionEsperada` | prestamoId, usuarioId, multaCalculada |
| Multa pagada | Actualización de `multasAcumuladas` | usuarioId, nuevoTotal |

### Implementación en frontend

El reporte se renderiza en `ReportesPage.jsx` consumiendo un endpoint que refleja los datos actualizados por los observers:

```
GET /reportes/multas/usuario
→ [
    { usuarioId: 1, nombre: "Juan", email: "juan@mail.com", totalMultas: 15000, prestamosMultados: 2 },
    { usuarioId: 2, nombre: "Ana", email: "ana@mail.com", totalMultas: 5000, prestamosMultados: 1 }
  ]
```

---

## 3. State — Reporte Libros Más Renovados

### Problema

Cada libro tiene un contador `vecesRenovado` que determina su categoría en el reporte. El **comportamiento visual y analítico** del libro en el reporte cambia según cuántas veces ha sido renovado (color, badge, recomendación). Con un enfoque procedural, esto llevaría a condicionales anidados difíciles de mantener.

```
Sin State:
if (veces == 0) { /* badge gris, sin destacar */ }
else if (veces <= 2) { /* badge azul, destacado medio */ }
else { /* badge rojo, muy destacado */ }
// ❌ Lógica dispersa, difícil de extender
```

### Solución: Patrón State

Cada nivel de renovación se modela como un **Estado** que encapsula el comportamiento asociado. El libro delega en su estado actual para determinar cómo aparece en el reporte.

```
Diagrama:
┌───────────────────────────────────┐
│    RenovacionState (interface)    │
├───────────────────────────────────┤
│ + getNombreEstado(): String      │
│ + getBadgeClass(): String        │
│ + getColorReporte(): String      │
│ + getRecomendacion(): String     │
│ + getOrdenPrioridad(): int       │
└──────────────┬────────────────────┘
               │
     ┌─────────┼─────────┐
     ▼         ▼         ▼
┌──────────┐ ┌────────┐ ┌──────────┐
│ Sin     │ │ Poca   │ │ Mucha    │
│ Renovacion│ │ Renovac│ │ Renovac  │
├──────────┤ ├────────┤ ├──────────┤
│ veces: 0 │ │ veces: │ │ veces:   │
│ badge:   │ │ 1-2    │ │ 3+       │
│ gris     │ │ badge: │ │ badge:   │
│          │ │ azul   │ │ rojo     │
│ orden: 3 │ │ orden: │ │ orden: 1 │
└──────────┘ │ 2      │ └──────────┘
              └────────┘
```

### Estructura de clases

| Clase | Rol | Descripción |
|-------|-----|-------------|
| `RenovacionState` | Interfaz State | Define el comportamiento según nivel de renovación |
| `SinRenovacion` | State concreto | 0 renovaciones — badge gris, orden 3 |
| `PocaRenovacion` | State concreto | 1-2 renovaciones — badge azul, orden 2 |
| `MuchaRenovacion` | State concreto | 3+ renovaciones — badge rojo, orden 1 |

### Transiciones de estado

```
         ┌──────────────┐
         │ SinRenovacion │  ← vecesRenovado = 0
         └──────┬───────┘
                │ renovar() → vecesRenovado = 1
                ▼
         ┌──────────────┐
         │ PocaRenovacion│  ← vecesRenovado entre 1 y 2
         └──────┬───────┘
                │ renovar() → vecesRenovado = 3
                ▼
         ┌──────────────┐
         │ MuchaRenovac. │  ← vecesRenovado >= 3
         └──────────────┘
```

### Integración con el reporte

El reporte `ReporteLibrosRenovados` itera sobre los préstamos, agrupa por libro y determina el `RenovacionState` de cada uno según su `vecesRenovado`. Luego:

1. Ordena los libros por `getOrdenPrioridad()` (MuchaRenovacion primero)
2. Asigna badge CSS según `getBadgeClass()`
3. Muestra recomendación según `getRecomendacion()`

```
GET /reportes/libros/renovados
→ {
    estados: [
      { nombre: "Mucha Renovación", libros: [ ... ], color: "rojo" },
      { nombre: "Poca Renovación", libros: [ ... ], color: "azul" },
      { nombre: "Sin Renovación", libros: [ ... ], color: "gris" }
    ]
  }
```

### Ejemplo de comportamiento por estado

| Estado | Badge | Posición en ranking | Recomendación |
|--------|-------|---------------------|---------------|
| `SinRenovacion` | Gris | Últimos | — |
| `PocaRenovacion` | Azul | Medios | Considerar extender plazo |
| `MuchaRenovacion` | Rojo | Primeros | Evaluar ampliar periodo de préstamo |

---

## 4. Strategy — Reporte Distribución por Formato

### Problema

La distribución de préstamos por formato (PDF, EPUB, MOBI) puede calcularse y presentarse de **múltiples formas**: porcentajes, valores absolutos, comparativas históricas. Cada variante es un algoritmo distinto pero todos resuelven el mismo problema: "¿cómo se distribuyen los préstamos entre formatos?". Sin Strategy, el código se llena de condicionales.

```
Sin Strategy:
if (calculo === "porcentual") { /* cálculo A */ }
else if (calculo === "absoluto") { /* cálculo B */ }
else if (calculo === "comparativo") { /* cálculo C */ }
// ❌ Violación de Open/Closed: hay que modificar la clase al añadir variantes
```

### Solución: Patrón Strategy

Cada algoritmo de distribución se encapsula en una **Estrategia** intercambiable. El reporte delega el cálculo en la estrategia activa, que puede cambiarse en tiempo de ejecución.

```
Diagrama:
┌──────────────────────────────────────┐
│   DistribucionStrategy (interface)   │
├──────────────────────────────────────┤
│ + getNombre(): String               │
│ + calcular(prestamos, libros):      │
│     List<FormatoData>               │
└──────────────┬───────────────────────┘
               │
     ┌─────────┼─────────┐
     ▼         ▼         ▼
┌────────────┐ ┌────────┐ ┌──────────────┐
│ Porcentual │ │Absoluto│ │ Comparativo  │
├────────────┤ ├────────┤ ├──────────────┤
│ Calcula    │ │ Cuenta │ │ Muestra      │
│ % de cada  │ │ raw    │ │ evolución    │
│ formato    │ │ por    │ │ respecto al  │
│ sobre total│ │ formato │ │ mes anterior │
└────────────┘ └────────┘ └──────────────┘

         Contexto:
    ┌─────────────────────────┐
    │ ReporteDistribucion     │
    ├─────────────────────────┤
    │ - strategy: Strategy    │
    │ + setStrategy(s)        │
    │ + generar(): Reporte    │
    └─────────────────────────┘
```

### Estructura de clases

| Clase | Rol | Descripción |
|-------|-----|-------------|
| `DistribucionStrategy` | Interfaz Strategy | Define el contrato para calcular distribución |
| `DistribucionPorcentual` | Strategy concreta | Calcula porcentaje de cada formato sobre el total |
| `DistribucionAbsoluta` | Strategy concreta | Cuenta el número bruto de préstamos por formato |
| `DistribucionComparativa` | Strategy concreta | Compara distribución actual vs. período anterior |
| `ReporteDistribucionFormato` | Contexto | Mantiene referencia a la Strategy y delega el cálculo |

### Flujo de datos

```
Frontend solicita: GET /reportes/distribucion?tipo=porcentual

1. ReporteController recibe parámetro "tipo"
         │
         ▼
2. ReporteService selecciona estrategia:
   - "porcentual"  → new DistribucionPorcentual()
   - "absoluto"    → new DistribucionAbsoluta()
   - "comparativo" → new DistribucionComparativa()
         │
         ▼
3. ReporteDistribucionFormato.setStrategy(strategy)
         │
         ▼
4. reporte.generar(prestamos, libros)
         │
         ▼
5. strategy.calcular(prestamos, libros)
         │
         ▼
6. → Resultado: { formato: "PDF", valor: 45, unidad: "%" }
                { formato: "EPUB", valor: 35, unidad: "%" }
                { formato: "MOBI", valor: 20, unidad: "%" }
```

### Ejemplo de resultados por estrategia

| Estrategia | Formato | Valor | Unidad |
|------------|---------|-------|--------|
| **Porcentual** | PDF | 45 | % |
| | EPUB | 35 | % |
| | MOBI | 20 | % |
| **Absoluta** | PDF | 90 | préstamos |
| | EPUB | 70 | préstamos |
| | MOBI | 40 | préstamos |
| **Comparativa** | PDF | +5% | vs mes anterior |
| | EPUB | -2% | vs mes anterior |
| | MOBI | +1% | vs mes anterior |

### Beneficio clave

Añadir una nueva estrategia (ej: `DistribucionPorRangoFecha`) solo requiere crear una nueva clase que implemente `DistribucionStrategy` — **no se modifica código existente** (Principio Open/Closed).

---

## 5. Command — Reporte Top Lectores

### Problema

Generar el ranking de "Top Lectores" implica múltiples pasos: filtrar préstamos, agrupar por usuario, ordenar por cantidad, y formatear resultado. Cada invocación encapsula una **solicitud completa** que debería poder encolarse, deshacerse o auditarse. Sin Command, la lógica está acoplada al controlador.

```
Sin Command:
ReporteController.generarTopLectores() {
    // 1. Obtener préstamos
    // 2. Filtrar y agrupar
    // 3. Ordenar
    // 4. Formatear
    // 5. Retornar
}
// ❌ El controller sabe demasiado
// ❌ No se puede encolar ni deshacer
```

### Solución: Patrón Command

Cada operación sobre el reporte es un objeto **Command** que encapsula la solicitud completa. Un **Invoker** puede ejecutar, encolar o deshacer comandos sin conocer los detalles de implementación.

```
Diagrama:
┌──────────────────────────────────────────────────┐
│             ReporteCommand (interface)           │
├──────────────────────────────────────────────────┤
│ + ejecutar(): Resultado                         │
│ + deshacer(): void                              │
│ + getNombre(): String                           │
└──────────────────────┬───────────────────────────┘
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
┌─────────────────┐ ┌──────────┐ ┌──────────────────┐
│ GenerarTop      │ │ Exportar │ │ FiltrarTop       │
│ LectoresCommand │ │ Top      │ │ LectoresCommand  │
├─────────────────┤ │ Lectores │ ├──────────────────┤
│ - usuarioId     │ │ Command  │ │ - filtro         │
│ - limite        │ ├──────────┤ │ - criterio       │
│ + ejecutar():   │ │ - formato│ │ + ejecutar():    │
│   Ranking       │ │ +ejecutar│ │   Ranking        │
│ + deshacer():   │ │ : archivo│ │ + deshacer():    │
│   elimina cache │ └──────────┘ │   restaura       │
└─────────────────┘              └──────────────────┘

Invoker:
┌──────────────────────────┐
│  ReporteInvoker          │
├──────────────────────────┤
│ - historial: List<Cmd>   │
│ + ejecutarComando(cmd)   │
│ + deshacerUltimo()       │
│ + obtenerHistorial()     │
└──────────────────────────┘

Receiver:
┌──────────────────────────┐
│  ReporteService          │
├──────────────────────────┤
│ + calcularTopLectores()  │
│ + exportarReporte()      │
│ + filtrarPorCriterio()   │
└──────────────────────────┘
```

### Estructura de clases

| Clase | Rol | Descripción |
|-------|-----|-------------|
| `ReporteCommand` | Interfaz Command | Define `ejecutar()` y `deshacer()` |
| `GenerarTopLectoresCommand` | Command concreto | Genera el ranking con límite configurable |
| `ExportarTopLectoresCommand` | Command concreto | Exporta el ranking a CSV/PDF |
| `FiltrarTopLectoresCommand` | Command concreto | Filtra el ranking por criterio (fecha, mínimo préstamos) |
| `ReporteInvoker` | Invoker | Ejecuta comandos, mantiene historial, permite deshacer |
| `ReporteService` | Receiver | Contiene la lógica real de cálculo del reporte |

### Flujo de datos

```
1. Usuario solicita: "Top 10 lectores del mes"

2. ReporteController (Invoker)
   └── ejecutarComando( new GenerarTopLectoresCommand(10, "mensual") )
         │
         ▼
3. ReporteInvoker
   ├── guarda en historial (para posible deshacer)
   └── command.ejecutar()
         │
         ▼
4. GenerarTopLectoresCommand.ejecutar()
   ├── llama a ReporteService.calcularTopLectores(10, "mensual")
   └── retorna RankingDTO
         │
         ▼
5. → Resultado: [
       { usuario: "Ana", email: "ana@mail.com", total: 12, esPremium: true },
       { usuario: "Luis", email: "luis@mail.com", total: 8, esPremium: false },
       ...
     ]
```

### Características habilitadas por Command

| Característica | Descripción | Cómo se logra |
|----------------|-------------|---------------|
| **Historial** | Cada comando ejecutado se registra | `ReporteInvoker.historial.add(cmd)` |
| **Deshacer** | Revertir la última generación de reporte | `cmd.deshacer()` elimina el cache generado |
| **Encolar** | Ejecutar reportes en lote | Cola de comandos en `ReporteInvoker` |
| **Auditoría** | Quién generó qué reporte y cuándo | `cmd.getNombre() + timestamp` |
| **Parametrización** | Mismo comando con distintos parámetros | Constructor acepta límite, filtro, formato |

### Ejemplo de deshacer

```
1. GenerarTopLectoresCommand(10, "mensual") → genera ranking, almacena en cache
2. Usuario se da cuenta que eligió mal el período
3. ReporteInvoker.deshacerUltimo()
4. → elimina el cache del ranking generado
5. → permite regenerar con nuevos parámetros
```

---

## 6. Resumen de Integración

### Mapa completo de nuevos reportes

| Reporte | Patrón | Endpoint propuesto | Cálculo |
|---------|--------|-------------------|---------|
| Multas por Usuario | **Observer** | `GET /reportes/multas/usuario` | Suma `multasAcumuladas` por usuario |
| Libros Más Renovados | **State** | `GET /reportes/libros/renovados` | Agrupa por `vecesRenovado` |
| Distribución por Formato | **Strategy** | `GET /reportes/distribucion?tipo=porcentual` | Cuenta préstamos por `BookFormato` |
| Top Lectores | **Command** | `GET /reportes/lectores/top?limite=10` | Cuenta préstamos por `usuarioId` |

### Relación con patrones existentes

```
Patrones existentes en el proyecto         Nuevos patrones de comportamiento
───────────────────────────────           ────────────────────────────────

Composite (reportes)          ◄────►      Observer (multas se actualizan solas)
  (estructura de árbol)                     (notificación de cambios)

Bridge (acceso premium/gratis) ◄────►      State (renovación cambia comportamiento)
  (separar abstracción)                      (estados determinan display)

Decorator (notificaciones)    ◄────►      Strategy (algoritmo intercambiable)
  (añadir responsabilidades)                 (fórmula de distribución)

Prototype (recomendaciones)   ◄────►      Command (solicitud como objeto)
  (clonar plantillas)                        (ejecución parametrizable)
```

### Principios SOLID reforzados

| Principio | Cómo lo cumple |
|-----------|----------------|
| **SRP** — Responsabilidad Única | Cada patrón tiene una responsabilidad: Observer notifica, State varía comportamiento, Strategy encapsula algoritmo, Command encapsula solicitud |
| **OCP** — Open/Closed | Nuevas estrategias o estados se añaden sin modificar código existente |
| **LSP** — Liskov | Todas las estrategias/estados son intercambiables vía interfaz común |
| **ISP** — Segregación | Interfaces pequeñas y específicas para cada patrón |
| **DIP** — Inversión | Dependencias apuntan a interfaces (`DistribucionStrategy`), no a concretas |

---

## 7. Conclusión

La aplicación de estos 4 patrones de comportamiento permite:

1. **Observer**: Reporte de multas siempre actualizado sin polling
2. **State**: Categorización clara de libros según su nivel de renovación
3. **Strategy**: Múltiples formas de visualizar la misma distribución de datos
4. **Command**: Operaciones sobre reportes desacopladas, encolables y deshacibles

Cada patrón se integra con la arquitectura hexagonal existente y respeta los principios SOLID, manteniendo el código extensible y mantenible.
