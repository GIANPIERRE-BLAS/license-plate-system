package com.colegiocesarvallejo.matriculas_platform.repository;
import com.colegiocesarvallejo.matriculas_platform.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DocumentoMatriculaRepository extends JpaRepository<DocumentoMatricula, Long> {

    List<DocumentoMatricula> findByMatriculaId(Long matriculaId);
    List<DocumentoMatricula> findByTipoDocumento(String tipoDocumento);
    List<DocumentoMatricula> findByValidado(Boolean validado);
    List<DocumentoMatricula> findByRechazado(Boolean rechazado);

    @Query("SELECT d FROM DocumentoMatricula d WHERE d.matricula.id = :matriculaId AND d.esObligatorio = true AND d.validado = false")
    List<DocumentoMatricula> encontrarObligatoriosPendientes(@Param("matriculaId") Long matriculaId);

    @Query("SELECT COUNT(d) FROM DocumentoMatricula d WHERE d.matricula.id = :matriculaId AND d.esObligatorio = true")
    Long contarDocumentosObligatorios(@Param("matriculaId") Long matriculaId);

    @Query("SELECT COUNT(d) FROM DocumentoMatricula d WHERE d.matricula.id = :matriculaId AND d.esObligatorio = true AND d.validado = true")
    Long contarDocumentosValidados(@Param("matriculaId") Long matriculaId);

    @Query("SELECT d FROM DocumentoMatricula d WHERE d.validado = false AND d.rechazado = false ORDER BY d.createdAt ASC")
    List<DocumentoMatricula> encontrarDocumentosPendientesValidacion();

    boolean existsByMatriculaIdAndTipoDocumento(Long matriculaId, String tipoDocumento);
}