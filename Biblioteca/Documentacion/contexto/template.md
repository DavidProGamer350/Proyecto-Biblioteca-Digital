# Patrón Template Method — Reporte Libros Olvidados

## 1. Problema

### Situación Actual

En la biblioteca digital, el catálogo contiene libros que nunca han sido prestados o que han tenido muy poca rotación. Identificar estos libros es útil para decisiones de colección, pero no existe un reporte que los liste.

Actualmente, para generar cualquier reporte de libros hay que repetir pasos comunes: obtener el catálogo completo, cruzar con préstamos, filtrar, ordenar y presentar. Sin un patrón, cada nuevo reporte duplica esta estructura.

```
Sin Template Method:
ReporteNuncaPrestados() {
  obtenerLibros();
  cruzarConPrestamos();
  filtrarNoPrestados();
  ordenar();
  presentar();
}

ReportePocoPrestados() {
  obtenerLibros();        // ← duplicado
  cruzarConPrestamos();   // ← duplicado
  filtrarPocoPrestados();
  ordenar();              // ← duplicado
  presentar();            // ← duplicado
}
// ❌ Código duplicado
// ❌ Cambiar un paso común implica modificar N reportes
// ❌ Difícil mantener consistencia entre reportes
```

**Problemas identificados:**
- ❌ La estructura del reporte se repite en cada variante
- ❌ Cambiar el orden o añadir un paso requiere modificar todas las implementaciones
- ❌ No hay un contrato claro que garantice que todos los reportes sigan la misma secuencia

---

## 2. Justificación del Patrón Template Method

### ¿Qué es el Patrón Template Method?

Template Method es un patrón de **comportamiento** que define el esqueleto de un algoritmo en una clase base, delegando pasos específicos a las subclases. Permite reutilizar la estructura común mientras las subclases solo implementan las partes variables.

```
Concepto:
┌─────────────────────────────────────────────┐
│            ClaseBase (Abstract)              │
├─────────────────────────────────────────────┤
│ + templateMetodo() {                        │
│     paso1();        // concreto             │
│     paso2();        // abstracto ⬅          │
│     paso3();        // concreto (hook)      │
│     paso4();        // abstracto ⬅          │
│   }                                         │
└─────────────────────────────────────────────┘
          ▲                    ▲
          │ implementa         │ implementa
┌─────────┴──────────┐ ┌──────┴──────────┐
│   ClaseConcretaA   │ │  ClaseConcretaB  │
│  + paso2() {...}   │ │ + paso2() {...}  │
│  + paso4() {...}   │ │ + paso4() {...}  │
└────────────────────┘ └──────────────────┘
```

### ¿Por qué Template Method para Libros Olvidados?

| Razón | Explicación |
|-------|-------------|
| **Estructura reutilizable** | Todos los reportes de libros comparten: obtener catálogo, cruzar con préstamos, filtrar, ordenar, presentar |
| **Open/Closed** | Nuevo reporte solo implementa los pasos variables sin alterar la estructura |
| **Consistencia** | Todos los reportes siguen la misma secuencia garantizada por la clase base |
| **No duplicación** | Los pasos comunes se escriben una sola vez en la clase base |

---

## 3. Diagrama de Clases (PlantUML)

```
@startuml
package "Frontend — Template (LibrosOlvidadosTemplate.js)" {
  class ReporteLibrosBase {
    # prestamos: Array
    # libros: Array
    # usersMap: Object
    + ReporteLibrosBase(prestamos, libros, usersMap)
    + generar(): Array
    # obtenerLibros(): Array
    # obtenerPrestamos(): Array
    # filtrarLibros(libros, prestamos): Array
    # ordenarLibros(librosFiltrados): Array
    # enriquecerLibros(librosOrdenados): Array
  }

  class LibrosNuncaPrestados {
    + getNombre(): "Nunca prestados"
    # filtrarLibros(libros, prestamos): Array
  }
}

package "Contexto (ReportesPage.jsx)" {
  class ReportesPage {
    + renderSection()
  }
}

ReporteLibrosBase <|-- LibrosNuncaPrestados : duck typing
ReportesPage ..> LibrosNuncaPrestados : crea y usa

@enduml
```

### Descripción de la Jerarquía

| Clase | Archivo | Rol |
|-------|---------|-----|
| `ReporteLibrosBase` | `LibrosOlvidadosTemplate.js` | Clase base con template method `generar()` que define el esqueleto: obtener libros → filtrar → ordenar → enriquecer |
| `LibrosNuncaPrestados` | `LibrosOlvidadosTemplate.js` | Subclase concreta: filtra libros cuyo `id` no aparece en ningún préstamo |

### Métodos del Template

