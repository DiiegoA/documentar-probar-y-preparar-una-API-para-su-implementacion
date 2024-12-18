package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

public record DatosDetalleConsulta(
        Long id,
        Long idMedico,
        Long idPaciente,
        LocalDateTime fecha
) {

    public static DatosDetalleConsulta fromConsulta(Consulta consulta) {
        return new DatosDetalleConsulta(
                consulta.getId(),
                consulta.getMedico().getId(),
                consulta.getPaciente().getId(),
                consulta.getFecha()
        );
    }
}
