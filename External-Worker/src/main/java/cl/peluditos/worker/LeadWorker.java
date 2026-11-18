package cl.peluditos.worker;

import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.annotation.FlowableWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class LeadWorker {

    // 1. Externalizamos la URL 
    @Value("${web.service.url:http://localhost:8082/api/clientes/registrar}")
    private String webServiceUrl;

    @FlowableWorker(topic = "registrarLeadTopic")
    public void procesarLead(AcquiredExternalWorkerJob job) {
        System.out.println("==================================================");
        System.out.println("¡Flowable me ha enviado un trabajo!");
        
        Map<String, Object> variables = job.getVariables();
        System.out.println("Datos del Lead recibidos: " + variables);
        System.out.println("==================================================");

        try {
            // 2. Extraer TODOS los datos del negocio desde Flowable
            String nombreCliente = (String) variables.getOrDefault("nombreCliente", "Desconocido");
            String nombreMascota = (String) variables.getOrDefault("nombreMascota", "No especificado");
            String fechaCumple = (String) variables.getOrDefault("fechaCumple", "No especificada");
            String canalOrigen = (String) variables.getOrDefault("canalOrigen", "No especificado");
            Boolean quiereComprar = (Boolean) variables.getOrDefault("quiereComprar", false);

            // 3. Armar el JSON que espera el Web Service de Bastián
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("nombreCliente", nombreCliente);
            payloadMap.put("nombreMascota", nombreMascota);
            payloadMap.put("fechaCumple", fechaCumple);
            payloadMap.put("canalOrigen", canalOrigen);
            payloadMap.put("quiereComprar", quiereComprar);

            // 4. Configurar las cabeceras HTTP para enviar JSON
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payloadMap, headers);

            // 5. Llamar al Web Service
            String respuesta = restTemplate.postForObject(webServiceUrl, request, String.class);
            System.out.println("Respuesta del Web Service: " + respuesta);
            
        } catch (Exception e) {
            System.out.println("❌ Error al llamar al Web Service: " + e.getMessage());
        }
    }
}