# Contexto del Proyecto - Biblioteca Digital

## Descripción General

El Sistema de Biblioteca Digital es una plataforma tecnológica orientada al ámbito educativo y académico, diseñada para gestionar libros electrónicos, préstamos, devoluciones, reservas, suscripciones y recomendaciones personalizadas.

El sistema permite manejar múltiples formatos de lectura (PDF, EPUB, MOBI), acceso multiusuario simultáneo y un modelo de suscripciones, garantizando disponibilidad y rendimiento.

## Objetivos del Proyecto

- Gestionar libros digitales (almacenamiento, consulta, administración)
- Administrar préstamos, devoluciones y reservas
- Implementar sistema de suscripciones
- Permitir acceso simultáneo multiusuario
- Ofrecer recomendaciones personalizadas
- Garantizar escalabilidad y alta disponibilidad
- Aplicar buenas prácticas de arquitectura y calidad de software

## Arquitectura

El proyecto está desarrollado bajo una **Arquitectura de Microservicios**, aplicando el enfoque de **Arquitectura Hexagonal (Ports & Adapters)**.

### Capas de la Arquitectura Hexagonal

| Capa | Descripción |
|------|-------------|
| **Domain** | Núcleo del sistema con lógica de negocio pura. Contiene modelos, puertos (in/out) y servicios de dominio |
| **Application** | Servicios de aplicación que orquestan los casos de uso |
| **Infrastructure** | Adaptadores externos (REST API, persistencia, notificaciones) |

### Estructura interna

- **Domain/Model**: Entidades principales (Book, User, Prestamo, Recomendacion)
- **Domain/Port/In**: Interfaces de casos de uso (UseCases)
- **Domain/Port/Out**: Interfaces para persistencia (RepositoryPorts)
- **Domain/Service**: Servicios de dominio (upload, subscription, notification)
- **Application/Service**: Servicios de aplicación (BookService, UserService, PrestamoService, etc.)
- **Infrastructure/Adapter/In/Web**: Controladores REST
- **Infrastructure/Adapter/Out/Persistence**: Repositorios JPA
- **Infrastructure/Config**: Configuraciones globales

## Patrones de Diseño GoF Implementados

El proyecto implementa patrones de diseño GoF (Gang of Four) estratégicamente seleccionados según las necesidades del sistema. El número de patrones es dinámico y se expandirá según los requerimientos del sistema.

| # | Patrón | Tipo | Ubicación en Código | Descripción |
|---|--------|------|--------------------|-------------|
| 1 | **Singleton (Enum)** | Creacional | `SubscriptionManager` | Garantiza una única instancia global mediante enum INSTANCE |
| 2 | **Factory Method** | Creacional | `FileUploaderCreator` | Define interfaz para crear uploaders, deja que subclases decidan el tipo concreto |
| 3 | **Abstract Factory** | Creacional | `FileUploaderFactory` + `UploadAbstractFactory` | Interfaz para crear familias de objetos relacionados sin especificar clases concretas |
| 4 | **Builder (Fluent)** | Creacional | `Prestamo.Builder` | Construcción paso a paso de objetos complejos con métodos encadenables |
| 5 | **Prototype** | Creacional | `Prototype` interface + `PruebaPrototype` | Clonación de objetos para reutilizar configuraciones predefinidas |
| 6 | **Adapter** | Estructural | `SubscriptionManagerAdapter` | Permite que interfaces incompatibles trabajen juntas |
| 7 | **Bridge** | Estructural | `LibroBridge` + `Acceso` (Premium/Gratis) | Separa abstracción de implementación para evolución independiente |
| 8 | **Decorator** | Estructural | `NotificadorDecorator`, `EmailDecorator`, `SmsDecorator`, `WhatsappDecorator` | Agrega funcionalidades dinámicamente sin modificar clase original |
| 9 | **Facade** | Estructural | `PrestamoFacade` + `PrestamoFacadeImpl` | Simplifica acceso a subsystems complejos (PrestamoService + NotificacionService + lógica de multas) |
| 10 | **Composite** | Estructural | `Reporte` + `ReporteCompuesto` + `ReporteBibliotecaCompleto` | Permite tratar reportes simples y compuestos de manera uniforme mediante estructura de árbol |

### Detalles de Implementación

#### 1. Singleton (Enum) - SubscriptionManager
- Ubicación: `com.biblioteca.digital.domain.subscription.SubscriptionManager`
- Implementación: Enum con constante `INSTANCE`
- Justificación: Código minimalista, thread-safe por diseño, protección frente a reflexión y serialización

