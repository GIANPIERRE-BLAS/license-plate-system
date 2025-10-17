package com.colegiocesarvallejo.matriculas_platform.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    private String tipo;
    private String mensaje;
    private String nivel;
    private Long cantidad;
    private String accion;
}