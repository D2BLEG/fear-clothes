# Guía de entrega de FEAR Clothes

Esta guía ayuda a completar lo que falta para la entrega: capturas, repositorio en GitHub y demostración.

## 1. Checklist de la rúbrica

| Requisito | Dónde se cumple |
|-----------|-----------------|
| POO (abstracción, herencia, polimorfismo, encapsulamiento) | `model/`, `venta/`; tabla en `README.md` |
| Spring Boot (backend) | `src/main/java` |
| HTML/CSS/Bootstrap + Thymeleaf | `src/main/resources/templates`, Bootstrap 5.3.3 por WebJar |
| MVC | `controller/` → `service/` → `repository/` → `model/` |
| Base relacional, 6 o más tablas con PK/FK | 13 tablas (`database/fear_clothes.sql`) |
| CRUD | Tabla «CRUD por entidad» del `README.md` |
| Autenticación | Spring Security, roles Comprador/Vendedor/Administrador |
| JPA/Hibernate | Entidades y repositorios Spring Data |
| Documento técnico PDF | `docs/FEAR_Clothes_Documentacion_Tecnica.pdf` |

## 2. Capturas para la sección «Evidencias de Maquetación»

Arranca la aplicación (`FearClothesApplication`) y toma estas pantallas. Cada una indica la ruta y la cuenta.

| # | Pantalla | Ruta | Cuenta |
|---|----------|------|--------|
| 1 | Catálogo con filtros | `/` | sin sesión |
| 2 | Detalle de una subasta | `/publicacion/3` | sin sesión |
| 3 | Detalle con ofertas | `/publicacion/1` | comprador |
| 4 | Registro | `/registro` | sin sesión |
| 5 | Inicio de sesión | `/login` | sin sesión |
| 6 | Publicar una prenda | `/vender` | vendedor |
| 7 | Mis ventas | `/mis-ventas` | vendedor |
| 8 | Aceptar una oferta | `/publicacion/1` (con una oferta hecha) | vendedor |
| 9 | Mis compras | `/mis-compras` | comprador |
| 10 | Panel de administración | `/admin` | admin |
| 11 | Usuarios | `/admin/usuarios` | admin |
| 12 | Mi perfil | `/perfil` | cualquiera |
| 13 | Vista en teléfono | `/` | herramientas de desarrollo del navegador (F12, modo móvil) |

Las 13 capturas ya están en `docs/capturas/` (`01_catalogo.png` … `13_vista_telefono.png`, mismo número que la tabla). Ya están integradas en la sección 15 del PDF técnico (figuras 7 a 19).

## 3. Subir el código a GitHub

**Ya publicado:** https://github.com/D2BLEG/fear-clothes (rama `main`). Para subir cambios nuevos: **Git → Commit** y luego **Git → Push** en IntelliJ.

Pasos que se siguieron (como referencia):

1. En GitHub crea un repositorio vacío (por ejemplo `fear-clothes`), sin README.
2. En IntelliJ: **Git → Create Git Repository** (o **VCS → Enable Version Control**), elige la carpeta del proyecto.
3. **Git → Commit** (el archivo `.gitignore` ya excluye `target/`, `data/` y `uploads/`).
4. **Git → Manage Remotes** → agrega la URL del repositorio como `origin`.
5. **Git → Push**.

Por terminal:

```bash
git init
git add .
git commit -m "FEAR Clothes: versión inicial"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/fear-clothes.git
git push -u origin main
```

## 4. Guion sugerido para la demostración (10 minutos)

1. **Arquitectura** (1 min): MVC por capas; mostrar el diagrama de arquitectura.
2. **Modelo de objetos** (2 min): jerarquías `Usuario`/`Prenda` y el patrón Strategy (`TipoVenta`).
3. **Base de datos** (1 min): consola H2 o diagrama ER; mencionar las 13 tablas.
4. **Visitante** (1 min): catálogo, búsqueda y filtros.
5. **Comprador** (2 min): iniciar sesión, pujar en una subasta, ofertar y comprar al instante.
6. **Vendedor** (2 min): publicar una prenda con foto, editar, aceptar una oferta.
7. **Administrador** (1 min): marcas, tallas, usuarios y moderación.
8. **Cierre**: plan de despliegue en AWS.

Cuentas de prueba: `admin@fearclothes.com / admin123`, `vendedor@fearclothes.com / vendedor123`,
`comprador@fearclothes.com / comprador123`.

Consejo: antes de la demostración borra la carpeta `data/` para arrancar con los datos de prueba limpios.
