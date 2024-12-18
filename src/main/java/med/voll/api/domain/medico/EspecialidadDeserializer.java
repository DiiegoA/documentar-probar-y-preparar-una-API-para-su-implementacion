package med.voll.api.domain.medico;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class EspecialidadDeserializer extends JsonDeserializer<Especialidad> {

    @Override
    public Especialidad deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        return Especialidad.fromValue(value); // Usa el método personalizado para procesar la entrada
    }
}