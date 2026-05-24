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
frontend/src/
├── services/
│   ├── MultasObserver.js             ← Observer (Multas)
│   ├── MultasObserver.test.js
│   ├── LibrosRenovadosState.js       ← State (Renovados)
│   ├── LibrosRenovadosState.test.js
│   ├── DistribucionFormatoStrategy.js ← Strategy (Formato)
│   └── DistribucionFormatoStrategy.test.js
├── pages/
│   └── ReportesPage.jsx              ← Consume todos los patrones
└── css/
    └── App.css
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

### Solución: Patrón Observer (Frontend-Only)

El gestor de multas (`MultasSubject`) actúa como **Subject** que notifica a los **Observers** cuando ocurren eventos relevantes (vencimiento, devolución tardía). Todo el patrón se ejecuta en el frontend después de cargar los datos desde `GET /prestamos`.

### Estructura de clases (Frontend)

Archivo: `frontend/src/services/MultasObserver.js`

| Clase/Instancia | Rol | Descripción |
|-----------------|-----|-------------|
| `MultasSubject` | Subject | Clase base: `agregarObserver()`, `eliminarObserver()`, `notificarObservers()`, `calcularMultas()` |
| `gestorMultas` | Instancia Singleton de MultasSubject | Detecta vencimientos y devoluciones, notifica a observers |
| `ReporteMultasObserver` | Observer concreto | Acumula multas por usuario y genera ranking |
| `reporteMultasObserver` | Instancia Singleton de ReporteMultasObserver | Usado en ReportesPage.jsx para obtener datos |
| `NotificadorObserver` | Observer concreto | Imprime en consola cada multa detectada |
| `notificadorObserver` | Instancia Singleton de NotificadorObserver | Log de diagnóstico |

### Flujo de datos (Frontend)

```
1. ReportesPage.loadAllData()
   ├── LoanService.getAll()           → prestamos[]
   ├── UserService.getAll()           → users[]
   └── BookService.getAll()           → books[]
         │
         ▼
2. gestorMultas.calcularMultas(prestamos, users[])
         │
         ├── Itera préstamos vencidos → crea evento VENCIMIENTO
         ├── Itera devoluciones tardías → crea evento DEVOLUCION_TARDIA
         │
         ▼
3. gestorMultas.notificarObservers(evento)
         │
         ├──► reporteMultasObserver.actualizar(evento)
         │       └── acumula en mapa: usuarioId → { totalMultas, cantidadPrestamos, eventos[] }
         │
         └──► notificadorObserver.actualizar(evento)
                 └── console.log("[Multas]", evento)

4. setMultas(reporteMultasObserver.obtenerMultas(usersMap))
         │
         ▼
5. Render: tabla con filtro por email
```

### Tests (19 pruebas)

Archivo: `frontend/src/services/MultasObserver.test.js`

| # | Test | Categoría |
|---|------|-----------|
| 1-3 | Subject: agregar/eliminar/notificar | Subject |
| 4-9 | Cálculo: detección de vencimientos | Cálculo |
| 10-16 | Observers: agregación por usuario | Agregación |
| 17 | Notificador: console.log | Notificador |
| 18-19 | Integración: flujo completo | Integración |

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

### Solución: Patrón State (Frontend-Only)

Cada nivel de renovación se modela como un **Estado** que encapsula el comportamiento asociado. El `ContextoRenovacion` delega en su estado actual para determinar cómo aparece el libro en el reporte.

### Estructura de clases (Frontend)

Archivo: `frontend/src/services/LibrosRenovadosState.js`

| Clase/Función | Rol | Descripción |
|---------------|-----|-------------|
| `SinRenovacion` | State concreto | 0 renovaciones — badge gris, orden 3 |
| `PocaRenovacion` | State concreto | 1-2 renovaciones — badge azul, orden 2, recomendación |
| `MuchaRenovacion` | State concreto | 3+ renovaciones — badge rojo, orden 1, recomendación |
| `StateFactory` | Factory | Crea el estado correcto según el número de renovaciones |
| `ContextoRenovacion` | Contexto | Mantiene el estado y delega `getNombreEstado()`, `getBadgeClass()`, `getOrdenPrioridad()`, `getRecomendacion()` |
| `agruparPorLibro()` | Utilidad | Agrupa préstamos por libro, calcula renovaciones totales, asigna estado y ordena por prioridad |

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

El reporte en `ReportesPage.jsx` (sección "Libros Más Renovados"): itera sobre los préstamos, agrupa por libro y determina el `ContextoRenovacion` de cada uno según su `vecesRenovado`. Luego:

1. Ordena los libros por `getOrdenPrioridad()` (MuchaRenovacion primero)
2. Asigna badge CSS según `getBadgeClass()`
3. Muestra recomendación según `getRecomendacion()`

