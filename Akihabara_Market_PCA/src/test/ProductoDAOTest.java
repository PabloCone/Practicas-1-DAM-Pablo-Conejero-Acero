package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import dao.ProductoDAO;
import model.ProductoOtaku;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import dao.ConexionBD;

/**
 * Clase de pruebas unitarias para ProductoDAO.
 * Valida las operaciones CRUD y búsquedas en la base de datos.
 */
public class ProductoDAOTest {

    private ProductoDAO dao;

    @Before
    public void setUp() {
        dao = new ProductoDAO();
        limpiarTablaProductos();
    }

    // Método auxiliar para limpiar la tabla productos antes de cada test
    private void limpiarTablaProductos() {
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM productos");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error al limpiar la tabla productos.");
        }
    }

    @Test
    public void testAgregarYObtenerProducto() {
        ProductoOtaku p = new ProductoOtaku("Naruto Figurine", "Figuras", 19.99, 10);
        dao.agregarProducto(p);
        assertTrue(p.getId() > 0);

        ProductoOtaku obtenido = dao.obtenerProductoPorId(p.getId());
        assertNotNull("El producto obtenido no debe ser nulo", obtenido);
        assertEquals("Naruto Figurine", obtenido.getNombre());
    }

    @Test
    public void testActualizarProducto() {
        ProductoOtaku p = new ProductoOtaku("One Piece Poster", "Posters", 9.99, 5);
        dao.agregarProducto(p);
        assertTrue(p.getId() > 0);

        p.setStock(20);
        p.setNombre("One Piece Poster Deluxe");
        boolean actualizado = dao.actualizarProducto(p);
        assertTrue("La actualización debe ser exitosa", actualizado);

        ProductoOtaku actualizadoProd = dao.obtenerProductoPorId(p.getId());
        assertNotNull(actualizadoProd);
        assertEquals(20, actualizadoProd.getStock());
        assertEquals("One Piece Poster Deluxe", actualizadoProd.getNombre());
    }

    @Test
    public void testEliminarProducto() {
        ProductoOtaku p = new ProductoOtaku("Test Delete", "Prueba", 1.0, 1);
        dao.agregarProducto(p);
        assertTrue(p.getId() > 0);

        boolean eliminado = dao.eliminarProducto(p.getId());
        assertTrue("La eliminación debe ser exitosa", eliminado);

        ProductoOtaku eliminadoProd = dao.obtenerProductoPorId(p.getId());
        assertNull(eliminadoProd);
    }

    @Test
    public void testBuscarPorNombre() {
        ProductoOtaku p = new ProductoOtaku("Pikachu Plush", "Peluches", 14.99, 7);
        dao.agregarProducto(p);
        assertTrue(p.getId() > 0);

        List<ProductoOtaku> resultados = dao.buscarProductosPorNombre("Pikachu");
        assertFalse("La lista de resultados no debe estar vacía", resultados.isEmpty());

        boolean encontrado = resultados.stream()
            .anyMatch(prod -> prod.getId() == p.getId());
        assertTrue("El producto buscado debe estar en los resultados", encontrado);
    }

    @Test
    public void testObtenerTodosLosProductos() {
        ProductoOtaku p1 = new ProductoOtaku("Goku Keychain", "Accesorios", 5.0, 15);
        ProductoOtaku p2 = new ProductoOtaku("Luffy Figure", "Figuras", 25.0, 5);
        dao.agregarProducto(p1);
        dao.agregarProducto(p2);

        List<ProductoOtaku> productos = dao.obtenerTodosLosProductos();
        assertEquals("Debe haber 2 productos", 2, productos.size());
    }
}
