package med.voll.api.domain.paciente;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(final PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Page<DatosListadoPaciente> listarPacientes(Pageable pageable) {
        return pacienteRepository.findByActivoTrue(pageable).map(DatosListadoPaciente::new);
    }

    @Transactional
    public Paciente registrarPaciente(final DatosRegistroPaciente datosRegistroPaciente) {
        // Verificar si ya existe un registro con el correo
        if (pacienteRepository.existsByEmail(datosRegistroPaciente.email())) {
            throw new IllegalStateException("ERR_DUPLICATE_EMAIL");
        }

        // Verificar si ya existe un registro con el documento
        if (pacienteRepository.existsByDocumentoIdentidad(datosRegistroPaciente.documentoIdentidad())) {
            throw new IllegalStateException("ERR_DUPLICATE_DOCUMENT");
        }

        // Guardar el médico
        return pacienteRepository.save(new Paciente(datosRegistroPaciente));
    }

    public URI construirUrlRegistro(Paciente paciente, UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
    }

    @Transactional
    public Paciente actualizarPaciente(final DatosActualizaPaciente datosActualizaPaciente) {
        Paciente pacienteExistente = pacienteRepository.findById(datosActualizaPaciente.id())
                .orElseThrow(() -> new EntityNotFoundException(""));

        // Verificar si el nuevo documento de identidad es diferente del actual y no está duplicado
        if (datosActualizaPaciente.documentoIdentidad() != null &&
                !datosActualizaPaciente.documentoIdentidad().equals(pacienteExistente.getEmail()) &&
                pacienteRepository.existsByDocumentoIdentidad(datosActualizaPaciente.documentoIdentidad())) {
            throw new IllegalStateException("ERR_DUPLICATE_DOCUMENT");
        }

        // Crear una nueva instancia actualizada
        Paciente pacienteActualizado = pacienteExistente.actualizarDatos(
                datosActualizaPaciente.nombre(),
                datosActualizaPaciente.documentoIdentidad(),
                datosActualizaPaciente.direccion()
        );

        // Guardar la nueva instancia actualizada
        return pacienteRepository.save(pacienteActualizado);
    }


    @Transactional
    public void desactivarPaciente(Long id) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(""));

        // Crear una nueva instancia desactivada
        Paciente pacienteDesactivado = pacienteExistente.desactivarPaciente();

        // Guardar la nueva instancia desactivada
        pacienteRepository.save(pacienteDesactivado);
    }

    public DatosRespuestaPaciente obtenerPacientePorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(""));
        return new DatosRespuestaPaciente(paciente);
    }
}
