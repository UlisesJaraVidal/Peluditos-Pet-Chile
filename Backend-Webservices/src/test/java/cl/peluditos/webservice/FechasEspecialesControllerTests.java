
package cl.peluditos.webservice;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FechasEspecialesControllerTests {

    @Test
    void debeDevolverDosClientesConFechasEspeciales() {

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

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
            new FechasEspecialesController(jdbcTemplate);

        List<Map<String, String>> resultado =
            controller.obtenerFechasProximas();

        assertEquals(2, resultado.size());
        assertEquals("Maria Gonzalez",
            resultado.get(0).get("cliente"));
        assertEquals("Luna",
            resultado.get(0).get("mascota"));
        assertEquals("Pedro Ramirez",
            resultado.get(1).get("cliente"));
        assertEquals("Max",
            resultado.get(1).get("mascota"));

        verify(jdbcTemplate, times(1)).query(
            contains("FROM fechas_especiales"),
            org.mockito.ArgumentMatchers
                .<RowMapper<Map<String, String>>>any()
        );
    }
}
