package med.voll.api.infra.security;

public record DatosJwtToken(
        Long id,
        String rol,
        String login
) {
}
