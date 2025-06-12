package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.ProductoOtaku;

/**
 * Clase encargada de las operaciones CRUD sobre la tabla 'productos' en la base de datos.
 * Implementa métodos para agregar, obtener, listar, actualizar, eliminar y buscar productos.
 */
public class ProductoDAO {

    /**
     * Inserta un nuevo producto en la base de datos.
     * @param producto Objeto ProductoOtaku con los datos a insertar.
     * Después de la inserción, asigna el ID generado al objeto producto.
     */
    public void agregarProducto(ProductoOtaku producto) {
        String sql = "INSERT INTO productos (nombre, categoria, precio, stock) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getCategoria());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());

            int filas = stmt.executeUpdate();

            if (filas == 0) {
                throw new SQLException("Error: no se insertó ningún producto.");
            }

            // Obtener el id generado automáticamente y asignarlo al producto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    producto.setId(idGenerado);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un producto de la base de datos por su ID.
     * @param id Identificador del producto a buscar.
     * @return Objeto ProductoOtaku si se encuentra, o null si no existe o ocurre error.
     */
    public ProductoOtaku obtenerProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ProductoOtaku(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Recupera todos los productos existentes en la base de datos.
     * @return Lista con todos los productos; puede estar vacía si no hay productos o en caso de error.
     */
    public List<ProductoOtaku> obtenerTodosLosProductos() {
        List<ProductoOtaku> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProductoOtaku p = new ProductoOtaku(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * @param producto Objeto ProductoOtaku con los datos actualizados (incluye ID).
     * @return true si la actualización fue exitosa, false en caso contrario o error.
     */
    public boolean actualizarProducto(ProductoOtaku producto) {
        String sql = "UPDATE productos SET nombre=?, categoria=?, precio=?, stock=? WHERE id=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getCategoria());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.setInt(5, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un producto de la base de datos según su ID.
     * @param id Identificador del producto a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario o error.
     */
    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca productos cuyo nombre contenga la cadena proporcionada.
     * @param nombre Cadena para buscar dentro del nombre del producto (búsqueda parcial).
     * @return Lista de productos que coinciden con la búsqueda; puede estar vacía si no hay resultados o error.
     */
    public List<ProductoOtaku> buscarProductosPorNombre(String nombre) {
        List<ProductoOtaku> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoOtaku p = new ProductoOtaku(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
