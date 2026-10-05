package modelo;

import java.util.Objects;

/**
 * Clase que representa un Pedido en el sistema SpeedFast.
 * Mapea la tabla 'pedidos' de la base de datos.
 */
public class Pedido {
    private int id;
    private String direccion;
    private TipoPedido tipo;
    private EstadoPedido estado;

    public Pedido() {
    }

    public Pedido(String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.direccion = direccion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public Pedido(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        this.id = id;
        this.direccion = direccion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public void setTipo(TipoPedido tipo) {
        this.tipo = tipo;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        if (id > 0) {
            return id + " - " + direccion + " (" + tipo + ")";
        }
        return direccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id == pedido.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
