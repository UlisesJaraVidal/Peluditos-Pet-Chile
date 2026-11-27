package cl.peluditos.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeadController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/leads")
    public String registrarLead(@RequestBody Map<String, Object> payload) {
        System.out.println("¡Lead recibido desde Flowable!: " + payload);
        
        try {
            // Extraemos los 5 datos del JSON
            String nombreCliente = (String) payload.getOrDefault("nombreCliente", "Sin nombre");
            String nombreMascota = (String) payload.getOrDefault("nombreMascota", "Sin mascota");
            String fechaCumple = (String) payload.getOrDefault("fechaCumple", "2000-01-01");

            // 1. Insertar en clientes
            String sqlCliente = "INSERT INTO clientes (nombre) VALUES (?) RETURNING id";
            Long clienteId = jdbcTemplate.queryForObject(sqlCliente, Long.class, nombreCliente);

            // 2. Insertar en mascotas
            String sqlMascota = "INSERT INTO mascotas (cliente_id, nombre) VALUES (?, ?) RETURNING id";
            Long mascotaId = jdbcTemplate.queryForObject(sqlMascota, Long.class, clienteId, nombreMascota);

            // 3. Insertar en fechas_especiales
            String sqlFecha = "INSERT INTO fechas_especiales (mascota_id, tipo, fecha) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlFecha, mascotaId, "Cumpleaños", java.sql.Date.valueOf(fechaCumple));

            return "Lead guardado exitosamente en la base de datos";
            
        } catch (Exception e) {
            System.out.println("❌ Error al guardar el lead: " + e.getMessage());
            return "Error al guardar el lead: " + e.getMessage();
        }
    }
}