package com.Sixt.sistemas.controller;


import com.Sixt.sistemas.model.Usuario;
import com.Sixt.sistemas.servicios.AuthService;
import com.Sixt.sistemas.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@Controller
public class LoginController  {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AuthService  authService;
    @GetMapping("/iniciarSesion")
    public String iniciarSesion() {
        return "login";
    }

    private static final Map<String, String> userRolesToViews = Map.of(
            "Cliente", "cliente",
            "Administrador", "administrador",
            "Vendedor", "vendedor"
    );
    @PostMapping("/PerformLogin")
    public String performLogin(@RequestParam("usuario") String username,
                               @RequestParam("password") String password,
                               Model model) {
        Usuario usuario = usuarioService.validateUser(username, password);
        if (authService.authenticate(username, password) && usuario != null) // Usuario validado correctamente
             {
            model.addAttribute("username", username);
            // Obtener el tipo de usuario y redirigir a la vista correspondiente
            String userType = usuario.getClass().getSimpleName();
            String redirectView = userRolesToViews.getOrDefault(userType, "defaultDashboard");
            return "redirect:/" + redirectView;
                // Redirige basado en el tipo de clase del usuario

        } else {// Fallo en la validación
            model.addAttribute("loginError", "Usuario o contraseña inválidos");
            return "login";
        }
              }

    }
