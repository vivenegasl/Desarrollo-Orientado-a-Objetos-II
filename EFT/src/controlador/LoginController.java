package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.sql.SQLException;

public class LoginController {
    private final UsuarioDAO usuarioDAO;

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario autenticar(String rut, String contrasena) throws SQLException {
        return usuarioDAO.autenticar(rut, contrasena);
    }
}
