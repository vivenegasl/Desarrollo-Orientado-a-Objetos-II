public class PedidoEncomienda extends Pedido {
    public PedidoEncomienda(int id, String direccion, double distancia) {
        super(id, direccion, distancia);
    }

    @Override
    public void calcularTiempoEntrega() {
        // Ecuación ajustada para coincidir con el ejemplo (7km = 30 min)
        this.tiempoEstimado = (int) (distancia * 2) + 16;
    }

    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor Automático (Encomienda)";
    }

    @Override
    public String toString() {
        return "PedidoEncomienda #" + id + " - entregado por " + repartidor;
    }
}