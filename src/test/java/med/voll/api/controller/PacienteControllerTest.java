package med.voll.api.controller;

import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.direccion.Direccion;
import med.voll.api.domain.medico.DatosRegistroMedico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.MedicoService;
import med.voll.api.domain.paciente.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteRepository pacienteRepository;

    @MockBean
    private PacienteService pacienteService;

    @Autowired
    private JacksonTester<DatosRegistroPaciente> datosRegistroPacienteJson;

    @Autowired
    private JacksonTester<DatosRespuestaPaciente> datosRespuestaPacienteJson;

    @Autowired
    private JacksonTester<DatosActualizaPaciente> datosActualizaPacienteJson;


    @Test
    @DisplayName("Debería devolver http 200 con una página vacía cuando no hay datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listadoPacientes() throws Exception {
        // Arrange: Simula un Page vacío
        when(pacienteRepository.findByActivoTrue(any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act: Realiza la petición GET
        MockHttpServletResponse response = mockMvc.perform(get("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Assert: Verifica el estado y el contenido esperado
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"content\":[]", "\"empty\":true");
    }

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registraPaciente1() throws Exception {

        var response = mockMvc.perform(post("/pacientes"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registraPaciente2() throws Exception {

        // Arrange: Datos de entrada simulados
        var direccion = new DatosDireccion("Calle 123", "Centro", "CiudadX", "456", "Apto 789");
        var datosRegistroPaciente = new DatosRegistroPaciente(
                "Juan Pérez",
                "juan.perez@mail.com",
                "12345678",
                "123456789",
                direccion
        );

        var paciente = new Paciente(
                1L,
                datosRegistroPaciente.nombre(),
                datosRegistroPaciente.email(),
                datosRegistroPaciente.documentoIdentidad(),
                datosRegistroPaciente.telefono(),
                new Direccion(direccion),
                true
        );

        var datosRespuestaPaciente = new DatosRespuestaPaciente(paciente);

        // Mock del servicio
        when(pacienteService.registrarPaciente(any(DatosRegistroPaciente.class))).thenReturn(paciente);
        when(pacienteService.construirUrlRegistro(eq(paciente), any(UriComponentsBuilder.class)))
                .thenReturn(new URI("/pacientes/1"));

        // Construcción de la respuesta esperada usando JacksonTester
        var jsonEsperado = """
            {
                "code": "CREATED",
                "message": "Paciente registrado exitosamente.",
                "paciente": %s
            }
            """.formatted(datosRespuestaPacienteJson.write(datosRespuestaPaciente).getJson());

        // Act: Ejecutar la petición POST
        MockHttpServletResponse response = mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(datosRegistroPacienteJson.write(datosRegistroPaciente).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()); // HTTP 201 CREATED
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false);
    }

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizarPaciente1() throws Exception {

        var response = mockMvc.perform(put("/pacientes"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizarPaciente2() throws Exception {

        // Arrange: Datos de entrada simulados
        var direccion = new DatosDireccion("Calle 123", "Centro", "CiudadX", "456", "Apto 789");
        var datosActualizaPaciente = new DatosActualizaPaciente(
                1L,
                "Juan Pérez Actualizado",
                "87654321",
                direccion
        );

        var pacienteActualizado = new Paciente(
                1L,
                datosActualizaPaciente.nombre(),
                "juan.perez@mail.com", // Email no se actualiza
                datosActualizaPaciente.documentoIdentidad(),
                "123456789", // Teléfono no se actualiza
                new Direccion(direccion),
                true
        );

        var datosRespuestaPaciente = new DatosRespuestaPaciente(pacienteActualizado);

        // Mock del servicio
        when(pacienteService.actualizarPaciente(any(DatosActualizaPaciente.class))).thenReturn(pacienteActualizado);

        // Construcción de la respuesta esperada usando JacksonTester
        var jsonEsperado = """
            {
                "code": "UPDATED",
                "message": "Paciente actualizado exitosamente.",
                "paciente": %s
            }
            """.formatted(datosRespuestaPacienteJson.write(datosRespuestaPaciente).getJson());

        // Act: Ejecutar la petición PUT
        MockHttpServletResponse response = mockMvc.perform(put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8") // Fuerza UTF-8
                        .content(datosActualizaPacienteJson.write(datosActualizaPaciente).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false);
    }

    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void desactivaPaciente1() throws Exception {

        var response = mockMvc.perform(delete("/pacientes/l"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void desactivaPaciente2() throws Exception {

        // Arrange: ID del paciente
        Long pacienteId = 1L;

        // JSON esperado
        String jsonEsperado = """
            {
                "code": "DELETED",
                "message": "Paciente desactivado exitosamente."
            }
            """;

        // Mock del servicio para que no arroje errores
        doNothing().when(pacienteService).desactivarPaciente(pacienteId);

        // Act: Realizar la petición DELETE
        MockHttpServletResponse response = mockMvc.perform(delete("/pacientes/{id}", pacienteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y el JSON de la respuesta
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), false); // Validación del JSON
    }

    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void retornaDatosPaciente1() throws Exception {

        var response = mockMvc.perform(get("/pacientes/l"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void retornaDatosPaciente2() throws Exception {
        // Arrange: Datos simulados de respuesta
        Long pacienteId = 1L;
        var direccion = new DatosDireccion("Calle 123", "Distrito Centro", "CiudadX", "456", "Apto 789");
        var datosRespuestaPaciente = new DatosRespuestaPaciente(
                pacienteId,
                "Juan Pérez",
                "juan.perez@mail.com",
                "123456789",
                "12345678",
                direccion
        );

        // Mock del servicio para devolver la respuesta esperada
        when(pacienteService.obtenerPacientePorId(pacienteId)).thenReturn(datosRespuestaPaciente);

        // JSON esperado usando JacksonTester
        String jsonEsperado = datosRespuestaPacienteJson.write(datosRespuestaPaciente).getJson();

        // Act: Ejecutar la petición GET
        MockHttpServletResponse response = mockMvc.perform(get("/pacientes/{id}", pacienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y el contenido de la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false); // Comparación del JSON
    }
}