| Método | Tipo | Descripción |
|--------|------|-------------|
| `generar()` | template (concreto) | Esqueleto del algoritmo. Llama a los pasos en orden |
| `obtenerLibros()` | concreto | Retorna `this.libros` (recibido en constructor) |
| `obtenerPrestamos()` | concreto | Retorna `this.prestamos` (recibido en constructor) |
| `filtrarLibros()` | **abstracto** | Cada subclase decide qué libros conservar |
| `ordenarLibros()` | concreto (hook) | Ordena por defecto por título ascendente. Subclases pueden sobreescribir |
| `enriquecerLibros()` | concreto | Agrega índice secuencial a cada libro |

---

## 4. Flujo de Datos

```
ReportesPage.jsx — loadAllData()
         │
         ▼
Carga loans[], users[], books[]
         │
         ▼
const template = new LibrosNuncaPrestados(loans, Object.values(booksMap));
const resultado = template.generar();
         │
         ▼
┌─────────────────────────────────────────────┐
│   ReporteLibrosBase.generar()               │
│                                             │
│   1. obtenerLibros()                        │
│      → retorna todos los libros             │
│                                             │
│   2. obtenerPrestamos()                     │
│      → retorna todos los préstamos          │
│                                             │
│   3. filtrarLibros(libros, prestamos) ──┐   │
│      ┌──────────────────────────────────┘   │
│      ▼                                      │
│      LibrosNuncaPrestados.filtrarLibros()   │
│      → libros sin préstamos                 │
│                                             │
│   4. ordenarLibros(filtrados)               │
│      → ordena por título (A→Z)             │
│                                             │
│   5. enriquecerLibros(ordenados)            │
│      → agrega formato, portada, etc.        │
│                                             │
│   6. retorna Array listo para renderizar    │
└─────────────────────────────────────────────┘
         │
         ▼
Render: tabla con libros nunca prestados
```

---

## 5. Implementación

### Estructura de Archivos

```
frontend/src/services/
└── LibrosOlvidadosTemplate.js              ← Implementación Template Method

frontend/src/pages/
└── ReportesPage.jsx                        ← Consume el patrón (sección 9)

frontend/src/services/
└── LibrosOlvidadosTemplate.test.js         ← Tests
```

### Endpoints REST

No se requieren endpoints adicionales. El patrón Template Method se ejecuta completamente en el frontend a partir de `GET /prestamos`, `GET /users`, `GET /books`.

---

## 6. Criterios del Reporte

| Reporte | Filtro | Descripción |
|---------|--------|-------------|
| **Nunca prestados** | `prestamos` no contiene `libroId` | Libros con 0 préstamos en toda la historia |

### Columnas de la tabla

| Columna | Fuente | Descripción |
|---------|--------|-------------|
| # | índice | Posición en el ranking |
| Título | `book.titulo` | Nombre del libro |
| Autor | `book.autor` | Autor del libro |
| ISBN | `book.isbn` | Identificador |
| Formato | `book.formato` | PDF, EPUB, MOBI, FISICO |

---

## 7. Relación con Otros Patrones

| Patrón | Relación |
|--------|----------|
| **Strategy** | Strategy define algoritmos intercambiables; Template Method define una estructura fija con pasos variables |
| **State** | State cambia comportamiento según estado interno; Template Method mantiene estructura fija variando solo pasos específicos |
| **Observer** | Observer notifica cambios; Template Method estructura la generación de reportes |
| **Composite** | Los reportes existentes usan Composite para estructura de árbol; Template Method define el proceso de generación de cada hoja |

---

## 8. Pruebas Unitarias

Archivo: `frontend/src/services/LibrosOlvidadosTemplate.test.js`

Framework: **Vitest**

| # | Test | Categoría | Descripción |
|---|------|-----------|-------------|
| 1 | `LibrosNuncaPrestados_sin_prestamos` | Template | Libro sin préstamos → aparece en resultado |
| 2 | `LibrosNuncaPrestados_con_prestamos` | Template | Libro con préstamos → no aparece |
| 3 | `LibrosNuncaPrestados_mixto` | Template | Mezcla de libros con y sin préstamos |
| 4 | `LibrosNuncaPrestados_nombre` | Template | getNombre() retorna "Nunca prestados" |
| 5 | `generar_orden_por_titulo` | Template | Resultados ordenados alfabéticamente por título |
| 6 | `generar_sin_libros_vacio` | Template | Catálogo vacío → array vacío |
| 7 | `generar_sin_prestamos_todos_los_libros` | Template | Sin préstamos registrados → todos los libros aparecen |
| 8 | `generar_enriquece_con_formato` | Template | Cada resultado incluye formato del libro |

---

## 9. Principios SOLID

| Principio | Cómo se cumple |
|-----------|----------------|
| **SRP** | `ReporteLibrosBase` define la estructura; `LibrosNuncaPrestados` solo implementa el filtro |
| **OCP** | Nuevo reporte se agrega creando una subclase que implemente `filtrarLibros()` — no se modifica código existente |
| **LSP** | Todas las subclases cumplen el mismo contrato y son intercambiables |
| **ISP** | Cada subclase solo implementa el método abstracto que necesita |
| **DIP** | La clase base depende de datos concretos, pero las subclases dependen de la abstracción del template |
