package modelo;

/**
 * Representa un estudiante registrado en la biblioteca.
 * Acceso limitado: solo consulta de libros, préstamos, devoluciones y reportes.
 * Extiende Usuario (herencia) e implementa los métodos abstractos (polimorfismo).
 */
public class Estudiante extends Usuario {

    private String curso;

    public Estudiante() {
    }

    public Estudiante(String nombre, String rut, String curso, String correo) {
        this.nombre = nombre;
        this.rut = rut;
        this.curso = curso;
        this.correo = correo;
    }

    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    @Override
    public String getRolDescripcion() {
        return "Estudiante - Acceso Limitado";
    }

    @Override
    public RolUsuario getRol() {
        return RolUsuario.ESTUDIANTE;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        if (id > 0) {
            return id + " - " + nombre + " (" + curso + ")";
        }
        return nombre;
    }
}
