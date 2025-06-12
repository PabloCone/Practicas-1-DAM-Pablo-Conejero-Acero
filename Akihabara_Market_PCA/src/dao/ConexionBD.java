package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import config.ConfigLoader; // Importa la clase que carga configuraciones desde archivo externo

/**
 * Clase encargada de manejar la conexión con la base de datos.
 * Utiliza los datos de conexión almacenados en un archivo de configuración 
 * a través de la clase ConfigLoader para obtener la URL, usuario y contraseña.
 */
public class ConexionBD {
    // URL de la base de datos, leída desde el archivo de configuración
    private static final String URL = ConfigLoader.getProperty("db.url");
    // Usuario para la conexión, leído desde el archivo de configuración
    private static final String USER = ConfigLoader.getProperty("db.user");
    // Contraseña para la conexión, leída desde el archivo de configuración
    private static final String PASSWORD = ConfigLoader.getProperty("db.password");

    /**
     * Método estático que retorna una nueva conexión a la base de datos.
     * Usa DriverManager con los datos cargados desde configuración.
     * 
     * @return objeto Connection que representa la conexión activa a la BD.
     * @throws SQLException si ocurre algún error durante la conexión.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
