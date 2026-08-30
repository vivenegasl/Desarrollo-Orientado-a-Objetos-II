public class PedidoExpress extends Pedido {
    public PedidoExpress(int id, String direccion, double distancia) {
        super(id, direccion, distancia);
    }

    @Override
    public void calcularTiempoEntrega() {
        this.tiempoEstimado = (int) (distancia * 2) + 5;
    }

    @Override
    public void asignarRepartidor() {
        this.repartidor = "Repartidor Automático (Express)";
    }

    @Override
    public void cancelar() {
        this.cancelado = true;
        System.out.println("Cancelando Pedido Express #" + id + "...");
        System.out.println("-> Pedido cancelado exitosamente.");
    }

    @Override
    public String toString() {
        return "PedidoExpress #" + id + " - entregado por " + repartidor;
    }
}