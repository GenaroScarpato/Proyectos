package com.Sixt.sistemas.controller;
import com.Sixt.sistemas.model.*;
import com.Sixt.sistemas.repositorio.ReservaRepository;
import com.Sixt.sistemas.servicios.ReservaService;
import com.Sixt.sistemas.servicios.UsuarioService;
import com.Sixt.sistemas.servicios.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/vendedor")
public class VendedorController {
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("")
    public String vendedorDashboard() {
        return "vendedor";
    }
    @GetMapping("/listar-clientes")
    public ModelAndView listarClientes() {
        List<Usuario> clientes = usuarioService.findByRole("ROLE_CLIENTE");
        ModelAndView modelAndView = new ModelAndView("lista");
        modelAndView.addObject("titulo", "Listado de Clientes");
        modelAndView.addObject("headers", Arrays.asList("ID", "Usuario", "Nombre","Telefono", "Email","Direccion"));
        modelAndView.addObject("fields", Arrays.asList("id", "usuario", "nombre","telefono", "mail","direccion"));
        modelAndView.addObject("items", clientes);
        modelAndView.addObject("canEdit", false); // Ajustar según permisos
        modelAndView.addObject("canDelete", false); // Ajustar según permisos
        modelAndView.addObject("deleteUrl", "/vendedor/eliminar-cliente"); // URL para eliminar cliente
        modelAndView.addObject("identifierField", "id");
        modelAndView.addObject("urlVolver", "/vendedor");
        return modelAndView;
    }

    @GetMapping("/listar-reservas")
    public ModelAndView listarReservas() {
        List<Reserva> reservas = reservaService.getAllReservas();
        ModelAndView modelAndView = new ModelAndView("lista");
        modelAndView.addObject("titulo", "Listado de Reservas");
        modelAndView.addObject("headers", Arrays.asList("ID", "Estado", "Patente", "Fecha Inicio", "Fecha Fin", "Comentarios","Nafta", "ID Vendedor", "ID Cliente"));
        modelAndView.addObject("fields", Arrays.asList("id", "estado", "patenteVehiculo", "fechaInicio", "fechaFin", "comentarios","naftaVehiculo", "vendedorId", "clienteId"));
        modelAndView.addObject("items", reservas);
        modelAndView.addObject("canCancel", true); // Permiso para cancelar
        modelAndView.addObject("canComplete", true); // Permiso para completar reserva
        modelAndView.addObject("canDeliver", true); // Permiso para marcar como entregado
        modelAndView.addObject("canDelete", true);
        modelAndView.addObject("deleteUrl", "/vendedor/eliminar-reserva");
        modelAndView.addObject("cancelUrl", "/vendedor/cancelar-reserva"); // URL para cancelar reserva
        modelAndView.addObject("completeUrl", "/vendedor/finalizar-reserva"); // URL para finalizar reserva
        modelAndView.addObject("deliverUrl", "/vendedor/entregar-reserva"); // URL para marcar como entregado
        modelAndView.addObject("identifierField", "id");
        modelAndView.addObject("urlVolver", "/vendedor");
        return modelAndView;
    }
    @GetMapping("/agregar-cliente")
    public String mostrarFormularioAgregarUsuario(Model model) {
        String tipoUsuarios = "Cliente";
        model.addAttribute("cliente", tipoUsuarios);
        return "agregarCliente";
    }

    @PostMapping("/agregar-cliente")
    public String agregarUsuario(@RequestParam Map<String,String> allParams) {
        Usuario nuevoUsuario;
            Cliente cliente = new Cliente();
            cliente.setDni(allParams.get("dni"));
            cliente.setNombre(allParams.get("nombre"));
            cliente.setDireccion(allParams.get("direccion"));
            cliente.setMail(allParams.get("mail"));
            cliente.setTelefono(allParams.get("telefono"));
            nuevoUsuario = cliente;

        nuevoUsuario.setUsuario(allParams.get("usuario"));
        nuevoUsuario.setContraseña(allParams.get("contrasena"));
        nuevoUsuario.setRole(allParams.get("tipo_usuario"));
        usuarioService.saveOrUpdateUsuario(nuevoUsuario);
        return "redirect:/vendedor/listar-clientes";
    }

