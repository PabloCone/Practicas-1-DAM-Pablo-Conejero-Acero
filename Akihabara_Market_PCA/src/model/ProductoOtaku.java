package model;

/**
 * Clase que representa un producto en Akihabara Market.
 * Contiene atributos básicos como id, nombre, categoría, precio y stock.
 */
public class ProductoOtaku {
    private int id;             // Identificador único del producto
    private String nombre;      // Nombre del producto
    private String categoria;   // Categoría a la que pertenece el producto
    private double precio;      // Precio del producto
    private int stock;          // Cantidad disponible en inventario

    /**
     * Constructor con todos los atributos, incluyendo el id.
     * Se usa cuando se conoce el id (por ejemplo, al obtener un producto de la base de datos).
     */
    public ProductoOtaku(int id, String nombre, String categoria, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    /**
     * Constructor sin id, útil para crear un nuevo producto antes de asignarle un id.
     */
    public ProductoOtaku(String nombre, String categoria, double precio, int stock) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y setters para acceder y modificar los atributos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Representación en String del objeto ProductoOtaku.
     * Útil para mostrar los datos del producto en consola o logs.
     */
    @Override
    public String toString() {
        return "ProductoOtaku{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }
}
