package view;

import java.util.List;
import java.util.Scanner;

import model.ClienteOtaku;
import model.ProductoOtaku;

/**
 * Clase que maneja la interacción con el usuario a través de la consola.
 * Permite mostrar menús, leer datos y mostrar resultados.
 */
public class InterfazConsola {
    private Scanner scanner = new Scanner(System.in); // Scanner para entrada por consola

    // --- MENÚ PRINCIPAL ---
    /**
     * Muestra el menú principal con las opciones disponibles para el usuario.
     */
    public void mostrarMenu() {
        System.out.println("""
                        === AKIHABARA MARKET - Menú Principal ===
                        1. Agregar nuevo producto
                        2. Buscar producto por ID
                        3. Listar todos los productos
                        4. Actualizar producto
                        5. Eliminar producto
                        6. Buscar productos por nombre
                        7. Asistente IA
                        8. Submenú de clientes
                        0. Salir
                        """);
    }

    // --- SUBMENÚ CLIENTES ---
    /**
     * Muestra el submenú para gestión de clientes, con opciones específicas.
     */
    public void mostrarSubmenuClientes() {
        System.out.println("""
                        === 👥 Gestión de Clientes ===
                        1. Agregar nuevo cliente
                        2. Buscar cliente por ID
                        3. Listar todos los clientes
                        4. Actualizar cliente
                        5. Eliminar cliente
                        0. Volver al menú principal
                        """);
    }

    // --- ENTRADAS DE DATOS ---
    /**
     * Solicita al usuario que elija una opción y la lee como entero.
     * @return la opción seleccionada
     */
    public int leerOpcion() {
        System.out.print("Elige una opción: ");
        return leerEntero();
    }

