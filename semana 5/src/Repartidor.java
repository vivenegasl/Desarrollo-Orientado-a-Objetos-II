public class Repartidor implements Runnable {
    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void run() {
        while (true) {
            // Retira un pedido de forma segura y concurrente desde el recurso compartido
            Pedido pedido = zonaDeCarga.retirarPedido();
            
            // Si no quedan pedidos en la zona de carga, finaliza la ejecución del hilo
            if (pedido == null) {
                break;
            }

            // Cambia el estado a EN_REPARTO y muestra los mensajes correspondientes
            pedido.setEstado(EstadoPedido.EN_REPARTO);
            synchronized (System.out) {
                System.out.println("[Repartidor - " + nombre + "] Retirando pedido #" + pedido.getId() + "...");
                System.out.println("[Repartidor - " + nombre + "] Estado: " + pedido.getEstado());
            }

            // Simula el tiempo de entrega con Thread.sleep()
            try {
                Thread.sleep(1500); // 1.5 segundos de simulación de traslado
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[Repartidor - " + nombre + "] Entrega interrumpida.");
                break;
            }

            // Cambia el estado a ENTREGADO y muestra el mensaje final
            pedido.setEstado(EstadoPedido.ENTREGADO);
            synchronized (System.out) {
                System.out.println("[Repartidor - " + nombre + "] Entregando pedido #" + pedido.getId() + "...");
                System.out.println("[Repartidor - " + nombre + "] Estado: " + pedido.getEstado());
            }
        }
    }
}
