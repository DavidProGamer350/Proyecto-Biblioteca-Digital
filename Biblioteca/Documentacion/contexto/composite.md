# Patrón Composite - Reportes de Préstamos

## 1. Problema

### Situación Actual

En el sistema de biblioteca digital, cuando el administrador necesita obtener información sobre los préstamos, actualmente debe:

1. **Llamar múltiples endpoints** para obtener cada tipo de información:
   - `GET /prestamos` → obtener todos los préstamos
   - Filtrar manualmente en el frontend para obtener activos, vencidos, multas

2. **Lógica de filtrado en el frontend**:
   - El Controller no tiene forma de generar reportes estructurados
   - El frontend debe hacer el trabajo de filtrado y organización

3. **Sin capacidad de generar reportes completos**:
   - No existe un mecanismo para generar un "reporte completo" que incluya múltiples secciones
   - El usuario debe combinar manualmente los datos

### Problema Específico

```
Frontend actual:
┌──────────────────┐
│  Obtiene todos   │
│   los préstamos  │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────────────┐
│  Filtra en JavaScript:               │
│  - Préstamos activos                 │
│  - Préstamos vencidos                │
│  - Multas pendientes                 │
└──────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────┐
│  Muestra en pantalla                 │
└──────────────────────────────────────┘
```

**Problemas identificados:**
- ❌ Acoplamiento entre presentación y lógica de negocio
- ❌ Lógica de filtrado duplicada en el frontend
- ❌ No hay forma de generar reportes compuestos
- ❌ El Backend no proporciona una interfaz unificada para reportes

---

## 2. Justificación del Patrón Composite

### ¿Qué es el Patrón Composite?

El patrón Composite es un patrón estructural que permite tratar **objetos individuales** y **composiciones de objetos** de manera uniforme mediante una estructura de árbol.

```
        [Reporte] (Componente)
              │
    ┌─────────┴─────────┐
    │                     │
[ReporteSimple]     [ReporteCompuesto]
  (Hoja)               (Contiene hijos)
     │                      │
     ▼                      ▼
┌─────────┐          ┌─────────────┐
│Activos  │          │ Completo    │
│Vencidos │          │  + Activos  │
│Multas   │          │  + Vencidos │
└─────────┘          │  + Multas   │
└─────────────┘
                      └─────────────┘
```

### Diagrama de Árbol - Clases del Proyecto

```
                        ┌─────────────────────┐
                        │     Reporte          │  ← Interfaz (PADRE)
                        │   (interface)        │      + getTitulo()
                        └──────────┬──────────┘      + generar()
                                   │                 + isCompuesto()
              ┌────────────────────┴────────────────────┐
              │                                         │
              ▼                                         ▼
┌─────────────────────────────┐      ┌─────────────────────────────────────┐
│       ReporteBase           │      │        ReporteCompuesto            │  ← COMPOSITE
│      (Abstract Class)       │      │        (CONTIENE HIJOS)            │
├─────────────────────────────┤      ├─────────────────────────────────────┤
│ - titulo: String            │      │ - hijos: List<Reporte>             │
├─────────────────────────────┤      │ + agregarHijo(Reporte)            │
│ + getTitulo(): String       │      │ + eliminarHijo(Reporte)            │
│ + isCompuesto(): false      │      │ + getHijos(): List<Reporte>        │
│ # generar(): abstract       │      │ + isCompuesto(): true              │
└──────────────┬──────────────┘      │ + generar(): combina hijos         │
               │                     └──────────────┬────────────────────┘
               │                                  │
        ┌──────┴──────┐                           │
        ▼            ▼                           ▼
┌────────────────┐  ┌────────────────────┐  ┌──────────────────────────────┐
│ ReporteSimple  │  │ ReporteSimple      │  │ ReporteBibliotecaCompleto   │  ← CONCRETO
│   (HOJA)       │  │    (HOJA)          │  │   (usa ReporteCompuesto)    │
├────────────────┤  ├────────────────────┤  ├──────────────────────────────┤
│ Prestamos      │  │ Préstamos          │  │ REPORTE COMPLETO             │
│ Activos        │  │ Vencidos           │  │ que contiene:               │
├────────────────┤  ├────────────────────┤  │ - ReportePrestamosActivos    │
│ - prestamos    │  │ - prestamos       │  │ - ReportePrestamosVencidos  │
│ + generar()    │  │ + generar()       │  │ - ReporteMultasPendientes   │
└────────────────┘  └────────────────────┘  └──────────────────────────────┘
                                                          │
                              ┌───────────────────────────┴────────────────────┐
                              ▼
                   ┌─────────────────────────────────────┐
                   │ ReporteMultasPendientes (HOJA)     │
                   ├─────────────────────────────────────┤
                   │ - prestamos: List<Prestamo>         │
                   │ + generar(): String               │
                   └─────────────────────────────────────┘
```

