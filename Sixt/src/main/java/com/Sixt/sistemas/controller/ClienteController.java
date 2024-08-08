package com.Sixt.sistemas.controller;

import com.Sixt.sistemas.model.Vehiculo;
import com.Sixt.sistemas.servicios.UsuarioService;
import com.Sixt.sistemas.servicios.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    private final VehiculoService vehiculoService;

    // Inyecta el servicio de usuario usando el constructor
    @Autowired
    public  ClienteController(UsuarioService usuarioService, VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }
    @GetMapping("")
    public String clienteDashboard() {
        return "cliente";  // Asume que tienes una plantilla 'cliente.html' en tu carpeta de recursos
    }

    @GetMapping("/listar-autos")
    public ModelAndView listarAutos() {
       List<Vehiculo> vehiculos = vehiculoService.obtenerVehiculosDisponibles();
        ModelAndView modelAndView = new ModelAndView("lista");
        modelAndView.addObject("titulo", "Listado de Autos Disponibles");
        modelAndView.addObject("items", vehiculos);
        modelAndView.addObject("headers", Arrays.asList("Patente", "Modelo", "Color", "Marca"));
        modelAndView.addObject("fields", Arrays.asList("patente", "modelo", "color", "marca"));
        modelAndView.addObject("identifierField", "patente");
        modelAndView.addObject("urlVolver", "/cliente");
        modelAndView.addObject("canEdit", false);
        return modelAndView;
    }


}
