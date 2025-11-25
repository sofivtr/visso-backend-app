-- ============================
-- CATEGORÍAS
-- ============================
INSERT INTO categoria (id_categoria, nombre) VALUES
(1, 'Lentes ópticos'),
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
(4, 'Genérico');

-- ============================
-- PRODUCTOS
-- OJO: usa los nombres de columnas de tu entidad Producto
-- ============================
INSERT INTO producto (
    id_producto,
    codigo_producto,
    nombre,
    descripcion,
    precio,
    stock,
    tipo,
    fecha_creacion,
    imagen_url,
    id_categoria,
    id_marca
) VALUES
(1, 'OPT-RB-01', 'Lente óptico Ray-Ban clásico',
 'Lente óptico con marco metálico', 79990.00, 10, 'O', '2025-11-18', '/images/optico_rayban_1.webp', 1, 1),

(2, 'SOL-OA-01', 'Lente de sol Oakley deportivo',
 'Lente de sol con protección UV400', 65990.00, 15, 'S', '2025-11-18', '/images/sol_oakley_1.webp', 2, 2),

(3, 'ACC-GEN-01', 'Líquido limpieza 250ml',
 'Solución para limpieza de lentes', 5990.00, 50, 'A', '2025-11-18', '/images/liquido_1.webp', 4, 4);

-- ============================
-- USUARIOS
-- Nota: La contraseña para ambos es '123456'
-- El hash $2a$10$P1.w.... es el resultado de encriptar '123456' con BCrypt
-- ============================

INSERT INTO usuario (nombre, apellido, rut, email, password_hash, rol, fecha_registro, activo) VALUES 
('Administrador', 'Visso', '11.111.111-1', 'admin@visso.cl', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpNaNSxFEAd4.QPK2A.F5CCAWhsrrZQm', 'ADMIN', '2024-11-20', 1);

INSERT INTO usuario (nombre, apellido, rut, email, password_hash, rol, fecha_registro, activo) VALUES 
('Juan', 'Pérez', '12.345.678-9', 'juan@duocuc.cl', '$2a$10$y3RdScRbLPiYqtCRtXCDzedIhizQ7qvXGRLO75xcIHDskqK9Gry1K', 'USER', '2024-11-20', 1);