### Descripción de la Jerarquía

| Clase | Tipo | Rol | Padres |
|-------|------|-----|--------|
| `Reporte` | Interfaz | Define contrato común | - |
| `ReporteBase` | Abstract | Implementa métodos base | Implementa `Reporte` |
| `ReportePrestamosActivos` | Clase | Reporte simple (HOJA) | Hereda de `ReporteBase` |
| `ReportePrestamosVencidos` | Clase | Reporte simple (HOJA) | Hereda de `ReporteBase` |
| `ReporteMultasPendientes` | Clase | Reporte simple (HOJA) | Hereda de `ReporteBase` |
| `ReporteCompuesto` | Clase | Composite (CONTIENE HIJOS) | Hereda de `ReporteBase` |
| `ReporteBibliotecaCompleto` | Clase | Composite concreto | Hereda de `ReporteCompuesto` |

### Flujo de Datos

```
Controller
    │
    ▼
ReporteService.generarReporteCompleto()
    │
    ├─► generarReporteActivos() → ReportePrestamosActivos (HOJA)
    │
    ├─► generarReporteVencidos() → ReportePrestamosVencidos (HOJA)
    │
    ├─► generarReporteMultas() → ReporteMultasPendientes (HOJA)
    │
    └─► new ReporteBibliotecaCompleto([activos, vencidos, multas])
            │
            ▼
        ReporteCompuesto (COMPOSITE)
            │
            ▼
        reporte.generar() → combina todos los hijos
```
┌─────────────────────────────────────────────────────────────────┐
│                     ReporteController                           │
│         (Recibe HTTP → llama a ReporteService)                 │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ReporteService                             │
│   - generarReporteActivos()                                     │
│   - generarReporteVencidos()                                   │
│   - generarReporteMultas()                                     │
│   - generarReporteCompleto()  ← Usa Composite                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Reporte (Interfaz)                           │
│         + getTitulo(): String                                  │
│         + generar(): String                                    │
│         + isCompuesto(): boolean                               │
└────────────────────────────┬────────────────────────────────────┘
                             │
         ┌───────────────────┴───────────────────┐
         ▼                                       ▼
┌─────────────────────┐             ┌─────────────────────────────┐
│  ReporteSimple      │             │     ReporteCompuesto         │
│  (Hoja)             │             │     (Composite)            │
├─────────────────────┤             ├─────────────────────────────┤
│ - PrestamosActivos  │             │ - ReporteBibliotecaCompleto │
│ - PrestamosVencidos │             │ - Lista<Reporte> hijos      │
│ - MultasPendientes  │             │ - agregarHijo()             │
└─────────────────────┘             └─────────────────────────────┘
```

### Flujo de Uso

**Caso 1: Reporte Simple**
```
GET /reportes/prestamos/activos

1. Controller → ReporteService.generarReporteActivos()
2. Service obtiene préstamos y filtra por estado ACTIVO
3. Crea ReportePrestamosActivos (Reportesimple)
4. .generar() → retorna String formateado
```

**Caso 2: Reporte Compuesto**
```
GET /reportes/prestamos/completo

1. Controller → ReporteService.generarReporteCompleto()
2. Service crea 3 reportes simples:
   - ReportePrestamosActivos
   - ReportePrestamosVencidos
   - ReporteMultasPendientes
3. Los combina en ReporteBibliotecaCompleto (Compuesto)
4. .generar() → concatena todos los hijos
```

---

## 4. Implementación

### Estructura de Archivos

```
src/main/java/com/biblioteca/digital/
├── domain/
│   └── model/
│       └── reporte/
│           ├── Reporte.java                 ← Interfaz
│           ├── ReporteBase.java              ← Clase abstracta
│           ├── simple/
│           │   ├── ReportePrestamosActivos.java
│           │   ├── ReportePrestamosVencidos.java
│           │   └── ReporteMultasPendientes.java
│           └── compuesto/
│               ├── ReporteCompuesto.java
│               └── ReporteBibliotecaCompleto.java
├── application/
│   └── service/
│       └── ReporteService.java
└── infrastructure/
    └── adapter/
        └── in/
            └── web/
                └── ReporteController.java
