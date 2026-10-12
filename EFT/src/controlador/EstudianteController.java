package controlador;

import dao.EstudianteDAO;
import modelo.Estudiante;
import java.sql.SQLException;
import java.util.List;

public class EstudianteController {
    private final EstudianteDAO estudianteDAO;

    public EstudianteController() {
        this.estudianteDAO = new EstudianteDAO();
    }

    public int registrar(Estudiante est) throws SQLException { return estudianteDAO.create(est); }
    public List<Estudiante> listarTodos() throws SQLException { return estudianteDAO.readAll(); }
    public Estudiante buscarPorId(int id) throws SQLException { return estudianteDAO.findById(id); }
    public boolean actualizar(Estudiante est) throws SQLException { return estudianteDAO.update(est); }
    public boolean eliminar(int id) throws SQLException { return estudianteDAO.delete(id); }
}
