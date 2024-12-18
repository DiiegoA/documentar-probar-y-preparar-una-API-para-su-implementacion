package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.consulta.ConsultaRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoConOtraConsultaEnElMismoHorario implements ValidadorDeConsultas {

    private final ConsultaRepository consultaRepository;

    public ValidadorMedicoConOtraConsultaEnElMismoHorario(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    public void validar(DatosReservaConsulta datosReservaConsulta){

        var medicoTieneOtraConsultaEnElMismoHrario = consultaRepository.existsByMedicoIdAndFecha(datosReservaConsulta.idMedico(),datosReservaConsulta.fecha());
        if (medicoTieneOtraConsultaEnElMismoHrario){
            throw new IllegalArgumentException("MEDICAL_SCHEDULE_CONFLICT");
        }
    }
}
