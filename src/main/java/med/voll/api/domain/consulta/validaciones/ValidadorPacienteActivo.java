package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteActivo implements ValidadorDeConsultas {

    private final PacienteRepository pacienteRepository;

    public ValidadorPacienteActivo(PacienteRepository pacienteRepository){
        this.pacienteRepository = pacienteRepository;
    }

    public void validar(DatosReservaConsulta datosReservaConsulta){
        var pacienteEstaActivo = pacienteRepository.findActivoById(datosReservaConsulta.idPaciente());
        if (!pacienteEstaActivo){
            throw new IllegalArgumentException("PATIENT_NOT_ACTIVE");
        }
    }

}
