package com.colegiocesarvallejo.matriculas_platform.maper;

import com.colegiocesarvallejo.matriculas_platform.dto.profesor.ProfesorDTO;
import com.colegiocesarvallejo.matriculas_platform.entity.Profesor;
import com.colegiocesarvallejo.matriculas_platform.dto.profesor.ProfesorRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProfesorMapper {

    public ProfesorDTO toDTO(Profesor p) {
        if (p == null) return null;
        return new ProfesorDTO(
                p.getId(),
                p.getNombre(),
                p.getApellido(),
                p.getDni(),
                p.getEspecialidad(),
                p.getEmail(),
                p.getTelefono(),
                p.getActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getCreatedBy()
        );
    }


    public void updateEntityFromRequest(Profesor target, ProfesorRequest req) {
        if (req.getNombre() != null) target.setNombre(req.getNombre());
        if (req.getApellido() != null) target.setApellido(req.getApellido());
        if (req.getDni() != null) target.setDni(req.getDni());
        if (req.getEmail() != null) target.setEmail(req.getEmail());
        if (req.getTelefono() != null) target.setTelefono(req.getTelefono());
        if (req.getEspecialidad() != null) target.setEspecialidad(req.getEspecialidad());
        if (req.getActivo() != null) target.setActivo(req.getActivo());
        target.setUpdatedAt(LocalDateTime.now());
    }

    public Profesor toEntity(ProfesorRequest req) {
        Profesor p = new Profesor();
        p.setNombre(req.getNombre());
        p.setApellido(req.getApellido());
        p.setDni(req.getDni());
        p.setEmail(req.getEmail());
        p.setTelefono(req.getTelefono());
        p.setEspecialidad(req.getEspecialidad());
        p.setActivo(req.getActivo() != null ? req.getActivo() : true);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        p.setCreatedBy("ADMIN");
        return p;
    }
}
