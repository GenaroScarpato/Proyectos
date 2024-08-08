package com.Sixt.sistemas.servicios;

import com.Sixt.sistemas.model.Usuario;
import com.Sixt.sistemas.repositorio.UsuarioDao;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioDao usuarioDao;

    @Autowired
    public UsuarioDetailsServiceImpl(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findUsuarioByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        String roleName = usuario.getRole().toUpperCase();
        return new User(usuario.getUsuario(), usuario.getContraseña(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName)));
    }
}
