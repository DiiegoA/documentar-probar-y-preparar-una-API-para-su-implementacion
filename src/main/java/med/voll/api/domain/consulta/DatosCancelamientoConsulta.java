package med.voll.api.domain.consulta;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public record DatosCancelamientoConsulta(
        @NotNull
        @JsonAlias("consulta_id") Long idConsulta,
        @NotNull(message = "Debe seleccionar una cancelacion válida")
        MotivoCancelamiento motivo
) {
}
