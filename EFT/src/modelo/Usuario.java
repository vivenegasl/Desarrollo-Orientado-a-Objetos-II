package modelo;

import java.util.Objects;

/**
 * Clase abstracta que representa un usuario genérico del sistema de biblioteca.
 * Aplica herencia y abstracción: las subclases Bibliotecario y Estudiante
 * heredan de esta clase e implementan su propio comportamiento polimórfico.
 */
public abstract class Usuario {

    protected int id;
    protected String nombre;
    protected String rut;
    protected String correo;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    /**
     * Método abstracto que retorna la descripción del rol.
     * Cada subclase implementa su propia versión (polimorfismo).
     */
    public abstract String getRolDescripcion();

    /**
     * Retorna el tipo de rol como enum.
     * Método abstracto implementado por cada subclase.
     */
    public abstract RolUsuario getRol();

    /**
     * Método polimórfico que retorna información resumida del usuario.
     */
    public String getInfoResumida() {
        return nombre + " (" + getRolDescripcion() + ")";
    }

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

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return nombre + " [" + rut + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
