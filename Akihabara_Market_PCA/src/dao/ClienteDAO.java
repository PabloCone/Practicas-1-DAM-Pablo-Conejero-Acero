/**
 * ClienteDAO.java
 *
 * Descripción: Esta clase 'ClienteDAO' (Data Access Object) se encarga de todas las operaciones
 * de persistencia de datos relacionadas con la entidad 'ClienteOtaku' en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar) sobre la tabla de clientes,
 * encapsulando la lógica de acceso a la base de datos y manejando las conexiones JDBC.
 * Cada método de operación de base de datos obtiene su propia conexión, lo que asegura un manejo
 * eficiente de los recursos y evita problemas de concurrencia al no mantener una conexión persistente
 * a nivel de instancia de la clase.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import model.ClienteOtaku;

public class ClienteDAO {

    /**
     * Agrega un nuevo cliente a la base de datos.
     * Este método inserta los datos de un objeto ClienteOtaku en la tabla 'clientes'.
     * Utiliza un PreparedStatement para prevenir inyecciones SQL y asegurar una inserción segura de datos.
     *
     * @param cliente El objeto ClienteOtaku que contiene la información del cliente a agregar.
     * @return true si la operación de inserción fue exitosa (es decir, se afectó al menos una fila),
     * false en caso contrario.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public boolean agregarCliente(ClienteOtaku cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, email, telefono) VALUES (?, ?, ?)";
        // El bloque try-with-resources asegura que la conexión (conn) y el PreparedStatement (stmt)
        // se cierren automáticamente al finalizar el bloque, liberando los recursos de la base de datos.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene una nueva conexión a la base de datos desde ConexionBD.
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Establece los valores de los parámetros en el PreparedStatement, que corresponden a los campos de la tabla.
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setInt(3, cliente.getTelefono());
            // Ejecuta la consulta de actualización (INSERT). executeUpdate() devuelve el número de filas afectadas.
            int filasAfectadas = stmt.executeUpdate();
            // Retorna true si se insertó al menos una fila, indicando éxito.
            return filasAfectadas > 0;
        }
    }

    /**
     * Busca un cliente en la base de datos por su identificador único (ID).
     * Este método recupera los datos de un cliente específico de la tabla 'clientes' usando su ID.
     *
     * @param id El ID del cliente a buscar en la base de datos.
     * @return Un objeto ClienteOtaku con los datos del cliente encontrado. Si no se encuentra ningún cliente
     * con el ID especificado, retorna null.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public ClienteOtaku buscarClientePorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        // try-with-resources para Connection y PreparedStatement para asegurar el cierre de recursos.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene la conexión.
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // Asigna el ID al primer parámetro de la consulta.
            // try-with-resources para ResultSet, asegurando su cierre.
            try (ResultSet rs = stmt.executeQuery()) { // Ejecuta la consulta SELECT y obtiene el ResultSet.
                if (rs.next()) { // Si el ResultSet contiene al menos una fila (es decir, se encontró el cliente).
                    // Construye un objeto ClienteOtaku a partir de los datos de la fila actual del ResultSet.
                    return construirClienteDesdeResultSet(rs);
                }
            }
        }
        return null; // Retorna null si no se encontró ningún cliente con el ID proporcionado.
    }

    /**
     * Recupera una lista de todos los clientes almacenados en la base de datos.
     * Este método consulta todas las filas de la tabla 'clientes' y las convierte en una lista de objetos ClienteOtaku.
     *
     * @return Una lista de objetos ClienteOtaku. La lista estará vacía si no hay clientes en la base de datos.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public List<ClienteOtaku> listarClientes() throws SQLException {
        List<ClienteOtaku> listaClientes = new ArrayList<>(); // Inicializa una lista vacía para almacenar los clientes.
        String sql = "SELECT * FROM clientes"; // Consulta SQL para seleccionar todos los registros de la tabla 'clientes'.
        // try-with-resources para Connection, Statement y ResultSet.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene la conexión.
             Statement stmt = conn.createStatement(); // Crea un objeto Statement para ejecutar consultas simples sin parámetros.
             ResultSet rs = stmt.executeQuery(sql)) { // Ejecuta la consulta y obtiene el ResultSet.
            while (rs.next()) { // Itera sobre cada fila del ResultSet mientras haya resultados.
                // Para cada fila, construye un objeto ClienteOtaku y lo añade a la lista.
                listaClientes.add(construirClienteDesdeResultSet(rs));
            }
        }
        return listaClientes; // Retorna la lista completa de clientes.
    }

    /**
     * Actualiza la información de un cliente existente en la base de datos.
     * Los campos 'nombre', 'email' y 'telefono' del cliente serán actualizados basándose en su ID.
     *
     * @param cliente El objeto ClienteOtaku que contiene la información actualizada del cliente,
     * incluyendo su ID para identificarlo.
     * @return true si la actualización fue exitosa (se modificó al menos una fila),
     * false en caso contrario (ej. el ID del cliente no existe).
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public boolean actualizarCliente(ClienteOtaku cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        // try-with-resources para Connection y PreparedStatement.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene la conexión.
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Asigna los nuevos valores a los parámetros del PreparedStatement.
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setInt(3, cliente.getTelefono());
            stmt.setInt(4, cliente.getId()); // El ID se usa en la cláusula WHERE para especificar qué cliente actualizar.
            // Ejecuta la consulta de actualización (UPDATE).
            int filasAfectadas = stmt.executeUpdate();
            // Retorna true si se actualizó al menos una fila.
            return filasAfectadas > 0;
        }
    }

    /**
     * Elimina un cliente de la base de datos por su identificador único (ID).
     *
     * @param id El ID del cliente a eliminar.
     * @return true si la eliminación fue exitosa (se eliminó al menos una fila),
     * false si no se encontró ningún cliente con el ID proporcionado.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public boolean eliminarCliente(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        // try-with-resources para Connection y PreparedStatement.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene la conexión.
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // Asigna el ID del cliente a eliminar como parámetro.
            // Ejecuta la consulta de eliminación (DELETE).
            int filasAfectadas = stmt.executeUpdate();
            // Retorna true si se eliminó al menos una fila.
            return filasAfectadas > 0;
        }
    }

    /**
     * Busca un cliente en la base de datos por su número de teléfono.
     *
     * @param telefono El número de teléfono del cliente a buscar.
     * @return Un objeto ClienteOtaku si se encuentra un cliente con el número de teléfono especificado.
     * Retorna null si no se encuentra ningún cliente con ese teléfono.
     * @throws SQLException Si ocurre un error de acceso a la base de datos o un problema de SQL.
     */
    public ClienteOtaku buscarClientePorTelefono(int telefono) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE telefono = ?";
        // try-with-resources para Connection y PreparedStatement.
        try (Connection conn = ConexionBD.getConnection(); // Obtiene la conexión.
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, telefono); // Asigna el número de teléfono al parámetro de la consulta.
            // try-with-resources para ResultSet.
            try (ResultSet rs = stmt.executeQuery()) { // Ejecuta la consulta SELECT.
                if (rs.next()) { // Si se encuentra al menos un resultado.
                    return construirClienteDesdeResultSet(rs); // Construye y retorna el objeto ClienteOtaku.
                }
            }
        }
        return null; // Retorna null si no se encontró ningún cliente.
    }

    /**
     * Método auxiliar privado para construir un objeto ClienteOtaku a partir de una fila del ResultSet.
     * Este método es fundamental para mapear los datos relacionales de la base de datos a objetos Java.
     *
     * @param rs El objeto ResultSet que contiene la fila actual de datos de un cliente.
     * @return Un objeto ClienteOtaku completamente populado con los datos de la fila del ResultSet.
     * @throws SQLException Si ocurre un error al intentar acceder a los datos dentro del ResultSet
     * (por ejemplo, si una columna no existe o el tipo de dato es incorrecto).
     */
    private ClienteOtaku construirClienteDesdeResultSet(ResultSet rs) throws SQLException {
        // Recupera la marca de tiempo de la columna "fecha_registro" de la base de datos.
        Timestamp ts = rs.getTimestamp("fecha_registro");
        LocalDateTime fechaRegistro = null;
        // Convierte el objeto Timestamp a un objeto LocalDateTime.
        // Esto es necesario porque java.sql.Timestamp es la representación de JDBC de un tipo de fecha y hora,
        // mientras que LocalDateTime es parte de la API de fecha y hora moderna de Java (java.time).
        if (ts != null) {
            fechaRegistro = ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        // Crea y retorna una nueva instancia de ClienteOtaku, inicializando sus atributos
        // con los valores obtenidos de las columnas correspondientes del ResultSet.
        return new ClienteOtaku(
            rs.getInt("id"),           // Obtiene el ID del cliente.
            rs.getString("nombre"),    // Obtiene el nombre del cliente.
            rs.getString("email"),     // Obtiene el email del cliente.
            rs.getInt("telefono"),     // Obtiene el teléfono del cliente.
            fechaRegistro              // Asigna la fecha de registro convertida.
        );
    }
}