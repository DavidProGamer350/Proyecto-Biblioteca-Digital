# Patrón Command — Reporte Top Lectores

## 1. Problema

### Situación Actual

El reporte "Top Lectores" debe mostrar el ranking de usuarios con más préstamos. Cada generación del reporte encapsula múltiples pasos: filtrar por fecha, agrupar por usuario, ordenar, y presentar. Sin Command, la lógica queda acoplada al componente y no se puede auditar ni reutilizar.

```
Sin Command:
ReportesPage.generarTopLectores() {
  filtrarPorFecha(desde, hasta);
  agruparPorUsuario();
  ordenarDescendente();
  setResultado();
}
// ❌ Acoplado al componente
// ❌ Sin historial ni auditoría
// ❌ Difícil de testear
```

**Problemas identificados:**
- ❌ La lógica de generación del ranking está mezclada con el UI
- ❌ No hay registro de qué reportes se generaron y cuándo
- ❌ No se puede deshacer o regenerar

---

## 2. Justificación del Patrón Command

### ¿Qué es el Patrón Command?

Command es un patrón de **comportamiento** que convierte una solicitud en un objeto independiente. Este objeto encapsula toda la información necesaria para ejecutar la acción, permitiendo parametrizar, encolar, registrar y deshacer operaciones.

```
Concepto:
┌──────────────┐     ┌─────────────────┐     ┌──────────────┐
│   Invoker    │────►│   Command       │────►│   Receiver   │
│  (botón UI)  │     │  + ejecutar()  │     │  (lógica)    │
└──────────────┘     └─────────────────┘     └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │   Historial  │
                     │  (log/undo)  │
                     └──────────────┘
```

### ¿Por qué Command para Top Lectores?

| Razón | Explicación |
|-------|-------------|
| **Encapsulación** | Cada solicitud de ranking es un objeto completo |
| **Historial** | Se registra cada comando ejecutado |
| **Desacoplamiento** | El UI no sabe cómo se genera el ranking |
| **Testeabilidad** | El comando se prueba independientemente del UI |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — Command (TopLectoresCommand.js)" {
  class ReporteCommand {
    + getNombre(): String
    + ejecutar(): Array
    + getTimestamp(): String
  }

  class GenerarTopLectoresCommand {
    - prestamos: Array
    - usersMap: Object
    - desde: String
    - hasta: String
    + getNombre(): "Top Lectores"
    + ejecutar(): Array
    + getTimestamp(): String
  }

  class TopLectoresResult {
    + usuarioId: Number
    + nombre: String
    + email: String
    + totalPrestamos: Number
    + esPremium: Boolean
  }

  class ComandoHistorial {
    - historial: Array
    + ejecutar(cmd): Array
    + getHistorial(): Array
    + limpiar(): void
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + ejecutarComando()
    + renderSection()
  }
}

ComandoHistorial o-> ReporteCommand : historial
ReporteCommand <|.. GenerarTopLectoresCommand : duck typing
GenerarTopLectoresCommand ..> TopLectoresResult : genera
ReportesPage ..> ComandoHistorial : usa
ReportesPage ..> GenerarTopLectoresCommand : crea

@enduml
```

### Descripción de la Jerarquía

| Clase | Archivo | Rol |
|-------|---------|-----|
| `ReporteCommand` | conceptual (duck typing) | Contrato: `getNombre()`, `ejecutar()`, `getTimestamp()` |
| `GenerarTopLectoresCommand` | `TopLectoresCommand.js` | Command concreto: filtra préstamos por fecha, agrupa por usuario, ordena por cantidad descendente |
| `TopLectoresResult` | conceptual (objeto de retorno) | DTO: usuarioId, nombre, email, totalPrestamos, esPremium |
| `ComandoHistorial` | `TopLectoresCommand.js` | Invoker: ejecuta comandos, mantiene historial, permite listar ejecuciones |

---

## 4. Flujo de Datos

```
ReportesPage.jsx — loadAllData()
         │
         ▼
Carga loans[], users[], books[]
         │
         ▼
