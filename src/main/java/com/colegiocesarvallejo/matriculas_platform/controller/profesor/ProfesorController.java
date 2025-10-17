package com.colegiocesarvallejo.matriculas_platform.controller.profesor;

import com.colegiocesarvallejo.matriculas_platform.dto.profesor.ProfesorDTO;
import com.colegiocesarvallejo.matriculas_platform.dto.profesor.ProfesorRequest;
import com.colegiocesarvallejo.matriculas_platform.entity.Profesor;
import com.colegiocesarvallejo.matriculas_platform.maper.ProfesorMapper;
import com.colegiocesarvallejo.matriculas_platform.service.profesor.ProfesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profesores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfesorController {

    private final ProfesorService profesorService;
    private final ProfesorMapper profesorMapper;

    @GetMapping
    public ResponseEntity<List<ProfesorDTO>> listarTodos() {
        List<ProfesorDTO> profesores = profesorService.obtenerTodos()
                .stream()
                .map(profesorMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profesores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesorDTO> obtenerPorId(@PathVariable Long id) {
        Profesor profesor = profesorService.obtenerPorId(id);
        return ResponseEntity.ok(profesorMapper.toDTO(profesor));
    }

    @PostMapping
    public ResponseEntity<ProfesorDTO> crear(@RequestBody ProfesorRequest request) {
        Profesor nuevo = profesorMapper.toEntity(request);
        Profesor guardado = profesorService.guardar(nuevo);
        return ResponseEntity.ok(profesorMapper.toDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesorDTO> actualizar(@PathVariable Long id, @RequestBody ProfesorRequest request) {
        Profesor actualizado = profesorService.actualizar(id, request);
        return ResponseEntity.ok(profesorMapper.toDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        profesorService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<ProfesorDTO>> buscarPorEspecialidad(@PathVariable String especialidad) {
        List<ProfesorDTO> profesores = profesorService.buscarPorEspecialidad(especialidad)
                .stream()
                .map(profesorMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profesores);
    }
}
