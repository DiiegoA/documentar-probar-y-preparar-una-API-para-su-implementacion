package med.voll.api.domain.consulta;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MotivoCancelamientoConverter implements AttributeConverter<MotivoCancelamiento, String> {

    @Override
    public String convertToDatabaseColumn(MotivoCancelamiento attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toValue(); // Convierte con formato especial
    }

    @Override
    public MotivoCancelamiento convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            throw new IllegalArgumentException("Motivo de cancelación no puede ser nulo o vacío.");
        }
        return MotivoCancelamiento.fromValue(dbData); // Reconstruye el Enum desde la base de datos
    }
}