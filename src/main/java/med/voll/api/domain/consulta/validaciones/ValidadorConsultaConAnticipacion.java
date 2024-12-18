package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.DatosReservaConsulta;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorConsultaConAnticipacion implements ValidadorDeConsultas{

    public void validar(DatosReservaConsulta datosReservaConsulta){

        var fechaConsulta = datosReservaConsulta.fecha();
        var ahora = LocalDateTime.now();
        var diferenciaEnMinutos = Duration.between(ahora, fechaConsulta).toMinutes();
        if (diferenciaEnMinutos < 30){
            throw new IllegalArgumentException("INSUFFICIENT_ANTICIPATION");
        }
    }
}
