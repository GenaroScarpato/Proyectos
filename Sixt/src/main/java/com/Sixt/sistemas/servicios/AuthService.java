package com.Sixt.sistemas.servicios;


import com.Sixt.sistemas.util.HttpUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean authenticate(String username, String password) {
        String url = "https://external-api.com/login";
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        try {
            String response = HttpUtil.sendPostRequest(url, requestBody);
            // Procesar la respuesta según la lógica del negocio
            // Por ejemplo, comprobar si el login es exitoso
            return response.contains("success");
        } catch (Exception e) {
            // Loggear el error o manejarlo según sea necesario
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }
}
