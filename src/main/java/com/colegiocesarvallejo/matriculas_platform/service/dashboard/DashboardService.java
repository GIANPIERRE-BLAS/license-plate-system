package com.colegiocesarvallejo.matriculas_platform.service.dashboard;

import com.colegiocesarvallejo.matriculas_platform.dto.dashboard.*;
import com.colegiocesarvallejo.matriculas_platform.dto.reportes_estadIsticas.EstadisticasMatriculaDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.*;
import com.colegiocesarvallejo.matriculas_platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final EstudianteRepository estudianteRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final PagoRepository pagoRepository;
    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardPadreDTO obtenerDashboardPadre(Long apoderadoId) {
        log.info("Generando dashboard para padre ID: {}", apoderadoId);

        Usuario apoderado = usuarioRepository.findById(apoderadoId)
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado"));

        DashboardPadreDTO dashboard = new DashboardPadreDTO();
        dashboard.setNombreApoderado(apoderado.getNombre());

        List<Estudiante> estudiantes = estudianteRepository.findByApoderadoId(apoderadoId);
        dashboard.setTotalEstudiantes((long) estudiantes.size());

        List<Matricula> matriculas = matriculaRepository.findByApoderadoId(apoderadoId);
        long matriculasActivas = matriculas.stream().filter(Matricula::isAprobada).count();
        long matriculasPendientes = matriculas.stream().filter(Matricula::isPendiente).count();
        dashboard.setMatriculasActivas(matriculasActivas);
        dashboard.setMatriculasPendientes(matriculasPendientes);

        Double deudaTotal = pagoRepository.calcularDeudaTotalApoderado(apoderadoId);
        dashboard.setTotalDeuda(deudaTotal != null ? BigDecimal.valueOf(deudaTotal) : BigDecimal.ZERO);

        Long pagosVencidos = pagoRepository.contarPagosVencidos();
        dashboard.setPagosVencidos(pagosVencidos);

        List<EstudianteResumenDTO> estudiantesResumen = estudiantes.stream()
                .map(this::convertirAEstudianteResumen)
                .collect(Collectors.toList());
        dashboard.setEstudiantes(estudiantesResumen);

        List<Pago> proximosVencimientos = pagoRepository.encontrarPorApoderadoYEstados(
                apoderadoId,
                List.of(EstadoPago.PENDIENTE, EstadoPago.PARCIAL)
        ).stream().limit(5).collect(Collectors.toList());

        List<PagoResumenDTO> pagosResumen = proximosVencimientos.stream()
                .map(this::convertirAPagoResumen)
                .collect(Collectors.toList());
        dashboard.setProximosVencimientos(pagosResumen);

        dashboard.setNotificaciones(generarNotificacionesSimuladas());

        return dashboard;
    }

    public DashboardAdminDTO obtenerDashboardAdmin() {
        log.info("Generando dashboard administrativo");

        DashboardAdminDTO dashboard = new DashboardAdminDTO();
        dashboard.setTotalEstudiantes(estudianteRepository.count());
        dashboard.setTotalMatriculas(matriculaRepository.count());
        dashboard.setMatriculasPendientes(
                Long.valueOf(matriculaRepository.contarMatriculasPorEstadoYAnio(EstadoMatricula.PENDIENTE, "2024"))
        );
        dashboard.setCursosActivos(
                Long.valueOf(cursoRepository.findByActivoTrue().size())
        );

        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = LocalDateTime.now().withDayOfMonth(
                LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

        Double ingresosMes = pagoRepository.calcularIngresosPorPeriodo(inicioMes, finMes);
        dashboard.setIngresosMes(ingresosMes != null ? BigDecimal.valueOf(ingresosMes) : BigDecimal.ZERO);

        dashboard.setPagosVencidos(pagoRepository.contarPagosVencidos());

        EstadisticasMatriculaDTO estadisticasMatricula = generarEstadisticasMatricula("2024");
        dashboard.setEstadisticasMatricula(estadisticasMatricula);

        List<AlertaDTO> alertas = generarAlertas();
        dashboard.setAlertas(alertas);

        return dashboard;
    }

    public EstadisticasMatriculaDTO generarEstadisticasMatricula(String anio) {
        EstadisticasMatriculaDTO estadisticas = new EstadisticasMatriculaDTO();
        estadisticas.setAnioAcademico(anio);

        Long totalMatriculas = matriculaRepository.contarMatriculasPorEstadoYAnio(null, anio);
        estadisticas.setTotalMatriculas(totalMatriculas != null ? totalMatriculas : 0L);

        Long matriculasPendientes = matriculaRepository.contarMatriculasPorEstadoYAnio(
                EstadoMatricula.PENDIENTE, anio);
        estadisticas.setTotalPendientes(matriculasPendientes != null ? matriculasPendientes : 0L);

        Long matriculasAprobadas = matriculaRepository.contarMatriculasPorEstadoYAnio(
                EstadoMatricula.APROBADA, anio);
        estadisticas.setTotalAprobadas(matriculasAprobadas != null ? matriculasAprobadas : 0L);

        Long matriculasRechazadas = matriculaRepository.contarMatriculasPorEstadoYAnio(
                EstadoMatricula.RECHAZADA, anio);
        estadisticas.setTotalReprobadas(matriculasRechazadas != null ? matriculasRechazadas : 0L);

        if (totalMatriculas > 0) {
            double porcentajeAprobacion = (matriculasAprobadas * 100.0) / totalMatriculas;
            estadisticas.setPorcentajeAprobacion(porcentajeAprobacion);
        } else {
            estadisticas.setPorcentajeAprobacion(0.0);
        }

        return estadisticas;
    }

    private EstudianteResumenDTO convertirAEstudianteResumen(Estudiante estudiante) {
        EstudianteResumenDTO resumen = new EstudianteResumenDTO();
        resumen.setId(estudiante.getId());
        resumen.setNombreCompleto(estudiante.getNombreCompleto());
        resumen.setGrado(estudiante.getGradoActual());
        resumen.setSeccion(estudiante.getSeccionActual());
        resumen.setEstado(estudiante.getEstadoEstudiante());

        Optional<Matricula> matricula = matriculaRepository.findByEstudianteId(estudiante.getId())
                .stream().findFirst();
        matricula.ifPresent(m -> {
            resumen.setNumeroMatricula(m.getNumeroMatricula());
            resumen.setEstadoMatricula(m.getEstado());
        });

        resumen.setPromedioGeneral(15.5);
        Double deuda = pagoRepository.calcularDeudaTotalApoderado(estudiante.getApoderado().getId());
        resumen.setDeudaPendiente(deuda != null ? BigDecimal.valueOf(deuda) : BigDecimal.ZERO);

        return resumen;
    }

    private PagoResumenDTO convertirAPagoResumen(Pago pago) {
        PagoResumenDTO resumen = new PagoResumenDTO();
        resumen.setId(pago.getId());
        resumen.setNumeroComprobante(pago.getNumeroComprobante());
        resumen.setConcepto(pago.getConcepto());
        resumen.setMonto(pago.getMontoPendiente());
        resumen.setFechaVencimiento(pago.getFechaVencimiento());
        resumen.setDiasParaVencimiento(pago.getDiasVencimiento());
        resumen.setEstado(pago.getEstado());
        return resumen;
    }

    private List<NotificacionDTO> generarNotificacionesSimuladas() {
        List<NotificacionDTO> notificaciones = new ArrayList<>();
        NotificacionDTO notif = new NotificacionDTO();
        notif.setTitulo("Pago Próximo a Vencer");
        notif.setMensaje("El pago de mensualidad vence en 3 días");
        notif.setTipo("warning");
        notif.setLeida(false);
        notif.setFechaCreacion(LocalDateTime.now().minusHours(2));
        notificaciones.add(notif);
        return notificaciones;
    }

    private List<AlertaDTO> generarAlertas() {
        List<AlertaDTO> alertas = new ArrayList<>();

        Long pagosVencidos = pagoRepository.contarPagosVencidos();
        if (pagosVencidos > 0) {
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo("Pagos Vencidos");
            alerta.setMensaje("Hay pagos vencidos que requieren atención");
            alerta.setNivel("WARNING");
            alerta.setCantidad(pagosVencidos);
            alerta.setAccion("Ver pagos vencidos");
            alertas.add(alerta);
        }

        Long matriculasPendientes = matriculaRepository.contarMatriculasPorEstadoYAnio(
                EstadoMatricula.PENDIENTE, "2024");
        if (matriculasPendientes > 0) {
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo("Matrículas Pendientes");
            alerta.setMensaje("Hay matrículas pendientes de revisión");
            alerta.setNivel("INFO");
            alerta.setCantidad(matriculasPendientes);
            alerta.setAccion("Revisar matrículas");
            alertas.add(alerta);
        }

        return alertas;
    }
}
