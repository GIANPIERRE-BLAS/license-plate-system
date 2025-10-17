package com.colegiocesarvallejo.matriculas_platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column(length = 200)
    private String direccion;

    @Column(length = 15)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(name = "grado_actual", length = 20)
    private String gradoActual;

    @Column(name = "seccion_actual", length = 10)
    private String seccionActual;

    @Column(name = "año_academico", length = 10)
    private String añoAcademico;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_estudiante", nullable = false)
    private EstadoEstudiante estadoEstudiante = EstadoEstudiante.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apoderado_id", nullable = false)
    private Usuario apoderado;

    @Column(name = "nombre_contacto_emergencia", length = 100)
    private String nombreContactoEmergencia;

    @Column(name = "telefono_emergencia", length = 15)
    private String telefonoEmergencia;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "condiciones_medicas", columnDefinition = "TEXT")
    private String condicionesMedicas;

    @Column(name = "tipo_sangre", length = 5)
    private String tipoSangre;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Matricula> matriculas;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Calificacion> calificaciones;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    public boolean isActivo() {
        return estadoEstudiante == EstadoEstudiante.ACTIVO;
    }
}
