package com.colegiocesarvallejo.matriculas_platform.dto.dashboard;

import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPadreDTO {
    private String nombreApoderado;
    private Long totalEstudiantes;
    private Long matriculasActivas;
    private Long matriculasPendientes;
    private BigDecimal totalDeuda;
    private Long pagosVencidos;
    private List<EstudianteResumenDTO> estudiantes;
    private List<PagoResumenDTO> proximosVencimientos;
    private List<NotificacionDTO> notificaciones;
    private List<PagoResponseDTO> pagos;
}
