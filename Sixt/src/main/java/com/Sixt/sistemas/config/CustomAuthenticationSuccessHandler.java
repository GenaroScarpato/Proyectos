package com.Sixt.sistemas.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String redirectUrl = "/home"; // Default redirect

        // Check roles and decide redirect URL
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))) {
            redirectUrl = "/administrador";
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VENDEDOR"))) {
            redirectUrl = "/vendedor";
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            redirectUrl = "/cliente";
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