```
Datos fuente: GET /prestamos → cada préstamo tiene "vecesRenovado"

Agrupación frontend:
  renovaciones = agruparPorLibro(loans, books)
  → [
      { libroId, totalRenovaciones, cantidadPrestamos,
        titulo, autor, formato,
        estado: "Mucha renovación",
        badgeClass: "state-badge--alta",
        ordenPrioridad: 1,
        recomendacion: "Evaluar ampliar periodo de préstamo estándar" },
      ...
    ]
```

### Tests (12 pruebas)

Archivo: `frontend/src/services/LibrosRenovadosState.test.js`

| # | Test | Categoría |
|---|------|-----------|
| 1-5 | Factory: creación de estados (0,1,2,3,5) | StateFactory |
| 6-8 | Estados: propiedades específicas (badge, orden, recomendación) | Comportamiento |
| 9-11 | Contexto: delegación correcta | Contexto |
| 12-17 | agruparPorLibro: suma, orden, desempate, defaults, vacío | Integración |

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

### Solución: Patrón Strategy (Frontend-Only)

Cada algoritmo de distribución se encapsula en una **Estrategia** intercambiable. El reporte delega el cálculo en la estrategia activa, que el usuario puede cambiar mediante pestañas en la UI.

### Estructura de clases (Frontend)

Archivo: `frontend/src/services/DistribucionFormatoStrategy.js`

| Clase/Función | Rol | Descripción |
|---------------|-----|-------------|
| `EstrategiaPorcentual` | Strategy concreta | Calcula % de cada formato sobre el total |
| `EstrategiaAbsoluta` | Strategy concreta | Cuenta el número bruto de préstamos por formato |
| `ContextoDistribucion` | Contexto | Mantiene la estrategia activa, delega `ejecutar()` |
| `crearStrategy(tipo)` | Factory | Crea estrategia según tipo: "porcentual", "absoluta" |
| `calcularDistribucion()` | Utilidad | Función conveniente que crea estrategia, contexto y ejecuta |

### Flujo de datos (Frontend)

```
1. ReportesPage carga prestamos[] y books[]
         │
         ▼
2. Usuario hace clic en pestaña: "Porcentual" | "Absoluta"
         │
         ▼
3. setTipoDistribucion(tipo) → renderiza con calcularDistribucion()
   └── calcularDistribucion(loansFiltrados, books, tipo)
         │
         ├── crearStrategy(tipo) → new EstrategiaPorcentual()
         ├── new ContextoDistribucion(strategy)
         └── ctx.ejecutar(loansFiltrados, books)
               │
               ▼
4. agruparPorFormato(prestamos, libros):
   └── Cruza préstamo → libroId → formato
   └── Cuenta por formato, ordena: PDF, EPUB, MOBI, FISICO
         │
         ▼
5. Según estrategia:
   Porcentual  → { formato, cantidad, porcentaje }
   Absoluta    → { formato, cantidad }
         │
         ▼
6. Render: tabla + barras de distribución + filtro de fecha
```

### Tests (11 pruebas)

Archivo: `frontend/src/services/DistribucionFormatoStrategy.test.js`

| # | Test | Categoría |
|---|------|-----------|
| 1-2 | agruparPorFormato: agrupa correctamente, libro sin catálogo | Agrupación |
| 3-5 | Porcentual: % correctos, 100% un formato, vacío | Estrategia |
| 6 | Absoluta: conteo exacto | Estrategia |
| 7-8 | Contexto: ejecuta estrategia, cambiar estrategia cambia resultado | Contexto |
| 9-10 | Factory: crear por tipo, tipo inválido lanza error | Factory |
| 11 | Integración: flujo completo orden descendente | Integración |

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
│             ReporteCommand (interface / duck typing) │
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

| Reporte | Patrón | Fuente de datos | Cálculo |
|---------|--------|----------------|---------|
| Multas por Usuario | **Observer** | `GET /prestamos` (frontend) | Suma días vencidos × $1/día por usuario |
| Libros Más Renovados | **State** | `GET /prestamos` (frontend) | Agrupa por `vecesRenovado` por libro |
| Distribución por Formato | **Strategy** | `GET /prestamos` + `GET /books` (frontend) | Cuenta préstamos por `BookFormato` |
| Top Lectores | **Command** | `GET /prestamos` + `GET /users` (frontend) | Cuenta préstamos por `usuarioId` |

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
| **DIP** — Inversión | Dependencias apuntan a abstracciones (`DistribucionStrategy` por duck typing), no a concretas |

---

## 7. Conclusión

La aplicación de estos 4 patrones de comportamiento permite:

1. **Observer**: Reporte de multas siempre actualizado sin polling
2. **State**: Categorización clara de libros según su nivel de renovación
3. **Strategy**: Múltiples formas de visualizar la misma distribución de datos
4. **Command**: Operaciones sobre reportes desacopladas, encolables y deshacibles

Cada patrón se integra con la arquitectura hexagonal existente y respeta los principios SOLID, manteniendo el código extensible y mantenible.
