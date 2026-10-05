package dao;

import modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Entrega.
 * Gestiona la persistencia de las entregas que asocian pedidos con repartidores.
 * Emplea PreparedStatement y ResultSet en todas las operaciones.
 */
public class EntregaDAO {

    /**
     * Registra una nueva entrega en la base de datos.
     *
     * @param entrega Objeto Entrega con los datos a registrar.
     * @return El ID generado para la nueva entrega, o -1 si falla.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public int create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        entrega.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Lista todas las entregas registradas, incluyendo nombres de repartidores y direcciones de pedidos.
     *
     * @return Lista completa de entregas con datos relacionados.
     * @throws SQLException Si ocurre un error al consultar.
     */
    public List<Entrega> readAll() throws SQLException {
        return readByFiltros(null, null);
    }

    /**
     * Lista entregas aplicando filtros opcionales por Pedido o por Repartidor.
     * Realiza JOIN con 'pedidos' y 'repartidores' para enriquecer la información visual.
     *
     * @param idPedido     Filtro por ID de pedido (opcional, null para ignorar).
     * @param idRepartidor Filtro por ID de repartidor (opcional, null para ignorar).
     * @return Lista de entregas filtradas.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public List<Entrega> readByFiltros(Integer idPedido, Integer idRepartidor) throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, " +
                "       p.direccion AS direccion_pedido, r.nombre AS nombre_repartidor " +
                "FROM entregas e " +
                "JOIN pedidos p ON e.id_pedido = p.id " +
                "JOIN repartidores r ON e.id_repartidor = r.id " +
                "WHERE 1=1"
        );

        if (idPedido != null && idPedido > 0) {
            sql.append(" AND e.id_pedido = ?");
        }
        if (idRepartidor != null && idRepartidor > 0) {
            sql.append(" AND e.id_repartidor = ?");
        }
        sql.append(" ORDER BY e.id ASC");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (idPedido != null && idPedido > 0) {
                ps.setInt(paramIndex++, idPedido);
            }
            if (idRepartidor != null && idRepartidor > 0) {
                ps.setInt(paramIndex++, idRepartidor);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega e = new Entrega();
                    e.setId(rs.getInt("id"));
                    e.setIdPedido(rs.getInt("id_pedido"));
                    e.setIdRepartidor(rs.getInt("id_repartidor"));
                    e.setFecha(rs.getDate("fecha").toLocalDate());
                    e.setHora(rs.getTime("hora").toLocalTime());
                    e.setDireccionPedido(rs.getString("direccion_pedido"));
                    e.setNombreRepartidor(rs.getString("nombre_repartidor"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    /**
     * Busca una entrega por su identificador único.
     *
     * @param id Identificador de la entrega.
     * @return Entrega encontrada o null si no existe.
     * @throws SQLException Si ocurre un error al consultar.
     */
    public Entrega findById(int id) throws SQLException {
        String sql = "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, " +
                     "       p.direccion AS direccion_pedido, r.nombre AS nombre_repartidor " +
                     "FROM entregas e " +
                     "JOIN pedidos p ON e.id_pedido = p.id " +
                     "JOIN repartidores r ON e.id_repartidor = r.id " +
                     "WHERE e.id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Entrega e = new Entrega();
                    e.setId(rs.getInt("id"));
                    e.setIdPedido(rs.getInt("id_pedido"));
                    e.setIdRepartidor(rs.getInt("id_repartidor"));
                    e.setFecha(rs.getDate("fecha").toLocalDate());
                    e.setHora(rs.getTime("hora").toLocalTime());
                    e.setDireccionPedido(rs.getString("direccion_pedido"));
                    e.setNombreRepartidor(rs.getString("nombre_repartidor"));
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Actualiza los datos de una entrega existente.
     *
     * @param entrega Objeto Entrega con los valores modificados y su ID.
     * @return true si se actualizó el registro, false en caso contrario.
     * @throws SQLException Si ocurre un error en la actualización.
     */
    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.setInt(5, entrega.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una entrega por su identificador.
     *
     * @param id Identificador de la entrega a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     * @throws SQLException Si ocurre un error al eliminar.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
