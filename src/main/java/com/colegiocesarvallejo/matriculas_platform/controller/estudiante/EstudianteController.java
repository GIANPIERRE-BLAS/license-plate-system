package com.colegiocesarvallejo.matriculas_platform.controller.estudiante;

import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.ActualizarEstudianteDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.EstudianteResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.estudiante.RegistroEstudianteDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoEstudiante;
import com.colegiocesarvallejo.matriculas_platform.service.estudiante.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<?> crearEstudiante(@Valid @RequestBody RegistroEstudianteDTO dto,
                                             BindingResult bindingResult,
                                             Authentication auth) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            dto.setCreatedBy(auth.getName());
            EstudianteResponseDTO estudiante = estudianteService.crearEstudiante(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(estudiante);
        } catch (RuntimeException e) {
            log.error("Error al crear estudiante: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstudiante(@PathVariable Long id,
                                                  @Valid @RequestBody ActualizarEstudianteDTO dto,
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
            EstudianteResponseDTO estudiante = estudianteService.actualizarEstudiante(id, dto);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            log.error("Error al actualizar estudiante con ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error interno al actualizar estudiante con ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/apoderado/{apoderadoId}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerPorApoderado(@PathVariable Long apoderadoId) {
        try {
            List<EstudianteResponseDTO> estudiantes = estudianteService.obtenerEstudiantesPorApoderado(apoderadoId);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            log.error("Error al obtener estudiantes para apoderado {}: {}", apoderadoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<EstudianteResponseDTO> estudiante = estudianteService.obtenerPorId(id);
            if (estudiante.isPresent()) {
                return ResponseEntity.ok(estudiante.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Estudiante no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            log.error("Error al obtener estudiante con ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EstudianteResponseDTO>> buscarEstudiantes(
            @RequestParam(required = false) String busqueda,
            Pageable pageable) {
        try {
            Page<EstudianteResponseDTO> estudiantes = estudianteService.buscarEstudiantes(busqueda, pageable);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            log.error("Error al buscar estudiantes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                           @RequestParam EstadoEstudiante estado,
                                           @RequestParam(required = false) String motivo) {
        try {
            estudianteService.cambiarEstado(id, estado, motivo);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Estado actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al cambiar estado del estudiante con ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error interno al cambiar estado del estudiante con ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/padre/{padreId}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstudiantesPaginados(
            @PathVariable Long padreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EstudianteResponseDTO> estudiantesPage = estudianteService
                    .obtenerEstudiantesPorApoderadoPaginado(padreId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", estudiantesPage.getContent());
            response.put("currentPage", estudiantesPage.getNumber());
            response.put("totalPages", estudiantesPage.getTotalPages());
            response.put("totalElements", estudiantesPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener estudiantes paginados para padre {}: {}", padreId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/padre/{padreId}/estadisticas")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasPadre(@PathVariable Long padreId) {
        try {
            List<EstudianteResponseDTO> estudiantes = estudianteService
                    .obtenerEstudiantesPorApoderado(padreId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", estudiantes.size());
            stats.put("activos", estudiantes.stream().filter(e -> "ACTIVO".equals(e.getEstadoEstudiante())).count());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error al obtener estadísticas para padre {}: {}", padreId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/padre/{padreId}/buscar")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<EstudianteResponseDTO>> buscarEstudiantesPorPadre(
            @PathVariable Long padreId,
            @RequestParam String termino) {
        try {
            List<EstudianteResponseDTO> estudiantes = estudianteService
                    .buscarEstudiantesPorApoderado(padreId, termino);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            log.error("Error al buscar estudiantes para padre {} con término {}: {}", padreId, termino, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
