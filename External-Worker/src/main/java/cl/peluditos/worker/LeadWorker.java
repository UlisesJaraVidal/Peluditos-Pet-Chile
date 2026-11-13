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

            // Armamos un JSON válido manualmente
            String jsonPayload = "{\"nombreCliente\":\"" + nombreCliente + "\"}";

            // Hacemos la llamada al Web Service (Puerto 8082)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8082/api/clientes/registrar"))
                    .header("Content-Type", "application/json") // ESTA LÍNEA ES LA QUE ARREGLA EL ERROR 415
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta del Web Service (" + response.statusCode() + "): " + response.body());
            
        } catch (Exception e) {
            System.out.println("Error al llamar al Web Service: " + e.getMessage());
        }
    }
}