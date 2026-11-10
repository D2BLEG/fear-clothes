# FEAR Clothes

Marketplace de ropa y sneakers (estilo StockX): venta directa, ofertas (bid/ask) y subastas.
Proyecto de Programación Orientada a Objetos — Java 21 + Spring Boot 3 (MVC) + Thymeleaf + **Bootstrap 5** + JPA/Hibernate.

El frontend usa Bootstrap 5.3.3 empaquetado como **WebJar** (dependencia de Maven), así que funciona sin internet.

## Cómo ejecutarlo en IntelliJ IDEA

1. **File → Open** y elige la carpeta `fear-clothes` (o su `pom.xml`) → *Open as Project*.
2. Espera a que Maven descargue las dependencias (requiere internet la primera vez).
3. Asegura el JDK 21: **File → Project Structure → Project SDK = 21**.
4. Ejecuta la clase `FearClothesApplication` (botón ▶ junto a `main`).
5. Abre http://localhost:8080

Por terminal: `mvn spring-boot:run`

## Base de datos

- Local: **H2 guardada en archivo** (carpeta `data/`). Cuentas, publicaciones, ofertas y compras **persisten** entre arranques.
- Los datos de prueba se cargan solo la primera vez (cuando la BD está vacía). Para empezar de cero: apaga la app y borra la carpeta `data/`.
- Consola de la BD: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/fearclothes`, usuario `sa`, sin contraseña).
- En AWS se usa MySQL en RDS con el perfil `aws` (ver más abajo).
- **13 tablas** (herencia mapeada con `JOINED`, sin columnas vacías):
  `usuarios`, `compradores`, `vendedores`, `administradores`, `marcas`, `tallas`,
  `prendas`, `calzados`, `camisetas`, `chamarras`, `publicaciones`, `pujas`, `ordenes`.
- **Importante:** si ya habías ejecutado una versión anterior (tablas con herencia en una sola tabla), borra la carpeta `data/` antes de arrancar para que se cree el esquema nuevo.

## Imágenes

- Los vendedores pueden **subir una foto** (JPG, PNG o WEBP, máx. 5 MB) al publicar. Se guarda en la carpeta `uploads/` y se muestra en `/uploads/...`.
- También se puede pegar la URL de una imagen.
- Las prendas de ejemplo usan ilustraciones en `src/main/resources/static/img/`.
- `AlmacenArchivos` es una interfaz: hoy la implementa `AlmacenLocal` (disco). Para AWS se crea una implementación con S3 sin tocar el resto del código.

## Entregables y documentación

| Entregable | Archivo |
|------------|---------|
| Documento técnico (PDF) | `docs/FEAR_Clothes_Documentacion_Tecnica.pdf` |
| Script de creación de la base de datos (MySQL 8) | `database/fear_clothes.sql` |
| DBML (dbdiagram.io) | `docs/fear_clothes.dbml` |
| Diagrama entidad-relación | `docs/diagrams/er_diagrama.png` |
| Diagramas UML (clases, capas, secuencia) | `docs/diagrams/uml_*.png`, `docs/diagrams/secuencia_*.png` |
| Diagrama de casos de uso | `docs/diagrams/casos_de_uso.png` |
| Arquitectura MVC | `docs/diagrams/arquitectura_mvc.png` |
| Capturas de pantalla (evidencias de maquetación) | `docs/capturas/*.png` |
| Guía de capturas, GitHub y demo | `docs/GUIA_DE_ENTREGA.md` |

## Cuentas de prueba

| Rol        | Correo                      | Contraseña    |
|------------|-----------------------------|---------------|
| Admin      | admin@fearclothes.com       | admin123      |
| Vendedor   | vendedor@fearclothes.com    | vendedor123   |
| Comprador  | comprador@fearclothes.com   | comprador123  |

## CRUD por entidad

| Entidad | Crear | Leer | Actualizar | Eliminar |
|---------|-------|------|------------|----------|
| Usuario | Registro | Listado (admin) y «Mi perfil» | Perfil propio (nombre/contraseña) y activar/desactivar (admin) | — (se desactiva) |
| Marca | Admin | Admin, filtros, formulario de venta | Admin | Admin (si no tiene prendas) |
| Talla | Admin | Admin, filtros, formulario de venta | Admin | Admin (si no tiene prendas) |
| Publicación (+ Prenda) | Vendedor | Catálogo y detalle | Dueño o admin (si está activa) | Dueño o admin (si no tiene ofertas ni compra) |
| Puja | Comprador | Detalle de la publicación | — | — |
| Orden | Compra / oferta aceptada / subasta ganada | Mis compras, Mis ventas | — | — |

## Rutas

| Ruta | Método | Acceso | Descripción |
|------|--------|--------|-------------|
| `/` | GET | Público | Catálogo con filtros (`q`, `categoria`, `marca`, `talla`, `tipo`) |
| `/publicacion/{id}` | GET | Público | Detalle de una publicación |
| `/login`, `/registro` | GET/POST | Público | Autenticación y alta de cuentas |
| `/vender` | GET/POST | Vendedor | Publicar una prenda (con subida de imagen) |
| `/publicacion/{id}/editar` | GET/POST | Dueño o admin | Editar publicación activa |
| `/publicacion/{id}/eliminar` | POST | Dueño o admin | Eliminar si no tiene ofertas ni compra |
| `/publicacion/{id}/cerrar` | POST | Dueño o admin | Cerrar publicación |
| `/publicacion/{id}/comprar` | POST | Con sesión | Comprar al instante |
| `/publicacion/{id}/pujar` | POST | Con sesión | Ofertar o pujar |
| `/publicacion/{id}/aceptar/{pujaId}` | POST | Dueño | Aceptar una oferta |
| `/mis-compras`, `/mis-ventas` | GET | Con sesión | Historial |
| `/perfil` | GET/POST | Con sesión | Editar nombre y contraseña |
| `/admin` | GET | Admin | Panel de marcas, tallas y moderación |
| `/admin/marcas`, `/admin/marcas/{id}/editar`, `/admin/marcas/{id}/eliminar` | POST / GET+POST / POST | Admin | CRUD de marcas |
| `/admin/tallas`, `/admin/tallas/{id}/editar`, `/admin/tallas/{id}/eliminar` | POST / GET+POST / POST | Admin | CRUD de tallas |
| `/admin/publicaciones/{id}/cerrar` | POST | Admin | Moderar publicaciones |
| `/admin/usuarios`, `/admin/usuarios/{id}/estado` | GET / POST | Admin | Listar y activar/desactivar usuarios |

## Qué puede hacer

- **Visitante:** ver catálogo, buscar y filtrar por categoría, marca, talla y tipo de venta; ver detalle.
- **Comprador:** registrarse, comprar al instante, ofertar, pujar en subastas, ver sus compras.
- **Vendedor:** publicar, editar, cerrar y eliminar sus prendas (venta directa, con ofertas o subasta), aceptar ofertas, ver sus ventas.
- **Admin:** CRUD de marcas y tallas, gestión de usuarios (activar/desactivar), moderar publicaciones.
- Las subastas vencidas se cierran solas (tarea programada cada minuto) y el mejor postor recibe la orden.
- El pago es **simulado**.

## Dónde está cada concepto de POO

| Concepto            | Dónde |
|---------------------|-------|
| **Abstracción**     | `Usuario` y `Prenda` (clases abstractas); interfaz `TipoVenta` |
| **Herencia**        | `Usuario` → `Comprador`, `Vendedor`, `Administrador`; `Prenda` → `Calzado`, `Camiseta`, `Chamarra` (ambas con `@Inheritance(JOINED)`); `TipoVentaBase` → estrategias |
| **Polimorfismo**    | `getRol()`, `puedePublicar()`, `getDetalleEspecifico()`, y todas las llamadas a `TipoVenta` desde `Publicacion` y `PublicacionService` |
| **Encapsulamiento** | Atributos privados con getters/setters; reglas de negocio dentro de las entidades y estrategias |
| **Interfaces**      | `TipoVenta`, `AlmacenArchivos`, repositorios de Spring Data |
| **Patrón Strategy** | `TipoVenta` + `VentaDirecta`, `VentaPorOferta`, `Subasta` |
| **Template Method** | `TipoVentaBase.validarPuja()` (pasos fijos) + `validarMonto()` (paso por subclase) |
| **Patrón Factory**  | `TipoVentaFactory` (estrategia por tipo) y `PrendaFactory` (subclase de prenda por categoría) |
| **Repository**      | paquete `repository` |
| **Capas**           | `controller` → `service` → `repository` → `model` |

## Estructura

```
src/main/java/com/fearclothes/
├── model/        Entidades (Usuario, Prenda, Publicacion, Puja, Orden, Marca, Talla...)
├── venta/        Strategy: TipoVenta, TipoVentaBase, VentaDirecta, VentaPorOferta, Subasta
├── factory/      PrendaFactory
├── repository/   Spring Data JPA
├── service/      Lógica de negocio
├── controller/   Controladores MVC
├── dto/          Formularios con validación
├── storage/      AlmacenArchivos (interfaz) y AlmacenLocal (imágenes en disco)
├── security/     Spring Security (login por correo, roles)
├── config/       DataLoader (datos de prueba)
└── exception/    ReglaNegocioException
src/main/resources/
├── templates/    Vistas Thymeleaf
└── static/        css/estilos.css (ajustes de marca sobre Bootstrap) e img/ (ilustraciones de ejemplo)
```

## Reglas de venta

- **Venta directa:** se compra al precio fijado; no admite ofertas.
- **Con ofertas (bid/ask):** el vendedor fija el precio de venta. Se puede comprar a ese precio o dejar una oferta menor que el vendedor puede aceptar.
- **Subasta:** tiene fecha de cierre; cada puja debe superar a la anterior por al menos $50; gana la más alta.

## Despliegue en AWS (siguiente etapa)

El proyecto ya trae `application-aws.properties` (perfil `aws`) con MySQL por variables de entorno.

1. `mvn clean package` → genera `target/fear-clothes-0.0.1-SNAPSHOT.jar`.
2. Crear una base de datos **RDS MySQL** y un **EC2** (o Elastic Beanstalk con Java 21).
3. En el servidor definir `DB_HOST`, `DB_USER`, `DB_PASSWORD` (y opcional `DB_NAME`).
4. `java -jar fear-clothes-0.0.1-SNAPSHOT.jar --spring.profiles.active=aws`
5. Imágenes: crear un bucket **S3** e implementar `AlmacenArchivos` con el SDK de AWS (en EC2 las fotos de `uploads/` se perderían si se recrea la instancia).

> Nota: en el perfil `aws` los datos de prueba (`DataLoader`) se cargan solo si la tabla de usuarios está vacía.
