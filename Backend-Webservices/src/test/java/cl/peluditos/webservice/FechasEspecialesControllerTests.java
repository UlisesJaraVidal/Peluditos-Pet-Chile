
package cl.peluditos.webservice;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FechasEspecialesControllerTests {

    @Test
    void debeDevolverDosClientesConFechasEspeciales() {

        FechasEspecialesController controller =
                new FechasEspecialesController();

        List<Map<String, String>> clientes =
                controller.obtenerFechasProximas();

        assertEquals(2, clientes.size());

        assertEquals("Maria Gonzalez",
                clientes.get(0).get("cliente"));

        assertEquals("Luna",
                clientes.get(0).get("mascota"));

        assertEquals("Pedro Ramirez",
                clientes.get(1).get("cliente"));

        assertEquals("Max",
                clientes.get(1).get("mascota"));
    }
}
