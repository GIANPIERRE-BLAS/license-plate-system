package com.colegiocesarvallejo.matriculas_platform.dto.profesor;

import java.time.LocalDateTime;

public record ProfesorDTO(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String especialidad,
        String email,
        String telefono,
        Boolean activo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy
) {}
