-- =======================================================
-- SpeedFast - Script de Base de Datos para Sumativa 3
-- Asignatura: Desarrollo Orientado a Objetos II (Semana 8)
-- =======================================================

-- 1. Creación de la base de datos
CREATE DATABASE IF NOT EXISTS speedfast_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE speedfast_db;

-- 2. Eliminar tablas previas si existen (respetando orden de llaves foráneas)
DROP TABLE IF EXISTS entregas;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS repartidores;

-- 3. Tabla: repartidores
CREATE TABLE repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- 4. Tabla: pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL DEFAULT 'PENDIENTE'
);

-- 5. Tabla: entregas
CREATE TABLE entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 6. Datos de prueba iniciales (opcionales para demostración)
INSERT INTO repartidores (nombre) VALUES 
('Carlos Mendoza'),
('María Fernández'),
('Andrés Silva'),
('Camila Morales');

INSERT INTO pedidos (direccion, tipo, estado) VALUES 
('Av. Libertador Bernardo O''Higgins 1234', 'COMIDA', 'PENDIENTE'),
('Calle Las Condes 567, Depto 402', 'ENCOMIENDA', 'EN_REPARTO'),
('Pasaje Los Aromos 89', 'EXPRESS', 'ENTREGADO'),
('Av. Providencia 1020', 'COMIDA', 'PENDIENTE'),
('Calle Los Leones 450', 'ENCOMIENDA', 'PENDIENTE');

INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES 
(2, 1, '2026-10-05', '11:30:00'),
(3, 2, '2026-10-05', '10:15:00');
