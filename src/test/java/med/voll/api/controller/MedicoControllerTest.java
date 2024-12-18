package med.voll.api.controller;

import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.direccion.Direccion;
import med.voll.api.domain.medico.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicoRepository medicoRepository;

    @MockBean
    private MedicoService medicoService;

    @Autowired
    private JacksonTester<DatosRegistroMedico> datosRegistroMedicoJson;

    @Autowired
    private JacksonTester<DatosRespuestaMedico> datosRespuestaMedicoJson;

    @Autowired
    private JacksonTester<DatosActualizaMedico> datosActualizaMedicoJson;

    @Test
    @DisplayName("Debería devolver http 200 con una página vacía cuando no hay datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listaMedicos1() throws Exception {
        // Arrange: Mock del repositorio devolviendo una página vacía
        when(medicoRepository.findByActivoTrue(any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act: Realiza la petición GET
        MockHttpServletResponse response = mockMvc.perform(get("/medicos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y contenido de la respuesta
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"content\":[]", "\"empty\":true");
    }

    @Test
    @DisplayName("Debería devolver http 201 cuando la request sea válida")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registraMedico2() throws Exception {
        // Arrange: Datos de entrada simulados
        var direccion = new DatosDireccion("Calle 123", "Centro", "CiudadX", "123", "Apto 456");
        var datosRegistroMedico = new DatosRegistroMedico(
                "Juan Pérez",
                "123456789",
                "juan.perez@mail.com",
                "12345678",
                Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()),
                direccion
        );

        var medico = new Medico(1L, datosRegistroMedico.nombre(), datosRegistroMedico.telefono(), datosRegistroMedico.email(),
                datosRegistroMedico.documento(), datosRegistroMedico.especialidad(), new Direccion(direccion), true);

        var datosRespuestaMedico = new DatosRespuestaMedico(medico);

        // Mock del servicio
        when(medicoService.registrarMedico(any(DatosRegistroMedico.class))).thenReturn(medico);

        // Construcción de la respuesta esperada usando JacksonTester
        var jsonEsperado = """
            {
                "code": "CREATED",
                "message": "Médico registrado exitosamente.",
                "medico": %s
            }
            """.formatted(datosRespuestaMedicoJson.write(datosRespuestaMedico).getJson());

        // Act: Ejecutar la petición POST
        MockHttpServletResponse response = mockMvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8") // Fuerza UTF-8
                        .content(datosRegistroMedicoJson.write(datosRegistroMedico).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado y la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()); // HTTP 201 CREATED
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false);
    }


    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizaMedico1() throws Exception {

        var response = mockMvc.perform(put("/medicos"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver http 200 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void actualizaMedico2() throws Exception {
        // Arrange: Datos de entrada simulados
        var direccionActualizada = new DatosDireccion("Calle Actualizada", "Distrito X", "Ciudad Y", "789", "Piso 2");
        var datosActualizaMedico = new DatosActualizaMedico(1L, "Juan Actualizado", "987654321", direccionActualizada);

        var medicoActualizado = new Medico(1L, datosActualizaMedico.nombre(), "123456789", "juan.actualizado@mail.com",
                datosActualizaMedico.documento(), Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()), new Direccion(direccionActualizada), true);

        var datosRespuestaMedico = new DatosRespuestaMedico(medicoActualizado);

        // Mock del servicio
        when(medicoService.actualizarDatosMedico(any(DatosActualizaMedico.class))).thenReturn(medicoActualizado);

        // Construcción de la respuesta esperada
        var jsonEsperado = """
            {
                "code": "UPDATED",
                "message": "Médico actualizado exitosamente.",
                "medico": %s
            }
            """.formatted(datosRespuestaMedicoJson.write(datosRespuestaMedico).getJson());

        // Act: Ejecutar la petición PUT
        MockHttpServletResponse response = mockMvc.perform(put("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(datosActualizaMedicoJson.write(datosActualizaMedico).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado y la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false);
    }


    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void desactivaMedico1() throws Exception {

        var response = mockMvc.perform(delete("/medicos/l"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver http 204 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void desactivaMedico2() throws Exception {
        // Arrange: ID del médico
        Long medicoId = 1L;

        // JSON esperado
        String jsonEsperado = """
            {
                "code": "DELETED",
                "message": "Médico desactivado exitosamente."
            }
            """;

        // Mock del servicio para simular la desactivación
        doNothing().when(medicoService).desactivarMedico(medicoId);

        // Act: Ejecutar la petición DELETE
        MockHttpServletResponse response = mockMvc.perform(delete("/medicos/{id}", medicoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP y el contenido JSON de la respuesta
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false); // Validación del JSON
    }

    @Test
    @DisplayName("Debería devolver http 404 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void retornaDatosMedico1() throws Exception {

        var response = mockMvc.perform(get("/medicos/l"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Debería devolver http 200 cuando la request no tenga datos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void retornaDatosMedico2() throws Exception {
        // Arrange: Simula la respuesta del servicio
        Long medicoId = 1L;
        var direccion = new DatosDireccion("Calle 123", "Distrito Centro", "CiudadX", "456", "Apto 789");
        var datosRespuestaMedico = new DatosRespuestaMedico(
                medicoId,
                "Juan Pérez",
                "juan.perez@mail.com",
                "123456789",
                "CARDIOLOGIA",
                direccion
        );

        // Mock del servicio
        when(medicoService.obtenerDatosMedicoPorId(medicoId)).thenReturn(datosRespuestaMedico);

        // Construcción del JSON esperado
        String jsonEsperado = datosRespuestaMedicoJson.write(datosRespuestaMedico).getJson();

        // Act: Ejecuta la petición GET
        MockHttpServletResponse response = mockMvc.perform(get("/medicos/{id}", medicoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(UTF_8))
                .andReturn()
                .getResponse();

        // Assert: Verifica el estado y el contenido de la respuesta
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value()); // HTTP 200 OK
        assertThat(response.getContentAsString(UTF_8)).isEqualTo(jsonEsperado); // JSON correcto
    }
}