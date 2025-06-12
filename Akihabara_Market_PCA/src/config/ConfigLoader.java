package config;

/**
 * Clase utilitaria para cargar configuraciones desde un archivo externo de propiedades.
 * 
 * Esta clase carga automáticamente al iniciarse las propiedades definidas en un archivo 
 * llamado "config.properties" ubicado en el sistema de archivos (ruta relativa o absoluta).
 * Permite obtener los valores de configuración mediante su clave, facilitando la gestión
 * centralizada de parámetros configurables en la aplicación sin necesidad de recompilar.
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    // Objeto Properties que almacenará las configuraciones cargadas del archivo
    private static final Properties properties = new Properties();

    // Bloque estático que se ejecuta una sola vez al cargar la clase
    static {
        // Ruta del archivo de configuración, puede ser relativa o absoluta
        String configFilePath = "config.properties"; // Ruta relativa o absoluta al archivo fuera del src
        try (InputStream input = new FileInputStream(configFilePath)) {
            // Carga las propiedades desde el archivo
            properties.load(input);
        } catch (Exception e) {
            // En caso de error al cargar el archivo, muestra un mensaje en la consola de error
            System.err.println("Error cargando configuración desde archivo externo: " + e.getMessage());
        }
    }

    /**
     * Método público para obtener el valor de una propiedad dada su clave.
     * @param key clave de la propiedad a buscar
     * @return valor asociado a la clave o null si no existe
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
