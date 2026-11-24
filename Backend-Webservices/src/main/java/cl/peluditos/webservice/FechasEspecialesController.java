
package cl.peluditos.webservice;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@Component
@Path("/api/clientes")
public class FechasEspecialesController {

    private final JdbcTemplate jdbcTemplate;
    private final LeadRegistrationService leadRegistrationService;

    public FechasEspecialesController(
            JdbcTemplate jdbcTemplate,
            LeadRegistrationService leadRegistrationService) {

        this.jdbcTemplate = jdbcTemplate;
        this.leadRegistrationService = leadRegistrationService;
    }

    // ==========================================
    // CONSULTAR FECHAS ESPECIALES
    // ==========================================

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

    // ==========================================
    // REGISTRAR LEAD DESDE FLOWABLE
    // ==========================================

    @POST
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registrarLead(Map<String, Object> payload) {

        try {

            // Registrar cliente, mascota y fecha
            // mediante el servicio transaccional
            leadRegistrationService.registrar(payload);

            System.out.println(
                "Lead registrado correctamente desde Flowable"
            );

            return Response.ok(
                "Lead guardado exitosamente"
            ).build();

        } catch (IllegalArgumentException e) {

            // Error de validacion de datos
            System.err.println(
                "Datos invalidos: " + e.getMessage()
            );

            return Response.status(
                Response.Status.BAD_REQUEST
            ).entity(
                "Datos invalidos: " + e.getMessage()
            ).build();

        } catch (Exception e) {

            // Error interno o de base de datos
            System.err.println(
                "Error al registrar Lead: " + e.getMessage()
            );

            return Response.status(
                Response.Status.INTERNAL_SERVER_ERROR
            ).entity(
                "Error al guardar el lead"
            ).build();
        }
    }
}
