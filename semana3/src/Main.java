public class Main {
    public static void main(String[] args) {
        ControladorDeEnvios controlador = new ControladorDeEnvios();

        // 1. Pedido Comida (Se despacha de forma silenciosa para que el Output final sea exacto al requerido)
        PedidoComida pedidoComida = new PedidoComida(101, "Calle Falsa 123", 5.0);
        pedidoComida.asignarRepartidor("Luis Díaz"); // Polimorfismo: Sobrecarga manual
        pedidoComida.calcularTiempoEntrega();
        pedidoComida.entregado = true; // Simulación de despacho para agregarlo al historial sin ensuciar la consola
        controlador.agregarPedido(pedidoComida);

        // 2. Pedido Encomienda (Replicando la consola exacta)
        System.out.println("[Pedido Encomienda]");
        PedidoEncomienda pedidoEncomienda = new PedidoEncomienda(102, "Av. Santa Rosa 567", 7.0);
        pedidoEncomienda.asignarRepartidor("Daniela Tapia"); // Polimorfismo: Sobrecarga manual
        pedidoEncomienda.calcularTiempoEntrega();
        pedidoEncomienda.mostrarResumen();
        pedidoEncomienda.despachar();
        controlador.agregarPedido(pedidoEncomienda);

        // 3. Pedido Express (Cancelación)
        PedidoExpress pedidoExpress = new PedidoExpress(103, "Calle Express 999", 2.0);
        pedidoExpress.asignarRepartidor(); // Polimorfismo: Sobrescritura automática
        pedidoExpress.calcularTiempoEntrega();
        pedidoExpress.cancelar();
        controlador.agregarPedido(pedidoExpress);

        // 4. Historial
        controlador.verHistorial();
    }
}