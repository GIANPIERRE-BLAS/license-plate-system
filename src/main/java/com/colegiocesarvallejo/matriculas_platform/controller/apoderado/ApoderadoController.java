package com.colegiocesarvallejo.matriculas_platform.controller.apoderado;

import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import com.colegiocesarvallejo.matriculas_platform.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apoderados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApoderadoController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listarApoderados() {
        List<Usuario> apoderados = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.PADRE && u.getActivo())
                .toList();

        List<Map<String, Object>> lista = apoderados.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("nombre", a.getNombre());
            map.put("email", a.getEmail());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }
}
