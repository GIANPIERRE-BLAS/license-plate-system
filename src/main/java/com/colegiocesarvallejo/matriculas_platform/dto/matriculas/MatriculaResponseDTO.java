package com.colegiocesarvallejo.matriculas_platform.dto.matriculas;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoMatricula;
import com.colegiocesarvallejo.matriculas_platform.entity.NivelAcademico;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponseDTO {
    private Long id;
    private String numeroMatricula;
    private Long estudianteId;
    private String nombreEstudiante;
    private String documentoEstudiante;
    private Long apoderadoId;
    private String nombreApoderado;
    private String emailApoderado;
    private String anioAcademico;
    private String grado;
    private String seccion;
    private NivelAcademico nivelAcademico;
    private EstadoMatricula estado;
    private String estadoDescripcion;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaRechazo;
    private LocalDate fechaVencimiento;
    private BigDecimal montoTotal;
    private BigDecimal montoFinal;
    private String observaciones;
    private String motivoRechazo;
    private Boolean documentosCompletos;
    private Boolean requiereEntrevista;
    private Boolean entrevistaCompletada;
    private Double porcentajeCompletitud;
    private Integer totalCursos;
    private List<CursoMatriculaDTO> cursos;
    private LocalDateTime createdAt;
    private Boolean pagosGenerados = false;
    private BigDecimal montoMatricula;
    private BigDecimal montoMensualidad;
    private BigDecimal montoMateriales;
    private List<PagoResponseDTO> pagos;
}