package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dao.ConexionBD;

/**
 * Clase utilitaria para insertar datos de prueba en la base de datos.
 * En este caso, inserta registros en la tabla 'productos' para facilitar
 * pruebas y desarrollo del sistema.
 */
public class SetUpDatos {

    /**
     * Método estático que inserta un conjunto fijo de productos en la base de datos.
     * Utiliza una consulta SQL tipo INSERT para añadir varios productos a la tabla 'productos'.
     */
    public static void setUpDatos() {
        // Consulta SQL para insertar 3 productos con nombre, categoría, precio y stock
        String sql = """
            INSERT INTO productos (nombre, categoria, precio, stock)
            VALUES 
                ("Figura de Anya Forger", "Figura", 59.95, 8),
                ("Manga Chainsaw Man Vol.1", "Manga", 9.99, 20),
                ("Póster Studio Ghibli Colección", "Póster", 15.50, 15)
        """;

        // Bloque try-with-resources para asegurar el cierre automático de recursos (Connection y Statement)
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {

            // Ejecuta la consulta SQL para insertar los datos
            stmt.executeUpdate(sql);
            // Mensaje informativo si la inserción fue exitosa
            System.out.println("📦 Datos de prueba insertados correctamente.");

        } catch (SQLException e) {
            // Captura y muestra errores en caso de fallo al ejecutar la consulta
            System.out.println("❌ Error al insertar datos de prueba: " + e.getMessage());
        }
    }
}
