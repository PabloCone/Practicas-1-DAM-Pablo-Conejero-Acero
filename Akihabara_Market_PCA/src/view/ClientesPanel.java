package view;

import dao.ClienteDAO;
import model.ClienteOtaku;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientesPanel extends JPanel {

    private ClienteDAO clienteDao;

    private JTextField txtClienteId, txtClienteNombre, txtClienteEmail, txtClienteTelefono;
    private JButton btnAgregarCliente, btnEditarCliente, btnEliminarCliente, btnLimpiarCampos;
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;

    /**
     * Constructor de la clase ClientesPanel.
     * Inicializa el DAO de clientes, configura el diseño del panel,
     * y añade los subpaneles para el formulario y la tabla de clientes.
     * También carga los datos de los clientes al iniciar.
     */
    public ClientesPanel() {
        this.clienteDao = new ClienteDAO(); // ClienteDAO ya no necesita Connection en el constructor
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen alrededor del panel

        add(crearPanelFormularioClientes(), BorderLayout.NORTH);
        add(crearPanelTablaClientes(), BorderLayout.CENTER);

        cargarClientes(); // Cargar clientes automáticamente al iniciar el panel
    }

    /**
     * Crea y devuelve un JPanel que contiene los campos del formulario
     * para la gestión de clientes (ID, Nombre, Email, Teléfono) y los botones de acción.
     * @return JPanel con el formulario y botones de clientes.
     */
    private JPanel crearPanelFormularioClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Espacio entre componentes
        panel.setBorder(BorderFactory.createTitledBorder("Gestión de Clientes"));

        JPanel campos = new JPanel(new GridLayout(4, 2, 10, 10)); // 4 filas para ID, Nombre, Email, Teléfono
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen interno

        campos.add(new JLabel("ID (solo lectura):"));
        txtClienteId = new JTextField();
        txtClienteId.setEditable(false); // ID no editable, se asigna al seleccionar o para edición
        campos.add(txtClienteId);

        campos.add(new JLabel("Nombre:"));
        txtClienteNombre = new JTextField();
        campos.add(txtClienteNombre);

        campos.add(new JLabel("Email:"));
        txtClienteEmail = new JTextField();
        campos.add(txtClienteEmail);

        campos.add(new JLabel("Teléfono:"));
        txtClienteTelefono = new JTextField();
        campos.add(txtClienteTelefono);

        panel.add(campos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centrar botones
        btnAgregarCliente = new JButton("Agregar Cliente");
        btnEditarCliente = new JButton("Editar Cliente");
        btnEliminarCliente = new JButton("Eliminar Cliente");
        btnLimpiarCampos = new JButton("Limpiar Campos");

        panelBotones.add(btnAgregarCliente);
        panelBotones.add(btnEditarCliente);
        panelBotones.add(btnEliminarCliente);
        panelBotones.add(btnLimpiarCampos);

        panel.add(panelBotones, BorderLayout.SOUTH);

        // Asignar listeners a los botones
        btnAgregarCliente.addActionListener(e -> agregarCliente());
        btnEditarCliente.addActionListener(e -> editarCliente());
        btnEliminarCliente.addActionListener(e -> eliminarClienteSeleccionado());
        btnLimpiarCampos.addActionListener(e -> limpiarCamposCliente());

        return panel;
    }

    /**
     * Crea y devuelve un JScrollPane que contiene la JTable para mostrar la lista de clientes.
     * Configura el modelo de la tabla y añade un listener para manejar la selección de filas.
     * @return JScrollPane con la tabla de clientes.
     */
    private JScrollPane crearPanelTablaClientes() {
        // Definimos columnas para la tabla de clientes
        // "Fecha de Registro" es el nuevo campo
        modeloClientes = new DefaultTableModel(new Object[]{"ID", "Nombre", "Email", "Teléfono", "Fecha de Registro"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que las celdas de la tabla no sean editables
            }
        };
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable

        // Evento para cuando se selecciona una fila, rellenar formulario con los datos
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    txtClienteId.setText(modeloClientes.getValueAt(fila, 0).toString());
                    txtClienteNombre.setText(modeloClientes.getValueAt(fila, 1).toString());
                    txtClienteEmail.setText(modeloClientes.getValueAt(fila, 2).toString());
                    txtClienteTelefono.setText(modeloClientes.getValueAt(fila, 3).toString());
                    // La fecha de registro no se edita directamente desde el formulario
                }
            }
        });

        return new JScrollPane(tablaClientes);
    }

    // Métodos para manejo de Clientes

    /**
     * Maneja la lógica para agregar un nuevo cliente a la base de datos.
     * Realiza validaciones de campos y muestra mensajes de éxito o error.
     */
    private void agregarCliente() {
        if (!validarCamposCliente()) {
            return; // Si la validación falla, salir
        }

        try {
            String nombre = txtClienteNombre.getText().trim();
            String email = txtClienteEmail.getText().trim();
            int telefono = Integer.parseInt(txtClienteTelefono.getText().trim());

            ClienteOtaku cliente = new ClienteOtaku(nombre, email, telefono);
            clienteDao.agregarCliente(cliente);

            JOptionPane.showMessageDialog(this, "Cliente agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarClientes();
            limpiarCamposCliente();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El teléfono debe ser un número entero válido.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { // SQLState para violación de restricción de integridad (ej. email duplicado)
                JOptionPane.showMessageDialog(this, "Error: El email proporcionado ya está registrado. Por favor, use otro.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al agregar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Maneja la lógica para editar un cliente existente en la base de datos.
     * Requiere que se seleccione una fila en la tabla y valida los campos de entrada.
     * Muestra mensajes de éxito o error, incluyendo el manejo de emails duplicados.
     */
    private void editarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCamposCliente()) {
            return; // Si la validación falla, salir
        }

        try {
            int id = Integer.parseInt(txtClienteId.getText());
            String nombre = txtClienteNombre.getText().trim();
            String email = txtClienteEmail.getText().trim();
            int telefono = Integer.parseInt(txtClienteTelefono.getText().trim());

            // Si ClienteOtaku requiere fechaRegistro para su constructor de actualización, deberías recuperarla.
            // Para este ejemplo, como la BD maneja DEFAULT CURRENT_TIMESTAMP, no se pasa en el constructor
            // pero ClienteOtaku sigue teniendo su getter/setter.
            ClienteOtaku cliente = new ClienteOtaku(id, nombre, email, telefono, null); // Fecha de registro se ignora si la DB la maneja

            boolean actualizado = clienteDao.actualizarCliente(cliente);

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
                limpiarCamposCliente();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente.", "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El ID y el teléfono deben ser números enteros válidos.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { // SQLState para violación de restricción de integridad (ej. email duplicado)
                JOptionPane.showMessageDialog(this, "Error: El email proporcionado ya está registrado para otro cliente.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al editar cliente: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al editar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Maneja la lógica para eliminar un cliente seleccionado de la tabla.
     * Solicita confirmación al usuario antes de proceder con la eliminación.
     * Muestra mensajes de éxito o error.
     */
    private void eliminarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloClientes.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar el cliente con ID: " + id + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean eliminado = clienteDao.eliminarCliente(id);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarClientes();
                    limpiarCamposCliente();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente. Es posible que no exista.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error inesperado al eliminar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Carga todos los clientes desde la base de datos y los muestra en la tabla.
     * Limpia las filas existentes antes de añadir los nuevos datos.
     * Formatea la fecha de registro para su visualización.
     */
    public void cargarClientes() {
        try {
            List<ClienteOtaku> clientes = clienteDao.listarClientes();
            modeloClientes.setRowCount(0); // Limpiar filas existentes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Formato para la fecha

            for (ClienteOtaku cliente : clientes) {
                String fechaRegistroStr = (cliente.getFechaRegistro() != null) ?
                                          cliente.getFechaRegistro().format(formatter) : "N/A";

                Object[] rowData = {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getEmail(),
                    cliente.getTelefono(),
                    fechaRegistroStr
                };
                modeloClientes.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al cargar clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Limpia todos los campos de texto del formulario de clientes
     * y deselecciona cualquier fila en la tabla.
     */
    private void limpiarCamposCliente() {
        txtClienteId.setText("");
        txtClienteNombre.setText("");
        txtClienteEmail.setText("");
        txtClienteTelefono.setText("");
        tablaClientes.clearSelection(); // Deseleccionar cualquier fila
    }

    /**
     * Valida los campos de entrada del formulario de cliente.
     * Verifica que los campos obligatorios no estén vacíos, el formato del email
     * y que el teléfono contenga solo números y tenga una longitud válida.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarCamposCliente() {
        String nombre = txtClienteNombre.getText().trim();
        String email = txtClienteEmail.getText().trim();
        String telefonoStr = txtClienteTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefonoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos (Nombre, Email, Teléfono) son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación de email básico
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(this, "El formato del email no es válido.", "Email Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación de teléfono: solo números
        if (!telefonoStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo números.", "Teléfono Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación de teléfono: longitud (opcional, ajusta según tu necesidad)
        if (telefonoStr.length() < 7 || telefonoStr.length() > 15) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener entre 7 y 15 dígitos.", "Teléfono Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(telefonoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El teléfono ingresado es demasiado grande o no es un número válido.", "Teléfono Inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}