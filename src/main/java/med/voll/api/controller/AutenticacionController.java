package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.infra.security.AutenticacionService;
import med.voll.api.domain.usuario.DatosAutenticacionUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@SecurityRequirement(name = "bearer-key")
public class AutenticacionController {

    private final AutenticacionService autenticacionService;

    public AutenticacionController(final AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> autenticarUsuario(@RequestBody @Valid final DatosAutenticacionUsuario datosAutenticacionUsuario) {
        String jwtToken = autenticacionService.autenticarYGenerarToken(datosAutenticacionUsuario);

        // Preparar la respuesta directamente dentro del método
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("code", "AUTH_SUCCESS");
        successResponse.put("message", "Autenticación exitosa.");
        successResponse.put("authenticationToken", jwtToken);

        return ResponseEntity.ok(successResponse);
    }
}