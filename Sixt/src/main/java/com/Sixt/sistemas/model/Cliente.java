package com.Sixt.sistemas.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter@Setter
@Entity @DiscriminatorValue("ROLE_CLIENTE")
public class Cliente extends Usuario {
String dni;
String nombre;
String direccion;
String mail;
String telefono;
    public Cliente() {}

    public Cliente(String usuario, String contraseña, String role, String dni, String nombre, String direccion, String mail, String telefono) {
        super(usuario, contraseña, role);
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.mail = mail;
        this.telefono = telefono;
    }

    public Cliente(String usuario, String contraseña, String role) {
        super(usuario, contraseña,role);

    }

    @Override
    public String mostrarMenu() {
        return "cliente"; // Nombre de la vista para cliente
    }
}


