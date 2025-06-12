package model;

import java.time.LocalDateTime;

/**
 * Clase que representa un cliente tipo Otaku con sus atributos básicos.
 * Contiene información sobre id, nombre, email, teléfono y fecha de registro del cliente.
 */
public class ClienteOtaku {
    // Identificador único del cliente
    private int id;
    // Nombre del cliente
    private String nombre;
    // Correo electrónico del cliente
    private String email;
    // Teléfono del cliente (guardado como entero)
    private int telefono;
    // Fecha de registro del cliente
    private LocalDateTime fechaRegistro;

    /**
     * Constructor completo que inicializa todos los campos, incluido el id y fecha de registro.
     * @param id Identificador del cliente.
     * @param nombre Nombre del cliente.
     * @param email Correo electrónico del cliente.
     * @param telefono Teléfono del cliente.
     * @param fechaRegistro Fecha y hora de registro del cliente.
     */
    public ClienteOtaku(int id, String nombre, String email, int telefono, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Constructor sin id, útil para crear clientes nuevos antes de asignar un id.
     * La fecha de registro puede quedar en null o asignarse automáticamente en BD.
     * @param nombre Nombre del cliente.
     * @param email Correo electrónico del cliente.
     * @param telefono Teléfono del cliente.
     */
    public ClienteOtaku(String nombre, String email, int telefono) {
        this(0, nombre, email, telefono, null);
    }

    // Getters y setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getTelefono() { return telefono; }
    public void setTelefono(int telefono) { this.telefono = telefono; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    /**
     * Representación en texto del objeto ClienteOtaku.
     * @return Cadena con los datos del cliente.
     */
    @Override
    public String toString() {
        return "Cliente[id=" + id + ", nombre=" + nombre + ", email=" + email + ", telefono=" + telefono +
            ", fechaRegistro=" + fechaRegistro + "]";
    }
}
