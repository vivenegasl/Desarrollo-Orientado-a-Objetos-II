package dao;

import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Pedido.
 * Implementa operaciones CRUD completas y consultas filtradas
 * utilizando PreparedStatement y ResultSet sobre la tabla 'pedidos'.
 */
public class PedidoDAO {

    /**
     * Registra un nuevo pedido en la base de datos.
     *
     * @param pedido Objeto Pedido con dirección, tipo y estado.
     * @return El ID generado para el nuevo pedido, o -1 si falla.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public int create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        pedido.setId(idGenerado);
                        return idGenerado;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Obtiene todos los pedidos registrados en la base de datos.
     *
     * @return Lista completa de pedidos.
     * @throws SQLException Si ocurre un error al consultar.
     */
    public List<Pedido> readAll() throws SQLException {
        return readByFiltros(null, null);
    }

    /**
     * Consulta pedidos aplicando filtros opcionales de Tipo y/o Estado.
     * Si un parámetro es null, no se aplica dicho filtro.
     *
     * @param tipo   Filtro por tipo de pedido (opcional).
     * @param estado Filtro por estado del pedido (opcional).
     * @return Lista de pedidos que coinciden con los criterios.
     * @throws SQLException Si ocurre un error al consultar.
     */
    public List<Pedido> readByFiltros(TipoPedido tipo, EstadoPedido estado) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1");

        if (tipo != null) {
            sql.append(" AND tipo = ?");
        }
        if (estado != null) {
            sql.append(" AND estado = ?");
        }
        sql.append(" ORDER BY id ASC");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (tipo != null) {
                ps.setString(paramIndex++, tipo.name());
            }
            if (estado != null) {
                ps.setString(paramIndex++, estado.name());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setTipo(TipoPedido.valueOf(rs.getString("tipo")));
                    p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    /**
     * Busca un pedido por su identificador único.
     *
     * @param id Identificador del pedido.
     * @return Pedido encontrado o null si no existe.
     * @throws SQLException Si ocurre un error al consultar.
     */
    public Pedido findById(int id) throws SQLException {
        String sql = "SELECT id, direccion, tipo, estado FROM pedidos WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setTipo(TipoPedido.valueOf(rs.getString("tipo")));
                    p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Actualiza la información de un pedido existente.
     *
     * @param pedido Objeto con datos modificados y su respectivo ID.
     * @return true si se actualizó el registro, false en caso contrario.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un pedido por su ID.
     *
     * @param id Identificador del pedido a eliminar.
     * @return true si se eliminó con éxito, false en caso contrario.
     * @throws SQLException Si ocurre un error al eliminar.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
