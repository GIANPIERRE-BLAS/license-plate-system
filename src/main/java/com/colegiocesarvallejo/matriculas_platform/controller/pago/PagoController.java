package com.colegiocesarvallejo.matriculas_platform.controller.pago;

import com.colegiocesarvallejo.matriculas_platform.dto.pagos.CrearPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.ProcesarPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import com.colegiocesarvallejo.matriculas_platform.entity.Pago;
import com.colegiocesarvallejo.matriculas_platform.repository.PagoRepository;
import com.colegiocesarvallejo.matriculas_platform.service.pagos.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PADRE')")
    public ResponseEntity<?> crearPago(@Valid @RequestBody CrearPagoDTO dto,
                                       BindingResult bindingResult,
                                       Authentication auth) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }
        dto.setCreatedBy(auth.getName());
        PagoResponseDTO pago = pagoService.crearPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    @PatchMapping("/{id}/procesar")
    @PreAuthorize("hasAnyRole('ADMIN','PADRE')")
    public ResponseEntity<?> procesarPago(@PathVariable Long id,
                                          @Valid @RequestBody ProcesarPagoDTO dto,
                                          BindingResult bindingResult) {
        // Log para debugging
        log.info("=== PROCESANDO PAGO ===");
        log.info("Pago ID: {}", id);
        log.info("DTO recibido: {}", dto);

        if (bindingResult.hasErrors()) {
            log.error("Errores de validación: {}", bindingResult.getAllErrors());
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            PagoResponseDTO pago = pagoService.procesarPago(id, dto);
            log.info("Pago procesado exitosamente: {}", pago.getId());
            return ResponseEntity.ok(pago);
        } catch (RuntimeException e) {
            log.error("Error al procesar pago: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Error al procesar el pago");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            log.error("Error inesperado al procesar pago: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Error interno del servidor");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/apoderado/{apoderadoId}")
    @PreAuthorize("hasAnyRole('PADRE','ADMIN')")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorApoderado(@PathVariable Long apoderadoId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorApoderado(apoderadoId));
    }

    @GetMapping("/apoderado/{apoderadoId}/pendientes")
    @PreAuthorize("hasAnyRole('PADRE','ADMIN')")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPendientesPorApoderado(@PathVariable Long apoderadoId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPendientes(apoderadoId));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PagoResponseDTO>> obtenerPorEstado(@PathVariable EstadoPago estado,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorEstado(estado, pageable));
    }

    @GetMapping("/matricula/{matriculaId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagosPorMatricula(@PathVariable Long matriculaId) {
        List<PagoResponseDTO> pagos = pagoService.obtenerPagosPorMatricula(matriculaId);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/apoderado/{apoderadoId}/deuda-total")
    @PreAuthorize("hasAnyRole('PADRE','ADMIN')")
    public ResponseEntity<Map<String, Object>> calcularDeudaTotal(@PathVariable Long apoderadoId) {
        Double deudaTotal = pagoService.calcularDeudaTotalApoderado(apoderadoId);
        Map<String, Object> response = new HashMap<>();
        response.put("apoderadoId", apoderadoId);
        response.put("deudaTotal", deudaTotal != null ? deudaTotal : 0.0);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/corregir-residuo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> corregirResiduo(@PathVariable Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        if (pago.getMontoPendiente() != null &&
                pago.getMontoPendiente().abs().compareTo(new java.math.BigDecimal("0.05")) < 0) {
            pago.setMontoPendiente(java.math.BigDecimal.ZERO);
            pago.setMontoPagado(pago.getMontoTotal());
            pago.setEstado(EstadoPago.PAGADO);
            pago.setFechaPago(LocalDateTime.now());
            if (pago.getNumeroComprobante() == null || pago.getNumeroComprobante().startsWith("TEMP-")) {
                pago.setNumeroComprobante("CV-" + LocalDate.now().getYear() + "-" +
                        String.format("%06d", pagoRepository.count() + 1));
            }
            pagoRepository.saveAndFlush(pago);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Residuo corregido exitosamente",
                    "montoPendienteDespues", java.math.BigDecimal.ZERO
            ));
        }
        return ResponseEntity.ok(Map.of("mensaje", "No se requiere corrección"));
    }


}
