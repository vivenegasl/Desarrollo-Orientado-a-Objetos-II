public class Pedido {
    private int id;
    private String direccionEntrega;
    private EstadoPedido estado;

    public Pedido(int id, String direccionEntrega) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public Pedido(int id, String direccionEntrega, EstadoPedido estado) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public void setEstado(String nuevoEstado) {
        try {
            this.estado = EstadoPedido.valueOf(nuevoEstado);
        } catch (IllegalArgumentException e) {
            System.err.println("Estado no válido: " + nuevoEstado);
        }
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " [Destino: " + direccionEntrega + ", Estado: " + estado + "]";
    }
}