Sección Top Lectores:
  │
  ├── Usuario ajusta filtro de fecha (desde/hasta)
  │
  ├── Al cambiar filtro:
  │     │
  │     ├── new GenerarTopLectoresCommand(loansFiltrados, usersMap, desde, hasta)
  │     │
  │     ├── historial.ejecutar(cmd)
  │     │     ├── cmd.ejecutar()
  │     │     │     ├── Filtra préstamos por rango de fecha
  │     │     │     ├── Agrupa por usuarioId, cuenta total
  │     │     │     ├── Resuelve nombre y email desde usersMap
  │     │     │     └── Ordena descendente por totalPrestamos
  │     │     │
  │     │     ├── cmd → guarda en historial[]
  │     │     │
  │     │     └── Retorna resultados
  │     │
  │     └── setResultado(resultados)
  │
  └── Render: tabla con ranking + medallas
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── TopLectoresCommand.js                 ← Implementación Command

frontend/src/pages/
└── ReportesPage.jsx                      ← Consume el patrón Command

frontend/src/services/
└── TopLectoresCommand.test.js            ← Tests
```

### Datos fuente

```
GET /prestamos → [{ usuarioId, fechaPrestamo, ... }]
GET /users     → [{ id, name, email, suscripcionActiva }]

Cálculo: filtrar por fecha → agrupar por usuarioId → contar → ordenar → resolver nombres
```

---

## 6. Comportamiento del Command

| Acción | Comando | Resultado |
|--------|---------|-----------|
| Generar ranking | `GenerarTopLectoresCommand` | Array de `TopLectoresResult` ordenado |
| Filtrar por fecha | Parámetros `desde`/`hasta` en el comando | Resultados filtrados |
| Ver historial | `ComandoHistorial.getHistorial()` | Lista de comandos ejecutados con timestamp |

Cada vez que se ejecuta un comando:
1. Se calcula el ranking con los filtros actuales
2. Se almacena el comando en el historial con timestamp
3. El historial permite auditar qué reportes se generaron

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Observer** | Observer notifica cambios; Command encapsula la acción de generar el reporte |
| **State** | State clasifica libros; Command gestiona la ejecución del ranking |
| **Strategy** | Strategy define algoritmos; Command encapsula solicitudes completas |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/TopLectoresCommand.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `ejecutar_retorna_ranking_ordenado` | Comando | 3 usuarios, orden descendente por total |
| 2 | `ejecutar_con_filtro_fecha` | Comando | Solo préstamos dentro del rango |
| 3 | `ejecutar_sin_prestamos_retorna_vacio` | Comando | Array vacío de préstamos |
| 4 | `ejecutar_resuelve_nombre_email` | Comando | Nombre y email desde usersMap |
| 5 | `ejecutar_usuario_sin_datos_mapa` | Comando | UsuarioId sin entrada en mapa → "Desconocido" |
| 6 | `ejecutar_marca_esPremium` | Comando | suscripcionActiva → esPremium true |
| 7 | `getNombre_retorna_Top Lectores` | Comando | Nombre descriptivo del comando |
| 8 | `getTimestamp_retorna_fecha` | Comando | Timestamp ISO al ejecutar |
| 9 | `historial_ejecutar_guarda_comando` | Historial | Comando aparece en historial tras ejecutar |
| 10 | `historial_ejecutar_multiples_comandos` | Historial | Múltiples ejecuciones se acumulan |
| 11 | `historial_limpiar_vacia_historial` | Historial | limpiar() deja el historial vacío |
| 12 | `historial_sin_ejecuciones_vacio` | Historial | Historial recién creado está vacío |
| 13 | `flujo_completo_filtrar_y_ordenar` | Integración | Filtro por fecha + ranking correcto + historial |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | Cada comando tiene una única responsabilidad: generar el ranking |
| **OCP** | Nuevos comandos se agregan sin modificar el invoker ni otros comandos |
| **LSP** | Todos los comandos cumplen el mismo contrato (duck typing) y son intercambiables |
| **ISP** | Cada comando solo expone los métodos que necesita |
| **DIP** | `ComandoHistorial` depende de la abstracción `ReporteCommand` (duck typing) |
