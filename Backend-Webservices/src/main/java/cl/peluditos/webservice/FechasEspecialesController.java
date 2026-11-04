package cl.peluditos.webservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class FechasEspecialesController {

    @GetMapping("/api/clientes/fechas-proximas")
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
