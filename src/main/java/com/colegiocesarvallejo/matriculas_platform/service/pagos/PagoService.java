package com.colegiocesarvallejo.matriculas_platform.service.pagos;

import com.colegiocesarvallejo.matriculas_platform.dto.matriculas.MatriculaResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.CrearPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.PagoResponseDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.pagos.ProcesarPagoDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.EstadoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PagoService {

    PagoResponseDTO crearPago(CrearPagoDTO dto);

    PagoResponseDTO procesarPago(Long pagoId, ProcesarPagoDTO dto);

    List<PagoResponseDTO> obtenerPagosPorApoderado(Long apoderadoId);

    List<PagoResponseDTO> obtenerPagosPorMatricula(Long matriculaId);

    List<PagoResponseDTO> obtenerPagosPendientes(Long apoderadoId);

    Page<PagoResponseDTO> obtenerPagosPorEstado(EstadoPago estado, Pageable pageable);

    Double calcularDeudaTotalApoderado(Long apoderadoId);

    void crearPagosPorMatricula(MatriculaResponseDTO matricula);
}
