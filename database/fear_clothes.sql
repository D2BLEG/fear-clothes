-- =====================================================================
--  FEAR Clothes  ·  Script de creación de la base de datos (MySQL 8)
--  Proyecto de Programación Orientada a Objetos
--
--  Modelo: 13 tablas relacionadas. La herencia de las entidades Java se
--  mapea con "una tabla por clase" (estrategia JOINED de JPA):
--     usuarios  ← compradores | vendedores | administradores
--     prendas   ← calzados    | camisetas   | chamarras
--  Así cada columna pertenece a una sola entidad y no hay columnas vacías
--  (1FN/2FN/3FN).
--
--  Uso:  mysql -u root -p < fear_clothes.sql
--  Nota: con el perfil "aws" Hibernate (ddl-auto=update) también puede
--  crear las tablas por su cuenta; este script es la versión explícita.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS fearclothes
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fearclothes;

-- Orden inverso por si se quiere recrear todo desde cero
DROP TABLE IF EXISTS ordenes;
DROP TABLE IF EXISTS pujas;
DROP TABLE IF EXISTS publicaciones;
DROP TABLE IF EXISTS chamarras;
DROP TABLE IF EXISTS camisetas;
DROP TABLE IF EXISTS calzados;
DROP TABLE IF EXISTS prendas;
DROP TABLE IF EXISTS tallas;
DROP TABLE IF EXISTS marcas;
DROP TABLE IF EXISTS administradores;
DROP TABLE IF EXISTS vendedores;
DROP TABLE IF EXISTS compradores;
DROP TABLE IF EXISTS usuarios;

