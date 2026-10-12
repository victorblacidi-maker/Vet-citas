package com.vetcitas.gestion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Configuration -> Le indica a Spring que esta clase contiene
 * definiciones de @Bean, es decir, objetos que Spring debe crear
 * y gestionar dentro de su contenedor de Inversión de Control (IoC),
 * para luego poder inyectarlos (@Autowired / inyección por constructor)
 * en cualquier otra clase que los necesite (como UsuarioService).
 *
 * @EnableWebSecurity -> Habilita explícitamente la configuración web de
 * Spring Security y nos permite definir nuestra propia SecurityFilterChain,
 * en vez de dejar que Spring Boot aplique su configuración automática
 * por defecto (que es la que generaba el formulario "Please sign in" con
 * usuario "user" y una contraseña aleatoria impresa en consola).
 *
 * NOTA PARA LA PRESENTACIÓN:
 * En este primer entregable NO se implementa el control de acceso por
 * roles A NIVEL de Spring Security (con hasRole(), etc.). Se usa
 * spring-boot-starter-security únicamente para obtener el algoritmo
 * BCrypt (encriptado de contraseñas). El control de "quién puede ver qué
 * panel" se resuelve a nivel de sesión HTTP desde el AuthController y se
 * valida en el frontend (auth.js). Por eso, en el SecurityFilterChain de
 * abajo se le indica a Spring Security que NO bloquee ninguna ruta y que
 * NO muestre su propio formulario de login: todo el flujo de
 * autenticación lo controla nuestro propio código. En un entregable
 * posterior se recomienda migrar a un control de acceso real con
 * hasRole()/JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * @Bean -> Método "fábrica": todo lo que retorna queda registrado
     * en el contenedor de Spring. A partir de aquí, cualquier clase
     * puede pedir un PasswordEncoder por inyección de dependencias y
     * Spring le entregará ESTA misma instancia (BCrypt).
     *
     * BCrypt es un algoritmo de hash diseñado específicamente para
     * contraseñas: es "lento" a propósito (para dificultar ataques de
     * fuerza bruta) y genera un "salt" aleatorio distinto en cada
     * ejecución, por lo que dos usuarios con la misma contraseña
     * tendrán hashes completamente diferentes en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Este bean es la pieza que faltaba: sin él, Spring Boot detecta
     * spring-boot-starter-security en el pom.xml y aplica AUTOMÁTICAMENTE
     * su configuración por defecto, que exige login (formulario propio
     * de Spring Security) para acceder a CUALQUIER ruta, incluyendo
     * login.html, registro.html y /api/auth/**.
     *
     * Al definir nuestro propio SecurityFilterChain, le decimos
     * explícitamente qué hacer y esa configuración automática se desactiva.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF (Cross-Site Request Forgery) es una protección pensada
            // para formularios HTML tradicionales con cookies de sesión.
            // La desactivamos porque nuestro frontend llama a la API con
            // fetch() enviando JSON, no con un <form> clásico. Si más
            // adelante se requiere, se puede reactivar y exponer el token
            // CSRF al frontend.
            .csrf(csrf -> csrf.disable())

            // authorizeHttpRequests define qué rutas requieren autenticación
            // de Spring Security. Aquí permitimos TODAS las rutas
            // (permitAll) porque el control de acceso a los paneles lo
            // hace nuestro propio AuthController + auth.js mediante
            // HttpSession, no Spring Security.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

            // Desactiva el formulario de login automático de Spring
            // Security (el que aparecía como "Please sign in").
            .formLogin(form -> form.disable())

            // Desactiva también la autenticación HTTP Basic (el pop-up
            // nativo del navegador pidiendo usuario/contraseña).
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