#### 2. Factory Method - FileUploaderCreator
- Ubicación: `com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator`
- subclases: PdfUploaderCreator, EpubUploaderCreator, MobiUploaderCreator
- Justificación: Evitar acoplamiento, permitir nuevos formatos sin modificar cliente

#### 3. Abstract Factory - FileUploaderFactory
- Ubicación: `com.biblioteca.digital.domain.service.upload.factory.FileUploaderFactory`
- Interfaz: `UploadAbstractFactory`
- Justificación: Proporcionar interfaz para familias de objetos relacionados

#### 4. Builder (Fluent) - Prestamo
- Ubicación: `com.biblioteca.digital.domain.model.Prestamo.Builder`
- Justificación: Evitar constructores telescópicos con 13 atributos (3 obligatorios, 10 opcionales)

#### 5. Prototype - Recomendacion
- Ubicación: `com.biblioteca.digital.domain.model.Prototype`
- Justificación: Clonar recomendaciones base para generar instancias similares

#### 6. Adapter - SubscriptionManagerAdapter
- Ubicación: `com.biblioteca.digital.domain.subscription.SubscriptionManagerAdapter`
- Interfaz: `SubscriptionChecker`
- Justificación: Reutilizar SubscriptionManager sin acoplar directamente al cliente

#### 7. Bridge - LibroBridge
- Ubicación: `com.biblioteca.digital.domain.bridge.libro.LibroBridge`
- Implementaciones: `LibroPdfBridge`, `LibroEpubBridge`, `LibroMobiBridge`
- Acceso: `AccesoGratis`, `AccesoPremium`
- Justificación: Separar formato del tipo de acceso para evolución independiente

#### 8. Decorator - Notificaciones
- Ubicación: `com.biblioteca.digital.domain.notification.NotificadorDecorator`
- Decoradores: `EmailDecorator`, `SmsDecorator`, `WhatsappDecorator`
- Justificación: Agregar múltiples canales de notificación dinámicamente sin herencia

#### 9. Facade - PrestamoFacade
- Ubicación: `com.biblioteca.digital.domain.port.in.PrestamoFacade` (interfaz)
- Implementación: `com.biblioteca.digital.application.facade.PrestamoFacadeImpl`
- Métodos:
  - `crearPrestamoCompleto()`: Valida disponibilidad, crea préstamo, notifica usuario
  - `devolverPrestamo()`: Calcula multa por retraso, actualiza estado, notifica
  - `renovarPrestamo()`: Verifica límite de renovaciones, prorroga fecha, notifica
- Justificación: Orchestrar flujos complejos que involucran múltiples servicios sin exponer complejidad al Controller
- Tests: 8 tests unitarios en `PrestamoFacadeImplTest` (todos pasando)

#### 10. Composite - Reportes de Préstamos
- Ubicación: `com.biblioteca.digital.domain.model.reporte.Reporte` (interfaz)
- Implementación:
  - Clase abstracta: `ReporteBase`
  - Reportes simples (hojas): `ReportePrestamosActivos`, `ReportePrestamosVencidos`, `ReporteMultasPendientes`
  - Composite: `ReporteCompuesto`, `ReporteBibliotecaCompleto`
- Estructura jerárquica:
  ```
                    [Reporte] (interface)
                          │
           ┌──────────────┴──────────────┐
           ▼                              ▼
    [ReporteBase]                  [ReporteCompuesto]
    (Abstract)                        (Composite)
           │                              │
     ┌─────┴─────┐                 ┌─────┴─────┐
     ▼           ▼                 ▼           ▼
  Activos    Vencidos        BibliotecaCompleto
  Multas       (hojas)            (contiene hijos)
  ```
- Justificación: Generar reportes simples o compuestos de manera uniforme, permitiendo al Controller tratar reportes individuales y completos de la misma forma
- Tests: 13 tests unitarios en `ReporteServiceTest` (todos pasando)

### Tests Unitarios del Proyecto

| Test Class | Descripción | Tests |
|------------|-------------|-------|
| `PrestamoFacadeImplTest` | Prueba el patrón Facade de préstamos | 8 tests |
| `ReporteServiceTest` | Prueba el patrón Composite de reportes | 13 tests |

#### Tests de PrestamoFacadeImplTest

