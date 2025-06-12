#Akihabara Market - Inventory Management System

_Akihabara Market_ es una aplicación de escritorio completa para la gestión de inventario de una tienda de artículos de coleccionismo otaku. Desarrollada en Java con arquitectura modular, incluye tanto una interfaz por consola como una interfaz gráfica amigable usando Swing. Además, integra una IA (LLM) para enriquecer la interacción mediante descripciones automáticas y sugerencias inteligentes.

---

##Tecnologías Utilizadas

- **Java 17**
- **Swing (GUI)**
- **JDBC + SQLite**
- **WindowBuilder (Eclipse)**
- **Mistral/OpenRouter API (Integración IA)**

---

##Funcionalidades Principales

### Gestión de Inventario (CRUD)

- Alta, modificación, eliminación y consulta de productos.
- Búsqueda por ID o por nombre.
- Atributos de producto: ID, nombre, categoría, precio y stock.

### Interfaz Gráfica

- Tabla interactiva con listado de productos.
- Formulario dinámico para ingresar o editar productos.
- Botones para ejecutar todas las operaciones.
- Funciones para limpiar y recargar el formulario.

### Integración con IA

- **Generación de Descripciones:** Crea descripciones breves y atractivas para productos.
- **Sugerencia de Categoría:** Propone la categoría más adecuada a partir del nombre del producto.

---

##Estructura del Proyecto

AkihabaraMarket/
├── controller/ # Lógica de control
│ └── MainApp.java
├── dao/ # Acceso a datos (SQLite)
│ ├── ConexionBD.java
│ └── ProductoDAO.java
├── model/ # Modelo de dominio
│ └── ProductoOtaku.java
├── service/ # Integración externa (IA)
│ └── LlmService.java
├── util/ # Utilidades (datos iniciales)
│ └── SetUpDatos.java
├── view/ # Vista: consola y GUI
│ ├── InterfazConsola.java
│ └── Interfaz.java
├── bd/ # Base de datos SQLite local
│ └── productos.db
└── README.md


##Instalación y Uso

### Requisitos Previos

- Eclipse con plugin WindowBuilder.
- Controlador JDBC de SQLite añadido al classpath.
- Archivo `llm_config.properties` con tu API key de OpenRouter.

### Ejecución

1. Clona el repositorio o abre el proyecto en Eclipse.
2. Ejecuta `MainApp.java` para la versión por consola.
3. Ejecuta `Interfaz.java` para la interfaz gráfica.
4. Si la base de datos está vacía, se inicializarán los datos automáticamente.

---

##Vista de la GUI

- **Tabla de Productos:** Lista en tiempo real.
- **Formulario:** Campos editables para nombre, categoría, precio y stock.
- **Botones:**
  - `Agregar`, `Buscar`, `Actualizar`, `Eliminar`
  - `Recargar` (vuelve a cargar la lista)
  - `Limpiar` (reinicia el formulario)
  - `Generar Descripción IA`
  - `Sugerir Categoría IA`
- **Mensajes Visuales:** Confirmaciones y alertas tras cada operación.

---

##Seguridad e Integración de la IA

- La API Key se carga desde un archivo externo seguro.
- Las peticiones a la IA están correctamente formateadas y validadas.
- Prompts optimizados para respuestas útiles y relevantes.

---

##Escalabilidad y Futuras Mejores

- Adaptable a otras bases de datos (por ejemplo, MySQL).
- Posibilidad de añadir login de usuarios y roles.
- Nuevas entidades: pedidos, clientes, proveedores.
- Extensión de la IA para recomendaciones avanzadas.

---

##Autor

Pablo Conejero Acero 
Desarrollador Java | Enfocado en aplicaciones prácticas, educativas e interactivas.  
Este proyecto fue desarrollado como desafío académico con orientación profesional.

---

##Licencia

Proyecto de uso educativo y demostrativo. Se permite su modificación y reutilización para aprendizaje personal o académico.

---