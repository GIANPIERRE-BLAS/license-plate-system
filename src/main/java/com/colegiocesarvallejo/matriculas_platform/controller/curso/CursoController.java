package com.colegiocesarvallejo.matriculas_platform.controller.curso;

import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoRequestDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.cursos.CursoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import com.colegiocesarvallejo.matriculas_platform.service.curso.CursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/con-profesor")
    public ResponseEntity<List<CursoResponseDTO>> listarCursosConProfesor() {
        List<CursoResponseDTO> cursos = cursoService.obtenerCursosConProfesor();
        return ResponseEntity.ok(cursos.isEmpty() ? Collections.emptyList() : cursos);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CursoResponseDTO>> obtenerTodos() {
        List<CursoResponseDTO> cursos = cursoService.obtenerCursosConProfesor();
        return ResponseEntity.ok(cursos.isEmpty() ? Collections.emptyList() : cursos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CursoResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            CursoResponseDTO curso = cursoService.obtenerPorId(id);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            log.error("Error al obtener curso con ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CursoResponseDTO> crearCurso(@RequestBody CursoRequestDTO dto) {
        CursoResponseDTO creado = cursoService.crearCurso(dto);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CursoResponseDTO> actualizarCurso(@PathVariable Long id,
                                                            @RequestBody CursoRequestDTO dto) {
        CursoResponseDTO actualizado = cursoService.actualizarCurso(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<CursoResponseDTO>> getCursosDisponibles(@RequestParam(required = false) String nivel,
                                                                       @RequestParam(required = false) String grado) {
        List<CursoResponseDTO> cursos;

        if (nivel != null && !nivel.trim().isEmpty()) {
            NivelAcademico nivelEnum;
            try {
                nivelEnum = NivelAcademico.valueOf(nivel.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Nivel académico inválido recibido: {}", nivel);
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            if (grado != null && !grado.trim().isEmpty()) {
                cursos = cursoService.obtenerCursosPorNivelYGrado(nivelEnum, grado.trim());
            } else {
                cursos = cursoService.findCursosByNivel(nivelEnum);
            }
        } else {
            cursos = cursoService.obtenerCursosDisponibles();
        }

        return ResponseEntity.ok(cursos.isEmpty() ? Collections.emptyList() : cursos);
    }

    @GetMapping("/nivel/{nivel}/grado/{grado}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<CursoResponseDTO>> obtenerPorNivelYGrado(@PathVariable String nivel,
                                                                        @PathVariable String grado) {
        NivelAcademico nivelEnum;
        try {
            nivelEnum = NivelAcademico.valueOf(nivel.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Nivel académico inválido recibido (path): {}", nivel);
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<CursoResponseDTO> cursos = cursoService.obtenerCursosPorNivelYGrado(nivelEnum, grado);
        return ResponseEntity.ok(cursos.isEmpty() ? Collections.emptyList() : cursos);
    }
}
