package modelo;

/**
 * Representa un usuario con rol Bibliotecario en el sistema.
 * Tiene acceso completo: gestión de libros, estudiantes, préstamos y reportes.
 * Extiende Usuario (herencia) e implementa los métodos abstractos (polimorfismo).
 */
public class Bibliotecario extends Usuario {

    private String contrasena;

    public Bibliotecario() {
    }

    public Bibliotecario(int id, String nombre, String rut, String correo, String contrasena) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
    }

    @Override
    public String getRolDescripcion() {
        return "Bibliotecario - Acceso Total";
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.BIBLIOTECARIO;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
