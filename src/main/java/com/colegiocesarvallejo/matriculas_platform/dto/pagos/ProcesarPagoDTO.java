package com.colegiocesarvallejo.matriculas_platform.dto.pagos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarPagoDTO {

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoPagado;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    private String numeroOperacion;
    private String banco;

    @Size(max = 4, message = "Solo se deben guardar los últimos 4 dígitos de la tarjeta")
    private String numeroTarjetaUltimos4;

    private String nombreTarjeta;

    private LocalDate fechaOperacion;

    private String telefonoYape;

    private String telefonoPlin;

    private String observaciones;

    @NotNull(message = "El ID del usuario que procesa es obligatorio")
    private Long procesadoPorId;

    private LocalDate fechaPagoEfectivo;


    private String titularTarjeta;
    private String codigoAutorizacion;
}