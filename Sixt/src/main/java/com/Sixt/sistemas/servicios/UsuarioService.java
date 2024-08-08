package com.Sixt.sistemas.servicios;

import com.Sixt.sistemas.repositorio.UsuarioDao;
import com.Sixt.sistemas.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    @Autowired
    private UsuarioDao usuarioDao;
    @Autowired
    private PasswordEncoder passwordEncoder;  // Para la encriptación de la contraseña
    public Usuario findUsuarioByUsername(String username) {
        return usuarioDao.findUsuarioByUsername(username);
    }

    // Método para recuperar todos los usuarios
    public List<Usuario> getAllUsers() {
        return usuarioDao.findAll();
    }

    public void  deleteUser(Long id){
        usuarioDao.deleteById(id);
    }

    public List<Usuario> findByRole(String role) {
        return usuarioDao.findByRole(role);
    }
    public Usuario validateUser(String username, String password) {
        log.debug("Attempting to find user with username: {}", username);
        Usuario usuario = usuarioDao.findUsuarioByUsername(username);
        if (usuario != null) {
            log.debug("User found: {}", usuario.getUsuario());
            System.out.println("antes del match: " + passwordEncoder.matches(password, usuario.getContraseña()));
            if (passwordEncoder.matches(password, usuario.getContraseña())) {
                log.debug("Password matches for user: {}", username);
                return usuario;
            } else {

                log.info("Password does not match for user: {}", username);
            }
        } else {
            log.info("User not found: {}", username);
        }
        return usuario;
    }
    /**
     * Saves or updates a user in the database. Encodes the password if present before saving.
     * @param usuario the user to save or update
     */
    public void saveOrUpdateUsuario(Usuario usuario) {
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
            usuario.setContraseña(encodedPassword);
        }
        usuarioDao.save(usuario);
    }
    //Encrypt all plain text passwords in the database.

    @PostConstruct
    public void encryptAllPasswords() {
        List<Usuario> usuarios = usuarioDao.findAll();
        for (Usuario usuario : usuarios) {
            // Verifica si la contraseña parece ser un hash BCrypt válido
            if (usuario.getContraseña() != null && usuario.getContraseña().startsWith("$2a$")) {
                // Verifica si la contraseña necesita recodificación
                if (passwordEncoder.upgradeEncoding(usuario.getContraseña())) {
                    String encryptedPassword = passwordEncoder.encode(usuario.getContraseña());
                    usuario.setContraseña(encryptedPassword);
                    usuarioDao.save(usuario);
                }
            } else {
                // Codifica la contraseña en texto plano
                String encryptedPassword = passwordEncoder.encode(usuario.getContraseña());
                usuario.setContraseña(encryptedPassword);
                usuarioDao.save(usuario);
            }
        }
        log.info("All passwords have been encrypted.");
    }
    public Usuario findById(Long id){
        Optional<Usuario> usuario = usuarioDao.findById(id);
        return usuario.orElse(null);
      }

    public Long obtenerIdUsuarioLogueado() {
        // Obtener el nombre de usuario del usuario logueado actualmente
        String username = obtenerNombreUsuarioLogueado();
        // Buscar el usuario en la base de datos
        Usuario usuario = findUsuarioByUsername(username);
        // Retornar el ID del usuario encontrado, si existe
        return usuario != null ? usuario.getId() : null;
    }
    private String obtenerNombreUsuarioLogueado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }


}
