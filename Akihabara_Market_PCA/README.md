# Akihabara Market - Inventory Management System

_Akihabara Market_ es una aplicación de escritorio completa para la gestión de inventario de una tienda de artículos de coleccionismo otaku. Desarrollada en Java con arquitectura modular, incluye tanto una interfaz por consola como una interfaz gráfica amigable usando Swing. Además, integra inteligencia artificial (IA) mediante un modelo de lenguaje (LLM) para enriquecer la interacción mediante descripciones automáticas y sugerencias inteligentes de productos, ofreciendo así una experiencia moderna y avanzada.

---

## Tecnologías Utilizadas

- **Java 17**
- **Swing (interfaz gráfica de usuario)**
- **JDBC con SQLite** para gestión de base de datos local
- **WindowBuilder (Eclipse)** para diseño gráfico y manejo visual de la interfaz
- **API de OpenRouter (Servicio Mistral) para integración IA**
- ¡ADVERTENCIA! Para que las funcionalidades de IA funcionen correctamente, es necesario crear una cuenta en https://openrouter.ai/ y obtener una 	apikey propia.
- **Google Material Design Icons** para iconografía consistente y familiar
- Arquitectura modular y orientada a objetos siguiendo buenas prácticas de diseño
- Manejo robusto de excepciones y carga segura de configuraciones externas

---

## Funcionalidades Principales

### Gestión de Inventario (CRUD)

- Alta, modificación, eliminación y consulta de productos.
- Búsqueda avanzada por ID o por nombre con resultados listados.
- Gestión integral de atributos: ID, nombre, categoría, precio y stock.
- Validaciones para garantizar integridad y tipos de datos correctos.
- Actualización y limpieza fácil de formularios para mejora en usabilidad.

### Gestión Completa de Clientes (CRUD)

- Registro, actualización, búsqueda y eliminación de clientes.
- Campos gestionados: ID, nombre, email, teléfono y fecha de registro.
- Formulario con validación de formatos (email, teléfono) y prevención de duplicados.
- Gestión visual en interfaz gráfica con tabla y panel de edición responsive.
- Manejadores de errores con mensajes claros para usuario final.

### Interfaz de Usuario

- Dualidad en interfaz: consola sencilla y GUI avanzada con pestañas para Clientes y Productos.
- Formularios dinámicos con controles para edición y gestión de datos.
- Tablas interactivas que muestran datos en tiempo real con selección para edición.
- Botones para todas las operaciones CRUD y para funciones de IA.
- Opciones para limpiar y recargar datos con actualizaciones instantáneas.
- Integración de validaciones, mensajes de confirmación, alertas y advertencias.

### Integración de Inteligencia Artificial

- **Generación automática de descripciones de marketing breves y atractivas** para productos existentes, mejorando su presentación.
- **Sugerencia inteligente de categorías** para nuevos productos a partir del nombre proporcionado, facilitando la clasificación.
- Uso de llamadas asíncronas para mantener la fluidez de la interfaz.
- Mensajes de estado y carga para informar procesos IA.
- Prompt optimizados y personalizados para respuestas contextualizadas.

### Manejo y Configuración

- Configuración externa centralizada en archivo `config.properties` para datos sensibles (API key, credenciales BD).
- Conexión a base de datos gestionada mediante clase dedicada, con cierre automático y manejo eficiente de recursos.
- Inicialización automática de base de datos con datos de ejemplo si ésta está vacía.
- Separación clara de responsabilidades mediante capas y patrones DAO.

---

## Estructura del Proyecto
Akihabara_Market
├── JRE System Library (jdk-21)
└── src
    ├── config
    │   └── ConfigLoader.java
    ├── controller
    │   ├── MainApp.java
    │   └── utilidades.java
    ├── dao
    │   ├── ClienteDAO.java
    │   ├── ConexionBD.java
    │   └── ProductoDAO.java
    ├── model
    │   ├── ClienteOtaku.java
    │   └── ProductoOtaku.java
    ├── service
    │   └── LlmService.java
    ├── test
    │   └── ProductoDAOTest.java
    ├── util
    │   └── SetUpDatos.java
    └── view
        ├── ClientesPanel.java
        ├── Interfaz.java
        └── InterfazConsola.java