```

### Formato de Salida: PDF

Para generar PDFs, se utilizará la librería **iText** o **Apache PDFBox**. Se elegirá una implementación que sea compatible con el proyecto.

### Endpoints REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reportes/prestamos/activos` | Reporte de préstamos activos (texto) |
| GET | `/reportes/prestamos/vencidos` | Reporte de préstamos vencidos (texto) |
| GET | `/reportes/prestamos/multas` | Reporte de multas pendientes (texto) |
| GET | `/reportes/prestamos/completo` | Reporte completo (texto) - Composite |

> **Nota**: La implementación actual genera texto plano. La generación de PDF puede implementarse en una versión futura usando librerías como iText o Apache PDFBox.

---

## 5. Pruebas Unitarias

### Tests Implementados (13 pruebas)

Se creará la clase `ReporteServiceTest` con las siguientes pruebas:

| # | Test | Descripción |
|---|------|-------------|
| 1 | `generarReporteActivos_vacio` | Sin préstamos activos → mensaje apropiado |
| 2 | `generarReporteActivos_conDatos` | Con préstamos activos → formato correcto |
| 3 | `generarReporteVencidos_vacio` | Sin préstamos vencidos → mensaje apropiado |
| 4 | `generarReporteVencidos_conDatos` | Con préstamos vencidos → formato correcto |
| 5 | `generarReporteMultas_vacio` | Sin multas → mensaje apropiado |
| 6 | `generarReporteMultas_conDatos` | Con multas → formato correcto |
| 7 | `generarReporteCompleto_sinDatos` | Reporte compuesto vacío → estructura correcta |
| 8 | `generarReporteCompleto_conDatos` | Reporte compuesto con datos → combina todos |
| 9 | `reporteCompuesto_tituloCorrecto` | Verifica que el título sea correcto |
| 10 | `reporteCompuesto_isCompuesto_true` | Verifica que isCompuesto() retorna true para compuesto |
| 11 | `reporteSimple_isCompuesto_false` | Verifica que isCompuesto() retorna false para simple |
| 12 | `generarReporteVencidos_diasCorrecto` | Verifica cálculo de días de retraso |
| 13 | `generarReporteMultas_sumaTotal` | Verifica suma total de multas |

**Resultado**: ✅ Todos los 13 tests pasando

### Dependencias Mockeadas

- `PrestamoUseCase` - simular lista de préstamos

---

## 6. Beneficios Esperados

| Beneficio | Descripción |
|-----------|-------------|
| **Desacoplamiento** | Lógica de reportes en el backend, no en frontend |
| **Uniformidad** | Misma interfaz para reportes simples y compuestos |
| **Extensibilidad** | Agregar nuevos reportes sin modificar código existente |
| **Mantenibilidad** | Cada reporte simple tiene responsabilidad única |
| **Reutilización** | Los reportes simples se pueden usar independientemente o en composites |

---

## 7. Relación con Otros Patrones

### Con Facade (Patrón #9)

El Composite **no reemplaza** al Facade, sino que lo **complementa**:

```
PrestamoFacade                    ReporteComposite
     │                                  │
     ▼                                  ▼
PrestamoService              ReporteService
     │                                  │
     └──────────────┬───────────────────┘
                    │
              Usa PrestamoUseCase
```

- **Facade**: Simplifica operaciones complejas de préstamos (crear, devolver, renovar)
- **Composite**: Genera reportes estructurados a partir de los mismos datos

### Con Builder (Patrón #4)

El Builder existente en `Prestamo` se utiliza para construir los objetos `Prestamo` que luego los reportes procesan.

---

## 8. Consideraciones Técnicas

### Generación de PDF

Para la generación de PDF se considerará:
- Usar una librería liviana compatible con Spring Boot
- El formato PDF permite impresión y distribución offline
- Alternativa: generar HTML que el frontend convierte a PDF

### Rendimiento

- Los reportes se generan on-demand
- Para grandes volúmenes, considerar generación asyncrona
- Cachear reportes frecuentes si es necesario

### Seguridad

- Los reportes solo muestran información que el usuario tiene permiso de ver
- Considerar sanitización de datos sensibles

---

## 9. Conclusión

El patrón Composite es la solución ideal para el sistema de reportes porque:

1. ✅ **Resuelve el problema** de generar reportes simples y compuestos con una interfaz unificada
2. ✅ **Se integra** naturalmente con los servicios existentes (PrestamoUseCase)
3. ✅ **Es extensible** para agregar nuevos tipos de reporte sin modificar código
4. ✅ **Complementa** el patrón Facade implementado previamente
5. ✅ **Tiene sentido de negocio** para una biblioteca digital donde los reportes son importantes