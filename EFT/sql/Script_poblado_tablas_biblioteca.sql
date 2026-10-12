USE biblioteca_escolar;

-- Usuarios del sistema (contraseña: 1234)
INSERT INTO usuarios (nombre, rut, correo, contrasena, rol) VALUES
('Ana Torres', '12345678-9', 'ana.torres@biblioteca.cl', '1234', 'BIBLIOTECARIO'),
('Carlos López', '98765432-1', 'carlos.lopez@estudiante.cl', '1234', 'ESTUDIANTE'),
('Sofía Martínez', '11223344-5', 'sofia.martinez@biblioteca.cl', '1234', 'BIBLIOTECARIO');

-- Categorías
INSERT INTO categorias (nombre) VALUES
('Novela'), ('Ciencia'), ('Historia'), ('Matemáticas'), ('Literatura Infantil'), ('Tecnología');

-- Estudiantes
INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES
('Carlos López', '98765432-1', '4to Medio A', 'carlos.lopez@estudiante.cl'),
('María González', '11222333-4', '3ro Medio B', 'maria.gonzalez@estudiante.cl'),
('Pedro Ramírez', '44556677-8', '2do Medio C', 'pedro.ramirez@estudiante.cl'),
('Valentina Soto', '55667788-9', '1ro Medio A', 'valentina.soto@estudiante.cl'),
('Diego Muñoz', '66778899-0', '4to Medio B', 'diego.munoz@estudiante.cl');

-- Libros
INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES
('Cien Años de Soledad', 'Gabriel García Márquez', '978-0307474728', 'Sudamericana', 5, 1),
('Breve Historia del Tiempo', 'Stephen Hawking', '978-0553380163', 'Bantam', 3, 2),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 'Harper', 4, 3),
('El Principito', 'Antoine de Saint-Exupéry', '978-0156012195', 'Mariner Books', 7, 5),
('Álgebra de Baldor', 'Aurelio Baldor', '978-6077440000', 'Patria', 2, 4),
('Don Quijote de la Mancha', 'Miguel de Cervantes', '978-8420412146', 'Alfaguara', 3, 1),
('Cosmos', 'Carl Sagan', '978-0345539434', 'Ballantine', 2, 2),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Prentice Hall', 4, 6);

-- Préstamos de ejemplo
INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES
(1, 1, '2026-10-01', '2026-10-08', FALSE),
(2, 3, '2026-10-03', '2026-10-10', FALSE),
(3, 4, '2026-09-25', '2026-10-02', TRUE),
(1, 5, '2026-10-05', '2026-10-12', FALSE);
