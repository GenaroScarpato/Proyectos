package com.Sixt.sistemas.servicios;

import java.util.List;

import com.Sixt.sistemas.model.Usuario;
import com.Sixt.sistemas.model.Vehiculo;
import com.Sixt.sistemas.repositorio.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> getAllVehiculos() {
        return vehiculoRepository.findAll();
    }

    public Vehiculo saveVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo findByPatente(String patente) {
        return vehiculoRepository.findById(patente).orElse(null);
    }

    public void deleteVehiculo(String patente) {
        vehiculoRepository.deleteById(patente);
    }

    public List<Vehiculo> obtenerVehiculosDisponibles() {
        return vehiculoRepository.findByReservadoFalse();
    }

    public void marcarVehiculoComoReservado(String patente,Boolean reservado) {

        // Obtener el vehículo por su patente
        Vehiculo vehiculo = vehiculoRepository.findById(patente).orElse(null);
        vehiculoRepository.save(vehiculo);


    }
}