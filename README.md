# PROYECTO-BIBLIOTECA-DIGITAL
Estudiantes:

•	Jhorger David Andrade Calderon

•	Cesar David Ojeda Gonzalez

Docente:

•	Eliecer Montero Ojeda

<p align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="50"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="50"/>
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="50"/>
</p>

# Descripción del Proyecto
 
El Sistema de Biblioteca Digital es una plataforma escalable orientada al dominio educativo/académico que permitirá la gestión integral de libros digitales, préstamos, reservas y recomendaciones personalizadas.

El sistema soportará múltiples formatos de lectura (PDF, EPUB, MOBI), acceso multiusuario simultáneo y un modelo de suscripciones, garantizando disponibilidad y rendimiento para más de 100.000 usuarios concurrentes.

# Objetivos
 
•	Gestionar libros digitales.

•	Administrar préstamos, devoluciones y reservas.

•	Implementar un sistema de suscripciones.

•	Permitir acceso simultáneo multiusuario.

•	Ofrecer recomendaciones.

•	Garantizar escalabilidad y alta disponibilidad.

•	Implementar buenas prácticas de arquitectura y calidad de software.

# Arquitectura del Sistema
 
El sistema será desarrollado bajo una Arquitectura de Microservicios, aplicando el enfoque de Arquitectura Hexagonal (Ports & Adapters).

# Microservicios
 
Cada dominio funcional será implementado como un microservicio independiente, por ejemplo: Servicio de usuarios, servicio de catálogo, servicio de autenticación, servicio de notificaciones, API Gateway, etc.

# Arquitectura Hexagonal
 
Cada microservicio seguirá el modelo hexagonal:

•	Dominio (Core) → Lógica de negocio pura.

•	Puertos (Ports) → Interfaces que definen contratos.

•	Adaptadores (Adapters) → Implementaciones externas (Base de datos, APIs, mensajería, etc.).

Esto permite:

•	Independencia de frameworks.

•	Alta testabilidad.

•	Bajo acoplamiento.

•	Mayor mantenibilidad.

 # Patrones de Diseño
 
Se implementarán 8 patrones de software (por definir), seleccionados estratégicamente según las necesidades del sistema.

# Control de Versiones
 
Se utilizará Git como sistema de control de versiones.

Flujo de trabajo

•	Uso de ramas (feature branches).

•	Pull Requests para revisión de código.

•	Convenciones de commits.

# Documentación UML
 
Se realizará documentación UML completa del sistema, incluyendo:

•	Diagramas de Casos de Uso
`
•	Diagramas de Clases

•	Diagramas de Secuencia

•	Diagramas de Componentes

•	Diagramas de Despliegue

•	Diagramas de Paquetes

La documentación permitirá visualizar:

•	Arquitectura general.

•	Relaciones entre microservicios.

•	Interacciones entre componentes.

•	Flujo de procesos críticos.

# Monitoreo y Logging
 
El sistema contará con un mecanismo de logging centralizado, que permitirá:

•	Trazabilidad de solicitudes.

•	Monitoreo de errores.

•	Auditoría de operaciones.

•	Observabilidad del sistema distribuido.

# Escalabilidad

El sistema estará diseñado para soportar más de 100.000 usuarios, mediante:

•	Microservicios independientes.

•	Escalamiento horizontal.

•	Separación de responsabilidades.

•	Arquitectura desacoplada.
