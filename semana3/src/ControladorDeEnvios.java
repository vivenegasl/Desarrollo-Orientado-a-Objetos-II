import java.util.ArrayList;

public class ControladorDeEnvios implements Rastreable {
    private ArrayList<Pedido> historial;

    public ControladorDeEnvios() {
        this.historial = new ArrayList<>();
    }

    public void agregarPedido(Pedido pedido) {
        historial.add(pedido);
    }

    @Override
    public void verHistorial() {
        System.out.println("Historial:");
        for (Pedido p : historial) {
            if (p.isEntregado()) {
                System.out.println("- " + p.toString().replace(" - ", " – "));
            }
        }
    }
}