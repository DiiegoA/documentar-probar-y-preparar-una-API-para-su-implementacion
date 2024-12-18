package med.voll.api.domain.medico;

import jakarta.persistence.EntityManager;
import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.MotivoCancelamiento;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.paciente.DatosRegistroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Debería devolver una lista vacía cuando no hay médicos disponibles en la fecha indicada")
    void elegirMedicoAleatorioDisponibleEnLaFechaEscenario1() {
        //Give o Arrange
        // Fecha del próximo lunes a las 10:00 am
        final LocalDateTime lunesSiguienteALas10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);

        // When o Act
        // Registrar un médico ocupado en esa fecha
        final Medico medico1 = registrarMedico("Medico1", "medico1@gmail.com", "123456100", Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()));
        final Paciente paciente1 = registrarPaciente("Paciente1", "paciente1@gmail.com", "987654321");
        registrarConsulta(medico1, paciente1, lunesSiguienteALas10, MotivoCancelamiento.fromValue(MotivoCancelamiento.MÉDICO_CANCELO.toValue()));

        // Intentar obtener un médico disponible para la especialidad y fecha
        List<Medico> medicosDisponibles = medicoRepository.elegirMedicoAleatorioDisponibleEnLaFecha(
                Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()),
                lunesSiguienteALas10
        );

        // Then o Assert
        // Verifica que la lista esté vacía
        assertThat(medicosDisponibles).isEmpty();
    }

    @Test
    @DisplayName("Debería devolver un médico cuando el médico buscado esta disponible en la fecha indicada")
    void elegirMedicoAleatorioDisponibleEnLaFechaEscenario2() {
        //Give o Arrange
        // Fecha del próximo lunes a las 10:00 am
        final LocalDateTime lunesSiguienteALas10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);

        // Registrar un médico ocupado en esa fecha
        final Medico medico1 = registrarMedico("Medico1", "medico1@gmail.com", "123456100", Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()));

        // When o Act
        // Intentar obtener un médico disponible para la especialidad y fecha
        List<Medico> medicosDisponibles = medicoRepository.elegirMedicoAleatorioDisponibleEnLaFecha(
                Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()),
                lunesSiguienteALas10
        );

        // Then o Assert
        // Verifica que la lista esté vacía
        assertThat(medicosDisponibles).containsExactly(medico1);
    }

    private void registrarConsulta(final Medico medico, final Paciente paciente, final LocalDateTime fecha, final MotivoCancelamiento motivoCancelamiento) {
        entityManager.persist(new Consulta(null, medico, paciente, fecha, motivoCancelamiento)); // Usar un motivo por defecto
    }

    private Medico registrarMedico(final String nombre, final String email, final String documento, final Especialidad especialidad) {
        final Medico medico = new Medico(datosRegistroMedico(nombre, email, documento, especialidad));
        entityManager.persist(medico);
        return medico;
    }

    private Paciente registrarPaciente(final String nombre, final String email, final String documentoIdentidad) {
        final Paciente paciente = new Paciente(datosRegistroPaciente(nombre, email, documentoIdentidad));
        entityManager.persist(paciente);
        return paciente;
    }

    private DatosRegistroMedico datosRegistroMedico(final String nombre, final String email, final String documento, final Especialidad especialidad) {
        return new DatosRegistroMedico(
                nombre,
                "3116257383",
                email,
                documento,
                especialidad,
                datosDireccion()
        );
    }

    private DatosRegistroPaciente datosRegistroPaciente(final String nombre, final String email, final String documentoIdentidad) {
        return new DatosRegistroPaciente(
                nombre,
                email,
                documentoIdentidad,
                "1234567891",
                datosDireccion()
        );
    }

    private DatosDireccion datosDireccion() {
        return new DatosDireccion(
                "Calle x",
                "Distrito y",
                "Ciudad z",
                "123",
                "1"
        );
    }
}