package com.Sixt.sistemas.repositorio;
import com.Sixt.sistemas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {
    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
    Usuario findUsuarioByUsername(@Param("usuario") String usuario);

    List<Usuario> findByRole(String role);


}

