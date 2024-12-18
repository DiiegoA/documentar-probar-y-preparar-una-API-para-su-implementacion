package med.voll.api.domain.medico;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EspecialidadDeserializer.class)
public enum Especialidad {

    ORTOPEDIA,
    CARDIOLOGIA,
    GINECOLOGIA,
    PEDIATRIA;

    // Devuelve la especialidad con formato: primera letra mayúscula, el resto en minúsculas
    public String toValue() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }

    public static Especialidad fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("");
        }
        // Normaliza la entrada
        String formattedValue = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();

        for (Especialidad especialidad : Especialidad.values()) {
            if (especialidad.toValue().equalsIgnoreCase(formattedValue)) {
                return especialidad;
            }
        }

        throw new IllegalArgumentException("Especialidad no válida: " + value);
    }

}