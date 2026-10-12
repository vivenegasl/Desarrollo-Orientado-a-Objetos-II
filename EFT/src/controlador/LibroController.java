package controlador;

import dao.LibroDAO;
import dao.CategoriaDAO;
import modelo.Libro;
import modelo.Categoria;
import java.sql.SQLException;
import java.util.List;

public class LibroController {
    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;

    public LibroController() {
        this.libroDAO = new LibroDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    public int registrar(Libro libro) throws SQLException { return libroDAO.create(libro); }
    public List<Libro> listarTodos() throws SQLException { return libroDAO.readAll(); }
    public Libro buscarPorId(int id) throws SQLException { return libroDAO.findById(id); }
    public boolean actualizar(Libro libro) throws SQLException { return libroDAO.update(libro); }
    public boolean eliminar(int id) throws SQLException { return libroDAO.delete(id); }
    public List<Categoria> listarCategorias() throws SQLException { return categoriaDAO.readAll(); }
}
