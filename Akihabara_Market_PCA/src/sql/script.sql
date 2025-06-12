
-- 1. Crear el usuario (ejemplo: usuario 'miusuario' con contraseña 'mipassword')
CREATE USER 'prácticas_PCA'@'localhost' IDENTIFIED BY 'campusfp';

-- 2. Otorgar todos los permisos globales
GRANT ALL PRIVILEGES ON *.* TO 'prácticas_PCA'@'localhost' WITH GRANT OPTION;

-- 3. Aplicar los cambios
FLUSH PRIVILEGES;

DROP DATABASE IF EXISTS akihabara_db;
CREATE DATABASE IF NOT EXISTS akihabara_db;
-- Usar la base de datos
USE akihabara_db;

-- Crear la tabla productos
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    categoria VARCHAR(100),
    precio DECIMAL(10, 2),
    stock INT
);

-- Crear la tabla clientes
CREATE TABLE clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,             
  nombre VARCHAR(255) NOT NULL,                
  email VARCHAR(255) NOT NULL UNIQUE,             
  telefono VARCHAR(20),                           
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP        
);

INSERT INTO clientes (nombre, email, telefono, fecha_registro) VALUES
('Laura García', 'laura.garcia@gmail.com', '612345678', '2024-11-05 10:15:00'),
('Carlos Pérez', 'carlos.perez@gmail.com', '634567891', '2024-12-01 12:00:00'),
('Pablo Conejero', 'pablo.conejero@gmail.com', '611223344', '2025-01-15 09:30:00'),
