package com.colegiocesarvallejo.matriculas_platform.service.profesor;

import com.colegiocesarvallejo.matriculas_platform.dto.profesor.ProfesorRequest;
import com.colegiocesarvallejo.matriculas_platform.entity.Profesor;
import com.colegiocesarvallejo.matriculas_platform.maper.ProfesorMapper;
import com.colegiocesarvallejo.matriculas_platform.repository.ProfesorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final ProfesorMapper profesorMapper;

    @Transactional(readOnly = true)
    public Profesor obtenerPorId(Long id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Profesor> obtenerTodos() {
        return profesorRepository.findAll();
    }

    public Profesor guardar(Profesor profesor) {
        String nombreCompleto = profesor.getNombreCompleto() != null ? profesor.getNombreCompleto() : "Sin nombre";
        log.info("Guardando nuevo profesor: {}", nombreCompleto);
        return profesorRepository.save(profesor);
    }


    public Profesor actualizar(Long id, ProfesorRequest request) {
        Profesor existente = obtenerPorId(id);
        profesorMapper.updateEntityFromRequest(existente, request);
        log.info("Profesor actualizado: {}", existente.getNombreCompleto());
        return profesorRepository.save(existente);
    }

    public void darDeBaja(Long id) {
        Profesor profesor = obtenerPorId(id);
        profesor.setActivo(false);
        log.info("Profesor dado de baja: {}", profesor.getNombreCompleto());
        profesorRepository.save(profesor);
    }

    @Transactional(readOnly = true)
    public List<Profesor> buscarPorEspecialidad(String especialidad) {
        return profesorRepository.findByEspecialidad(especialidad);
    }
}
