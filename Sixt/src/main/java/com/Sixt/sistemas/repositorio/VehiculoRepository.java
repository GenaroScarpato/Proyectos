package com.Sixt.sistemas.repositorio;


import com.Sixt.sistemas.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, String> {
    List<Vehiculo> findByReservadoFalse();
    Vehiculo findByPatente(String patente);


}
