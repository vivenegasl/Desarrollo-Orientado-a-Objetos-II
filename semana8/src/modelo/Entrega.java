package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Clase que representa una Entrega en el sistema SpeedFast.
 * Asocia un Pedido con un Repartidor en una fecha y hora específicas.
 * Mapea la tabla 'entregas' de la base de datos.
 */
public class Entrega {
    private int id;
    private int idPedido;
    private int idRepartidor;
    private LocalDate fecha;
    private LocalTime hora;

    // Campos auxiliares para visualización en tablas y vistas
    private String nombreRepartidor;
    private String direccionPedido;

    public Entrega() {
    }

    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public String getDireccionPedido() {
        return direccionPedido;
    }

    public void setDireccionPedido(String direccionPedido) {
        this.direccionPedido = direccionPedido;
    }

    @Override
    public String toString() {
        return "Entrega #" + id + " [Pedido: " + idPedido + ", Repartidor: " + idRepartidor +
                ", Fecha: " + fecha + ", Hora: " + hora + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrega entrega = (Entrega) o;
        return id == entrega.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
