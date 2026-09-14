import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Paso 5: Instancia de la ZonaDeCarga compartida
        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();

        System.out.println("[Zona de carga inicializada]");

        // Agrega al menos 5 pedidos al sistema
        Pedido[] pedidos = {
            new Pedido(1, "Santiago Centro"),
            new Pedido(2, "Providencia"),
            new Pedido(3, "Ñuñoa"),
            new Pedido(4, "Recoleta"),
            new Pedido(5, "Las Condes")
        };

        for (Pedido p : pedidos) {
            zonaDeCarga.agregarPedido(p);
            System.out.println("Pedido #" + p.getId() + " agregado. Destino: " + p.getDireccionEntrega());
        }

        // Crea e inicia 3 hilos de tipo Repartidor
        Repartidor r1 = new Repartidor("Juan", zonaDeCarga);
        Repartidor r2 = new Repartidor("Camila", zonaDeCarga);
        Repartidor r3 = new Repartidor("Pedro", zonaDeCarga);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(r1);
        executor.execute(r2);
        executor.execute(r3);

        // Apagar el ExecutorService para esperar a que terminen las tareas en ejecución
        executor.shutdown();

        try {
            // Espera la finalización de los hilos repartidores
            boolean terminaron = executor.awaitTermination(2, TimeUnit.MINUTES);
            if (terminaron) {
                System.out.println("[Zona de carga vacía]");
                System.out.println("Todas los pedidos han sido entregados correctamente.");
            } else {
                System.out.println("El tiempo de entrega excedió el límite establecido.");
            }
        } catch (InterruptedException e) {
            System.err.println("El proceso principal fue interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
