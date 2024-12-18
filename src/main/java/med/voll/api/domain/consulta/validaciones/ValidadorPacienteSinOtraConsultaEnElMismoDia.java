package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosReservaConsulta;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteSinOtraConsultaEnElMismoDia implements ValidadorDeConsultas {

    private final ConsultaRepository consultaRepository;

    public ValidadorPacienteSinOtraConsultaEnElMismoDia(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public void validar(DatosReservaConsulta datosReservaConsulta) {

        var primerHorario = datosReservaConsulta.fecha().withHour(7);
        var ultimoHorario = datosReservaConsulta.fecha().withHour(18);

        var pacienteTieneOtraConsultaEnElDia = consultaRepository.existsByPacienteIdAndFechaBetween(datosReservaConsulta.idPaciente(), primerHorario, ultimoHorario);
        if (pacienteTieneOtraConsultaEnElDia) {
            throw new IllegalArgumentException("DUPLICATE_APPOINTMENT");
        }
    }
}