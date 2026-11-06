
package cl.peluditos.webservice;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Component
@Path("/api/clientes")
public class FechasEspecialesController {

    private final JdbcTemplate jdbcTemplate;

    public FechasEspecialesController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GET
    @Path("/fechas-proximas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> obtenerFechasProximas() {

        String sql = """
            SELECT
                c.nombre AS cliente,
                m.nombre AS mascota,
                f.tipo,
                f.fecha
            FROM fechas_especiales f
            JOIN mascotas m ON f.mascota_id = m.id
            JOIN clientes c ON m.cliente_id = c.id
            ORDER BY f.fecha
            """;

        return jdbcTemplate.query(sql, (rs, fila) ->
            Map.of(
                "cliente", rs.getString("cliente"),
                "mascota", rs.getString("mascota"),
                "tipo", rs.getString("tipo"),
                "fecha", rs.getDate("fecha")
                           .toLocalDate().toString()
            )
        );
    }
}
