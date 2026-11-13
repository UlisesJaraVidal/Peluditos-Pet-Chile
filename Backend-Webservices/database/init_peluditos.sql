
-- Peluditos Pet Chile
-- Evaluacion 2
-- Base de datos: peluditos_db
-- Responsable: Bastian Fernandez Garrido

-- 1. Crear tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(150),
    telefono VARCHAR(30)
);

-- 2. Crear tabla de mascotas
CREATE TABLE IF NOT EXISTS mascotas (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50)
);

-- 3. Crear tabla de fechas especiales
CREATE TABLE IF NOT EXISTS fechas_especiales (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mascota_id BIGINT NOT NULL REFERENCES mascotas(id),
    tipo VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL
);

-- 4. Insertar clientes ficticios de prueba
INSERT INTO clientes (nombre)
SELECT v.nombre
FROM (VALUES
    ('Maria Gonzalez'),
    ('Pedro Ramirez')
) AS v(nombre)
WHERE NOT EXISTS (
    SELECT 1 FROM clientes c WHERE c.nombre = v.nombre
);

-- 5. Insertar mascotas de prueba
INSERT INTO mascotas (cliente_id, nombre)
SELECT c.id, v.mascota
FROM (VALUES
    ('Maria Gonzalez', 'Luna'),
    ('Pedro Ramirez', 'Max')
) AS v(cliente, mascota)
JOIN clientes c ON c.nombre = v.cliente
WHERE NOT EXISTS (
    SELECT 1 FROM mascotas m
    WHERE m.cliente_id = c.id AND m.nombre = v.mascota
);

-- 6. Insertar fechas especiales ficticias
INSERT INTO fechas_especiales (mascota_id, tipo, fecha)
SELECT m.id, v.tipo, v.fecha
FROM (VALUES
    ('Maria Gonzalez', 'Luna', 'Cumpleanos', DATE '2026-11-05'),
    ('Pedro Ramirez', 'Max', 'Cumpleanos', DATE '2026-11-08')
) AS v(cliente, mascota, tipo, fecha)
JOIN clientes c ON c.nombre = v.cliente
JOIN mascotas m ON m.cliente_id = c.id
    AND m.nombre = v.mascota
WHERE NOT EXISTS (
    SELECT 1 FROM fechas_especiales f
    WHERE f.mascota_id = m.id
      AND f.tipo = v.tipo
      AND f.fecha = v.fecha
);

-- 7. Verificar los registros
SELECT
    c.nombre AS cliente,
    m.nombre AS mascota,
    f.tipo,
    f.fecha
FROM fechas_especiales f
JOIN mascotas m ON f.mascota_id = m.id
JOIN clientes c ON m.cliente_id = c.id
ORDER BY f.fecha;
