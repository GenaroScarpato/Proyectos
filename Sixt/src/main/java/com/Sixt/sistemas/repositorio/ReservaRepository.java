package com.Sixt.sistemas.repositorio;

import com.Sixt.sistemas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByVehiculoPatenteAndFechaFinAfterAndFechaInicioBefore(String patente, LocalDate fechaInicio, LocalDate fechaFin);
}
