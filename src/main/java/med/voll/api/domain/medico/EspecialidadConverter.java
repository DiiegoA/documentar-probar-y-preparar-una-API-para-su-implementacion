package med.voll.api.domain.medico;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EspecialidadConverter implements AttributeConverter<Especialidad, String> {

    @Override
    public String convertToDatabaseColumn(Especialidad attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toValue(); // Almacena con la primera letra en mayúscula
    }

    @Override
    public Especialidad convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            throw new IllegalArgumentException("Especialidad no válida.");
        }
        return Especialidad.fromValue(dbData); // Convierte de la base de datos al Enum
    }
}
