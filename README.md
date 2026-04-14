# 📚 Sistema de Gestión de Biblioteca (Java MVC)

Proyecto desarrollado para la asignatura de **Programación (DAM)** en el IES Al-Andalus. Esta aplicación implementa un sistema robusto de gestión bibliotecaria utilizando el patrón de diseño **Modelo-Vista-Controlador (MVC)**.

---

## 🚀 Funcionalidades y Lógica de Negocio

El sistema permite una gestión integral de los recursos de la biblioteca a través de una arquitectura desacoplada:

- **Gestión de Recursos Multimedia:** Soporte para diferentes tipos de materiales, incluyendo `Audiolibro` y `Libro`.
- **Control de Préstamos:** Lógica de negocio para vincular `Usuarios` con recursos y gestionar fechas.
- **Validación Estricta:** Uso de clases de apoyo como `Direccion` y `Autor` para garantizar la integridad del dominio.
- **Interfaz de Usuario:** Sistema basado en consola (`Vista`, `Consola`, `Opcion`) para una interacción fluida y jerarquizada.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java
- **Arquitectura:** Modelo-Vista-Controlador (MVC).
- **Entorno:** IntelliJ IDEA.
- **Gestión de Datos:** Almacenamiento organizado en capas de `Negocio` y `Dominio`.

---

## 📐 Estructura de Clases y Paquetes

La jerarquía del proyecto sigue los estándares de limpieza y organización de Java:

```text
src/org/iesalandalus/programacion/biblioteca/
 ├── controlador/       # Orquestador entre el modelo y la vista
 │    └── Controlador.java
 ├── modelo/            
 │    ├── dominio/      # Entidades (POJOs) y lógica de datos
 │    │    ├── Libro, Audiolibro, Autor, Usuario, Prestamo...
 │    └── negocio/      # Lógica de gestión de colecciones
 │         ├── Libros, Usuarios, Prestamos...
 │    └── Modelo.java   # Fachada del modelo
 ├── vista/             # Interfaz de usuario (Consola)
 │    ├── Vista, Consola, Opcion...
 └── AppBiblioteca.java # Punto de entrada (Main)
