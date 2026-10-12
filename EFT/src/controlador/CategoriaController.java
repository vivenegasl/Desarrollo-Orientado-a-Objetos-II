package controlador;

import dao.CategoriaDAO;
import modelo.Categoria;
import java.sql.SQLException;
import java.util.List;

public class CategoriaController {
    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public int registrar(Categoria cat) throws SQLException { return categoriaDAO.create(cat); }
    public List<Categoria> listarTodos() throws SQLException { return categoriaDAO.readAll(); }
    public Categoria buscarPorId(int id) throws SQLException { return categoriaDAO.findById(id); }
    public boolean actualizar(Categoria cat) throws SQLException { return categoriaDAO.update(cat); }
    public boolean eliminar(int id) throws SQLException { return categoriaDAO.delete(id); }
}
