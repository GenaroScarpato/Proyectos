package com.Sixt.sistemas.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity @DiscriminatorValue("ROLE_VENDEDOR")
public class Vendedor extends Usuario {

    public Vendedor(){
    }
    public Vendedor(String usuario, String contraseña, String role) {
        super(usuario, contraseña,role);
    }

    @Override
    public String mostrarMenu() {
        return "vendedor"; // Nombre de la vista para vendedor
    }
}
