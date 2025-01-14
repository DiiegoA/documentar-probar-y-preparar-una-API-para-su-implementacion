package med.voll.api.domain.paciente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByEmail(String email);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    boolean existsByIdAndActivoTrue(Long idPaciente);

    @Query("""
            SELECT p.activo
            FROM Paciente p
            WHERE
            p.id = :idPaciente
            """)
    boolean findActivoById(Long idPaciente);

    Page<Paciente> findByActivoTrue(Pageable pageable);



}

