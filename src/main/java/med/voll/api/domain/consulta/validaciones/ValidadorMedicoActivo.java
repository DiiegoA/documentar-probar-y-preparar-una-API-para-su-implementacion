package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.DatosReservaConsulta;
import med.voll.api.domain.medico.MedicoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoActivo implements ValidadorDeConsultas {

    private final MedicoRepository medicoRepository;

    public ValidadorMedicoActivo(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public void validar(DatosReservaConsulta datosReservaConsulta){

        if (datosReservaConsulta.idMedico() == null){
            return;
        }

        var medicoEstaActivo = medicoRepository.findActivoById(datosReservaConsulta.idMedico());
        if (!medicoEstaActivo){
            throw new IllegalArgumentException("MEDICAL_NOT_ACTIVE");
        }
    }
}
