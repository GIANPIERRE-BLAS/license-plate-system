package com.colegiocesarvallejo.matriculas_platform.controller.usuario;

import com.colegiocesarvallejo.matriculas_platform.dto.usuario.UsuarioDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import com.colegiocesarvallejo.matriculas_platform.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Usuario> usuarios = usuarioService.listarTodos(pageRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("content", usuarios.getContent());
            response.put("currentPage", usuarios.getNumber());
            response.put("totalItems", usuarios.getTotalElements());
            response.put("totalPages", usuarios.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al listar usuarios", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al listar usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error al listar todos los usuarios", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al listar usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            return usuarioService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "Usuario no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body((Usuario) error);
                    });
        } catch (Exception e) {
            log.error("Error al obtener usuario con ID: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al obtener usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Usuario usuario = usuarioService.crearUsuario(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            log.error("Error al crear usuario", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error interno al crear usuario", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @Valid @RequestBody UsuarioDTO usuarioDTO,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Usuario usuario = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            log.error("Error al actualizar usuario con ID: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error interno al actualizar usuario", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario desactivado correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al eliminar usuario con ID: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error interno al eliminar usuario", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                           @RequestParam Boolean activo) {
        try {
            Usuario usuario = usuarioService.cambiarEstado(id, activo);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            log.error("Error al cambiar estado del usuario con ID: {}", id, e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error interno al cambiar estado", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarUsuarios(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Usuario> usuarios = usuarioService.buscar(termino, pageRequest);
            return ResponseEntity.ok(usuarios.getContent());
        } catch (Exception e) {
            log.error("Error al buscar usuarios", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al buscar usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorRol(@PathVariable Rol rol) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorRol(rol);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error al buscar usuarios por rol: {}", rol, e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al buscar usuarios por rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarActivos() {
        try {
            List<Usuario> usuarios = usuarioService.buscarActivos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error al buscar usuarios activos", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al buscar usuarios activos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("total", usuarioService.contarTotal());
            estadisticas.put("activos", usuarioService.contarActivos());
            estadisticas.put("admins", usuarioService.contarPorRol(Rol.ADMIN));
            estadisticas.put("docentes", usuarioService.contarPorRol(Rol.PROFESOR));
            estadisticas.put("padres", usuarioService.contarPorRol(Rol.PADRE));

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error("Error al obtener estadísticas", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al obtener estadísticas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

    }
    @GetMapping("/apoderados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarApoderados() {
        try {
            List<Usuario> apoderados = usuarioService.buscarPorRol(Rol.PADRE);
            List<Usuario> apoderadosActivos = apoderados.stream()
                    .filter(usuario -> Boolean.TRUE.equals(usuario.getActivo()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(apoderadosActivos);
        } catch (Exception e) {
            log.error("Error al listar apoderados", e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al listar apoderados");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}