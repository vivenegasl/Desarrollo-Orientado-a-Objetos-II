package modelo;

/**
 * Enumeración que representa los estados del ciclo de vida de un pedido en SpeedFast.
 * Coincide con la definición ENUM('PENDIENTE','EN_REPARTO','ENTREGADO') de la base de datos.
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_REPARTO,
    ENTREGADO
}