| Test | Escenario | Resultado esperado |
|------|-----------|-------------------|
| `crearPrestamoCompleto_exito` | Datos válidos | Préstamo creado + notificación enviada |
| `crearPrestamoCompleto_usuarioPremium` | Usuario premium | Notificación por múltiples canales |
| `devolverPrestamo_exitoSinMulta` | Devolución antes de fecha | Estado DEVUELTO, multa = 0 |
| `devolverPrestamo_exitoConMulta` | Devolución después de fecha | Multa calculada ($500/día) |
| `devolverPrestamo_noEncontrado` | ID no existe | Exception |
| `renovarPrestamo_exito` | Renovación válida | Nueva fecha incrementada |
| `renovarPrestamo_limiteExcedido` | +2 renovaciones | Exception "límite excedido" |
| `renovarPrestamo_primeraRenovacion` | Primera renovación desde null | vecesRenovado = 1 |

#### Tests de ReporteServiceTest

| Test | Escenario | Resultado esperado |
|------|-----------|-------------------|
| `generarReporteActivos_vacio` | Sin préstamos activos | Mensaje apropiado |
| `generarReporteActivos_conDatos` | Con préstamos activos | Formato correcto |
| `generarReporteVencidos_vacio` | Sin préstamos vencidos | Mensaje apropiado |
| `generarReporteVencidos_conDatos` | Con préstamos vencidos | Formato correcto |
| `generarReporteMultas_vacio` | Sin multas | Mensaje apropiado |
| `generarReporteMultas_conDatos` | Con multas | Formato correcto |
| `generarReporteCompleto_sinDatos` | Reporte compuesto vacío | Estructura correcta |
| `generarReporteCompleto_conDatos` | Reporte compuesto con datos | Combina todos |
| `reporteCompuesto_tituloCorrecto` | Verificar título | Correcto |
| `reporteCompuesto_isCompuesto_true` | Verificar isCompuesto | true para compuesto |
| `reporteSimple_isCompuesto_false` | Verificar isCompuesto | false para simple |
| `generarReporteVencidos_diasCorrecto` | Calcular días de retraso | Correcto |
| `generarReporteMultas_sumaTotal` | Sumar total de multas | $4500 |

## Módulos del Sistema

| Módulo | Funcionalidad |
|--------|--------------|
| **Gestión de Libros** | Registro, clasificación, organización por formato (PDF, EPUB, MOBI) |
| **Gestión de Préstamos** | Préstamos, devoluciones, reservas con código único |
| **Gestión de Suscripciones** | Diferentes niveles de acceso y beneficios |
| **Recomendaciones** | Sugerencias basadas en comportamiento y preferencias |
| **Notificaciones** | Múltiples canales (Email, SMS, WhatsApp) |

## Tecnologías Utilizadas

### Backend
- **Lenguaje**: Java
- **Framework**: Spring Boot 4.x
- **Arquitectura**: REST API
- **Persistencia**: JPA / Spring Data
- **Build Tool**: Maven

### Frontend
- **Lenguaje**: JavaScript
- **Framework**: React 18
- **Build Tool**: Vite
- **Puerto**: 5173
- **Estilo**: CSS vanilla

### Seguridad
- **Autenticación**: JWT (JSON Web Tokens)
- **Encriptación**: BCrypt
- **CORS**: Configurado para http://localhost:5173

## Estructura del Frontend

```
frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── Navbar.jsx
│   │   └── PrivateRoute.jsx
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   └── DashboardPage.jsx
│   ├── services/
│   │   └── AuthService.js
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── css/
│   │   └── App.css
│   ├── App.jsx
│   └── main.jsx
├── index.html
├── vite.config.js
└── package.json
```

## Endpoints de Autenticación

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/auth/register` | POST | Registrar nuevo usuario (retorna JWT) |
| `/auth/login` | POST | Iniciar sesión (retorna JWT) |

## Endpoints de Préstamos

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/prestamos/completo` | POST | Crear préstamo completo (Facade) - crea + notifica |
| `/prestamos` | POST | Crear préstamo simple |
| `/prestamos` | GET | Listar todos los préstamos |
| `/prestamos/{id}` | GET | Buscar préstamo por ID |
| `/prestamos/{id}` | PUT | Actualizar préstamo |
| `/prestamos/{id}` | DELETE | Eliminar préstamo |
| `/prestamos/{id}/devolver` | POST | Devolver préstamo con cálculo de multa (Facade) |
| `/prestamos/{id}/renovar?dias=X` | POST | Renovar/prorrogar préstamo (Facade) |

## Referencias

- Patrones de diseño GoF (Gang of Four)
- Arquitectura Hexagonal (Ports & Adapters)
- Principios SOLID