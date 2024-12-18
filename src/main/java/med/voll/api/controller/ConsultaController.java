package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.consulta.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {

    private final ReservaDeConsultas reservaDeConsultas;

    public ConsultaController(final ReservaDeConsultas reservaDeConsultas) {
        this.reservaDeConsultas = reservaDeConsultas;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> reserva(
            @Valid @RequestBody final DatosReservaConsulta datosReservaConsulta,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        // Llama al servicio para reservar la consulta
        Consulta consulta = reservaDeConsultas.reservar(datosReservaConsulta);

        // Convierte la entidad Consulta a un DTO para la respuesta
        DatosDetalleConsulta datosDetalleConsulta = DatosDetalleConsulta.fromConsulta(consulta);

        // Genera la URL del recurso creado
        URI url = uriComponentsBuilder.path("/consultas/{id}").buildAndExpand(consulta.getId()).toUri();

        // Construye el cuerpo de la respuesta
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "CREATED");
        successResponse.put("message", "Consulta reservada exitosamente.");
        successResponse.put("consulta", datosDetalleConsulta);

        return ResponseEntity.created(url).body(successResponse);
    }



    @DeleteMapping
    @Transactional
    public ResponseEntity cancela(@RequestBody @Valid DatosCancelamientoConsulta datos) {
        reservaDeConsultas.cancelar(datos);
        return ResponseEntity.noContent().build();
    }
}
