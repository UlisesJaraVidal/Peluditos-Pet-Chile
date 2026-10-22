package cl.peluditos.worker;

import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.annotation.FlowableWorker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LeadWorker {

    @FlowableWorker(topic = "registrarLeadTopic")
    public void procesarLead(AcquiredExternalWorkerJob job) {
        System.out.println("==================================================");
        System.out.println("¡Flowable me ha enviado un trabajo!");
        
        // Obtener los datos que vienen desde Flowable
        String payload = job.getVariables().toString(); 
        System.out.println("Datos del Lead recibidos: " + payload);
        System.out.println("==================================================");

        // Llamar a tu Web Service
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/leads";
        
        try {
            String respuesta = restTemplate.postForObject(url, payload, String.class);
            System.out.println("Respuesta del Web Service: " + respuesta);
        } catch (Exception e) {
            System.out.println("Error al llamar al Web Service: " + e.getMessage());
        }
    }
}