package com.colegiocesarvallejo.matriculas_platform.dto.pagos;

import com.colegiocesarvallejo.matriculas_platform.entity.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPagoDTO {
    private Long matriculaId;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El ID del apoderado es obligatorio")
    private Long apoderadoId;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 200, message = "El concepto no puede exceder 200 caracteres")
    private String concepto;

    private String descripcion;

    @NotNull(message = "El monto original es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto original debe ser mayor a 0")
    private BigDecimal montoOriginal;

    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    private BigDecimal descuento;

    @DecimalMin(value = "0.0", message = "El recargo debe ser mayor o igual a 0")
    private BigDecimal recargo;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser una fecha futura")
    private LocalDate fechaVencimiento;

    private Boolean generaBoleta;
    private String rucFactura;
    private String razonSocialFactura;
    private String createdBy;
}
