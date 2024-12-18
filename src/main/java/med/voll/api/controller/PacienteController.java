package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
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
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteRepository pacienteRepository;

    public PacienteController(final PacienteService pacienteService, final PacienteRepository pacienteRepository) {
        this.pacienteService = pacienteService;
        this.pacienteRepository = pacienteRepository;
    }


    @GetMapping
    public ResponseEntity<Page<DatosListadoPaciente>> listadoPacientes(
            @Parameter(
                    schema = @Schema(example = "{}") // Esto personaliza el esquema como vacío
            )
            @PageableDefault(sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(pacienteRepository.findByActivoTrue(pageable).map(DatosListadoPaciente::new));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registraPaciente(@Valid @RequestBody final DatosRegistroPaciente datosRegistroPaciente, UriComponentsBuilder uriComponentsBuilder) {
        Paciente paciente = pacienteService.registrarPaciente(datosRegistroPaciente);
        URI url = pacienteService.construirUrlRegistro(paciente, uriComponentsBuilder);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "CREATED");
        successResponse.put("message", "Paciente registrado exitosamente.");
        successResponse.put("paciente", new DatosRespuestaPaciente(paciente));

        return ResponseEntity.created(url).body(successResponse);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizarPaciente(@Valid @RequestBody final DatosActualizaPaciente datosActualizaPaciente) {
        Paciente paciente = pacienteService.actualizarPaciente(datosActualizaPaciente);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "UPDATED");
        successResponse.put("message", "Paciente actualizado exitosamente.");
        successResponse.put("paciente", new DatosRespuestaPaciente(paciente));

        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> desactivaPaciente(@PathVariable Long id) {
        pacienteService.desactivarPaciente(id);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("code", "DELETED");
        successResponse.put("message", "Paciente desactivado exitosamente.");

        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaPaciente> retornaDatosPaciente(@PathVariable Long id) {
        DatosRespuestaPaciente datosRespuesta = pacienteService.obtenerPacientePorId(id);
        return ResponseEntity.ok(datosRespuesta);
    }
}