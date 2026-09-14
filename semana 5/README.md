# SpeedFast - Semana 5: Sincronizando Procesos en Sistemas Concurrentes

## 📦 Descripción del Proyecto
Este proyecto corresponde a la actividad formativa de la **Semana 5** de la asignatura **Desarrollo Orientado a Objetos II**.
Se implementa una solución de concurrencia y sincronización en Java para el sistema de despacho y entregas de la empresa **SpeedFast**, resolviendo la problemática de condiciones de carrera (*race conditions*) y entregas duplicadas al acceder a un recurso compartido.

---

## 🚀 Arquitectura y Componentes Desarrollados

### 1. `EstadoPedido` (Enum)
Define de forma tipada y segura los posibles estados por los que transita un pedido:
- `PENDIENTE`: Estado inicial al llegar a la zona de carga.
- `EN_REPARTO`: Cuando un repartidor retira el pedido e inicia el despacho.
- `ENTREGADO`: Cuando el repartidor culmina con éxito la entrega.

### 2. `Pedido` (Clase)
Modela una encomienda con los atributos:
- `id` (`int`): Identificador único del pedido.
- `direccionEntrega` (`String`): Dirección de destino.
- `estado` (`EstadoPedido`): Estado actual del pedido.
- Incluye constructores, getters, setters, método `setEstado(String nuevoEstado)` y `toString()`.

### 3. `ZonaDeCarga` (Recurso Compartido Concurrente)
Gestiona la cola de pedidos pendientes de forma thread-safe mediante métodos sincronizados (`synchronized`):
- `public synchronized void agregarPedido(Pedido p)`: Añade un pedido de forma segura a la cola.
- `public synchronized Pedido retirarPedido()`: Extrae el siguiente pedido evitando lecturas sucias y retiros duplicados por parte de múltiples hilos.
- `public synchronized boolean estaVacia()`: Indica si ya no quedan encomiendas pendientes.
- `public synchronized int getCantidadPedidosRestantes()`: Consulta el tamaño actual de la cola.

### 4. `Repartidor` (Implementa `Runnable`)
Representa a un repartidor independiente que ejecuta su jornada de trabajo en paralelo:
- Atributos: `nombre` y referencia a la `ZonaDeCarga`.
- En el método `run()`, retira pedidos uno a uno de la zona de carga compartida, actualiza el estado a `EN_REPARTO`, simula el tiempo de traslado mediante `Thread.sleep()`, actualiza el estado a `ENTREGADO` y muestra las trazas correspondientes en consola hasta que la zona de carga quede vacía.

### 5. `Main` (Simulación y Coordinación de Hilos)
- Inicializa la `ZonaDeCarga`.
- Carga al menos 5 pedidos iniciales en el sistema.
- Crea e inicia 3 hilos de repartidores (`Juan`, `Camila`, `Pedro`) usando `ExecutorService` (`Executors.newFixedThreadPool(3)`).
- Espera la culminación de todos los hilos (`executor.shutdown()` y `executor.awaitTermination(...)`).
- Notifica cuando todos los pedidos han sido entregados con éxito.

---

## 💻 Compilación y Ejecución

```bash
# Compilar los archivos Java de la semana 5
javac -d out/production/SpeedFast src/*.java

# Ejecutar la simulación
java -cp out/production/SpeedFast Main
```
