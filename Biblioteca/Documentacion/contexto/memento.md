# Patrón Memento — Reporte Premium vs Gratuitos

## 1. Problema

### Situación Actual

La sección "Premium vs Gratuitos" del reporte calcula en vivo cuántos usuarios son premium, cuántos gratuitos, el porcentaje y el ingreso estimado. Estos cálculos se realizan directamente en el componente sin posibilidad de preservar o restaurar estados anteriores.

```
Sin Memento:
const premiumUsers = users.filter(u => u.suscripcionActiva);
const freeUsers = users.filter(u => !u.suscripcionActiva);
const pctPremium = Math.round((premiumUsers.length / totalUsers) * 100);
const ingresoPremium = premiumUsers.length * PRECIO_PREMIUM;
// ❌ No hay forma de restaurar un estado anterior
// ❌ Los cálculos se pierden al re-renderizar
// ❌ No se pueden comparar estados en el tiempo
```

**Problemas identificados:**
- ❌ El estado del reporte (métrica calculada) no se puede preservar ni restaurar
- ❌ No hay historial de estados previos
- ❌ Difícil implementar funcionalidades como "volver atrás" o comparar con datos anteriores

---

## 2. Justificación del Patrón Memento

### ¿Qué es el Patrón Memento?

Memento es un patrón de **comportamiento** que permite capturar y externalizar el estado interno de un objeto sin violar su encapsulamiento, de modo que el objeto pueda restaurarse a ese estado más tarde.

```
Concepto:
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│  Originator  │      │   Memento    │      │  Caretaker   │
│              │─────►│              │─────►│              │
│ + crear()    │ crea │  - estado    │      │ + guardar()  │
│ + restaurar()│      │              │      │ + deshacer() │
└──────────────┘      └──────────────┘      └──────────────┘
```

### ¿Por qué Memento para Premium vs Gratuitos?

| Razón | Explicación |
|-------|-------------|
| **Preservación** | Se pueden guardar snapshots del estado del reporte en diferentes momentos |
| **Restauración** | Permite volver a un estado anterior del reporte |
| **Encapsulación** | El estado interno del originator no se expone directamente |
| **Independencia** | El caretaker gestiona los mementos sin conocer su estructura interna |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — Memento (PremiumMemento.js)" {
  class PremiumMemento {
    - premiumUsers: Number
    - freeUsers: Number
    - totalUsers: Number
    - pctPremium: Number
    - ingresoPremium: Number
    + getEstado(): Object
  }

  class PremiumOriginator {
    - premiumUsers: Number
    - freeUsers: Number
    - totalUsers: Number
    - pctPremium: Number
    - ingresoPremium: Number
    + calcular(users): void
    + guardarMemento(): PremiumMemento
    + restaurarMemento(memento): void
    + getPremiumUsers(): Number
    + getFreeUsers(): Number
    + getPctPremium(): Number
    + getIngresoPremium(): Number
  }

  class PremiumCaretaker {
    - mementos: PremiumMemento[]
    + guardar(memento): void
    + deshacer(): PremiumMemento
    + limpiar(): void
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + renderSection()
  }
}

PremiumOriginator ..> PremiumMemento : crea
PremiumCaretaker o-> PremiumMemento : gestiona
ReportesPage ..> PremiumOriginator : usa
ReportesPage ..> PremiumCaretaker : usa

