package com.colegiocesarvallejo.matriculas_platform.dto;


import com.colegiocesarvallejo.matriculas_platform.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private String mensaje;

    public AuthResponse(String token, Long id, String nombre, String email, Rol rol) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.mensaje = "Autenticación exitosa";
    }

    public AuthResponse(String mensaje) {
        this.mensaje = mensaje;
    }
}