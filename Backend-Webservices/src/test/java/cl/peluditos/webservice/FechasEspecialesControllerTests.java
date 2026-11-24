

package cl.peluditos.webservice;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FechasEspecialesControllerTests {

    // PRUEBA 1: CONSULTAR FECHAS ESPECIALES

    @Test
    void debeDevolverDosClientesConFechasEspeciales() {

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        LeadRegistrationService servicio =
            mock(LeadRegistrationService.class);

        List<Map<String, String>> datos = List.of(
            Map.of(
                "cliente", "Maria Gonzalez",
                "mascota", "Luna",
                "tipo", "Cumpleanos",
                "fecha", "2026-11-05"
            ),
            Map.of(
                "cliente", "Pedro Ramirez",
                "mascota", "Max",
                "tipo", "Cumpleanos",
                "fecha", "2026-11-08"
            )
        );

        when(jdbcTemplate.query(
            anyString(),
            org.mockito.ArgumentMatchers
                .<RowMapper<Map<String, String>>>any()
        )).thenReturn(datos);

        FechasEspecialesController controller =
            new FechasEspecialesController(
                jdbcTemplate, servicio
            );

        List<Map<String, String>> resultado =
            controller.obtenerFechasProximas();

        assertEquals(2, resultado.size());
        assertEquals(
            "Maria Gonzalez",
            resultado.get(0).get("cliente")
        );
        assertEquals(
            "Luna",
            resultado.get(0).get("mascota")
        );

        verify(jdbcTemplate).query(
            contains("FROM fechas_especiales"),
            org.mockito.ArgumentMatchers
                .<RowMapper<Map<String, String>>>any()
        );
    }

    // PRUEBA 2: REGISTRAR LEAD CORRECTAMENTE

    @Test
    void debeRegistrarLeadConCincoDatos() {

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        LeadRegistrationService servicio =
            mock(LeadRegistrationService.class);

        FechasEspecialesController controller =
            new FechasEspecialesController(
                jdbcTemplate, servicio
            );

        Map<String, Object> datos = Map.of(
            "nombreCliente", "Cliente Prueba",
            "nombreMascota", "Luna",
            "canalOrigen", "WhatsApp",
            "fechaCumple", "2026-11-24",
            "quiereComprar", true
        );

        Response respuesta = controller.registrarLead(datos);

        assertEquals(200, respuesta.getStatus());
        assertEquals(
            "Lead guardado exitosamente",
            respuesta.getEntity()
        );

        verify(servicio, times(1)).registrar(datos);
    }

    // PRUEBA 3: DATOS INVALIDOS

    @Test
    void debeRechazarDatosInvalidos() {

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        LeadRegistrationService servicio =
            mock(LeadRegistrationService.class);

        FechasEspecialesController controller =
            new FechasEspecialesController(
                jdbcTemplate, servicio
            );

        doThrow(new IllegalArgumentException(
            "Nombre obligatorio"
        )).when(servicio).registrar(anyMap());

        Response respuesta = controller.registrarLead(
            Map.of("nombreCliente", "")
        );

        assertEquals(400, respuesta.getStatus());
    }

    // PRUEBA 4: ERROR DE BASE DE DATOS

    @Test
    void debeResponderErrorSiFallaElRegistro() {

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        LeadRegistrationService servicio =
            mock(LeadRegistrationService.class);

        FechasEspecialesController controller =
            new FechasEspecialesController(
                jdbcTemplate, servicio
            );

        doThrow(new RuntimeException(
            "Error de base de datos"
        )).when(servicio).registrar(anyMap());

        Response respuesta = controller.registrarLead(
            Map.of("nombreCliente", "Cliente Prueba")
        );

        assertEquals(500, respuesta.getStatus());
    }
}