└── Referenced Libraries
    ├── JUnit 5
    └── lib
        ├── gson-2.13.1.jar
        ├── mysql-connector-j-8.3.0.jar
	├── config.properties
	└── README.md

---

## Instalación y Uso

### Requisitos Previos

- Java 17 o superior instalado en el sistema.
- Eclipse IDE con plugin WindowBuilder recomendado para edición visual.
- Controlador JDBC de SQLite agregado al classpath del proyecto.
- Archivo `config.properties` configurado con:
  - URL, usuario y contraseña para la base de datos.
  - API Key válida de OpenRouter para el servicio IA.
  - Modelo de IA configurado (ej. nombre del modelo).

### Ejecución

1. Clona o descarga el proyecto y abre en Eclipse o IDE compatible.
2. Asegura que `config.properties` está correctamente configurado y en el lugar esperado.
3. Ejecuta `MainApp.java` para usar la aplicación mediante la consola.
4. Ejecuta `Interfaz.java` para acceder a la interfaz gráfica.
5. La aplicación detecta si la base de datos está vacía e inserta datos iniciales automáticamente.
6. Interactúa con la gestión de productos y clientes, y prueba las funcionalidades de IA.

---

## Detalle de la Interfaz Gráfica

- **Pestañas diferenciadas** para Clientes y Productos para manejo ordenado.
- **Tablas interactivas** con selección simple para cargar datos en formularios.
- **Formularios con campos editables** y visualmente arreglados con márgenes y espaciados consistentes.
- **Botones claros y accesibles** para cada operación básica y avanzada.
- **Funcionalidad de búsqueda** por ID con campo dinámico visible según necesidad.
- **Integración IA en productos:**
  - Botón para generar descripciones por IA.
  - Botón para sugerir categorías por IA.
- Mensajes retráctiles y cuadros de diálogo con texto con licencia para scrolling.
- Validaciones de campos y formatos para mejorar experiencia y evitar errores.
- Gestión de errores informativa y amigable.
- Uso de iconografía consistente basada en Google Material Design Icons para familiaridad.

---

## Seguridad y Gestión de Datos

- Uso de PreparedStatements y cierre automático para evitar inyección SQL y fugas de recursos.
- API Key y configuración cargada desde archivo externo, nunca hardcodeada.
- Validaciones estrictas en entrada de usuario (email, teléfono, números).
- Control de excepciones y mensajes claros para facilitar uso y mantenimiento.
- Logs para depuración en consola y manejo seguro de errores críticos.

---

## Escalabilidad y Futuras Mejoras

- Adaptar la lógica para conectar con bases de datos más robustas como MySQL o PostgreSQL.
- Incorporar sistema completo de autenticación, roles y permisos.
- Ampliar el sistema para gestión de pedidos, proveedores y entregas.
- Mejorar la integración de IA para recomendaciones personalizadas y análisis predictivo.
- Añadir soporte multilenguaje y soporte para distintas monedas.
- Optimización para mayor performance con grandes volúmenes de datos.
- Migrar interfaz Swing a tecnologías web o móviles con experiencia cross-platform.

---

## Autor

Pablo Conejero Acero   
Este proyecto fue creado como desafío académico con orientación profesional.

---

## Licencia

Proyecto de uso exclusivamente educativo y demostrativo.  
Se permite su modificación y reutilización para aprendizaje personal o académico.

---
## GitHub
Enlace al proyecto subdo a GitHub
https://github.com/PabloCone/Practicas-1-DAM-Pablo-Conejero-Acero.git