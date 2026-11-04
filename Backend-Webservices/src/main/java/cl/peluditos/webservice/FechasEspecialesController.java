
package cl.peluditos.webservice;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@Path("/api/clientes")
public class FechasEspecialesController {

    @GET
    @Path("/fechas-proximas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> obtenerFechasProximas() {

        return List.of(
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
    }
}
