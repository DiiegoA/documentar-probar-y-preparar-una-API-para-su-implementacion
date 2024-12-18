package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoRepository medicoRepository;

    public MedicoController(final MedicoService medicoService,
                            final MedicoRepository medicoRepository) {
        this.medicoService = medicoService;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    // public ResponseEntity<Page<DatosListadoMedico>> listaMedicos(@PageableDefault(size = 2, page = 1, sort = "nombre") Pageable pageable) {
    public ResponseEntity<Page<DatosListadoMedico>> listaMedicos(
            @Parameter(
                    schema = @Schema(example = "{}") // Esto personaliza el esquema como vacío
            )
            @PageableDefault(sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(pageable).map(DatosListadoMedico::new));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registraMedico(@Valid @RequestBody final DatosRegistroMedico datosRegistroMedico, UriComponentsBuilder uriComponentsBuilder) {
        Medico medico = medicoService.registrarMedico(datosRegistroMedico);

        DatosRespuestaMedico datosRespuestaMedico = new DatosRespuestaMedico(medico);
        URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "CREATED");
        successResponse.put("message", "Médico registrado exitosamente.");
        successResponse.put("medico", datosRespuestaMedico);

        return ResponseEntity.created(url).body(successResponse);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizaMedico(@Valid @RequestBody final DatosActualizaMedico datosActualizaMedico) {
        Medico medicoActualizado = medicoService.actualizarDatosMedico(datosActualizaMedico);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "UPDATED");
        successResponse.put("message", "Médico actualizado exitosamente.");
        successResponse.put("medico", new DatosRespuestaMedico(medicoActualizado));

        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivaMedico(@PathVariable Long id) {
        medicoService.desactivarMedico(id);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("code", "DELETED");
        successResponse.put("message", "Médico desactivado exitosamente.");

        return ResponseEntity.ok(successResponse);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
        DatosRespuestaMedico datosRespuestaMedico = medicoService.obtenerDatosMedicoPorId(id);
        return ResponseEntity.ok(datosRespuestaMedico);
    }


    /*@DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> eliminarMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ERR_MEDICO_NOT_FOUND")); // Manejado en el global

        medicoRepository.delete(medico);

        // Respuesta de éxito
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("code", "DELETED");
        successResponse.put("message", "Médico eliminado exitosamente.");
        successResponse.put("id", String.valueOf(id));

        return ResponseEntity.ok(successResponse);
    }*/
}