-- ---------------------------------------------------------------------
-- 1. USUARIOS (superclase abstracta) y sus subclases
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL COMMENT 'Hash BCrypt, nunca texto plano',
    activo    BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE compradores (
    id BIGINT NOT NULL,
    CONSTRAINT pk_compradores PRIMARY KEY (id),
    CONSTRAINT fk_compradores_usuario FOREIGN KEY (id)
        REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE vendedores (
    id            BIGINT       NOT NULL,
    nombre_tienda VARCHAR(255) NULL,
    CONSTRAINT pk_vendedores PRIMARY KEY (id),
    CONSTRAINT fk_vendedores_usuario FOREIGN KEY (id)
        REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE administradores (
    id BIGINT NOT NULL,
    CONSTRAINT pk_administradores PRIMARY KEY (id),
    CONSTRAINT fk_administradores_usuario FOREIGN KEY (id)
        REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. CATÁLOGOS: marcas y tallas
-- ---------------------------------------------------------------------
CREATE TABLE marcas (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    pais   VARCHAR(255) NULL,
    CONSTRAINT pk_marcas PRIMARY KEY (id),
    CONSTRAINT uq_marcas_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE tallas (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    etiqueta VARCHAR(255) NOT NULL COMMENT 'S, M, L... o 8 MX, 9 MX...',
    CONSTRAINT pk_tallas PRIMARY KEY (id),
    CONSTRAINT uq_tallas_etiqueta UNIQUE (etiqueta)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. PRENDAS (superclase abstracta) y sus subclases
--    La columna "categoria" indica qué subclase corresponde a cada fila.
-- ---------------------------------------------------------------------
CREATE TABLE prendas (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(255)  NOT NULL,
    descripcion VARCHAR(1000) NULL,
    color       VARCHAR(255)  NULL,
    imagen_url  VARCHAR(255)  NULL COMMENT 'Ruta /img/..., /uploads/... o URL externa',
    categoria   ENUM('CALZADO','CAMISETA','CHAMARRA')   NOT NULL,
    condicion   ENUM('NUEVA','COMO_NUEVA','USADA')      NOT NULL,
    marca_id    BIGINT        NOT NULL,
    talla_id    BIGINT        NOT NULL,
    CONSTRAINT pk_prendas PRIMARY KEY (id),
    CONSTRAINT fk_prendas_marca FOREIGN KEY (marca_id)
        REFERENCES marcas (id) ON DELETE RESTRICT,
    CONSTRAINT fk_prendas_talla FOREIGN KEY (talla_id)
        REFERENCES tallas (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX ix_prendas_marca     ON prendas (marca_id);
CREATE INDEX ix_prendas_talla     ON prendas (talla_id);
CREATE INDEX ix_prendas_categoria ON prendas (categoria);

CREATE TABLE calzados (
    id      BIGINT       NOT NULL,
    silueta VARCHAR(255) NULL,
    CONSTRAINT pk_calzados PRIMARY KEY (id),
    CONSTRAINT fk_calzados_prenda FOREIGN KEY (id)
        REFERENCES prendas (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE camisetas (
    id          BIGINT       NOT NULL,
    tipo_cuello VARCHAR(255) NULL,
    CONSTRAINT pk_camisetas PRIMARY KEY (id),
    CONSTRAINT fk_camisetas_prenda FOREIGN KEY (id)
        REFERENCES prendas (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE chamarras (
    id          BIGINT  NOT NULL,
    impermeable BOOLEAN NULL,
    CONSTRAINT pk_chamarras PRIMARY KEY (id),
    CONSTRAINT fk_chamarras_prenda FOREIGN KEY (id)
        REFERENCES prendas (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. PUBLICACIONES: una prenda puesta a la venta
--    tipo = DIRECTA (precio fijo) | OFERTA (bid/ask) | SUBASTA
--    precio = precio fijo, precio de venta ("ask") o precio inicial
--    fecha_cierre solo se usa en subastas
-- ---------------------------------------------------------------------
CREATE TABLE publicaciones (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    prenda_id      BIGINT        NOT NULL,
    vendedor_id    BIGINT        NOT NULL,
    tipo           ENUM('DIRECTA','OFERTA','SUBASTA') NOT NULL,
    precio         DECIMAL(12,2) NOT NULL,
    fecha_cierre   DATETIME(6)   NULL,
    estado         ENUM('ACTIVA','VENDIDA','CERRADA') NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_publicaciones PRIMARY KEY (id),
    CONSTRAINT uq_publicaciones_prenda UNIQUE (prenda_id),
    CONSTRAINT fk_publicaciones_prenda FOREIGN KEY (prenda_id)
        REFERENCES prendas (id) ON DELETE RESTRICT,
    CONSTRAINT fk_publicaciones_vendedor FOREIGN KEY (vendedor_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT ck_publicaciones_precio CHECK (precio > 0),
    -- Una subasta siempre debe tener fecha de cierre
    CONSTRAINT ck_publicaciones_subasta CHECK (tipo <> 'SUBASTA' OR fecha_cierre IS NOT NULL)
) ENGINE=InnoDB;

CREATE INDEX ix_publicaciones_estado_tipo ON publicaciones (estado, tipo);
CREATE INDEX ix_publicaciones_vendedor    ON publicaciones (vendedor_id);

-- ---------------------------------------------------------------------
-- 5. PUJAS: ofertas (bids) y pujas de subasta
-- ---------------------------------------------------------------------
CREATE TABLE pujas (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    publicacion_id BIGINT        NOT NULL,
    postor_id      BIGINT        NOT NULL,
    monto          DECIMAL(12,2) NOT NULL,
    fecha          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_pujas PRIMARY KEY (id),
    CONSTRAINT fk_pujas_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE CASCADE,
    CONSTRAINT fk_pujas_postor FOREIGN KEY (postor_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT ck_pujas_monto CHECK (monto > 0)
) ENGINE=InnoDB;

CREATE INDEX ix_pujas_publicacion_monto ON pujas (publicacion_id, monto);
CREATE INDEX ix_pujas_postor            ON pujas (postor_id);

-- ---------------------------------------------------------------------
-- 6. ÓRDENES: compra concretada (pago simulado). Una por publicación.
-- ---------------------------------------------------------------------
CREATE TABLE ordenes (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    comprador_id   BIGINT        NOT NULL,
    publicacion_id BIGINT        NOT NULL,
    precio_final   DECIMAL(12,2) NOT NULL,
    metodo_pago    VARCHAR(255)  NULL,
    fecha          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_ordenes PRIMARY KEY (id),
    CONSTRAINT uq_ordenes_publicacion UNIQUE (publicacion_id),
    CONSTRAINT fk_ordenes_comprador FOREIGN KEY (comprador_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ordenes_publicacion FOREIGN KEY (publicacion_id)
        REFERENCES publicaciones (id) ON DELETE RESTRICT,
    CONSTRAINT ck_ordenes_precio CHECK (precio_final > 0)
) ENGINE=InnoDB;

CREATE INDEX ix_ordenes_comprador ON ordenes (comprador_id);

-- =====================================================================
--  DATOS INICIALES (catálogos)
-- =====================================================================
INSERT INTO marcas (nombre, pais) VALUES
    ('Nike',           'Estados Unidos'),
    ('Jordan',         'Estados Unidos'),
    ('Adidas',         'Alemania'),
    ('New Balance',    'Estados Unidos'),
    ('Supreme',        'Estados Unidos'),
    ('Stüssy',         'Estados Unidos'),
    ('Carhartt WIP',   'Estados Unidos'),
    ('The North Face', 'Estados Unidos');

INSERT INTO tallas (etiqueta) VALUES
    ('XS'), ('S'), ('M'), ('L'), ('XL'), ('XXL'),
    ('6 MX'), ('7 MX'), ('8 MX'), ('9 MX'), ('10 MX');

-- ---------------------------------------------------------------------
-- USUARIOS DE PRUEBA
-- No se incluyen aquí porque la contraseña debe guardarse con hash BCrypt
-- generado por Spring Security. La aplicación crea automáticamente (clase
-- DataLoader, solo si la tabla usuarios está vacía):
--     admin@fearclothes.com      / admin123     (Administrador)
--     vendedor@fearclothes.com   / vendedor123  (Vendedor)
--     comprador@fearclothes.com  / comprador123 (Comprador)
-- junto con 9 publicaciones de ejemplo.
-- ---------------------------------------------------------------------

-- =====================================================================
--  CONSULTAS DE EJEMPLO
-- =====================================================================

-- 1) Catálogo de publicaciones activas con marca, talla y vendedor
SELECT p.id,
       pr.nombre      AS prenda,
       m.nombre       AS marca,
       t.etiqueta     AS talla,
       pr.categoria,
       p.tipo,
       p.precio,
       u.nombre       AS vendedor
FROM publicaciones p
JOIN prendas  pr ON pr.id = p.prenda_id
JOIN marcas   m  ON m.id  = pr.marca_id
JOIN tallas   t  ON t.id  = pr.talla_id
JOIN usuarios u  ON u.id  = p.vendedor_id
WHERE p.estado = 'ACTIVA'
ORDER BY p.fecha_creacion DESC;

-- 2) Puja más alta de cada subasta activa
SELECT p.id AS publicacion,
       pr.nombre AS prenda,
       MAX(pj.monto) AS mejor_puja,
       COUNT(pj.id)  AS total_pujas
FROM publicaciones p
JOIN prendas pr    ON pr.id = p.prenda_id
LEFT JOIN pujas pj ON pj.publicacion_id = p.id
WHERE p.tipo = 'SUBASTA' AND p.estado = 'ACTIVA'
GROUP BY p.id, pr.nombre;

-- 3) Subastas vencidas que el temporizador debe cerrar
SELECT id, fecha_cierre
FROM publicaciones
WHERE tipo = 'SUBASTA' AND estado = 'ACTIVA' AND fecha_cierre <= NOW();

-- 4) Compras de un usuario (cambiar el id)
SELECT o.id, pr.nombre AS prenda, o.precio_final, o.fecha
FROM ordenes o
JOIN publicaciones p ON p.id = o.publicacion_id
JOIN prendas pr      ON pr.id = p.prenda_id
WHERE o.comprador_id = 1
ORDER BY o.fecha DESC;

-- 5) Prendas con su detalle específico según la subclase
SELECT pr.id, pr.nombre, pr.categoria,
       COALESCE(c.silueta, ca.tipo_cuello,
                CASE WHEN ch.impermeable IS NULL THEN NULL
                     WHEN ch.impermeable = 1 THEN 'Impermeable' ELSE 'No impermeable' END) AS detalle
FROM prendas pr
LEFT JOIN calzados  c  ON c.id  = pr.id
LEFT JOIN camisetas ca ON ca.id = pr.id
LEFT JOIN chamarras ch ON ch.id = pr.id;

-- 6) Rol de cada usuario (según la tabla de la subclase)
SELECT u.id, u.nombre, u.email,
       CASE WHEN a.id IS NOT NULL THEN 'ADMIN'
            WHEN v.id IS NOT NULL THEN 'VENDEDOR'
            WHEN c.id IS NOT NULL THEN 'COMPRADOR' END AS rol
FROM usuarios u
LEFT JOIN administradores a ON a.id = u.id
LEFT JOIN vendedores      v ON v.id = u.id
LEFT JOIN compradores     c ON c.id = u.id;
