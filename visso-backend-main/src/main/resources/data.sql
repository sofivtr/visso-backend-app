-- ============================
-- CATEGORIAS (Sin tildes)
-- ============================
INSERT INTO categoria (id_categoria, nombre) VALUES
(1, 'Opticos'),
(2, 'Lentes de sol'),
(3, 'Lentes de contacto'),
(4, 'Accesorios');

-- ============================
-- MARCAS
-- ============================
INSERT INTO marca (id_marca, nombre) VALUES
(1, 'Ray-Ban'),
(2, 'Oakley'),
(3, 'Gucci'),
(4, 'Generico');

-- ============================
-- PRODUCTOS
-- ============================
INSERT INTO producto (
    id_producto,
    codigo_producto,
    nombre,
    descripcion,
    precio,
    stock,
    fecha_creacion,
    imagen_url,
    id_categoria,
    id_marca
) VALUES
(1, 'OPT-RB-01', 'Lente optico Ray-Ban clasico',
 'Lente optico con marco metalico', 79990.00, 10, '2025-11-18', '/images/optico_rayban_1.webp', 1, 1),

(2, 'SOL-OA-01', 'Lente de sol Oakley deportivo',
 'Lente de sol con proteccion UV400', 65990.00, 15, '2025-11-18', '/images/sol_oakley_1.webp', 2, 2),

(3, 'ACC-GEN-01', 'Liquido limpieza 250ml',
 'Solucion para limpieza de lentes', 5990.00, 50, '2025-11-18', '/images/liquido_1.webp', 4, 4);

-- ============================
-- USUARIOS
-- Nota: La contrasena para ambos es '123456'
-- ============================

INSERT INTO usuario (nombre, apellido, rut, email, password_hash, rol, fecha_registro, activo) VALUES 
('Administrador', 'Visso', '11.111.111-1', 'admin@visso.cl', '$2a$10$y3RdScRbLPiYqtCRtXCDzedIhizQ7qvXGRLO75xcIHDskqK9Gry1K', 'ADMIN', '2024-11-20', 1);

INSERT INTO usuario (nombre, apellido, rut, email, password_hash, rol, fecha_registro, activo) VALUES 
('Juan', 'Perez', '12.345.678-9', 'juan@duocuc.cl', '$2a$10$y3RdScRbLPiYqtCRtXCDzedIhizQ7qvXGRLO75xcIHDskqK9Gry1K', 'USER', '2024-11-20', 1);