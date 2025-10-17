package com.colegiocesarvallejo.matriculas_platform.dto.cursos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDisponibleDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String nivel;
    private String grado;
    private String seccion;
    private Integer creditos;
    private String horario;
    private String aula;
    private Integer capacidadActual;
    private Integer capacidadMaxima;
    private BigDecimal costoMatricula;
    private BigDecimal costoMensualidad;
    private BigDecimal costoMateriales;
    private Boolean activo;
    private String profesorNombre;
}
