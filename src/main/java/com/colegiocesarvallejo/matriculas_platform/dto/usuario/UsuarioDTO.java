package com.colegiocesarvallejo.matriculas_platform.dto.usuario;

import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
    private String telefono;
    private String direccion;
    private String numeroDocumento;
    private String tipoDocumento;
    private Boolean activo;
}
