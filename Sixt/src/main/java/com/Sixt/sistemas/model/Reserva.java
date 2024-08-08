package com.Sixt.sistemas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patente")  // Usar patente directamente
    private Vehiculo vehiculo;


    @Column(name = "vendedor_id")
    private Long vendedorId;


    @Column(name = "cliente_id")
    private long clienteId;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "comentarios")
    private String comentarios;

    public enum Estado{
        PENDIENTE,
        ENTREGADO,
        FINALIZADA,
        CANCELADA,

    }
    public String getPatenteVehiculo() {
        return this.vehiculo != null ? this.vehiculo.getPatente() : null;
    }
    public int getNaftaVehiculo() {
        return this.vehiculo != null ? this.vehiculo.getNafta() : null;
    }

}
