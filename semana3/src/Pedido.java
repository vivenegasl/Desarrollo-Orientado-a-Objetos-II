public abstract class Pedido implements Despachable, Cancelable {
    protected int id;
    protected String direccion;
    protected double distancia;
    protected String repartidor;
    protected int tiempoEstimado;
    protected boolean entregado;
    protected boolean cancelado;

    public Pedido(int id, String direccion, double distancia) {
        this.id = id;
        this.direccion = direccion;
        this.distancia = distancia;
        this.entregado = false;
        this.cancelado = false;
    }

    // Abstracción: Método abstracto que obliga a cada tipo de pedido a tener su propia lógica
    public abstract void calcularTiempoEntrega();

    // Polimorfismo (Sobrescritura): Cada subclase define cómo asigna el repartidor automáticamente
    public abstract void asignarRepartidor();

    // Polimorfismo (Sobrecarga): Permite asignar un repartidor manualmente en cualquier subclase
    public void asignarRepartidor(String nombre) {
        this.repartidor = nombre;
    }

    // Reutilización: Método común a todas las clases derivadas
    public void mostrarResumen() {
        System.out.println("Pedido #" + id);
        System.out.println("Dirección: " + direccion);
        if(distancia % 1 == 0) {
            System.out.println("Distancia: " + (int)distancia + " km");
        } else {
            System.out.println("Distancia: " + distancia + " km");
        }
        System.out.println("Repartidor asignado: " + repartidor);
        System.out.println("Tiempo estimado: " + tiempoEstimado + " minutos");
    }

    @Override
    public void despachar() {
        this.entregado = true;
        System.out.println("Pedido despachado correctamente.");
    }

    @Override
    public void cancelar() {
        this.cancelado = true;
        System.out.println("Cancelando Pedido " + this.getClass().getSimpleName().replace("Pedido", "") + " #" + id + "...");
        System.out.println("-> Pedido cancelado exitosamente.");
    }

    public boolean isEntregado() {
        return entregado;
    }

    public int getId() {
        return id;
    }
}