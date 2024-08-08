package com.Sixt.sistemas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Utiliza una única tabla para todas las subclases
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;
    @Column(name = "contraseña", nullable = false)
    private String contraseña;
    @Column(name = "tipo_usuario", insertable = false, updatable = false)
    private String role;  // Campo para almacenar el rol de acceso


    public Usuario() {
    }
    public Usuario(String usuario, String contraseña,String role) {
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.role = role;
    }
    // Método abstracto para mostrar el menú, cada subclase implementará su propio menú
    public abstract String mostrarMenu();
}