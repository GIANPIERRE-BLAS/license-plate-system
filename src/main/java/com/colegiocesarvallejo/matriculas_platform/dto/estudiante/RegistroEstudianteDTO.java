package com.colegiocesarvallejo.matriculas_platform.dto.estudiante;

import com.colegiocesarvallejo.matriculas_platform.entity.Genero;
import com.colegiocesarvallejo.matriculas_platform.entity.TipoDocumento;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroEstudianteDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String numeroDocumento;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private Genero genero;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    @NotNull(message = "El ID del apoderado es obligatorio")
    private Long apoderadoId;

    @Size(max = 100, message = "El nombre del contacto de emergencia no puede exceder 100 caracteres")
    private String nombreContactoEmergencia;

    @Size(max = 15, message = "El teléfono de emergencia no puede exceder 15 caracteres")
    private String telefonoEmergencia;

    private String alergias;
    private String condicionesMedicas;

    @Size(max = 5, message = "El tipo de sangre no puede exceder 5 caracteres")
    private String tipoSangre;

    private String createdBy;
}
