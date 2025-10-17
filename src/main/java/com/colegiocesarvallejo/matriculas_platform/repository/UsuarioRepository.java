package com.colegiocesarvallejo.matriculas_platform.repository;

import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByActivoTrue();

    @Query("SELECT u FROM Usuario u WHERE u.rol = 'PROFESOR' AND u.activo = true")
    List<Usuario> encontrarProfesoresActivos();

    @Query("SELECT u FROM Usuario u WHERE CONCAT(u.nombre, ' ', u.email) LIKE %:busqueda%")
    Page<Usuario> buscarUsuarios(@Param("busqueda") String busqueda, Pageable pageable);
}
