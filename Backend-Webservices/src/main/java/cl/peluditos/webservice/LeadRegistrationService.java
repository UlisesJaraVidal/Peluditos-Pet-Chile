
package cl.peluditos.webservice;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class LeadRegistrationService {

    private final JdbcTemplate jdbcTemplate;

    public LeadRegistrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void registrar(Map<String, Object> datos) {

        if (datos == null) {
            throw new IllegalArgumentException(
                "No se recibieron datos"
            );
        }

        String nombreCliente = texto(
            datos, "nombreCliente", 120
        );

        String nombreMascota = texto(
            datos, "nombreMascota", 100
        );

        String canalOrigen = texto(
            datos, "canalOrigen", 50
        );

        String fechaTexto = texto(
            datos, "fechaCumple", 10
        );

        Object compra = datos.get("quiereComprar");

        if (compra == null ||
            !(
                compra instanceof Boolean ||
                "true".equalsIgnoreCase(compra.toString()) ||
                "false".equalsIgnoreCase(compra.toString())
            )) {
            throw new IllegalArgumentException(
                "Intencion de compra invalida"
            );
        }

        boolean quiereComprar = Boolean.parseBoolean(
            compra.toString()
        );

        LocalDate fechaCumple;

        try {
            fechaCumple = LocalDate.parse(fechaTexto);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Fecha de cumpleanos invalida", e
            );
        }

        // 1. Registrar cliente
        Long clienteId = jdbcTemplate.queryForObject(
            """
            INSERT INTO clientes
            (nombre, canal_origen, quiere_comprar)
            VALUES (?, ?, ?)
            RETURNING id
            """,
            Long.class,
            nombreCliente,
            canalOrigen,
            quiereComprar
        );

        // 2. Registrar mascota
        Long mascotaId = jdbcTemplate.queryForObject(
            """
            INSERT INTO mascotas (cliente_id, nombre)
            VALUES (?, ?)
            RETURNING id
            """,
            Long.class,
            clienteId,
            nombreMascota
        );

        // 3. Registrar fecha especial
        jdbcTemplate.update(
            """
            INSERT INTO fechas_especiales
            (mascota_id, tipo, fecha)
            VALUES (?, ?, ?)
            """,
            mascotaId,
            "Cumpleanos",
            Date.valueOf(fechaCumple)
        );

        System.out.println(
            "Registro completado: " + nombreCliente
        );
    }

    private String texto(
        Map<String, Object> datos,
        String campo,
        int longitudMaxima
    ) {
        Object valor = datos.get(campo);

        String resultado = valor == null
            ? ""
            : valor.toString().trim();

        if (resultado.isBlank()
            || resultado.length() > longitudMaxima) {

            throw new IllegalArgumentException(
                "Dato obligatorio o invalido: " + campo
            );
        }

        return resultado;
    }
}
