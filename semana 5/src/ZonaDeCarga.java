import java.util.LinkedList;
import java.util.Queue;

public class ZonaDeCarga {
    private final Queue<Pedido> pedidos;

    public ZonaDeCarga() {
        this.pedidos = new LinkedList<>();
    }

    /**
     * Agrega un pedido a la zona de carga de forma segura y sincronizada.
     * @param p El pedido a agregar.
     */
    public synchronized void agregarPedido(Pedido p) {
        if (p != null) {
            pedidos.offer(p);
        }
    }

    /**
     * Retira el siguiente pedido disponible de la zona de carga de forma segura y sincronizada.
     * @return El pedido retirado o null si la zona de carga está vacía.
     */
    public synchronized Pedido retirarPedido() {
        return pedidos.poll();
    }

    /**
     * Verifica si la zona de carga se encuentra sin pedidos.
     * @return true si está vacía, false si quedan pedidos.
     */
    public synchronized boolean estaVacia() {
        return pedidos.isEmpty();
    }

    /**
     * Retorna la cantidad de pedidos restantes en la zona de carga.
     * @return Número de pedidos en cola.
     */
    public synchronized int getCantidadPedidosRestantes() {
        return pedidos.size();
    }
}
