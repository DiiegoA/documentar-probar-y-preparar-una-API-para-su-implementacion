package med.voll.api.domain.consulta;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MotivoCancelamientoDeserializer extends JsonDeserializer<MotivoCancelamiento> {

    @Override
    public MotivoCancelamiento deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        return MotivoCancelamiento.fromValue(value); // Convierte el texto JSON al Enum
    }
}
