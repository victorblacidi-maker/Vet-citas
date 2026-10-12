package com.vetcitas.gestion.service;

import com.vetcitas.gestion.dto.AuthResponse;
import com.vetcitas.gestion.dto.LoginRequest;
import com.vetcitas.gestion.dto.RegistroRequest;

/**
 * Definimos la lógica de negocio como una INTERFAZ y luego la
 * implementamos en UsuarioServiceImpl. Esto es una buena práctica
 * en Spring porque:
 *   1) Desacopla el "qué se hace" (contrato) del "cómo se hace" (implementación).
 *   2) Facilita las pruebas unitarias: se puede crear un "mock" de esta
 *      interfaz sin necesitar la lógica real ni la base de datos.
 *   3) Permite cambiar la implementación en el futuro sin tocar el
 *      controlador que la usa.
 */
public interface UsuarioService {

    /**
     * Registra un nuevo usuario con rol CLIENTE por defecto.
     * Lanza CorreoDuplicadoException si el correo ya está registrado.
     */
    AuthResponse registrarCliente(RegistroRequest request);

    /**
     * Valida las credenciales del usuario y retorna sus datos junto
     * con su rol (CLIENTE, VETERINARIO o ADMIN).
     * Lanza CredencialesInvalidasException si el correo no existe,
     * la contraseña no coincide, o la cuenta está inactiva.
     */
    AuthResponse iniciarSesion(LoginRequest request);
}
