
package cl.peluditos.worker;

import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.annotation.FlowableWorker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LeadWorker {

    // URL configurable sin modificar el codigo
    @Value("${PELUDITOS_BACKEND_URL:http://localhost:8082}")
    private String backendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @FlowableWorker(topic = "registrarLeadTopic")
    public void procesarLead(AcquiredExternalWorkerJob job) {

        System.out.println("====================================");
        System.out.println("Procesando Lead desde Flowable");

        Map<String, Object> variables = job.getVariables();

        if (variables == null) {
            throw new IllegalArgumentException(
                "El proceso no contiene variables"
            );
        }

        // Obtener los cinco datos del formulario
        String nombreCliente = obtenerTexto(
            variables, "nombreCliente"
        );

        String nombreMascota = obtenerTexto(
            variables, "nombreMascota"
        );

        String canalOrigen = obtenerTexto(
            variables, "canalOrigen"
        );

        String fechaCumple = obtenerTexto(
            variables, "fechaCumple"
        );

        boolean quiereComprar = Boolean.parseBoolean(
            String.valueOf(
                variables.getOrDefault("quiereComprar", false)
            )
        );

        // Validar dato principal
        if (nombreCliente.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre del cliente es obligatorio"
            );
        }

        // Preparar los datos que recibira el Backend
        Map<String, Object> payload = new LinkedHashMap<>();

        payload.put("nombreCliente", nombreCliente);
        payload.put("nombreMascota", nombreMascota);
        payload.put("canalOrigen", canalOrigen);
        payload.put("fechaCumple", fechaCumple);
        payload.put("quiereComprar", quiereComprar);

        // Configurar la direccion del servicio
        String url = backendUrl.replaceAll("/+$", "")
            + "/api/clientes/registrar";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(payload, headers);

        try {
            // Enviar solicitud HTTP al Backend JAX-RS
            ResponseEntity<String> response =
                restTemplate.postForEntity(
                    url,
                    request,
                    String.class
                );

            String respuesta = response.getBody();

            // Comprobar que el Backend confirme el registro
            if (!response.getStatusCode().is2xxSuccessful()
                || !"Lead guardado exitosamente".equals(respuesta)) {

                throw new IllegalStateException(
                    "El Backend no confirmo el registro: "
                    + respuesta
                );
            }

            System.out.println(
                "Lead registrado correctamente: " + nombreCliente
            );

        } catch (RestClientException e) {

            System.err.println(
                "Error de comunicacion con el Backend: "
                + e.getMessage()
            );

            throw new IllegalStateException(
                "No fue posible registrar el Lead",
                e
            );
        }
    }

    // Obtener valores de texto de manera segura
    private String obtenerTexto(
        Map<String, Object> variables,
        String clave
    ) {
        Object valor = variables.get(clave);

        return valor == null
            ? ""
            : valor.toString().trim();
    }
}
