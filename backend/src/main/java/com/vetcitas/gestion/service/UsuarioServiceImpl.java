package com.vetcitas.gestion.service;

import com.vetcitas.gestion.dto.AuthResponse;
import com.vetcitas.gestion.dto.LoginRequest;
import com.vetcitas.gestion.dto.RegistroRequest;
import com.vetcitas.gestion.entity.Rol;
import com.vetcitas.gestion.entity.Usuario;
import com.vetcitas.gestion.exception.CorreoDuplicadoException;
import com.vetcitas.gestion.exception.CredencialesInvalidasException;
import com.vetcitas.gestion.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Service -> Marca esta clase como un componente de la capa de
 * "lógica de negocio" (Service Layer). Al igual que @Repository y
 * @Controller, es una especialización de @Component: Spring la
 * detecta automáticamente y la registra en su contenedor para poder
 * inyectarla donde se necesite (en este caso, en AuthController).
 *
 * Aquí es donde viven las REGLAS DE NEGOCIO (validar duplicados,
 * encriptar contraseñas, asignar el rol por defecto, etc.), separadas
 * a propósito del Controller (que solo debe encargarse de recibir/
 * responder peticiones HTTP) y del Repository (que solo se encarga
 * de hablar con la base de datos).
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inyección de dependencias POR CONSTRUCTOR (recomendada sobre
     * @Autowired en el campo). Spring detecta este constructor y,
     * como esta clase es un @Service, automáticamente le "inyecta"
     * el UsuarioRepository y el PasswordEncoder (el bean que
     * definimos en SecurityConfig) sin que nosotros los creemos
     * manualmente con "new".
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse registrarCliente(RegistroRequest request) {
        // Regla de negocio: no permitir correos duplicados.
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new CorreoDuplicadoException("Ya existe una cuenta registrada con este correo");
        }

        // Nunca se guarda la contraseña tal cual llega del formulario:
        // se pasa por BCrypt para obtener un hash irreversible.
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

        // Regla de negocio: todo usuario que se registra desde el
        // formulario público queda con rol CLIENTE (los roles
        // VETERINARIO y ADMIN se asignan manualmente desde el panel
        // de administración, no desde el registro abierto).
        Usuario nuevoUsuario = new Usuario(
                request.getNombre(),
                request.getCorreo(),
                request.getTelefono(),
                passwordEncriptada,
                Rol.CLIENTE
        );

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return new AuthResponse(
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getRol(),
                "Registro exitoso. Ahora puedes iniciar sesión."
        );
    }

    @Override
    public AuthResponse iniciarSesion(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        // matches() compara la contraseña en texto plano que llega del
        // formulario contra el HASH guardado en la base de datos.
        // BCrypt internamente vuelve a aplicar el mismo algoritmo con
        // el "salt" ya almacenado en el hash y compara el resultado.
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        // Autenticación estricta: una cuenta inactiva no puede entrar
        // al panel principal aunque la contraseña sea correcta.
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("Tu cuenta se encuentra inactiva. Contacta al administrador.");
        }

        return new AuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(), // <- el frontend usa este campo para redirigir al panel correcto
                "Inicio de sesión exitoso"
        );
    }
}
