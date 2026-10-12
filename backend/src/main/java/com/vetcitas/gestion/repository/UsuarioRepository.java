package com.vetcitas.gestion.repository;

import com.vetcitas.gestion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Repository -> Marca esta interfaz como un componente de acceso a datos.
 * Spring la detecta automáticamente (component scanning) y crea una
 * implementación en tiempo de ejecución, así que NO es necesario escribir
 * el código SQL/JDBC manualmente para las operaciones básicas.
 *
 * Al extender JpaRepository<Usuario, Long> ya obtenemos "gratis" métodos
 * como save(), findById(), findAll(), deleteById(), etc.
 * Usuario  -> tipo de la entidad que maneja este repositorio.
 * Long     -> tipo de dato de la clave primaria (id) de esa entidad.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Spring Data JPA genera automáticamente la consulta SQL a partir
     * del NOMBRE del método (esto se llama "Query Method" / "derived query").
     * "findByCorreo" se traduce internamente a:
     *   SELECT * FROM usuarios WHERE correo = ?
     *
     * Se usa Optional<Usuario> en vez de Usuario directamente para
     * evitar NullPointerException: nos obliga a manejar explícitamente
     * el caso en que el correo no exista en la base de datos.
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Igual que el anterior, pero en vez de traer el objeto completo,
     * solo comprueba si existe una fila con ese correo. Es más eficiente
     * para validar duplicados antes de registrar un nuevo usuario.
     */
    boolean existsByCorreo(String correo);
}
