package com.Sixt.sistemas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity @DiscriminatorValue("ROLE_ADMINISTRADOR")
public class Administrador extends Usuario {

    public Administrador() {}
    public Administrador(String usuario, String contraseña, String role)
    {
        super(usuario, contraseña, role);
    }

    @Override
    public String mostrarMenu() {
        return "administrador"; // Nombre de la vista para administrador
    }
}