    /**
     * Lee los datos de un producto desde la consola y crea un objeto ProductoOtaku.
     * @return el producto leído
     */
    public ProductoOtaku leerDatosProducto() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Categoría: ");
        String categoria = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = leerDouble();
        System.out.print("Stock: ");
        int stock = leerEntero();
        return new ProductoOtaku(nombre, categoria, precio, stock);
    }

    /**
     * Lee los datos de un cliente desde la consola y crea un objeto ClienteOtaku.
     * Este método es para *agregar* un nuevo cliente, por lo que el ID puede ser 0 o ignorado si la base de datos lo genera.
     * @return el cliente leído
     */
     public ClienteOtaku leerDatosNuevoCliente() { // Renombrado para mayor claridad
        System.out.print("Nombre del cliente: ");
        String nombre = scanner.nextLine();
        System.out.print("Email del cliente: ");
        String email = scanner.nextLine();
        System.out.print("Teléfono del cliente: ");
        int telefono = leerEntero();
        // El ID se suele generar en la base de datos para nuevos clientes, por eso se pasa 0 o se ignora en el constructor.
        // Si tu constructor de ClienteOtaku requiere un ID único al crear, deberías manejarlo aquí o en el DAO.
        return new ClienteOtaku(0, nombre, email, telefono, null); // ID 0 o placeholder
    }

    /**
     * Lee los datos de un cliente desde la consola para *actualizar* un cliente existente.
     * @param clienteExistente El objeto ClienteOtaku con los datos actuales para mostrar al usuario.
     * @return un nuevo objeto ClienteOtaku con los datos actualizados, manteniendo el ID original.
     */
    public ClienteOtaku leerDatosActualizarCliente(ClienteOtaku clienteExistente) {
        System.out.println("Cliente actual: " + clienteExistente);
        System.out.print("Nuevo nombre (" + clienteExistente.getNombre() + "): ");
        String nombre = leerTextoConDefault(clienteExistente.getNombre());
        System.out.print("Nuevo email (" + clienteExistente.getEmail() + "): ");
        String email = leerTextoConDefault(clienteExistente.getEmail());
        System.out.print("Nuevo teléfono (" + clienteExistente.getTelefono() + "): ");
        int telefono = leerEnteroConDefault(clienteExistente.getTelefono());
        return new ClienteOtaku(clienteExistente.getId(), nombre, email, telefono, null);
    }

    // --- MOSTRAR PRODUCTOS ---
    /**
     * Muestra en consola la información de un producto.
     * @param producto producto a mostrar
     */
    public void mostrarProducto(ProductoOtaku producto) {
        if (producto != null) {
            System.out.printf("ID: %d%n", producto.getId());
            System.out.printf("Nombre: %s%n", producto.getNombre());
            System.out.printf("Categoría: %s%n", producto.getCategoria());
            System.out.printf("Precio: %.2f€%n", producto.getPrecio());
            System.out.printf("Stock: %d unidades%n", producto.getStock());
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    /**
     * Muestra en consola una lista de productos.
     * Si la lista está vacía, muestra mensaje indicándolo.
     * @param productos lista de productos a mostrar
     */
    public void mostrarListaProductos(List<ProductoOtaku> productos) {
        if (productos == null || productos.isEmpty()) {
            System.out.println("⚠️ No hay productos registrados.");
        } else {
            System.out.printf("%-5s %-30s %-15s %-10s %-6s%n", "ID", "Nombre", "Categoría", "Precio", "Stock");
            System.out.println("----------------------------------------------------------------------------");
            for (ProductoOtaku p : productos) {
                System.out.printf("%-5d %-30s %-15s %-10.2f %-6d%n",
                        p.getId(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock());
            }
        }
    }

    // --- MOSTRAR CLIENTES ---
    /**
     * Muestra en consola la información de un cliente.
     * @param cliente cliente a mostrar
     */
     public void mostrarCliente(ClienteOtaku cliente) {
        if (cliente != null) {
            System.out.printf("\n🧍 Cliente encontrado:\n");
            System.out.printf("ID: %d%n", cliente.getId());
            System.out.printf("Nombre: %s%n", cliente.getNombre());
            System.out.printf("Email: %s%n", cliente.getEmail());
            System.out.printf("Teléfono: %d%n", cliente.getTelefono());
            if (cliente.getFechaRegistro() != null) {
                System.out.printf("Fecha de registro: %s%n", cliente.getFechaRegistro().toString());
            } else {
                System.out.println("Fecha de registro: N/A");
            }
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    /**
     * Muestra en consola una lista de clientes.
     * Si la lista está vacía, muestra mensaje indicándolo.
     * @param clientes lista de clientes a mostrar
     */
    public void mostrarListaClientes(List<ClienteOtaku> clientes) {
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("⚠️ No hay clientes registrados.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-10s %-25s%n", "ID", "Nombre", "Email", "Teléfono", "Fecha Registro");
            System.out.println("-----------------------------------------------------------------------------------------");
            for (ClienteOtaku c : clientes) {
                System.out.printf("%-5d %-20s %-30s %-10d %-25s%n",
                        c.getId(),
                        c.getNombre(),
                        c.getEmail(),
                        c.getTelefono(),
                        c.getFechaRegistro() != null ? c.getFechaRegistro().toString() : "N/A"
                );
            }
        }
    }

    // --- UTILIDADES ---
    /**
     * Muestra un mensaje genérico por consola.
     * @param mensaje mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Muestra un mensaje y lee un entero desde la consola.
     * @param mensaje mensaje para solicitar dato
     * @return número entero leído
     */
    public int leerEntero(String mensaje) {
        System.out.print(mensaje);
        return leerEntero();
    }

    /**
     * Muestra un mensaje y lee un número decimal (double) desde la consola.
     * @param mensaje mensaje para solicitar dato
     * @return número decimal leído
     */
    public double leerDouble(String mensaje) {
        System.out.print(mensaje);
        return leerDouble();
    }

    /**
     * Muestra un mensaje y lee una cadena de texto desde la consola.
     * @param mensaje mensaje para solicitar dato
     * @return texto leído
     */
    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Lee un texto, permitiendo al usuario presionar Enter para usar un valor por defecto.
     * @param defaultValue El valor por defecto a usar si el usuario presiona Enter.
     * @return El texto introducido por el usuario o el valor por defecto.
     */
    private String leerTextoConDefault(String defaultValue) {
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Lee un entero, permitiendo al usuario presionar Enter para usar un valor por defecto.
     * @param defaultValue El valor por defecto a usar si el usuario presiona Enter.
     * @return El entero introducido por el usuario o el valor por defecto.
     */
    private int leerEnteroConDefault(int defaultValue) {
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Ingresa un número válido o presiona Enter para usar el valor predeterminado: ");
            }
        }
    }

    /**
     * Lee un entero de forma segura, repitiendo la lectura hasta que sea válido.
     * @return entero válido leído
     */
    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Ingresa un número válido: ");
            }
        }
    }

    /**
     * Lee un número decimal (double) de forma segura, repitiendo la lectura hasta que sea válido.
     * @return double válido leído
     */
    private double leerDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Ingresa un número decimal válido: ");
            }
        }
    }
}