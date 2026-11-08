package cl.peluditos.webservice;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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

    // 1. ENDPOINT ORIGINAL (GET) - Para leer las fechas
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
                "fecha", rs.getDate("fecha").toLocalDate().toString()
            )
        );
    }

    // 2. NUEVO ENDPOINT (POST) - Para que el Worker guarde al cliente desde Flowable
    @POST
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String registrarLead(Map<String, Object> payload) {
        try {
            // Extraemos el nombre que envía Flowable (si no viene, ponemos uno por defecto)
            String nombreCliente = (String) payload.getOrDefault("nombreCliente", "Cliente Sin Nombre");
            
            // Hacemos el INSERT en la base de datos PostgreSQL
            String sql = "INSERT INTO clientes (nombre) VALUES (?)";
            jdbcTemplate.update(sql, nombreCliente);
            
            System.out.println("✅ ¡Lead guardado en la base de datos!: " + nombreCliente);
            return "Lead guardado exitosamente";
        } catch (Exception e) {
            System.out.println("❌ Error al guardar el lead: " + e.getMessage());
            return "Error al guardar el lead";
        }
    }
}