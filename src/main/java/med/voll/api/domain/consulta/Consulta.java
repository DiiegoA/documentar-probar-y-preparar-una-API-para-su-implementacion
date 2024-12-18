package med.voll.api.domain.consulta;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;

import java.io.IOException;
import java.time.LocalDateTime;

@Table(name = "consultas")
@Entity(name = "Consulta")
@Getter
@NoArgsConstructor(force = true)
@EqualsAndHashCode(of = "id")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private final Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private final Paciente paciente;

    private final LocalDateTime fecha;

    @Column(name = "motivo_cancelamiento")
    @Convert(converter = MotivoCancelamientoConverter.class) // Usa el convertidor
    private final MotivoCancelamiento motivoCancelamiento;

    /// Constructor con lógica explícita
    // Constructor principal
    public Consulta(Long id, Medico medico, Paciente paciente, LocalDateTime fecha, MotivoCancelamiento motivoCancelamiento) {
        this.id = id;
        this.medico = medico;
        this.paciente = paciente;
        this.fecha = fecha;
        // Asignar MotivoCancelamiento o usar un valor predeterminado
        this.motivoCancelamiento = motivoCancelamiento != null ? motivoCancelamiento : MotivoCancelamiento.fromValue(MotivoCancelamiento.OTROS.toValue());
    }

    public Consulta cancelar(MotivoCancelamiento motivo) {
        if (motivo == null) {
            throw new IllegalArgumentException("El motivo de cancelación no puede ser nulo.");
        }
        // Retorna una nueva instancia de Consulta con el motivo actualizado
        return new Consulta(this.id, this.medico, this.paciente, this.fecha, MotivoCancelamiento.fromValue(motivo.toValue()));
    }
}