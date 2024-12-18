package med.voll.api.domain.consulta.validaciones;

import med.voll.api.domain.consulta.DatosReservaConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorFueraHorarioConsultas implements ValidadorDeConsultas {


    public void validar(DatosReservaConsulta datosReservaConsulta){
        var fechaConsulta = datosReservaConsulta.fecha();
        var domingo  = fechaConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var horarioAntesDeAperturaClinica = fechaConsulta.getHour() < 7;
        var horarioDespuesDeCierreClinica = fechaConsulta.getHour() > 18;

        if (domingo || horarioAntesDeAperturaClinica || horarioDespuesDeCierreClinica){
            throw new IllegalArgumentException("OUT_OF_BUSINESS_HOURS");
        }
    }
}
