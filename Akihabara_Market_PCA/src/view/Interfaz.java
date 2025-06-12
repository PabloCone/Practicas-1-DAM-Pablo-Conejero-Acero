package view;

// Importamos clases para la gestión de clientes y productos
import dao.ClienteDAO; // DAO para operaciones de base de datos de clientes.
import dao.ProductoDAO; // DAO para operaciones de base de datos de productos.
import dao.ConexionBD; // Clase para gestionar la conexión a la base de datos.

// Modelos para cliente y producto
import model.ClienteOtaku; // Clase que representa la entidad Cliente.
import model.ProductoOtaku; // Clase que representa la entidad Producto.
import service.LlmService; // Servicio para interactuar con la IA.

import javax.swing.*; // Importa todas las clases del paquete Swing para crear interfaces gráficas de usuario.
import javax.swing.table.DefaultTableModel; // Para definir el modelo de datos de las tablas Swing.
import java.awt.*; // Importa clases para gráficos y UI de AWT (layouts, colores, etc.).
import java.awt.event.MouseAdapter; // Clase abstracta para escuchar eventos de ratón.
import java.awt.event.MouseEvent; // Clase que representa un evento de ratón.
import java.sql.Connection; // Para gestionar la conexión a la base de datos JDBC.
import java.sql.SQLException; // Excepción para errores de acceso a la base de datos.
import java.util.List; // Para trabajar con colecciones de objetos.
import java.util.concurrent.ExecutionException; // Para manejar excepciones de SwingWorker.

/**
 * Clase principal que crea la interfaz gráfica con pestañas para gestionar clientes y productos.
 * Extiende JFrame para ser la ventana principal de la aplicación.
 */
public class Interfaz extends JFrame {

    // ClienteDAO y ProductoDAO ahora solo se usan para las operaciones de negocio.
    private ProductoDAO productoDao = new ProductoDAO(); // Se mantiene para la gestión de productos directamente en esta interfaz.

    // --- Componentes gráficos para la Gestión de Productos ---
    private JTextField txtProductoIdDisplay; // Campo para mostrar el ID del producto (solo lectura).
    private JTextField txtProductoNombre, txtProductoCategoria, txtProductoPrecio, txtProductoStock;
    private JButton btnAgregarProducto, btnActualizarProducto, btnEliminarProducto;
    private JButton btnLimpiarCamposProductos; // Botón para limpiar los campos del formulario de productos.
    private JTable tablaProductos1; // Tabla para mostrar productos.
    private DefaultTableModel modeloProductos1; // Modelo de datos para la tabla de productos.

    // --- Componentes para la funcionalidad de Búsqueda por ID de Producto ---
    private JButton btnBuscarProductoPorId;
    private JTextField txtBuscarProductoId; // Este es el campo de entrada para el ID de búsqueda.
    private JButton btnBuscarProductoConfirmar;

    // --- Componentes gráficos para el Asistente IA (integrados en la misma pestaña de Productos) ---
    private JButton btnGenerarDescripcionIA; // Botón para activar la generación de descripción.
    private JButton btnSugerirCategoriaIA; // Botón para activar la sugerencia de categoría.


    // Instancia del ClientesPanel, que gestiona su propia UI y lógica de clientes.
    private ClientesPanel clientesPanel;


