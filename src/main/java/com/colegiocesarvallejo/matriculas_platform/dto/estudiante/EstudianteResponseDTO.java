package com.colegiocesarvallejo.matriculas_platform.dto.estudiante;

import com.colegiocesarvallejo.matriculas_platform.entity.EstadoEstudiante;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResponseDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String numeroDocumento;
    private String tipoDocumento;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String genero;
    private String direccion;
    private String telefono;
    private String email;
    private String gradoActual;
    private String seccionActual;
    private String añoAcademico;
    private EstadoEstudiante estadoEstudiante;
    private String nombreContactoEmergencia;
    private String telefonoEmergencia;
    private String alergias;
    private String condicionesMedicas;
    private String tipoSangre;
    private Long apoderadoId;
    private String nombreApoderado;
    private String emailApoderado;
    private LocalDateTime createdAt;
}
