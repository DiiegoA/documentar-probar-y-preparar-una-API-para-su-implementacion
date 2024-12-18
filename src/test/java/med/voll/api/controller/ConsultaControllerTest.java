package med.voll.api.controller;

import med.voll.api.domain.consulta.*;
import med.voll.api.domain.medico.Especialidad;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
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
import org.springframework.security.test.context.support.*;

import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosReservaConsulta> datosReservaConsultaJson;

    @Autowired
    private JacksonTester<DatosDetalleConsulta> datosDetalleConsultaJson;

    @Autowired
    private JacksonTester<DatosCancelamientoConsulta> datosCancelamientoConsultaJson;

    @MockBean
    private ReservaDeConsultas reservaDeConsultas;

    @Test
    @DisplayName("Debería devolver http 400 cuando la request no tenga datos")
    @WithMockUser
    void reserva_escenario1() throws Exception {

        var response = mockMvc.perform(post("/consultas"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Debería devolver http 201 cuando la request sea válida")
    @WithMockUser
    void reserva_escenario2() throws Exception {
        // Arrange: Datos de entrada simulados
        var fechaConsulta = LocalDateTime.of(2024, 12, 20, 14, 30);
        var datosReservaConsulta = new DatosReservaConsulta(
                1L,       // ID del médico
                2L,       // ID del paciente
                fechaConsulta,
                Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue())
        );

        var medico = new Medico(1L, "Dr. Juan", "123456789", "juan@mail.com", "1234567",
                Especialidad.fromValue(Especialidad.CARDIOLOGIA.toValue()), null, true);
        var paciente = new Paciente(2L, "Pedro Pérez", "pedro@mail.com", "987654321",
                "12345678", null, true);
        var consulta = new Consulta(1L, medico, paciente, fechaConsulta, null);

        var datosDetalleConsulta = DatosDetalleConsulta.fromConsulta(consulta);

        // Mock del servicio
        when(reservaDeConsultas.reservar(any(DatosReservaConsulta.class))).thenReturn(consulta);

        // Construcción de la respuesta esperada usando JacksonTester
        var jsonEsperado = """
            {
                "code": "CREATED",
                "message": "Consulta reservada exitosamente.",
                "consulta": %s
            }
            """.formatted(datosDetalleConsultaJson.write(datosDetalleConsulta).getJson());

        // Act: Ejecutar la petición POST
        MockHttpServletResponse response = mockMvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8") // Fuerza UTF-8
                        .content(datosReservaConsultaJson.write(datosReservaConsulta).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado y la respuesta JSON
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value()); // HTTP 201 CREATED
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(UTF_8), false);
    }

    @Test
    @DisplayName("Debería devolver http 204 cuando la request no tenga datos")
    @WithMockUser
    void cancela_escenario2() throws Exception {
        /// Arrange: Datos de entrada simulados
        var datosCancelamientoConsulta = new DatosCancelamientoConsulta(
                1L,                         // ID de la consulta
                MotivoCancelamiento.fromValue(MotivoCancelamiento.MÉDICO_CANCELO.toValue())   // Motivo de la cancelación
        );

        // Mock del servicio para que no lance excepciones
        doNothing().when(reservaDeConsultas).cancelar(any(DatosCancelamientoConsulta.class));

        // Act: Ejecutar la petición DELETE
        MockHttpServletResponse response = mockMvc.perform(delete("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosCancelamientoConsultaJson.write(datosCancelamientoConsulta).getJson()))
                .andReturn()
                .getResponse();

        // Assert: Verificar el estado HTTP
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value()); // HTTP 204 NO CONTENT
    }
}