public class PedidoComida extends Pedido {
    public PedidoComida(int id, String direccion, double distancia) {
        super(id, direccion, distancia);
    }

    @Override
    public void calcularTiempoEntrega() {
        this.tiempoEstimado = (int) (distancia * 3) + 15;
    }

    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor Automático (Comida)";
    }

    @Override
    public String toString() {
        return "PedidoComida #" + id + " - entregado por " + repartidor;
    }
}