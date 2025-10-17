package com.colegiocesarvallejo.matriculas_platform.service.usuario;

import com.colegiocesarvallejo.matriculas_platform.dto.RegisterRequest;
import com.colegiocesarvallejo.matriculas_platform.dto.usuario.UsuarioDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import com.colegiocesarvallejo.matriculas_platform.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(RegisterRequest request) {
        log.info("Registrando nuevo usuario con email: {}", request.getEmail());
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.PADRE);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setNumeroDocumento(request.getNumeroDocumento());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setActivo(true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        usuario.setCreatedBy((auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : "SYSTEM");

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioGuardado;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Page<Usuario> listarTodos(Pageable pageable) {
        log.info("Listando usuarios - Página: {}, Tamaño: {}", pageable.getPageNumber(), pageable.getPageSize());
        return usuarioRepository.findAll(pageable);
    }

    public List<Usuario> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Page<Usuario> buscar(String termino, Pageable pageable) {
        log.info("Buscando usuarios con término: {}", termino);
        return usuarioRepository.buscarUsuarios(termino, pageable);
    }

    public List<Usuario> buscarPorRol(Rol rol) {
        log.info("Buscando usuarios por rol: {}", rol);
        return usuarioRepository.findByRol(rol);
    }

    public List<Usuario> buscarActivos() {
        log.info("Buscando usuarios activos");
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional
    public Usuario crearUsuario(UsuarioDTO usuarioDTO) {
        log.info("Creando nuevo usuario con email: {}", usuarioDTO.getEmail());
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email: " + usuarioDTO.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setRol(usuarioDTO.getRol() != null ? usuarioDTO.getRol() : Rol.PADRE);
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setNumeroDocumento(usuarioDTO.getNumeroDocumento());
        usuario.setTipoDocumento(usuarioDTO.getTipoDocumento());
        usuario.setActivo(usuarioDTO.getActivo() != null ? usuarioDTO.getActivo() : true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        usuario.setCreatedBy((auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : "SYSTEM");

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Ya existe otro usuario con este email: " + usuarioDTO.getEmail());
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        usuario.setRol(usuarioDTO.getRol());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setNumeroDocumento(usuarioDTO.getNumeroDocumento());
        usuario.setTipoDocumento(usuarioDTO.getTipoDocumento());
        usuario.setActivo(usuarioDTO.getActivo());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente con ID: {}", usuarioActualizado.getId());
        return usuarioActualizado;
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Eliminando (desactivando) usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado exitosamente con ID: {}", id);
    }

    @Transactional
    public void eliminarFisicamente(Long id) {
        log.info("Eliminando físicamente usuario con ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado físicamente con ID: {}", id);
    }

    @Transactional
    public Usuario cambiarEstado(Long id, Boolean activo) {
        log.info("Cambiando estado del usuario ID: {} a {}", id, activo);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(activo);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Estado del usuario actualizado exitosamente");
        return usuarioActualizado;
    }

    public long contarTotal() {
        return usuarioRepository.count();
    }

    public long contarActivos() {
        return usuarioRepository.findByActivoTrue().size();
    }

    public long contarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).size();
    }
}
