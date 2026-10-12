package com.vetcitas.gestion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) que representa el cuerpo JSON que el
 * frontend envía al endpoint de registro.
 *
 * ¿Por qué no usar directamente la entidad Usuario aquí?
 * Porque el cliente NUNCA debería poder enviar campos como "rol",
 * "activo" o "id" desde el formulario de registro público. Si
 * expusiéramos la entidad tal cual, alguien podría manipular el
 * JSON y auto-asignarse el rol ADMIN. El DTO actúa como una "capa
 * de seguridad" que solo permite los campos que realmente vienen
 * del formulario (nombre, correo, teléfono, password).
 *
 * Las anotaciones @NotBlank, @Email y @Size son de Bean Validation
 * (jakarta.validation). Sirven como una segunda capa de validación
 * en el SERVIDOR, complementando las validaciones que ya hace
 * auth.js en el navegador (el frontend nunca es 100% confiable,
 * porque el usuario podría desactivar JavaScript o llamar a la
 * API directamente).
 */
public class RegistroRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    public RegistroRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
