package med.voll.api.domain.medico;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(final MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public Medico registrarMedico(final DatosRegistroMedico datosRegistroMedico) {
        // Verificar si ya existe un registro con el correo
        if (medicoRepository.existsByEmail(datosRegistroMedico.email())) {
            throw new IllegalStateException("ERR_DUPLICATE_EMAIL");
        }

        // Verificar si ya existe un registro con el documento
        if (medicoRepository.existsByDocumento(datosRegistroMedico.documento())) {
            throw new IllegalStateException("ERR_DUPLICATE_DOCUMENT");
        }

        // Guardar el médico
        return medicoRepository.save(new Medico(datosRegistroMedico));
    }


    @Transactional
    public Medico actualizarDatosMedico(final DatosActualizaMedico datosActualizaMedico) {
        // Buscar el médico por ID
        Medico medicoExistente = medicoRepository.findById(datosActualizaMedico.id())
                .orElseThrow(() -> new EntityNotFoundException(""));

        // Verificar si el nuevo documento es diferente del actual y no está duplicado
        if (datosActualizaMedico.documento() != null &&
                !datosActualizaMedico.documento().equals(medicoExistente.getEmail()) &&
                medicoRepository.existsByDocumento(datosActualizaMedico.documento())) {
            throw new IllegalStateException("ERR_DUPLICATE_DOCUMENT");
        }

        // Actualizar los datos del médico
        Medico medicoActualizado = medicoExistente.actualizarDatos(
                datosActualizaMedico.nombre(),
                datosActualizaMedico.documento(),
                datosActualizaMedico.direccion()
        );

        // Guardar la nueva instancia actualizada
        return medicoRepository.save(medicoActualizado);
    }

    @Transactional
    public void desactivarMedico(Long id) {

        // Buscar el médico por ID
        Medico medicoExistente = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(""));

        // Crear una nueva instancia desactivada
        Medico medicoDesactivado = medicoExistente.desactivarMedico();

        // Guardar la nueva instancia desactivada
        medicoRepository.save(medicoDesactivado);
    }

    public DatosRespuestaMedico obtenerDatosMedicoPorId(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ERR_RECORD_NOT_FOUND"));
        return new DatosRespuestaMedico(medico);
    }
}
