package med.voll.api.domain.consulta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByPacienteIdAndFechaBetween(Long idPacinte, LocalDateTime primerHorario, LocalDateTime ultimoHorario);

    boolean existsByMedicoIdAndFecha(Long idMedico, LocalDateTime fecha);

}
