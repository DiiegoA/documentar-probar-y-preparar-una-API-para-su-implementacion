package med.voll.api.infra.security;

import med.voll.api.domain.usuario.DatosAutenticacionUsuario;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacionService(@Lazy AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return usuarioRepository.findByLogin(username);
    }


    public String autenticarYGenerarToken(final DatosAutenticacionUsuario datosAutenticacionUsuario) {
        Usuario usuario = (Usuario) usuarioRepository.findByLogin(datosAutenticacionUsuario.login());
        if (usuario == null) {
            throw new UsernameNotFoundException("ERR_USER_NOT_FOUND");
        }

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                datosAutenticacionUsuario.login(),
                datosAutenticacionUsuario.clave()
        );

        try {
            Authentication usuarioAutenticado = authenticationManager.authenticate(authToken);

            Usuario authenticatedUser = (Usuario) usuarioAutenticado.getPrincipal();

            /*// Obtener múltiples roles concatenados
            String roles = authenticatedUser.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .collect(Collectors.joining(","));*/

            // Obtén el único rol del usuario
            String role = authenticatedUser.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER_PATIENT"); // Valor por defecto

            DatosJwtToken datosJwtToken = new DatosJwtToken(
                    authenticatedUser.getId(),
                    role, // Solo un rol
                    authenticatedUser.getLogin()
            );

            return tokenService.gerarToken(datosJwtToken);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("ERR_INVALID_PASSWORD");
        }
    }
}