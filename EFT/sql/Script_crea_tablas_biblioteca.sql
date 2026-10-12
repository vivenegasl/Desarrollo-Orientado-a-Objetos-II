-- =========================================================
-- Biblioteca Escolar - Script de Creación de Tablas
-- EFT - Desarrollo Orientado a Objetos II (Semana 9)
-- =========================================================

CREATE DATABASE IF NOT EXISTS biblioteca_escolar
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE biblioteca_escolar;

-- Eliminar tablas en orden de dependencias
DROP TABLE IF EXISTS prestamos;
DROP TABLE IF EXISTS libros;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS estudiantes;
DROP TABLE IF EXISTS usuarios;

-- Tabla: usuarios (login del sistema)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL UNIQUE,
    correo VARCHAR(100),
    contrasena VARCHAR(100) NOT NULL,
    rol ENUM('BIBLIOTECARIO', 'ESTUDIANTE') NOT NULL DEFAULT 'ESTUDIANTE'
);

-- Tabla: estudiantes
CREATE TABLE estudiantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL UNIQUE,
    curso VARCHAR(50),
    correo VARCHAR(100)
);

-- Tabla: categorias
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla: libros
CREATE TABLE libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    editorial VARCHAR(150),
    stock INT NOT NULL DEFAULT 0,
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Tabla: prestamos
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_libro INT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion DATE,
    devuelto BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_libro) REFERENCES libros(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
