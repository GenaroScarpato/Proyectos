package com.Sixt.sistemas.controller;

import com.Sixt.sistemas.model.*;
import com.Sixt.sistemas.servicios.UsuarioService;
import com.Sixt.sistemas.servicios.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {
    private final UsuarioService usuarioService;
    private final VehiculoService vehiculoService;

    @Autowired
    public AdministradorController(UsuarioService usuarioService, VehiculoService vehiculoService) {
        this.usuarioService = usuarioService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("")
    public String administradorDashboard() {
        return "administrador";
    }

    @GetMapping("/listar-usuarios")
    public ModelAndView listarUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView("lista");
        modelAndView.addObject("titulo", "Listado de Usuarios");
        modelAndView.addObject("items", usuarios);
        modelAndView.addObject("headers", Arrays.asList("ID", "Usuario", "Role"));
        modelAndView.addObject("identifierField", "id");
        modelAndView.addObject("fields", Arrays.asList("id", "usuario", "role"));
        modelAndView.addObject("editUrl", "/administrador/editar-usuario");
        modelAndView.addObject("deleteUrl", "/administrador/eliminar-usuario");
        modelAndView.addObject("canEdit", true);
        modelAndView.addObject("canDelete", true);
        modelAndView.addObject("urlVolver", "/administrador");
        return modelAndView;
    }

    @GetMapping("/listar-autos")
    public ModelAndView listarAutos() {
        List<Vehiculo> vehiculos = vehiculoService.getAllVehiculos();
        ModelAndView modelAndView = new ModelAndView("lista");
        modelAndView.addObject("titulo", "Listado de Autos");
        modelAndView.addObject("items", vehiculos);
        modelAndView.addObject("headers", Arrays.asList("Patente", "Modelo", "Color", "Marca", "Reservado"));
        modelAndView.addObject("fields", Arrays.asList("patente", "modelo", "color", "marca", "reservado"));
        modelAndView.addObject("identifierField", "patente");
        modelAndView.addObject("editUrl", "/administrador/editar-auto");
        modelAndView.addObject("deleteUrl", "/administrador/eliminar-auto");
        modelAndView.addObject("urlVolver", "/administrador");
        modelAndView.addObject("canDelete", true);
        return modelAndView;
    }

    @GetMapping("/agregar-auto")
    public String mostrarFormularioAgregarAuto(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "agregarVehiculo";
    }
    @PostMapping("/agregar-auto")
    public String agregarAuto(Vehiculo vehiculo) {
        vehiculo.setNafta(100);
        vehiculoService.saveVehiculo(vehiculo);
        return "redirect:/administrador/listar-autos";
    }

    @GetMapping("/agregar-usuario")
    public String mostrarFormularioAgregarUsuario(Model model) {
        // No se crea ninguna instancia concreta aquí, solo se prepara la vista
        Map<String, String> tipoUsuarios = Map.of(
                "cliente", "Cliente",
                "vendedor", "Vendedor",
                "administrador", "Administrador"
        );
        model.addAttribute("tipoUsuarios", tipoUsuarios);
        return "agregarUsuario";
    }
    @PostMapping("/agregar-usuario")
    public String agregarUsuario(@RequestParam Map<String, String> allParams) {
        Usuario nuevoUsuario;
        if ("Cliente".equals(allParams.get("tipoUsuario"))) {
            Cliente cliente = new Cliente();
            cliente.setDni(allParams.get("dni"));
            cliente.setNombre(allParams.get("nombre"));
            cliente.setDireccion(allParams.get("direccion"));
            cliente.setMail(allParams.get("mail"));
            cliente.setTelefono(allParams.get("telefono"));
            nuevoUsuario = cliente;
        } else if ("Vendedor".equals(allParams.get("tipoUsuario"))) {
            nuevoUsuario = new Vendedor();
        } else {
            nuevoUsuario = new Administrador();
        }
        nuevoUsuario.setUsuario(allParams.get("usuario"));
        nuevoUsuario.setContraseña(allParams.get("contrasena"));
        nuevoUsuario.setRole(allParams.get("tipo_usuario"));

        usuarioService.saveOrUpdateUsuario(nuevoUsuario);
        return "redirect:/administrador/listar-usuarios";
    }

    @DeleteMapping("/eliminar-auto/{patente}")
    @ResponseBody // Asegúrate de que estás devolviendo una respuesta adecuada para AJAX
    public ResponseEntity<?> eliminarAuto(@PathVariable("patente") String patente, RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.deleteVehiculo(patente);
            return ResponseEntity.ok().body("{\"message\":\"Auto eliminado con éxito.\"}"); // Devuelve JSON
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error al eliminar el auto.\"}");
        }
    }

    @DeleteMapping("/eliminar-usuario/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok().body("{\"message\":\"Usuario eliminado con éxito.\"}"); // Devuelve JSON
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Error al eliminar el usuario.\"}");
        }
    }  @GetMapping("/editar-usuario/{id}")
    public ModelAndView editarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            // Aquí puedes manejar el caso en que no se encuentre el usuario (por ejemplo, redirigir a una página de error)
            return new ModelAndView("redirect:/administrador/error");
        }

        ModelAndView modelAndView = new ModelAndView("editarUsuario");
        modelAndView.addObject("usuario", usuario);
        System.out.println(usuario.getId());
        return modelAndView;
    }

    @PostMapping("/actualizar-usuario")
    public String actualizarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttrs) {
        try {
            System.out.println("adentro de post \n\n\n");
            usuarioService.saveOrUpdateUsuario(usuario);
            redirectAttrs.addFlashAttribute("success", "Usuario actualizado con éxito.");
            return "redirect:/administrador/lista-usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el usuario.");
            return "redirect:/administrador/editar-usuario/" + usuario.getId();
        }


    }


    @GetMapping("/editar-auto/{patente}")
    public ModelAndView editarAuto(@PathVariable String patente) {
        Vehiculo vehiculo = vehiculoService.findByPatente(patente);
        if (vehiculo == null) {
            return new ModelAndView("redirect:/administrador/error");
        }
        ModelAndView modelAndView = new ModelAndView("editarVehiculo");
        modelAndView.addObject("vehiculo", vehiculo);
        return modelAndView;
    }

    @PostMapping("/actualizar-auto")
    public String actualizarAuto(@ModelAttribute Vehiculo vehiculo, RedirectAttributes redirectAttrs) {
        try {
            vehiculoService.saveVehiculo(vehiculo);
            redirectAttrs.addFlashAttribute("success", "Usuario actualizado con éxito.");
            return "redirect:/administrador/listar-autos";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el vehiculo.");
            return "redirect:/administrador/editar-auto/" + vehiculo.getPatente();
        }
    }

}