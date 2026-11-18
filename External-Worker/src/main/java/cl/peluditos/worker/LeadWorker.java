
package cl.peluditos.worker;

import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.annotation.FlowableWorker;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class LeadWorker {

    @FlowableWorker(topic = "registrarLeadTopic")
    public void procesarLead(AcquiredExternalWorkerJob job) {

        System.out.println("==================================================");
        System.out.println("¡Flowable me ha enviado un trabajo!");

        Map<String, Object> variables = job.getVariables();
        System.out.println("Datos del Lead recibidos: " + variables);
        System.out.println("==================================================");

        try {
            // Extraemos el nombre del cliente
            String nombreCliente = "Desconocido";

            if (variables.containsKey("nombreCliente")) {
                nombreCliente = variables.get("nombreCliente").toString();
            }

            // Armamos el JSON para enviar al Backend
            String jsonPayload = "{\"nombreCliente\":\"" + nombreCliente + "\"}";

            // URL del Backend configurable mediante variable de entorno
            String backendUrl = System.getenv().getOrDefault(
                    "PELUDITOS_BACKEND_URL",
                    "http://localhost:8082"
            );

            // Evitamos duplicar la barra final de la URL
            if (backendUrl.endsWith("/")) {
                backendUrl = backendUrl.substring(0, backendUrl.length() - 1);
            }

            // Creamos el cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Construimos la solicitud al Web Service JAX-RS
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(backendUrl + "/api/clientes/registrar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Enviamos la solicitud al Backend
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Mostramos el resultado de la comunicación
            System.out.println(
                    "Respuesta del Web Service (" +
                    response.statusCode() + "): " +
                    response.body()
            );

        } catch (Exception e) {
            System.out.println(
                    "Error al llamar al Web Service: " + e.getMessage()
            );
        }
    }
}