    @DeleteMapping("/eliminar-cliente/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok().body("{\"message\":\"Usuario eliminado con éxito.\"}"); // Devuelve JSON
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error al eliminar el usuario.\"}");
        }
    }

    @GetMapping("/nueva-reserva")
    public String mostrarFormularioCrearReserva(Model model) {

        List<Vehiculo> vehiculos = vehiculoService.obtenerVehiculosDisponibles();
        List<Usuario> clientes = usuarioService.findByRole("ROLE_CLIENTE");
        model.addAttribute("titulo", "Crear Reserva");
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("clientes", clientes);
        model.addAttribute("headersVehiculos", Arrays.asList("Patente", "Marca", "Modelo", "Color","Nafta_Lt"));
        model.addAttribute("fieldsVehiculos", Arrays.asList("patente", "marca", "modelo", "color","nafta"));
        model.addAttribute("headersClientes", Arrays.asList("ID", "Usuario", "Nombre"));
        model.addAttribute("fieldsClientes", Arrays.asList("id", "usuario", "nombre"));
        return "reserva";
    }

    @PostMapping("/nueva-reserva")
    public String nuevaReserva(@RequestParam String patente,
                               @RequestParam Long clienteId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               @RequestParam String comentarios,
                               RedirectAttributes redirectAttrs) {
        try {
            Vehiculo vehiculo = vehiculoService.findByPatente(patente);
            int naftaInicial = vehiculo.getNafta();
            // Obtener el ID del vendedor actual
            Long vendedorId = usuarioService.obtenerIdUsuarioLogueado();
            // Llamar al servicio de reserva para crear la reserva
            reservaService.crearReserva(comentarios, "PENDIENTE", fechaFin, fechaInicio, vendedorId, clienteId, patente, naftaInicial);
            vehiculoService.marcarVehiculoComoReservado(patente, true);

            redirectAttrs.addFlashAttribute("success", "Reserva creada con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al crear la reserva: " + e.getMessage());
        }
        return "redirect:/vendedor/listar-reservas";
    }



    @GetMapping("/cancelar-reserva/{reservaId}")
    public String cancelarReserva(@PathVariable Long reservaId) {

            Reserva reserva = reservaService.findById(reservaId)
                    .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservaId));

            reserva.setEstado(Reserva.Estado.CANCELADA);

        vehiculoService.marcarVehiculoComoReservado(reserva.getPatenteVehiculo(),false);

        // Guardar los cambios en la base de datos
            reservaRepository.save(reserva);
        return "redirect:/vendedor/listar-reservas";
    }





    @GetMapping("/entregar-reserva/{reservaId}")
    public String marcarEntregado(@PathVariable Long reservaId) {
        Reserva reserva = reservaService.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservaId));
        reserva.setEstado(Reserva.Estado.ENTREGADO);

        reservaRepository.save(reserva);
        return "redirect:/vendedor/listar-reservas";
    }

    @DeleteMapping("/eliminar-reserva/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarReserva(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.deleteReserva(id);
            return ResponseEntity.ok().body("{\"message\":\"Reserva Eliminada con éxito.\"}"); // Devuelve JSON
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error al eliminar reserva.\"}");
        }
    }


    @PostMapping("/finalizar-reserva/{reservaId}")
    public String finalizarReserva(@PathVariable Long reservaId,
                                   @RequestParam int naftaFinal,
                                   RedirectAttributes redirectAttrs) {
        Reserva reserva = reservaService.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservaId));
        Vehiculo vehiculo = reserva.getVehiculo();
        int naftaInicial = vehiculo.getNafta();  // Obtener la nafta inicial desde el vehículo
        vehiculo.setNafta(naftaFinal);
        reserva.setEstado(Reserva.Estado.FINALIZADA);
        vehiculoService.marcarVehiculoComoReservado(reserva.getPatenteVehiculo(), false);
        reservaRepository.save(reserva);
        redirectAttrs.addFlashAttribute("naftaInicial", naftaInicial);  // Guardar naftaInicial
        redirectAttrs.addFlashAttribute("naftaFinal", naftaFinal);  // Guardar naftaFinal
        return "redirect:/vendedor/facturacion/" + reservaId + "?naftaFinal=" + naftaFinal;
    }

    @GetMapping("/facturacion/{reservaId}")
    public String facturacionReserva(@PathVariable Long reservaId,
                                     @RequestParam int naftaFinal,
                                     Model model,
                                     @ModelAttribute("naftaInicial") int naftaInicial) {
        Reserva reserva = reservaService.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservaId));

        Vehiculo vehiculo = reserva.getVehiculo();
        int diasAlquiler = calcularDiasAlquiler(reserva);

                BigDecimal precioTotal = calcularPrecioTotal(reserva, diasAlquiler, naftaInicial, naftaFinal);

        BigDecimal costoAdicionalPorNafta = BigDecimal.ZERO;
        if (naftaFinal < naftaInicial) {
            BigDecimal costoPorLitro = new BigDecimal("10"); // Costo por litro de nafta faltante
            BigDecimal litrosFaltantes = new BigDecimal(naftaInicial - naftaFinal);
            costoAdicionalPorNafta = costoPorLitro.multiply(litrosFaltantes);
        }
        BigDecimal costoTotal = precioTotal.add(costoAdicionalPorNafta);
        model.addAttribute("diasAlquiler", diasAlquiler);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("vehiculo", reserva.getVehiculo().getPatente());
        model.addAttribute("naftaInicial", naftaInicial);
        model.addAttribute("naftaFinal", naftaFinal);
        model.addAttribute("costoAdicionalPorNafta", costoAdicionalPorNafta);
        model.addAttribute("costoTotal", costoTotal);

        return "facturacion";
    }
    private BigDecimal calcularPrecioTotal(Reserva reserva, int diasAlquiler, int naftaInicial, int naftaFinal) {
        BigDecimal precioPorDia = new BigDecimal("100"); // Precio por día fijo, ajustar según sea necesario
        BigDecimal precioTotal = precioPorDia.multiply(new BigDecimal(diasAlquiler));

        // Calcula el adicional por nafta si es necesario
        if (naftaFinal < naftaInicial) {
            BigDecimal costoAdicionalPorNafta = new BigDecimal("10"); // Costo por litro de nafta faltante
            BigDecimal litrosFaltantes = new BigDecimal(naftaInicial - naftaFinal);
            precioTotal = precioTotal.add(costoAdicionalPorNafta.multiply(litrosFaltantes));
        }
        return precioTotal;
    }

    private int calcularDiasAlquiler(Reserva reserva) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
    }



}




