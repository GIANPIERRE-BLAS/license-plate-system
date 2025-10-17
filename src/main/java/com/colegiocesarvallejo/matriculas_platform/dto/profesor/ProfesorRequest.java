package com.colegiocesarvallejo.matriculas_platform.dto.profesor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfesorRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String especialidad;
    private Boolean activo;
}
