package med.voll.api.controller;

import med.voll.api.domain.usuario.DatosAutenticacionUsuario;
import med.voll.api.infra.security.AutenticacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;


import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AutenticacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosAutenticacionUsuario> datosAutenticacionUsuarioJson;

    @MockBean
    private AutenticacionService autenticacionService;

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    void autenticarUsuario1() throws Exception {

        var response = mockMvc.perform(post("/login"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver HTTP 200 y un token JWT válido cuando la autenticación sea exitosa")
    void autenticarUsuario2() throws Exception {
        // Arrange: Datos de entrada simulados
        var datosAutenticacionUsuario = new DatosAutenticacionUsuario(
                "usuarioTest",   // Login
                "password123"    // Clave
        );

        // Token simulado devuelto por el servicio
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.fakeTokenForTest";

        // Mock del servicio
        when(autenticacionService.autenticarYGenerarToken(any(DatosAutenticacionUsuario.class)))
                .thenReturn(jwtToken);

        // Construcción del JSON esperado
        String jsonEsperado = """
            {
                "code": "AUTH_SUCCESS",
                "message": "Autenticación exitosa.",
                "authenticationToken": "%s"
            }
            """.formatted(jwtToken);

        // Act: Ejecutar la petición POST
        MockHttpServletResponse response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(datosAutenticacionUsuarioJson.write(datosAutenticacionUsuario).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y el contenido de la respuesta
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(StandardCharsets.UTF_8), false);
    }
}