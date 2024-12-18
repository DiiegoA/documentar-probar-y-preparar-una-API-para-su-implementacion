package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validaciones.ValidadorDeConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ReservaDeConsultas {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final List<ValidadorDeConsultas> validadores;

    public ReservaDeConsultas(final ConsultaRepository consultaRepository, final MedicoRepository medicoRepository, final PacienteRepository pacienteRepository, List<ValidadorDeConsultas> validadores) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadores = validadores;
    }

    public Consulta reservar(final DatosReservaConsulta datosReservaConsulta) {
        if (datosReservaConsulta == null) {
            throw new IllegalArgumentException("ERR_INVALID_REQUEST");
        }

        if (datosReservaConsulta.idMedico() != null && !medicoRepository.existsById(datosReservaConsulta.idMedico())) {
            throw new IllegalArgumentException("ERR_MEDICAL_NOT_FOUND");
        }

        if (datosReservaConsulta.idMedico() != null) {
            boolean medicoActivo = medicoRepository.existsByIdAndActivoTrue(datosReservaConsulta.idMedico());
            if (!medicoActivo) {
                throw new IllegalArgumentException("ERR_MEDICAL_NOT_AVAILABLE");
            }
        }

        if (datosReservaConsulta.idPaciente() != null) {
            boolean pacienteActivo = pacienteRepository.existsByIdAndActivoTrue(datosReservaConsulta.idPaciente());
            if (!pacienteActivo) {
                throw new IllegalArgumentException("ERR_PATIENT_NOT_AVAILABLE");
            }
        }

        // validaciones

        validadores.forEach(v -> v.validar(datosReservaConsulta));


        Paciente paciente = pacienteRepository.findById(datosReservaConsulta.idPaciente())
                .orElseThrow(() -> new IllegalArgumentException("ERR_PATIENT_NOT_FOUND"));

        var medico = elegirMedico(datosReservaConsulta);
        var consulta = new Consulta(null, medico, paciente, datosReservaConsulta.fecha(), null);
        consultaRepository.save(consulta);
        return consulta;
    }

    private Medico elegirMedico(final DatosReservaConsulta datosReservaConsulta) {

        if (datosReservaConsulta.especialidad() == null) {
            throw new IllegalArgumentException("ERR_SPECIALITY_REQUIRED");
        }

        // Consulta en JPQL para encontrar médicos disponibles
        List<Medico> medicosDisponibles = medicoRepository.elegirMedicoAleatorioDisponibleEnLaFecha(
                datosReservaConsulta.especialidad(),
                datosReservaConsulta.fecha()
        );

        if (medicosDisponibles.isEmpty()) {
            throw new IllegalArgumentException("ERR_NO_RANDOM_MEDICAL_AVAILABLE");
        }

        // Seleccionar un médico aleatorio de la lista
        Random random = new Random();
        return medicosDisponibles.get(random.nextInt(medicosDisponibles.size()));
    }



    public void cancelar(final DatosCancelamientoConsulta datos) {
        var consulta = consultaRepository.findById(datos.idConsulta())
                .orElseThrow(() -> new IllegalArgumentException("ERR_CANCELLATION_NOT_FOUND"));

        // Crea una nueva instancia de Consulta con el motivo actualizado
        var consultaCancelada = consulta.cancelar(datos.motivo());

        // Guarda la nueva instancia en la base de datos
        consultaRepository.save(consultaCancelada);
    }
}