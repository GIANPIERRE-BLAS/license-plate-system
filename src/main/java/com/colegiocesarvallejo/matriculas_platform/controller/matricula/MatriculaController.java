package com.colegiocesarvallejo.matriculas_platform.controller.matricula;

import com.colegiocesarvallejo.matriculas_platform.dto.dashboard.NotificacionDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.IniciarMatriculaDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.MatriculaResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoMatricula;
import com.colegiocesarvallejo.matriculas_platform.entity.Usuario;
import com.colegiocesarvallejo.matriculas_platform.repository.UsuarioRepository;
import com.colegiocesarvallejo.matriculas_platform.service.matriculas.MatriculaService;
import com.colegiocesarvallejo.matriculas_platform.service.pagos.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final PagoService pagoService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<?> iniciarMatricula(@Valid @RequestBody IniciarMatriculaDTO dto,
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
            MatriculaResponseDTO matricula = matriculaService.iniciarMatricula(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MatriculaResponseDTO>> obtenerTodasMatriculas(Pageable pageable) {
        Page<MatriculaResponseDTO> matriculas = matriculaService.obtenerTodas(pageable);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MatriculaResponseDTO>> obtenerPorEstado(@PathVariable String estado,
                                                                       Pageable pageable) {
        Page<MatriculaResponseDTO> matriculas;
        if (estado.equalsIgnoreCase("todas")) {
            matriculas = matriculaService.obtenerTodas(pageable);
        } else {
            EstadoMatricula estadoEnum;
            try {
                estadoEnum = EstadoMatricula.valueOf(estado.toUpperCase().replaceAll("S$", ""));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
            matriculas = matriculaService.obtenerMatriculasPorEstado(estadoEnum, pageable);
        }
        return ResponseEntity.ok(matriculas);
    }

    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> aprobarMatricula(@PathVariable Long id,
                                              @RequestParam(required = false) String observaciones,
                                              Authentication auth) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Long procesadoPorId = usuario.getId();
            MatriculaResponseDTO matricula = matriculaService.aprobarMatricula(id, observaciones, procesadoPorId);
            pagoService.crearPagosPorMatricula(matricula);

            messagingTemplate.convertAndSendToUser(
                    matricula.getApoderadoId().toString(),
                    "/queue/notificaciones",
                    new NotificacionDTO(
                            null,
                            "Matrícula Aprobada",
                            "Matrícula " + matricula.getNumeroMatricula() + " aprobada. Se han generado pagos pendientes.",
                            "success",
                            false,
                            LocalDateTime.now()
                    )
            );

            return ResponseEntity.ok(matricula);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rechazarMatricula(@PathVariable Long id,
                                               @RequestParam String motivo,
                                               Authentication auth) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Long procesadoPorId = usuario.getId();
            MatriculaResponseDTO matricula = matriculaService.rechazarMatricula(id, motivo, procesadoPorId);
            return ResponseEntity.ok(matricula);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/apoderado/{apoderadoId}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<MatriculaResponseDTO>> obtenerPorApoderado(@PathVariable Long apoderadoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.obtenerMatriculasPorApoderado(apoderadoId);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<MatriculaResponseDTO> matricula = matriculaService.obtenerPorId(id);
        return matricula.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Matrícula no encontrada")));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MatriculaResponseDTO>> buscarMatriculas(
            @RequestParam String busqueda,
            Pageable pageable) {
        Page<MatriculaResponseDTO> matriculas = matriculaService.buscarMatriculas(busqueda, pageable);
        return ResponseEntity.ok(matriculas);
    }

    @GetMapping("/anios")
    public ResponseEntity<List<String>> obtenerAniosAcademicos() {
        List<String> anios = matriculaService.obtenerAniosAcademicos();
        return ResponseEntity.ok(anios);
    }

    @GetMapping("/estudiante/{estudianteId}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<List<MatriculaResponseDTO>> obtenerPorEstudiante(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) String anioAcademico) {
        List<MatriculaResponseDTO> matriculas;
        if (anioAcademico != null && !anioAcademico.isEmpty()) {
            matriculas = matriculaService.obtenerMatriculasPorEstudianteYAnio(estudianteId, anioAcademico);
        } else {
            matriculas = matriculaService.obtenerMatriculasPorEstudiante(estudianteId);
        }

        if (matriculas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }
        return ResponseEntity.ok(matriculas);
    }
}
