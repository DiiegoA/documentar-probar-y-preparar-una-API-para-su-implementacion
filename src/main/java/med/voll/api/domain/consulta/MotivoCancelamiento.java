package med.voll.api.domain.consulta;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MotivoCancelamientoDeserializer.class)
public enum MotivoCancelamiento {

    PACIENTE_DESISTIO,
    MÉDICO_CANCELO,
    OTROS;

    // Convierte el Enum a String con formato: primera letra mayúscula, resto minúsculas
    public String toValue() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase().replace("_", " ");
    }

    // Convierte de String a Enum con formato flexible
    public static MotivoCancelamiento fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El motivo no puede ser nulo o vacío.");
        }
        String formattedValue = value.toUpperCase().replace(" ", "_");

        for (MotivoCancelamiento motivo : MotivoCancelamiento.values()) {
            if (motivo.name().equals(formattedValue)) {
                return motivo;
            }
        }
        throw new IllegalArgumentException("Motivo de cancelación no válido: " + value);
    }
}