# 📚 Sistema de Gestión de Biblioteca (Java MVC) 🛠️ `En Desarrollo`

Proyecto desarrollado para la asignatura de **Programación (DAM)** en el **IES Al-Andalus**. Esta aplicación implementa un sistema robusto de gestión bibliotecaria utilizando el patrón de diseño **Modelo-Vista-Controlador (MVC)**, asegurando un código escalable, organizado y profesional.

> [!IMPORTANTE¡]
> **Estado del proyecto:** Actualmente en desarrollo activo. Se están implementando las capas de lógica de préstamos y refinando la interacción con el usuario.

---

## 🚀 Funcionalidades y Arquitectura

El sistema se basa en una arquitectura desacoplada para facilitar el mantenimiento y la evolución del software:

- **Modelo de Dominio:** Implementación de entidades clave como `Libro`, `Audiolibro`, `Autor` y `Usuario`.
- **Lógica de Negocio:** Gestión centralizada de colecciones en el paquete `negocio`, separando la estructura de datos de su manipulación.
- **Interfaz MVC:** Comunicación fluida entre el `Modelo` y la `Vista` a través de un `Controlador` centralizado.
- **Validación de Datos:** Uso de constructores y métodos con validaciones de seguridad para garantizar la integridad del sistema.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** [Java](https://www.oracle.com/java/)
- **Paradigma:** Programación Orientada a Objetos (POO).
- **Arquitectura:** Modelo-Vista-Controlador (MVC).
- **IDE Sugerido:** IntelliJ IDEA / VS Code.

---

## 📂 Estructura del Proyecto

La jerarquía de clases sigue los estándares de organización de Java, facilitando la lectura del código:

```text
src/org/iesalandalus/programacion/biblioteca/
 ├── controlador/       # Orquestador (Enlace entre Modelo y Vista)
 │    └── Controlador.java
 ├── modelo/            
 │    ├── dominio/      # Entidades y objetos de datos (POJOs)
 │    │    ├── Libro, Audiolibro, Autor, Usuario, Prestamo...
 │    └── negocio/      # Lógica de gestión de colecciones y persistencia
 │         ├── Libros, Usuarios, Prestamos...
 │    └── Modelo.java   # Fachada para la comunicación con el controlador
 ├── vista/             # Interfaz de usuario (Entorno de Consola)
 │    ├── Vista, Consola, Opcion...
 └── AppBiblioteca.java # Clase principal (Punto de entrada)
