package com.colegiocesarvallejo.matriculas_platform.controller.dashboard;

import com.colegiocesarvallejo.matriculas_platform.dto.dashboard.DashboardAdminDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.dashboard.DashboardPadreDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas.EstadisticasMatriculaDTO;
import com.colegiocesarvallejo.matriculas_platform.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/padre/{apoderadoId}")
    @PreAuthorize("hasRole('PADRE') or hasRole('ADMIN')")
    public ResponseEntity<DashboardPadreDTO> obtenerDashboardPadre(@PathVariable Long apoderadoId) {
        try {
            DashboardPadreDTO dashboard = dashboardService.obtenerDashboardPadre(apoderadoId);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Error al generar dashboard del padre: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAdminDTO> obtenerDashboardAdmin() {
        try {
            DashboardAdminDTO dashboard = dashboardService.obtenerDashboardAdmin();
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            log.error("Error al generar dashboard administrativo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/estadisticas/matriculas/{anio}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadisticasMatriculaDTO> obtenerEstadisticasMatricula(@PathVariable String anio) {
        try {
            EstadisticasMatriculaDTO estadisticas = dashboardService.generarEstadisticasMatricula(anio);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            log.error("Error al generar estadísticas de matrícula: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
