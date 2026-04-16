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

## Patrones de Diseño Implementados (8 patrones GoF)

Se implementaron 8 patrones de diseño GoF (Gang of Four) estratégicamente seleccionados según las necesidades del sistema:

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

## Referencias

- Patrones de diseño GoF (Gang of Four)
- Arquitectura Hexagonal (Ports & Adapters)
- Principios SOLID