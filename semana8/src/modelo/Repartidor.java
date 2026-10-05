package modelo;

import java.util.Objects;

/**
 * Clase que representa a un Repartidor en el sistema SpeedFast.
 * Mapea la tabla 'repartidores' de la base de datos.
 */
public class Repartidor {
    private int id;
    private String nombre;

    public Repartidor() {
    }

    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    @Override
    public String toString() {
        if (id > 0) {
            return id + " - " + nombre;
        }
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repartidor that = (Repartidor) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
