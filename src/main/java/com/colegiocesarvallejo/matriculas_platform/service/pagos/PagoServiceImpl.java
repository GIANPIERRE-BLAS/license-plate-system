package com.colegiocesarvallejo.matriculas_platform.service.pagos;

import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.MatriculaResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.CrearPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.ProcesarPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.*;
import com.colegiocesarvallejo.matriculas_platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    @Override
    public PagoResponseDTO crearPago(CrearPagoDTO dto) {
        var estudiante = estudianteRepository.findById(dto.getEstudianteId())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        var apoderado = usuarioRepository.findById(dto.getApoderadoId())
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado"));

        Pago pago = new Pago();
        pago.setEstudiante(estudiante);
        pago.setApoderado(apoderado);
        pago.setTipoPago(dto.getTipoPago());
        pago.setConcepto(dto.getConcepto());
        pago.setDescripcion(dto.getDescripcion());
        pago.setMontoOriginal(dto.getMontoOriginal());
        pago.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        pago.setRecargo(dto.getRecargo() != null ? dto.getRecargo() : BigDecimal.ZERO);
        pago.setFechaEmision(LocalDate.now());
        pago.setFechaVencimiento(dto.getFechaVencimiento());
        pago.setCreatedBy(dto.getCreatedBy());
        pago.setEstado(EstadoPago.PENDIENTE);

        return convertirAResponseDTO(pagoRepository.save(pago));
    }

    @Override
    @Transactional
    public PagoResponseDTO procesarPago(Long pagoId, ProcesarPagoDTO dto) {
        log.info("Iniciando procesamiento de pago ID: {}", pagoId);
        log.info("Datos recibidos - Método: {}, Monto: {}", dto.getMetodoPago(), dto.getMontoPagado());

        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));

        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new RuntimeException("El pago ya fue procesado anteriormente");
        }

        log.info("Estado actual del pago: {}, Monto total: {}, Monto pagado: {}",
                pago.getEstado(), pago.getMontoTotal(), pago.getMontoPagado());

        if (pago.getMontoTotal() == null) {
            BigDecimal montoOriginal = pago.getMontoOriginal() != null ? pago.getMontoOriginal() : BigDecimal.ZERO;
            BigDecimal descuento = pago.getDescuento() != null ? pago.getDescuento() : BigDecimal.ZERO;
            BigDecimal recargo = pago.getRecargo() != null ? pago.getRecargo() : BigDecimal.ZERO;
            BigDecimal montoTotalCalculado = montoOriginal.subtract(descuento).add(recargo);
            pago.setMontoTotal(montoTotalCalculado);
        }

        BigDecimal montoTotal = pago.getMontoTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoPagadoAnterior = (pago.getMontoPagado() != null ? pago.getMontoPagado() : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoPagar = (dto.getMontoPagado() != null ? dto.getMontoPagado() : montoTotal)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal montoPendienteActual = montoTotal.subtract(montoPagadoAnterior);

        if (montoPagar.compareTo(montoPendienteActual) > 0) {
            throw new RuntimeException("El monto pagado excede el monto pendiente");
        }

        if (montoPagar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto pagado debe ser mayor a cero");
        }

        BigDecimal nuevoMontoPagado = montoPagadoAnterior.add(montoPagar).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevoMontoPendiente = montoTotal.subtract(nuevoMontoPagado).setScale(2, RoundingMode.HALF_UP);

        if (nuevoMontoPendiente.abs().compareTo(new BigDecimal("0.05")) < 0) {
            nuevoMontoPendiente = BigDecimal.ZERO;
            nuevoMontoPagado = montoTotal;
        }

        pago.setMontoPagado(nuevoMontoPagado);
        pago.setMontoPendiente(nuevoMontoPendiente);

        if (nuevoMontoPendiente.compareTo(BigDecimal.ZERO) == 0) {
            pago.setEstado(EstadoPago.PAGADO);
            pago.setFechaPago(LocalDateTime.now());

            if (pago.getNumeroComprobante() == null ||
                    pago.getNumeroComprobante().isEmpty() ||
                    pago.getNumeroComprobante().startsWith("TEMP-")) {

                String nuevoComprobante = generarNumeroComprobanteUnico();
                log.info("Generando nuevo comprobante: {}", nuevoComprobante);
                pago.setNumeroComprobante(nuevoComprobante);
            } else {
                log.info("Manteniendo comprobante existente: {}", pago.getNumeroComprobante());
            }
        } else if (nuevoMontoPagado.compareTo(BigDecimal.ZERO) > 0) {
            pago.setEstado(EstadoPago.PARCIAL);
        } else {
            pago.setEstado(EstadoPago.PENDIENTE);
        }

        pago.setMetodoPago(dto.getMetodoPago());
        pago.setNumeroOperacion(dto.getNumeroOperacion());
        pago.setBanco(dto.getBanco());

        StringBuilder observacionesCompletas = new StringBuilder();
        if (pago.getObservaciones() != null && !pago.getObservaciones().isEmpty()) {
            observacionesCompletas.append(pago.getObservaciones()).append(" | ");
        }

        if (dto.getObservaciones() != null && !dto.getObservaciones().isEmpty()) {
            observacionesCompletas.append(dto.getObservaciones());
        }

        if ("TARJETA".equals(dto.getMetodoPago())) {
            if (dto.getNombreTarjeta() != null) {
                observacionesCompletas.append(" | Titular: ").append(dto.getNombreTarjeta());
            }
            if (dto.getNumeroTarjetaUltimos4() != null) {
                observacionesCompletas.append(" | Tarjeta: ****").append(dto.getNumeroTarjetaUltimos4());
            }
        } else if ("YAPE".equals(dto.getMetodoPago()) || "PLIN".equals(dto.getMetodoPago())) {
            if (dto.getTelefonoYape() != null) {
                observacionesCompletas.append(" | Teléfono: ").append(dto.getTelefonoYape());
            }
        } else if ("EFECTIVO".equals(dto.getMetodoPago())) {
            if (dto.getFechaPagoEfectivo() != null) {
                observacionesCompletas.append(" | Fecha programada: ").append(dto.getFechaPagoEfectivo());
            }
        } else if ("TRANSFERENCIA".equals(dto.getMetodoPago())) {
            if (dto.getFechaOperacion() != null) {
                observacionesCompletas.append(" | Fecha operación: ").append(dto.getFechaOperacion());
            }
        }

        pago.setObservaciones(observacionesCompletas.toString());

        if (dto.getProcesadoPorId() != null) {
            usuarioRepository.findById(dto.getProcesadoPorId())
                    .ifPresent(pago::setProcesadoPor);
        }

        Pago pagoGuardado = pagoRepository.saveAndFlush(pago);
        log.info("Pago procesado exitosamente. ID: {}, Estado: {}, Método: {}, Comprobante: {}",
                pagoGuardado.getId(), pagoGuardado.getEstado(), pagoGuardado.getMetodoPago(),
                pagoGuardado.getNumeroComprobante());

        return convertirAResponseDTO(pagoGuardado);
    }

    private String generarNumeroComprobanteUnico() {
        String anio = String.valueOf(LocalDate.now().getYear());
        String numeroBase;
        String comprobanteGenerado;
        int intentos = 0;
        int maxIntentos = 10;

        do {
            Long contador = pagoRepository.count() + 1 + intentos;
            numeroBase = String.format("%06d", contador);
            comprobanteGenerado = "CV-" + anio + "-" + numeroBase;
            intentos++;

            if (intentos >= maxIntentos) {
                comprobanteGenerado = "CV-" + anio + "-" + System.currentTimeMillis();
                break;
            }
        } while (pagoRepository.existsByNumeroComprobante(comprobanteGenerado));

        log.info("Comprobante único generado: {} (intentos: {})", comprobanteGenerado, intentos);
        return comprobanteGenerado;
    }

    @Override
    public List<PagoResponseDTO> obtenerPagosPorApoderado(Long apoderadoId) {
        return pagoRepository.findByApoderadoId(apoderadoId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDTO> obtenerPagosPendientes(Long apoderadoId) {
        List<EstadoPago> estadosPendientes = List.of(EstadoPago.PENDIENTE, EstadoPago.PARCIAL, EstadoPago.VENCIDO);
        return pagoRepository.encontrarPorApoderadoYEstados(apoderadoId, estadosPendientes)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDTO> obtenerPagosPorMatricula(Long matriculaId) {
        return pagoRepository.findByMatriculaId(matriculaId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PagoResponseDTO> obtenerPagosPorEstado(EstadoPago estado, Pageable pageable) {
        return pagoRepository.findByEstado(estado, pageable)
                .map(this::convertirAResponseDTO);
    }

    @Override
    public Double calcularDeudaTotalApoderado(Long apoderadoId) {
        Double deuda = pagoRepository.calcularDeudaTotalApoderado(apoderadoId);
        return deuda != null ? deuda : 0.0;
    }

    @Override
    public void crearPagosPorMatricula(MatriculaResponseDTO matriculaDTO) {

        if (!"APROBADA".equals(matriculaDTO.getEstado().name()) && !"COMPLETADA".equals(matriculaDTO.getEstado().name())) {
            log.warn("No se crearán pagos. Matrícula no aprobada. ID: {}, Estado: {}",
                    matriculaDTO.getId(), matriculaDTO.getEstado());
            return;
        }

        if (matriculaDTO.getPagosGenerados() || pagoRepository.existsByMatriculaId(matriculaDTO.getId())) {
            log.warn("Ya existen pagos para la matrícula ID: {}", matriculaDTO.getId());
            return;
        }

        Matricula matricula = matriculaRepository.findById(matriculaDTO.getId())
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        BigDecimal costoMatricula = matricula.getMontoMatricula() != null ? matricula.getMontoMatricula() : BigDecimal.ZERO;
        BigDecimal costoMensualidad = matricula.getMontoMensualidad() != null ? matricula.getMontoMensualidad() : BigDecimal.ZERO;
        BigDecimal costoMateriales = matricula.getMontoMateriales() != null ? matricula.getMontoMateriales() : BigDecimal.ZERO;
        int cantidadCursos = matricula.getCursos() != null ? matricula.getCursos().size() : 1;

        // ✅ 1️⃣ PAGO DE MATRÍCULA
        if (costoMatricula.compareTo(BigDecimal.ZERO) > 0) {
            Pago pagoMatricula = new Pago();
            pagoMatricula.setMatricula(matricula);
            pagoMatricula.setEstudiante(matricula.getEstudiante());
            pagoMatricula.setApoderado(matricula.getApoderado());
            pagoMatricula.setTipoPago(TipoPago.MATRICULA);
            pagoMatricula.setConcepto("Matrícula " + matricula.getGrado() + " - " + matricula.getAnioAcademico());
            pagoMatricula.setDescripcion("Costo de inscripción académica para el año " + matricula.getAnioAcademico());
            pagoMatricula.setMontoOriginal(costoMatricula);
            pagoMatricula.setDescuento(BigDecimal.ZERO);
            pagoMatricula.setRecargo(BigDecimal.ZERO);
            pagoMatricula.setFechaEmision(LocalDate.now());
            pagoMatricula.setFechaVencimiento(LocalDate.now().plusDays(30));
            pagoMatricula.setCreatedBy("SYSTEM");
            pagoMatricula.setEstado(EstadoPago.PENDIENTE);

            pagoRepository.save(pagoMatricula);
            log.info("✅ Pago de MATRÍCULA creado: S/ {}", costoMatricula);
        }

        // ✅ 2️⃣ PAGO DE MENSUALIDAD
        if (costoMensualidad.compareTo(BigDecimal.ZERO) > 0) {
            Pago pagoMensualidad = new Pago();
            pagoMensualidad.setMatricula(matricula);
            pagoMensualidad.setEstudiante(matricula.getEstudiante());
            pagoMensualidad.setApoderado(matricula.getApoderado());
            pagoMensualidad.setTipoPago(TipoPago.MENSUALIDAD);
            pagoMensualidad.setConcepto("Mensualidad " + matricula.getGrado() + " - " + matricula.getAnioAcademico());
            pagoMensualidad.setDescripcion("Pago mensual por " + cantidadCursos + " curso(s) inscritos");
            pagoMensualidad.setMontoOriginal(costoMensualidad);
            pagoMensualidad.setDescuento(BigDecimal.ZERO);
            pagoMensualidad.setRecargo(BigDecimal.ZERO);
            pagoMensualidad.setFechaEmision(LocalDate.now());
            pagoMensualidad.setFechaVencimiento(LocalDate.now().plusDays(15));
            pagoMensualidad.setCreatedBy("SYSTEM");
            pagoMensualidad.setEstado(EstadoPago.PENDIENTE);

            pagoRepository.save(pagoMensualidad);
            log.info("✅ Pago de MENSUALIDAD creado: S/ {}", costoMensualidad);
        }

        // ✅ 3️⃣ PAGO DE MATERIALES
        if (costoMateriales.compareTo(BigDecimal.ZERO) > 0) {
            Pago pagoMateriales = new Pago();
            pagoMateriales.setMatricula(matricula);
            pagoMateriales.setEstudiante(matricula.getEstudiante());
            pagoMateriales.setApoderado(matricula.getApoderado());
            pagoMateriales.setTipoPago(TipoPago.MATERIALES);
            pagoMateriales.setConcepto("Materiales Educativos " + matricula.getGrado() + " - " + matricula.getAnioAcademico());
            pagoMateriales.setDescripcion("Material didáctico para " + cantidadCursos + " curso(s)");
            pagoMateriales.setMontoOriginal(costoMateriales);
            pagoMateriales.setDescuento(BigDecimal.ZERO);
            pagoMateriales.setRecargo(BigDecimal.ZERO);
            pagoMateriales.setFechaEmision(LocalDate.now());
            pagoMateriales.setFechaVencimiento(LocalDate.now().plusDays(45));
            pagoMateriales.setCreatedBy("SYSTEM");
            pagoMateriales.setEstado(EstadoPago.PENDIENTE);

            pagoRepository.save(pagoMateriales);
            log.info("✅ Pago de MATERIALES creado: S/ {}", costoMateriales);
        }

        // ✅ Marcar que los pagos ya fueron generados
        matricula.setPagosGenerados(true);
        matriculaRepository.save(matricula);

        log.info("✅ TODOS los pagos creados exitosamente para matrícula ID: {}", matricula.getId());
        log.info("   - Matrícula: S/ {}", costoMatricula);
        log.info("   - Mensualidad: S/ {}", costoMensualidad);
        log.info("   - Materiales: S/ {}", costoMateriales);
        log.info("   - TOTAL: S/ {}", costoMatricula.add(costoMensualidad).add(costoMateriales));
    }

    private PagoResponseDTO convertirAResponseDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setNumeroComprobante(pago.getNumeroComprobante());
        dto.setNombreEstudiante(pago.getEstudiante().getNombreCompleto());
        dto.setNombreApoderado(pago.getApoderado().getNombre());
        dto.setTipoPago(pago.getTipoPago());
        dto.setConcepto(pago.getConcepto());
        dto.setDescripcion(pago.getDescripcion());
        dto.setMontoOriginal(pago.getMontoOriginal());
        dto.setDescuento(pago.getDescuento());
        dto.setRecargo(pago.getRecargo());
        dto.setMontoTotal(pago.getMontoTotal());
        dto.setMontoPagado(pago.getMontoPagado());
        dto.setMontoPendiente(pago.getMontoPendiente());
        dto.setFechaPago(pago.getFechaPago());
        dto.setNumeroOperacion(pago.getNumeroOperacion());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstado(pago.getEstado());
        dto.setFechaVencimiento(pago.getFechaVencimiento());
        dto.setFechaEmision(pago.getFechaEmision());
        dto.setBanco(pago.getBanco());
        dto.setObservaciones(pago.getObservaciones());

        if (pago.getMatricula() != null && pago.getMatricula().getGrado() != null) {
            dto.setGrado(pago.getMatricula().getGrado());
        } else if (pago.getEstudiante() != null && pago.getEstudiante().getGradoActual() != null) {
            dto.setGrado(pago.getEstudiante().getGradoActual());
        } else {
            dto.setGrado("N/A");
        }

        return dto;
    }
}