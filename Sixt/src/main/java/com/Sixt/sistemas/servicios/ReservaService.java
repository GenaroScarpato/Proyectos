package com.Sixt.sistemas.servicios;
import com.Sixt.sistemas.model.Reserva;
import com.Sixt.sistemas.model.Usuario;
import com.Sixt.sistemas.model.Vehiculo;
import com.Sixt.sistemas.repositorio.ReservaRepository;
import com.Sixt.sistemas.repositorio.UsuarioDao;
import com.Sixt.sistemas.repositorio.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private UsuarioDao usuarioDao;


    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }
    public boolean isVehiculoDisponible(String patente, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = reservaRepository.findByVehiculoPatenteAndFechaFinAfterAndFechaInicioBefore(patente, fechaInicio, fechaFin);
        return reservas.isEmpty();
    }
    public void  deleteReserva(Long id){
        reservaRepository.deleteById(id);
    }

    @Transactional
    public void crearReserva(String comentarios, String estado, LocalDate fechaFin, LocalDate fechaInicio, Long vendedorId, Long clienteId, String patente,int nafta) {
        Vehiculo vehiculo = vehiculoService.findByPatente(patente);

        // Obtener el cliente y vendedor
        Usuario cliente = usuarioDao.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Usuario vendedor = usuarioDao.findById(vendedorId).orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado"));
        Reserva reserva = new Reserva();
        reserva.setComentarios(comentarios);
        reserva.setEstado(Reserva.Estado.PENDIENTE);
        reserva.setFechaFin(fechaFin.atStartOfDay().toLocalDate());
        reserva.setFechaInicio(fechaInicio.atStartOfDay().toLocalDate());
        reserva.setVendedorId(vendedorId);
        reserva.setClienteId(clienteId);
        reserva.setVehiculo(vehiculo);  // Asociar el objeto Vehiculo completo


        System.out.println("AUTO GUARDADO ANASHEEEEEE");
        reservaRepository.save(reserva);
    }
}
