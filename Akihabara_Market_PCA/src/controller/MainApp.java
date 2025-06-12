/**
 * MainApp.java
 *
 * Descripción: Esta aplicación Java sirve como el punto de entrada principal para un sistema de gestión
 * de una tienda otaku. Permite a los usuarios interactuar con los datos de productos y clientes
 * a través de una interfaz de consola. La aplicación maneja operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * para ambos, productos y clientes, y además integra funcionalidades de inteligencia artificial
 * (IA) para mejorar la gestión de productos, como la generación de descripciones y la sugerencia de categorías.
 * Utiliza un diseño modular con capas para la vista (InterfazConsola), el controlador (MainApp),
 * el acceso a datos (DAO), los modelos (ProductoOtaku, ClienteOtaku) y los servicios externos (LlmService).
 */
package controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import dao.ClienteDAO;
import dao.ConexionBD;
import dao.ProductoDAO;
import model.ClienteOtaku;
import model.ProductoOtaku;
import service.LlmService;
import view.InterfazConsola;

public class MainApp {
    // Instancias de las clases DAO y de Vista para interactuar con la base de datos y la interfaz de usuario.
    private static ProductoDAO dao = new ProductoDAO();
    // Se inicializa ClienteDAO con null temporalmente; la conexión real la maneja internamente el DAO o se inyecta.
    private static ClienteDAO clienteDao = new ClienteDAO();
    private static InterfazConsola vista = new InterfazConsola();

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     * Se encarga de la inicialización de la base de datos (si está vacía) y del bucle principal del menú
     * para interactuar con el usuario.
     * @param args Argumentos de la línea de comandos (no utilizados en esta aplicación).
     */
    public static void main(String[] args) {
        // Bloque try-with-resources para asegurar que la conexión y el statement se cierren automáticamente.
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM productos")) {

            // Verifica si la tabla de productos está vacía para cargar datos iniciales.
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("🔧 Base de datos vacía. Cargando datos iniciales...");
                util.SetUpDatos.setUpDatos(); // Llama al método para configurar los datos.
            }

        } catch (Exception e) {
            // Manejo de errores en caso de problemas con la base de datos durante la inicialización.
            System.err.println("❌ Error al verificar o inicializar la base de datos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para depuración.
            return; // Termina la aplicación si hay un error crítico al inicio.
        }

        int opcion;
        // Bucle principal del menú de la aplicación. Se repite hasta que el usuario elija salir (opción 0).
        do {
            vista.mostrarMenu(); // Muestra el menú principal al usuario.
            opcion = vista.leerOpcion(); // Lee la opción seleccionada por el usuario.

            // Estructura switch para manejar las diferentes opciones del menú.
            switch (opcion) {
                case 1 -> agregarProducto(); // Llama al método para agregar un producto.
                case 2 -> buscarPorId();     // Llama al método para buscar un producto por ID.
                case 3 -> listarProductos(); // Llama al método para listar todos los productos.
                case 4 -> actualizarProducto(); // Llama al método para actualizar un producto.
                case 5 -> eliminarProducto(); // Llama al método para eliminar un producto.
                case 6 -> buscarPorNombre(); // Llama al método para buscar productos por nombre.
                case 7 -> submenuIA();       // Llama al método para el submenú de funciones de IA.
                case 8 -> submenuClientes(); // Llama al método para el submenú de gestión de clientes.
                case 0 -> vista.mostrarMensaje("¡Hasta luego!"); // Mensaje de despedida al salir.
                default -> vista.mostrarMensaje("Opción inválida."); // Mensaje para opciones no válidas.
            }
        } while (opcion != 0); // El bucle continúa mientras la opción no sea 0.
    }

    /**
     * Permite al usuario agregar un nuevo producto a la base de datos.
     * Solicita los datos del producto a través de la vista y los envía al DAO para su almacenamiento.
     */
    private static void agregarProducto() {
        vista.mostrarMensaje("\n--- Agregar producto ---");
        ProductoOtaku producto = vista.leerDatosProducto(); // Obtiene los datos del producto desde la vista.
        dao.agregarProducto(producto); // Llama al DAO para agregar el producto a la base de datos.
        vista.mostrarMensaje("✅ Producto agregado correctamente."); // Confirma la operación.
    }

    /**
     * Recupera y muestra en la consola una lista de todos los productos disponibles en la base de datos.
     */
    private static void listarProductos() {
        List<ProductoOtaku> lista = dao.obtenerTodosLosProductos(); // Obtiene la lista de productos del DAO.
        vista.mostrarListaProductos(lista); // Muestra la lista de productos a través de la vista.
    }

    /**
     * Permite al usuario buscar un producto específico por su ID.
     * Muestra los detalles del producto si es encontrado, o un mensaje de error si no existe.
     */
    private static void buscarPorId() {
        int id = vista.leerEntero("ID del producto a buscar: "); // Solicita el ID del producto.
        ProductoOtaku producto = dao.obtenerProductoPorId(id); // Busca el producto por ID en el DAO.
        if (producto != null) {
            vista.mostrarProducto(producto); // Muestra los detalles del producto si se encuentra.
        } else {
            vista.mostrarMensaje("❌ Producto no encontrado."); // Mensaje si el producto no existe.
        }
    }

    /**
     * Permite al usuario actualizar la información de un producto existente.
     * Solicita el ID del producto, muestra sus datos actuales y permite modificarlos.
     */
    private static void actualizarProducto() {
        int id = vista.leerEntero("ID del producto a actualizar: "); // Solicita el ID del producto a actualizar.
        ProductoOtaku producto = dao.obtenerProductoPorId(id); // Busca el producto por ID.
        if (producto != null) {
            vista.mostrarMensaje("Producto actual: " + producto); // Muestra los datos actuales del producto.
            // Permite al usuario ingresar nuevos datos, usando los actuales como predeterminados si no se modifican.
            String nuevoNombre = vista.leerTexto("Nuevo nombre (" + producto.getNombre() + "): ");
            String nuevaCategoria = vista.leerTexto("Nueva categoría (" + producto.getCategoria() + "): ");
            double nuevoPrecio = vista.leerDouble("Nuevo precio (" + producto.getPrecio() + "): ");
            int nuevoStock = vista.leerEntero("Nuevo stock (" + producto.getStock() + "): ");

            // Actualiza los campos del producto si se han proporcionado nuevos valores.
            producto.setNombre(nuevoNombre.isEmpty() ? producto.getNombre() : nuevoNombre);
            producto.setCategoria(nuevaCategoria.isEmpty() ? producto.getCategoria() : nuevaCategoria);
            producto.setPrecio(nuevoPrecio); // Los tipos numéricos no tienen un "isEmpty".
            producto.setStock(nuevoStock);  // Se asume que el valor introducido es el nuevo.

            boolean actualizado = dao.actualizarProducto(producto); // Llama al DAO para actualizar el producto.
            vista.mostrarMensaje(actualizado ? "✅ Actualizado correctamente." : "❌ Fallo al actualizar.");
        } else {
            vista.mostrarMensaje("❌ Producto no encontrado."); // Mensaje si el producto no existe.
        }
    }

    /**
     * Permite al usuario eliminar un producto de la base de datos por su ID.
     * Confirma si la eliminación fue exitosa o si el producto no fue encontrado.
     */
    private static void eliminarProducto() {
        int id = vista.leerEntero("ID del producto a eliminar: "); // Solicita el ID del producto a eliminar.
        boolean eliminado = dao.eliminarProducto(id); // Llama al DAO para eliminar el producto.
        vista.mostrarMensaje(eliminado ? "🗑️ Producto eliminado." : "❌ Producto no encontrado.");
    }

    /**
     * Permite al usuario buscar productos por su nombre (o parte de él).
     * Muestra una lista de los productos que coinciden con el criterio de búsqueda.
     */
    private static void buscarPorNombre() {
        String nombre = vista.leerTexto("Nombre a buscar: "); // Solicita el nombre o parte del nombre.
        List<ProductoOtaku> lista = dao.buscarProductosPorNombre(nombre); // Busca productos por nombre en el DAO.
        vista.mostrarListaProductos(lista); // Muestra la lista de productos encontrados.
    }

    /**
     * Genera una descripción de marketing para un producto existente utilizando el servicio de IA.
     * El prompt se construye con el nombre y la categoría del producto.
     */
    private static void generarDescripcionIA() {
        int id = vista.leerEntero("ID del producto para generar descripción: "); // Solicita el ID del producto.
        ProductoOtaku p = dao.obtenerProductoPorId(id); // Obtiene el producto del DAO.
        if (p != null) {
            // Construye el prompt para el servicio de IA.
            String prompt = "Genera una descripción en castellano de marketing breve y atractiva para el producto otaku: " +
                                  p.getNombre() + " de la categoría " + p.getCategoria() + ".";
            String respuesta = LlmService.generarRespuesta(prompt); // Llama al servicio de IA.
            vista.mostrarMensaje("\n📝 Descripción generada por IA:\n" + respuesta); // Muestra la descripción generada.
        } else {
            vista.mostrarMensaje("❌ Producto no encontrado."); // Mensaje si el producto no existe.
        }
    }

    /**
     * Sugiere una categoría para un nuevo producto utilizando el servicio de IA.
     * El prompt incluye el nombre del producto y una lista de categorías posibles.
     */
    private static void sugerirCategoriaIA() {
        String nombre = vista.leerTexto("Nombre del nuevo producto: "); // Solicita el nombre del nuevo producto.
        // Construye el prompt con el nombre del producto y las categorías a sugerir.
        String prompt = "Para un producto otaku llamado '" + nombre +
                              "', sugiere una categoría adecuada de esta lista: Figura, Manga, Póster, Llavero, Ropa, Videojuego, Otro.";
        String respuesta = LlmService.generarRespuesta(prompt); // Llama al servicio de IA.
        vista.mostrarMensaje("\n🧠 Categoría sugerida por IA:\n" + respuesta); // Muestra la categoría sugerida.
    }

    /**
     * Muestra un submenú para las funcionalidades relacionadas con el asistente de IA.
     * Permite al usuario elegir entre generar descripciones o sugerir categorías.
     */
    private static void submenuIA() {
        int opcion;
        do {
            // Imprime el submenú de opciones de IA.
            System.out.println("""
                    \n=== 🤖 Asistente IA ===
                    1. Generar descripción para producto existente
                    2. Sugerir categoría para nuevo producto
                    0. Volver al menú principal
                    """);
            opcion = vista.leerOpcion(); // Lee la opción del submenú.
            switch (opcion) {
                case 1 -> generarDescripcionIA(); // Llama a la función para generar descripción.
                case 2 -> sugerirCategoriaIA();  // Llama a la función para sugerir categoría.
                case 0 -> vista.mostrarMensaje("↩️ Volviendo al menú principal..."); // Mensaje al salir.
                default -> vista.mostrarMensaje("❌ Opción inválida."); // Mensaje para opción inválida.
            }
        } while (opcion != 0); // El bucle continúa mientras la opción no sea 0.
    }

    /**
     * Muestra un submenú para las funcionalidades relacionadas con la gestión de clientes.
     * Permite agregar, buscar, listar, actualizar y eliminar clientes.
     */
    private static void submenuClientes() {
        int opcion;
        do {
            vista.mostrarSubmenuClientes(); // Muestra el submenú de clientes.
            opcion = vista.leerOpcion(); // Lee la opción del submenú.
            switch (opcion) {
                case 1 -> agregarCliente(); // Llama a la función para agregar cliente.
                case 2 -> buscarClientePorId(); // Llama a la función para buscar cliente por ID.
                case 3 -> listarClientes(); // Llama a la función para listar clientes.
                case 4 -> actualizarCliente(); // Llama a la función para actualizar cliente.
                case 5 -> eliminarCliente();  // Llama a la función para eliminar cliente.
                case 0 -> vista.mostrarMensaje("↩️ Volviendo al menú principal..."); // Mensaje al salir.
                default -> vista.mostrarMensaje("❌ Opción inválida."); // Mensaje para opción inválida.
            }
        } while (opcion != 0); // El bucle continúa mientras la opción no sea 0.
    }

    /**
     * Permite al usuario agregar un nuevo cliente a la base de datos.
     * Solicita los datos del cliente y maneja posibles errores de SQL durante la adición.
     */
    private static void agregarCliente() {
        vista.mostrarMensaje("\n--- Agregar cliente ---");
        ClienteOtaku cliente = vista.leerDatosNuevoCliente(); // Obtiene los datos del nuevo cliente.
        try {
            clienteDao.agregarCliente(cliente); // Intenta agregar el cliente a través del DAO.
            vista.mostrarMensaje("✅ Cliente agregado correctamente."); // Confirma la operación.
        } catch (SQLException e) {
            vista.mostrarMensaje("❌ Error al agregar cliente: " + e.getMessage()); // Muestra error si falla.
            e.printStackTrace(); // Para depuración.
        }
    }

    /**
     * Permite al usuario buscar un cliente específico por su ID.
     * Muestra los detalles del cliente si es encontrado, o un mensaje de error si no existe o hay un problema de SQL.
     */
    private static void buscarClientePorId() {
        int id = vista.leerEntero("ID del cliente a buscar: "); // Solicita el ID del cliente.
        ClienteOtaku cliente = null;
        try {
            cliente = clienteDao.buscarClientePorId(id); // Intenta buscar el cliente por ID.
        } catch (SQLException e) {
            vista.mostrarMensaje("❌ Error al buscar cliente: " + e.getMessage()); // Muestra error si falla.
            e.printStackTrace(); // Para depuración.
        }

        if (cliente != null) {
            vista.mostrarCliente(cliente); // Muestra los detalles del cliente.
        } else {
            vista.mostrarMensaje("❌ Cliente no encontrado."); // Mensaje si no se encuentra.
        }
    }

    /**
     * Recupera y muestra en la consola una lista de todos los clientes registrados en la base de datos.
     * Maneja posibles errores de SQL durante la recuperación.
     */
    private static void listarClientes() {
        List<ClienteOtaku> lista = null;
        try {
            lista = clienteDao.listarClientes(); // Intenta obtener la lista de clientes.
        } catch (SQLException e) {
            vista.mostrarMensaje("❌ Error al listar clientes: " + e.getMessage()); // Muestra error si falla.
            e.printStackTrace(); // Para depuración.
        }
        vista.mostrarListaClientes(lista); // Muestra la lista de clientes.
    }

    /**
     * Permite al usuario actualizar los datos de un cliente existente.
     * Primero busca el cliente por ID, luego permite editar sus campos y finalmente guarda los cambios.
     */
    private static void actualizarCliente() {
        vista.mostrarMensaje("\n--- Actualizar cliente ---");
        int id = vista.leerEntero("ID del cliente a actualizar: "); // Solicita el ID del cliente.
        ClienteOtaku clienteExistente = null;
        try {
            clienteExistente = clienteDao.buscarClientePorId(id); // Busca el cliente existente.
        } catch (SQLException e) {
            vista.mostrarMensaje("❌ Error al buscar cliente para actualizar: " + e.getMessage()); // Muestra error si falla.
            e.printStackTrace(); // Para depuración.
        }

        if (clienteExistente != null) {
            // Obtiene los datos actualizados del cliente a través de la vista.
            ClienteOtaku clienteActualizado = vista.leerDatosActualizarCliente(clienteExistente);
            try {
                // Intenta actualizar el cliente en la base de datos.
                boolean actualizado = clienteDao.actualizarCliente(clienteActualizado);
                vista.mostrarMensaje(actualizado ? "✅ Cliente actualizado correctamente." : "❌ Fallo al actualizar el cliente.");
            } catch (SQLException e) {
                vista.mostrarMensaje("❌ Error al actualizar cliente: " + e.getMessage()); // Muestra error si falla.
                e.printStackTrace(); // Para depuración.
            }
        } else {
            vista.mostrarMensaje("❌ Cliente no encontrado."); // Mensaje si el cliente no existe.
        }
    }

    /**
     * Permite al usuario eliminar un cliente existente por su ID.
     * Confirma si la eliminación fue exitosa o si el cliente no fue encontrado o no pudo ser eliminado.
     */
    private static void eliminarCliente() {
        vista.mostrarMensaje("\n--- Eliminar cliente ---");
        int id = vista.leerEntero("ID del cliente a eliminar: "); // Solicita el ID del cliente a eliminar.
        try {
            boolean eliminado = clienteDao.eliminarCliente(id); // Intenta eliminar el cliente.
            vista.mostrarMensaje(eliminado ? "🗑️ Cliente eliminado correctamente." : "❌ Cliente no encontrado o no se pudo eliminar.");
        } catch (SQLException e) {
            vista.mostrarMensaje("❌ Error al eliminar cliente: " + e.getMessage()); // Muestra error si falla.
            e.printStackTrace(); // Para depuración.
        }
    }
}