@enduml
```

### Descripción de la Jerarquía

| Clase | Archivo | Rol |
|-------|---------|-----|
| `PremiumMemento` | `PremiumMemento.js` | Objeto inmutable que almacena una foto del estado del originator |
| `PremiumOriginator` | `PremiumMemento.js` | Calcula los datos del reporte y puede crear/restaurar mementos |
| `PremiumCaretaker` | `PremiumMemento.js` | Gestiona el historial de mementos (guardar, deshacer, limpiar) |

### Métodos

| Clase | Método | Descripción |
|-------|--------|-------------|
| `PremiumOriginator` | `calcular(users)` | Procesa el arreglo de usuarios y actualiza el estado interno |
| `PremiumOriginator` | `guardarMemento()` | Crea un snapshot del estado actual y lo retorna |
| `PremiumOriginator` | `restaurarMemento(memento)` | Restaura el estado desde un memento previo |
| `PremiumCaretaker` | `guardar(memento)` | Agrega un memento al historial |
| `PremiumCaretaker` | `deshacer()` | Retorna el memento anterior (LIFO) |

---

## 4. Flujo de Datos

```
ReportesPage — loadAllData()
         │
         ▼
  const originator = new PremiumOriginator();
  const caretaker = new PremiumCaretaker();
         │
         ▼
  originator.calcular(users);
  caretaker.guardar(originator.guardarMemento());
         │
         ▼
  const premiumUsers = originator.getPremiumUsers();
  const freeUsers = originator.getFreeUsers();
  const pctPremium = originator.getPctPremium();
  const ingresoPremium = originator.getIngresoPremium();
         │
         ▼
Render: 2 tarjetas (Premium / Gratuitos) + barra de progreso
         │
         ▼
Si se necesita deshacer (nuevos datos):
  const anterior = caretaker.deshacer();
  if (anterior) originator.restaurarMemento(anterior);
  // El render se actualiza con los valores anteriores
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── PremiumMemento.js                      ← Implementación Memento

frontend/src/pages/
└── ReportesPage.jsx                       ← Consume el patrón (sección 1)

frontend/src/services/
└── PremiumMemento.test.js                 ← Tests
```

### Endpoints REST

No se requieren endpoints adicionales. El patrón opera sobre los datos de `GET /users`.

---

## 6. Comportamiento del Memento

| Acción | Método | Descripción |
|--------|--------|-------------|
| Calcular | `originator.calcular(users)` | Filtra usuarios premium/free y calcula métricas |
| Guardar snapshot | `caretaker.guardar(originator.guardarMemento())` | Almacena el estado actual en el historial |
| Deshacer | `caretaker.deshacer()` | Recupera el snapshot más reciente |
| Restaurar | `originator.restaurarMemento(memento)` | Restaura el originator al estado del memento |

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Command** | Command puede usar Memento para implementar deshacer en operaciones |
| **State** | State puede usar Memento para guardar y restaurar estados anteriores |
| **Observer** | Observer puede notificar al caretaker para guardar un memento automáticamente |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/PremiumMemento.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `calcular_con_usuarios_mixtos` | Originator | 2 premium, 3 free → valores correctos |
| 2 | `calcular_todos_premium` | Originator | 100% premium |
| 3 | `calcular_todos_free` | Originator | 0% premium |
| 4 | `calcular_sin_usuarios` | Originator | Usuarios vacío → 0 en todas las métricas |
| 5 | `guardarMemento_retorna_estado_correcto` | Memento | Snapshot contiene valores actuales |
| 6 | `restaurarMemento_restaura_estado` | Memento | Restaurar desde snapshot recupera valores |
| 7 | `ciclo_guardar_y_restaurar` | Integración | Calcular → guardar → calcular otros datos → restaurar → valores originales |
| 8 | `caretaker_guarda_y_deshace` | Caretaker | guardar() + deshacer() retorna el memento correcto |
| 9 | `caretaker_deshacer_sin_mementos` | Caretaker | deshacer() sin mementos retorna null |
| 10 | `caretaker_limpia_historial` | Caretaker | limpiar() vacía el historial |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | `PremiumOriginator` calcula; `PremiumMemento` almacena; `PremiumCaretaker` gestiona — cada uno tiene una responsabilidad |
| **OCP** | Nuevos tipos de snapshot se agregan sin modificar el originator ni el caretaker |
| **LSP** | Los mementos son intercambiables y pueden ser gestionados por el caretaker |
| **ISP** | Cada clase expone solo los métodos que necesita |
| **DIP** | El caretaker depende de `PremiumMemento` (abstracción), no de implementaciones concretas del estado |
