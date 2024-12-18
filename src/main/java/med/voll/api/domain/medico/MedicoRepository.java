package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);
    boolean existsByIdAndActivoTrue(Long idMedico);

    @Query("""
            SELECT m.activo
            FROM Medico m
            WHERE
            m.id = :idMedico
            """)
    boolean findActivoById(Long idMedico);

    Page<Medico> findByActivoTrue(Pageable pageable);

    @Query("""
        SELECT m FROM Medico m
        WHERE m.activo = true
        AND m.especialidad = :especialidad
        AND NOT EXISTS (
            SELECT c FROM Consulta c
            WHERE c.medico = m
            AND c.fecha = :fecha
        )
        """)
    List<Medico> elegirMedicoAleatorioDisponibleEnLaFecha(Especialidad especialidad, LocalDateTime fecha);
}