    /**
     * Constructor de la clase Interfaz.
     * Configura las propiedades básicas de la ventana (título, operación de cierre, tamaño, posición)
     * y crea los paneles con pestañas para la gestión de clientes y productos.
     * También realiza una prueba de conexión a la base de datos al inicio.
     */
    public Interfaz() {
        setTitle("Gestión Otaku"); // Establece el título de la ventana.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Define que la aplicación se cierra al cerrar la ventana.
        setSize(900, 700); // Establece un tamaño inicial un poco más grande para acomodar los nuevos botones.
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.

        // Intenta cargar las configuraciones de la BD al inicio y verificar la conexión.
        // Es una buena práctica verificar si la conexión funciona al inicio para avisar al usuario.
        try (Connection testConn = ConexionBD.getConnection()) {
            // Si la conexión es exitosa, se imprime un mensaje en consola.
            System.out.println("Conexión a la base de datos establecida correctamente.");
        } catch (SQLException e) {
            // Si hay un error de conexión, muestra un mensaje al usuario y registra la excepción.
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime el stack trace para depuración.
            // System.exit(1); // Descomenta esta línea si quieres que la aplicación se cierre al fallar la conexión.
        }

        // Creamos un contenedor con pestañas para organizar la interfaz.
        JTabbedPane pestañas = new JTabbedPane();

        // Creamos una instancia de ClientesPanel y la añadimos como una pestaña.
        clientesPanel = new ClientesPanel();
        pestañas.addTab("Clientes", clientesPanel);

        // Creamos el panel para la gestión de productos.
        JPanel panelProductos = new JPanel(new BorderLayout(10, 10)); // Añadimos espaciado.
        // Añadimos el formulario de productos y los botones (incluyendo los de IA) en la parte NORTH.
        panelProductos.add(crearPanelFormularioProductos(), BorderLayout.NORTH);
        // Añadimos la tabla de productos en el centro.
        panelProductos.add(crearPanelTablaProductos(), BorderLayout.CENTER);
        // Añadimos el panel de productos como una pestaña.
        pestañas.addTab("Productos", panelProductos);

        // Añadimos el contenedor de pestañas a la ventana principal.
        add(pestañas);

        // Hacemos visible la ventana principal.
        setVisible(true);

        // Cargar productos automáticamente al inicio de la aplicación.
        try {
            cargarProductos();
        } catch (SQLException ex) {
            // Manejo de errores si la carga inicial de productos falla.
            JOptionPane.showMessageDialog(this, "Error al cargar productos al inicio: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Crea el panel con el formulario para productos, incluyendo sus campos de entrada (ID, Nombre, Categoría, Precio, Stock)
     * y los botones de acción (Agregar, Editar, Eliminar, Limpiar Campos, Buscar por ID, Buscar, Generar Descripción IA, Sugerir Categoría IA).
     * Sigue la estética del ClientesPanel.
     * @return JPanel que contiene el formulario y los controles para productos.
     */
    private JPanel crearPanelFormularioProductos() {
        // Panel principal para el formulario de productos, con BorderLayout y espaciado.
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Productos")); // Título del borde del panel.

        // Panel para las etiquetas y campos de entrada de datos del producto.
        // Utiliza GridLayout para organizar los campos en 5 filas y 2 columnas (ID, Nombre, Categoría, Precio, Stock).
        JPanel campos = new JPanel(new GridLayout(5, 2, 10, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen interno.

        // Campo de ID (solo lectura), similar al ID de cliente.
        campos.add(new JLabel("ID (solo lectura):"));
        txtProductoIdDisplay = new JTextField();
        txtProductoIdDisplay.setEditable(false);
        campos.add(txtProductoIdDisplay);

        // Campos de texto para ingresar los datos del producto.
        campos.add(new JLabel("Nombre:"));
        txtProductoNombre = new JTextField();
        campos.add(txtProductoNombre);

        campos.add(new JLabel("Categoría:"));
        txtProductoCategoria = new JTextField();
        campos.add(txtProductoCategoria);

        campos.add(new JLabel("Precio:"));
        txtProductoPrecio = new JTextField();
        campos.add(txtProductoPrecio);

        campos.add(new JLabel("Stock:"));
        txtProductoStock = new JTextField();
        campos.add(txtProductoStock);

        panel.add(campos, BorderLayout.CENTER); // Añade el panel de campos al centro del panel principal del formulario.

        // Panel para los botones de acción de productos.
        // Utiliza FlowLayout para centrar los botones con espaciado.
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Botones principales para operaciones CRUD y limpiar campos.
        btnAgregarProducto = new JButton("Agregar Producto");
        btnActualizarProducto = new JButton("Editar Producto");
        btnEliminarProducto = new JButton("Eliminar Producto");
        btnLimpiarCamposProductos = new JButton("Limpiar Campos");

        // Añadimos los botones principales al panel de botones.
        panelBotones.add(btnAgregarProducto);
        panelBotones.add(btnActualizarProducto);
        panelBotones.add(btnEliminarProducto);
        panelBotones.add(btnLimpiarCamposProductos);

        // --- Botones de Asistente IA ---
        btnGenerarDescripcionIA = new JButton("Generar Descripción IA");
        btnSugerirCategoriaIA = new JButton("Sugerir Categoría IA");

        panelBotones.add(btnGenerarDescripcionIA);
        panelBotones.add(btnSugerirCategoriaIA);


        // Panel para agrupar los componentes de búsqueda por ID (inicialmente ocultos).
        JPanel panelBusquedaId = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Espaciado más pequeño dentro del grupo de búsqueda.
        btnBuscarProductoPorId = new JButton("Buscar por ID");
        txtBuscarProductoId = new JTextField(5); // Este es el campo de entrada real para el ID de búsqueda.
        txtBuscarProductoId.setVisible(false); // Oculto por defecto.
        btnBuscarProductoConfirmar = new JButton("Buscar"); // Botón para confirmar la búsqueda.
        btnBuscarProductoConfirmar.setVisible(false); // Oculto por defecto.

        panelBusquedaId.add(btnBuscarProductoPorId);
        panelBusquedaId.add(txtBuscarProductoId);
        panelBusquedaId.add(btnBuscarProductoConfirmar);

        // Añade el panel de búsqueda por ID al panel principal de botones.
        panelBotones.add(panelBusquedaId);

        panel.add(panelBotones, BorderLayout.SOUTH); // Añade el panel de botones a la parte inferior del panel del formulario.

        // --- Listeners para los botones de productos y la funcionalidad de búsqueda ---

        // Listener para el botón "Agregar producto".
        btnAgregarProducto.addActionListener(e -> agregarProducto());

        // Listener para el botón "Editar Producto".
        btnActualizarProducto.addActionListener(e -> actualizarProducto());

        // Listener para el botón "Eliminar Producto".
        btnEliminarProducto.addActionListener(e -> eliminarProductoSeleccionado());

        // Listener para el nuevo botón "Limpiar Campos" de productos.
        btnLimpiarCamposProductos.addActionListener(e -> limpiarCamposProducto());

        // Listener para el botón "Buscar por ID". Al pulsarlo, hace visible el campo de ID y el botón de confirmar.
        btnBuscarProductoPorId.addActionListener(e -> {
            txtBuscarProductoId.setVisible(true);
            btnBuscarProductoConfirmar.setVisible(true);
            txtBuscarProductoId.requestFocusInWindow(); // Pone el foco en el campo de texto para que el usuario pueda escribir.
            pack(); // Ajusta el tamaño de la ventana para acomodar los nuevos componentes visibles.
        });

        // Listener para el botón "Confirmar búsqueda por ID".
        btnBuscarProductoConfirmar.addActionListener(e -> buscarProductoPorId());

        // --- Listeners para los botones de Asistente IA ---
        btnGenerarDescripcionIA.addActionListener(e -> {
            int selectedRow = tablaProductos1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(Interfaz.this, "Selecciona un producto de la tabla para generar la descripción IA.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productoId = (int) modeloProductos1.getValueAt(selectedRow, 0); // Obtener ID del producto seleccionado.
            generarDescripcionIA(productoId); // Llamar al método de IA con el ID.
        });

        btnSugerirCategoriaIA.addActionListener(e -> {
            int selectedRow = tablaProductos1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(Interfaz.this, "Selecciona un producto de la tabla para sugerir la categoría IA.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String productoNombre = (String) modeloProductos1.getValueAt(selectedRow, 1); // Obtener nombre del producto seleccionado.
            sugerirCategoriaIA(productoNombre); // Llamar al método de IA con el nombre.
        });


        return panel;
    }


    /**
     * Crea la tabla que mostrará la lista de productos y configura su modelo de datos.
     * También asigna un listener para el ratón para rellenar automáticamente el formulario
     * con los datos de un producto cuando se selecciona una fila en la tabla.
     * @return JScrollPane que contiene la JTable de productos.
     */
    private JScrollPane crearPanelTablaProductos() {
        // Definimos las columnas para la tabla de productos.
        modeloProductos1 = new DefaultTableModel(new Object[]{"ID", "Nombre", "Categoría", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas de la tabla no sean editables directamente por el usuario.
            }
        };
        tablaProductos1 = new JTable(modeloProductos1);
        tablaProductos1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite seleccionar solo una fila a la vez.

        // Evento de click en una fila de la tabla para cargar los datos del producto en el formulario.
        tablaProductos1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos1.getSelectedRow(); // Obtiene el índice de la fila seleccionada.
                if (fila >= 0) { // Si se ha seleccionado una fila válida.
                    // Carga los datos de la fila seleccionada en los campos de texto del formulario.
                    txtProductoIdDisplay.setText(modeloProductos1.getValueAt(fila, 0).toString()); // Carga el ID en el campo de display.
                    txtProductoNombre.setText(modeloProductos1.getValueAt(fila, 1).toString());
                    txtProductoCategoria.setText(modeloProductos1.getValueAt(fila, 2).toString());
                    txtProductoPrecio.setText(modeloProductos1.getValueAt(fila, 3).toString());
                    txtProductoStock.setText(modeloProductos1.getValueAt(fila, 4).toString());

                    // Oculta el campo de búsqueda por ID y su botón de confirmar si estaban visibles.
                    txtBuscarProductoId.setVisible(false);
                    btnBuscarProductoConfirmar.setVisible(false);
                }
            }
        });

        return new JScrollPane(tablaProductos1); // Envuelve la tabla en un JScrollPane para permitir el desplazamiento.
    }

    // --- Métodos para Manejo de Productos ---

    /**
     * Agrega un nuevo producto a la base de datos.
     * Este método obtiene los datos del formulario de productos, valida su formato,
     * crea un objeto ProductoOtaku y lo guarda utilizando el ProductoDAO.
     * Finalmente, actualiza la tabla de productos y limpia el formulario.
     */
    private void agregarProducto() {
        if (!validarCamposProducto()) { // Valida que los campos no estén vacíos y tengan formato correcto.
            return; // Sale del método si la validación falla.
        }
        try {
            // Obtenemos los datos del formulario de producto, eliminando espacios en blanco extra.
            String nombre = txtProductoNombre.getText().trim();
            String categoria = txtProductoCategoria.getText().trim();
            double precio = Double.parseDouble(txtProductoPrecio.getText().trim()); // Convierte el texto del precio a double.
            int stock = Integer.parseInt(txtProductoStock.getText().trim());        // Convierte el texto del stock a int.

            // Creamos un nuevo objeto ProductoOtaku. El ID se pasa como 0, ya que será asignado por la base de datos (autoincremento).
            ProductoOtaku producto = new ProductoOtaku(0, nombre, categoria, precio, stock);

            // Guarda el producto en la base de datos a través del DAO.
            productoDao.agregarProducto(producto);

            // Muestra un mensaje de éxito al usuario.
            JOptionPane.showMessageDialog(this, "Producto agregado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCamposProducto(); // Limpia los campos del formulario después de la adición exitosa.

            // Recarga la tabla de productos para mostrar el nuevo producto.
            cargarProductos();

        } catch (NumberFormatException ex) {
            // Captura errores si el precio o el stock no se pueden convertir a número.
            JOptionPane.showMessageDialog(this, "Error: Ingrese un número válido para el precio y el stock.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Captura cualquier otro error general durante la operación de agregar.
            JOptionPane.showMessageDialog(this, "No se pudo agregar producto. Consulte el registro de errores.", "Error al agregar producto", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime el stack trace para depuración.
        }
    }

    /**
     * Busca un producto por su ID, el cual se ingresa en el campo de texto `txtBuscarProductoId`.
     * Si el producto es encontrado, sus datos se cargan en el formulario principal de productos
     * y la fila correspondiente en la tabla es seleccionada.
     */
    private void buscarProductoPorId() {
        String idStr = txtBuscarProductoId.getText().trim(); // Obtiene el texto del campo de ID de búsqueda.
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID para buscar.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr); // Convierte el texto del ID a entero.

            // Obtiene el producto de la base de datos utilizando el DAO.
            ProductoOtaku producto = productoDao.obtenerProductoPorId(id);

            if (producto != null) {
                // Si el producto es encontrado, rellena los campos del formulario con sus datos.
                txtProductoIdDisplay.setText(String.valueOf(producto.getId())); // Rellena el campo de ID de display.
                txtProductoNombre.setText(producto.getNombre());
                txtProductoCategoria.setText(producto.getCategoria());
                txtProductoPrecio.setText(String.valueOf(producto.getPrecio()));
                txtProductoStock.setText(String.valueOf(producto.getStock()));

                // Busca la fila en la tabla que corresponde al ID encontrado y la selecciona.
                boolean foundInTable = false;
                for (int i = 0; i < modeloProductos1.getRowCount(); i++) {
                    if ((int) modeloProductos1.getValueAt(i, 0) == id) {
                        tablaProductos1.setRowSelectionInterval(i, i); // Selecciona la fila 'i'.
                        foundInTable = true;
                        break;
                    }
                }
                if (!foundInTable) {
                    JOptionPane.showMessageDialog(this, "Producto encontrado en la base de datos, pero no visible en la tabla actual.", "Información", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Producto encontrado.", "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
                }

                // Oculta el campo de búsqueda de ID y su botón después de la operación.
                txtBuscarProductoId.setText(""); // Limpia el campo.
                txtBuscarProductoId.setVisible(false);
                btnBuscarProductoConfirmar.setVisible(false);
                pack(); // Ajusta el tamaño de la ventana.

            } else {
                // Si el producto no es encontrado, muestra un mensaje y limpia el formulario.
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Búsqueda Fallida", JOptionPane.WARNING_MESSAGE);
                limpiarCamposProducto(); // Limpia los campos si no se encuentra el producto.
            }
        } catch (NumberFormatException ex) {
            // Captura errores si el ID ingresado no es un número válido.
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Captura cualquier otro error general durante la búsqueda.
            JOptionPane.showMessageDialog(this, "Error al buscar producto: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Actualiza la información de un producto seleccionado en la tabla con los datos actuales del formulario.
     * Requiere que una fila esté seleccionada en la tabla.
     */
    private void actualizarProducto() {
        int fila = tablaProductos1.getSelectedRow(); // Obtiene el índice de la fila seleccionada.

        // Verificamos que se haya seleccionado una fila.
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return; // Sale del método si no hay selección.
        }
        if (!validarCamposProducto()) { // Valida los campos del formulario antes de actualizar.
            return;
        }

        try {
            // Obtenemos el ID del producto de la fila seleccionada en la tabla.
            int id = (int) modeloProductos1.getValueAt(fila, 0);

            // Leemos los nuevos datos de los campos del formulario.
            String nombre = txtProductoNombre.getText().trim();
            String categoria = txtProductoCategoria.getText().trim();
            double precio = Double.parseDouble(txtProductoPrecio.getText().trim());
            int stock = Integer.parseInt(txtProductoStock.getText().trim());

            // Creamos un objeto ProductoOtaku con el ID y los datos actualizados.
            ProductoOtaku producto = new ProductoOtaku(id, nombre, categoria, precio, stock);

            // Intenta actualizar el producto en la base de datos a través del DAO.
            boolean actualizado = productoDao.actualizarProducto(producto);

            if (actualizado) {
                // Si la actualización fue exitosa, muestra un mensaje y recarga la tabla.
                JOptionPane.showMessageDialog(this, "Producto actualizado.", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                limpiarCamposProducto(); // Limpia el formulario.
                cargarProductos(); // Recarga los productos para mostrar los cambios.
            } else {
                // Si no se pudo actualizar (ej. el ID no existía), muestra un mensaje de error.
                JOptionPane.showMessageDialog(this, "No se pudo actualizar producto.", "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            // Captura errores si el precio o el stock no son números válidos.
            JOptionPane.showMessageDialog(this, "Ingrese valores válidos para precio y stock.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Captura cualquier otro error general durante la actualización.
            JOptionPane.showMessageDialog(this, "Error al actualizar producto: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Elimina el producto que está seleccionado actualmente en la tabla.
     * Solicita confirmación al usuario antes de proceder con la eliminación.
     */
    private void eliminarProductoSeleccionado() {
        try {
            int fila = tablaProductos1.getSelectedRow(); // Obtiene el índice de la fila seleccionada.

            // Verificamos que haya una fila seleccionada.
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtenemos el ID del producto a eliminar de la fila seleccionada.
            int id = (int) modeloProductos1.getValueAt(fila, 0);

            // Solicita confirmación al usuario antes de eliminar.
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar el producto con ID: " + id + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) { // Si el usuario confirma la eliminación.
                // Intenta eliminar el producto de la base de datos mediante el DAO.
                boolean eliminado = productoDao.eliminarProducto(id);

                if (eliminado) {
                    // Si se eliminó correctamente, muestra un mensaje de éxito y recarga la tabla.
                    JOptionPane.showMessageDialog(this, "Producto eliminado.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCamposProducto(); // Limpia el formulario.
                    cargarProductos(); // Recarga los productos.
                } else {
                    // Si no se pudo eliminar (ej. no encontrado), muestra un mensaje de fallo.
                    JOptionPane.showMessageDialog(this, "Producto no encontrado o no eliminado.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            // Captura cualquier excepción y muestra un mensaje de error.
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Carga todos los productos desde la base de datos y los visualiza en la tabla de productos.
     * Limpia la tabla antes de añadir los nuevos datos para asegurar que esté actualizada.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    private void cargarProductos() throws SQLException {
        // Obtenemos la lista de productos del DAO.
        List<ProductoOtaku> productos = productoDao.obtenerTodosLosProductos();

        // Limpiamos todas las filas previas de la tabla para evitar duplicados.
        modeloProductos1.setRowCount(0);

        // Añadimos cada producto de la lista como una nueva fila en la tabla.
        for (ProductoOtaku producto : productos) {
            Object[] rowData = {
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStock()
            };
            modeloProductos1.addRow(rowData);
        }
    }

    /**
     * Limpia todos los campos de texto del formulario de producto.
     * También oculta los campos de búsqueda por ID si estaban visibles
     * y deselecciona cualquier fila en la tabla.
     */
    private void limpiarCamposProducto() {
        txtProductoIdDisplay.setText("");   // Limpia el campo de ID de display.
        txtProductoNombre.setText("");      // Limpia el campo de nombre.
        txtProductoCategoria.setText("");   // Limpia el campo de categoría.
        txtProductoPrecio.setText("");      // Limpia el campo de precio.
        txtProductoStock.setText("");       // Limpia el campo de stock.

        // Limpia el campo de búsqueda de ID y lo oculta junto a su botón de confirmar.
        txtBuscarProductoId.setText("");
        txtBuscarProductoId.setVisible(false);
        btnBuscarProductoConfirmar.setVisible(false);
        tablaProductos1.clearSelection(); // Deseleccionar la fila de la tabla.

        // Ajusta el tamaño de la ventana si es necesario después de ocultar componentes.
        pack();
    }

    /**
     * Valida los campos de entrada del formulario de producto antes de realizar una operación de guardado o actualización.
     * Verifica que los campos obligatorios no estén vacíos y que los campos numéricos (precio y stock)
     * contengan valores válidos y no negativos.
     * @return true si todos los campos son válidos y cumplen las restricciones, false en caso contrario.
     */
    private boolean validarCamposProducto() {
        String nombre = txtProductoNombre.getText().trim();
        String categoria = txtProductoCategoria.getText().trim();
        String precioStr = txtProductoPrecio.getText().trim();
        String stockStr = txtProductoStock.getText().trim();

        // Verifica si algún campo obligatorio está vacío.
        if (nombre.isEmpty() || categoria.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos (Nombre, Categoría, Precio, Stock) son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Valida el campo de precio.
        try {
            double precio = Double.parseDouble(precioStr);
            if (precio < 0) { // El precio no puede ser negativo.
                JOptionPane.showMessageDialog(this, "El precio no puede ser negativo.", "Precio Inválido", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            // Captura si el precio no es un número válido.
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Valida el campo de stock.
        try {
            int stock = Integer.parseInt(stockStr);
            if (stock < 0) { // El stock no puede ser negativo.
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Stock Inválido", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            // Captura si el stock no es un número entero válido.
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true; // Todos los campos son válidos.
    }

    // --- Métodos para el Asistente IA (Integrados directamente en Interfaz) ---

    /**
     * Genera una descripción de marketing para un producto existente utilizando el servicio de IA.
     * El ID del producto se pasa como parámetro.
     * Muestra la descripción generada en una ventana emergente.
     * Las llamadas a la IA se realizan en un hilo separado para evitar bloquear la interfaz gráfica.
     * @param productId El ID del producto para el cual generar la descripción.
     */
    private void generarDescripcionIA(int productId) {
        // Muestra un mensaje de "cargando" inmediatamente.
        JDialog loadingDialog = new JDialog(this, "Generando Descripción...", true);
        JLabel loadingLabel = new JLabel("<html><p align=\"center\">Generando descripción con IA...<br>Por favor, espera.</p></html>", SwingConstants.CENTER);
        loadingLabel.setPreferredSize(new Dimension(250, 70));
        loadingDialog.add(loadingLabel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);

        // Usamos SwingWorker para ejecutar la tarea de IA en segundo plano.
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Obtener el producto de la BD (puede lanzar SQLException).
                ProductoOtaku producto = productoDao.obtenerProductoPorId(productId);
                if (producto == null) {
                    throw new IllegalArgumentException("Producto no encontrado con ID: " + productId);
                }
                // Construye el prompt para el servicio de IA.
                String prompt = "Genera una descripción en castellano de marketing breve y atractiva para el producto otaku: " +
                                  producto.getNombre() + " de la categoría " + producto.getCategoria() + ".";
                return LlmService.generarRespuesta(prompt); // Ejecuta la llamada a la IA.
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Cierra el diálogo de carga.
                try {
                    String respuesta = get(); // Obtiene el resultado de la IA.
                    // Muestra la descripción generada en un JOptionPane.
                    JTextArea resultArea = new JTextArea(respuesta);
                    resultArea.setWrapStyleWord(true);
                    resultArea.setLineWrap(true);
                    resultArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(resultArea);
                    scrollPane.setPreferredSize(new Dimension(400, 200)); // Tamaño del área de texto.

                    JOptionPane.showMessageDialog(Interfaz.this, scrollPane, "📝 Descripción Generada por IA", JOptionPane.INFORMATION_MESSAGE);

                } catch (InterruptedException | ExecutionException ex) {
                    Throwable cause = ex.getCause(); // Obtener la causa real de la excepción.
                    String errorMessage = "Error al generar descripción: " + (cause != null ? cause.getMessage() : ex.getMessage());
                    JOptionPane.showMessageDialog(Interfaz.this, errorMessage, "Error de IA", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
        loadingDialog.setVisible(true); // Mostrar el diálogo de carga.
    }

    /**
     * Sugiere una categoría para un nuevo producto utilizando el servicio de IA.
     * El nombre del producto se pasa como parámetro.
     * Muestra la categoría sugerida en una ventana emergente.
     * Las llamadas a la IA se realizan en un hilo separado para evitar bloquear la interfaz gráfica.
     * @param productName El nombre del producto para el cual sugerir una categoría.
     */
    private void sugerirCategoriaIA(String productName) {
        // Muestra un mensaje de "cargando" inmediatamente.
        JDialog loadingDialog = new JDialog(this, "Sugiriendo Categoría...", true);
        JLabel loadingLabel = new JLabel("<html><p align=\"center\">Sugiriendo categoría con IA...<br>Por favor, espera.</p></html>", SwingConstants.CENTER);
        loadingLabel.setPreferredSize(new Dimension(250, 70));
        loadingDialog.add(loadingLabel);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);

        // Usamos SwingWorker para ejecutar la tarea de IA en segundo plano.
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Construye el prompt con el nombre del producto y una lista de categorías posibles.
                String prompt = "Para un producto otaku llamado '" + productName +
                                  "', sugiere una categoría adecuada de esta lista: Figura, Manga, Póster, Llavero, Ropa, Videojuego, Otro. Responde solo con la categoría.";
                return LlmService.generarRespuesta(prompt); // Ejecuta la llamada a la IA.
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Cierra el diálogo de carga.
                try {
                    String respuesta = get(); // Obtiene el resultado de la IA.
                    // Limpia la respuesta para asegurar que solo sea la categoría (elimina puntos y espacios extra).
                    String categoriaLimpia = respuesta.replace(".", "").trim();

                    // Muestra la categoría sugerida en un JOptionPane.
                    JOptionPane.showMessageDialog(Interfaz.this, "🧠 Categoría Sugerida por IA:\n" + categoriaLimpia, "Sugerencia de Categoría", JOptionPane.INFORMATION_MESSAGE);

                } catch (InterruptedException | ExecutionException ex) {
                    Throwable cause = ex.getCause(); // Obtener la causa real de la excepción.
                    String errorMessage = "Error al sugerir categoría: " + (cause != null ? cause.getMessage() : ex.getMessage());
                    JOptionPane.showMessageDialog(Interfaz.this, errorMessage, "Error de IA", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
        loadingDialog.setVisible(true); // Mostrar el diálogo de carga.
    }


    /**
     * Método main que inicia la interfaz gráfica de la aplicación.
     * Utiliza `SwingUtilities.invokeLater` para asegurar que la creación y actualización
     * de componentes Swing se realicen en el Event Dispatch Thread (EDT), lo cual es crucial
     * para la estabilidad y el correcto funcionamiento de las aplicaciones Swing.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interfaz()); // Crea y muestra la ventana de Interfaz en el EDT.
    }
}
