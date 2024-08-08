package com.Sixt.sistemas.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    @Id
    @Column(name = "patente", nullable = false)
    private String patente;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "reservado", nullable = false)
    private boolean reservado;

    @Column(name = "nafta",nullable = false)
    private int nafta;

}
