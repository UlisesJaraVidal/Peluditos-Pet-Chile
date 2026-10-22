package cl.peluditos.webservice;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LeadController {

    @GetMapping("/status")
    public String status() {
        return "Web Service de Peluditos Pet funcionando correctamente";
    }

    @PostMapping("/leads")
    public String registrarLead(@RequestBody String datosLead) {
        System.out.println("¡Lead recibido desde Flowable!: " + datosLead);
        return "Lead registrado con éxito en el sistema";
    }
}