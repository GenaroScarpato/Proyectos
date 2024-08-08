package com.Sixt.sistemas.config;

import com.Sixt.sistemas.servicios.UsuarioDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private UsuarioDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // Utilizando cookies para el CSRF

                // .csrf(csrf -> csrf.ignoringRequestMatchers("/iniciarSesion/**","/PerformLogin/**"))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/iniciarSesion/**").permitAll()
                        .requestMatchers("/PerformLogin/**").permitAll()
                        .requestMatchers("/api/usuarios/**").permitAll()
                        .requestMatchers("/admin/user/**").permitAll()
                        .requestMatchers("/administrador/**").hasAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers("/vendedor/**").hasAnyAuthority("ROLE_VENDEDOR", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/admin/encrypt-passwords").hasAuthority("ROLE_ADMINISTRADOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/iniciarSesion")
                        .loginProcessingUrl("/PerformLogin")
                        .usernameParameter("usuario")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)  // Utiliza tu manejador personalizado
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/iniciarSesion?logout=true")
                        .deleteCookies("JSESSIONID") // Elimina la cookie de sesión
                        .clearAuthentication(true) // Limpia la autenticación
                        .invalidateHttpSession(true) // Invalida la sesión
                        .permitAll())
                .exceptionHandling(e -> e
                        .accessDeniedPage("/acceso-denegado")); // Página personalizada de acceso denegado



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, @Lazy PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
