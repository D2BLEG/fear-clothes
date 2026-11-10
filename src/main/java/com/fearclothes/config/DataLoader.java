package com.fearclothes.config;

import com.fearclothes.model.*;
import com.fearclothes.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos de prueba para la demo. Solo se cargan si la base está vacía.
 *
 * Usuarios:
 *   admin@fearclothes.com      / admin123
 *   vendedor@fearclothes.com   / vendedor123
 *   comprador@fearclothes.com  / comprador123
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final MarcaRepository marcas;
    private final TallaRepository tallas;
    private final PublicacionRepository publicaciones;
    private final PasswordEncoder encoder;

    public DataLoader(UsuarioRepository usuarios, MarcaRepository marcas, TallaRepository tallas,
                      PublicacionRepository publicaciones, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.marcas = marcas;
        this.tallas = tallas;
        this.publicaciones = publicaciones;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }

        usuarios.save(new Administrador("Admin FEAR", "admin@fearclothes.com", encoder.encode("admin123")));
        Vendedor vendedor = usuarios.save(
                new Vendedor("Street Vault", "vendedor@fearclothes.com", encoder.encode("vendedor123")));
        Comprador comprador = usuarios.save(
                new Comprador("Gael", "comprador@fearclothes.com", encoder.encode("comprador123")));

        // Marcas
        Marca nike = marcas.save(new Marca("Nike", "Estados Unidos"));
        Marca jordan = marcas.save(new Marca("Jordan", "Estados Unidos"));
        Marca adidas = marcas.save(new Marca("Adidas", "Alemania"));
        Marca newBalance = marcas.save(new Marca("New Balance", "Estados Unidos"));
        Marca supreme = marcas.save(new Marca("Supreme", "Estados Unidos"));
        Marca stussy = marcas.save(new Marca("Stüssy", "Estados Unidos"));
        Marca carhartt = marcas.save(new Marca("Carhartt WIP", "Estados Unidos"));
        Marca northFace = marcas.save(new Marca("The North Face", "Estados Unidos"));

        // Tallas
        List<String> etiquetas = List.of("XS", "S", "M", "L", "XL", "XXL",
                "6 MX", "7 MX", "8 MX", "9 MX", "10 MX");
        etiquetas.forEach(e -> tallas.save(new Talla(e)));
        Talla xs = talla("XS"), s = talla("S"), m = talla("M"), l = talla("L"), xl = talla("XL");
        Talla mx8 = talla("8 MX"), mx9 = talla("9 MX"), mx10 = talla("10 MX");

        // Publicaciones de ejemplo
        LocalDateTime enDosDias = LocalDateTime.now().plusDays(2);
        LocalDateTime enCincoDias = LocalDateTime.now().plusDays(5);

        publicaciones.save(new Publicacion(
                calzado("Air Jordan 1 Retro High 'Chicago'", jordan, mx9, "Rojo/Blanco", Condicion.NUEVA,
                        "Silueta alta clásica. Caja original incluida.", "High OG", "/img/jordan-1.svg"),
                vendedor, TipoPublicacion.OFERTA, new BigDecimal("6500"), null));

        publicaciones.save(new Publicacion(
                calzado("Nike Dunk Low 'Panda'", nike, mx8, "Blanco/Negro", Condicion.NUEVA,
                        "Edición general, nunca usados.", "Low", "/img/dunk-panda.svg"),
                vendedor, TipoPublicacion.DIRECTA, new BigDecimal("3200"), null));

        publicaciones.save(new Publicacion(
                calzado("New Balance 550 'White Green'", newBalance, mx10, "Blanco/Verde", Condicion.COMO_NUEVA,
                        "Usados dos veces.", "Basketball retro", "/img/nb-550.svg"),
                vendedor, TipoPublicacion.SUBASTA, new BigDecimal("1800"), enDosDias));

        publicaciones.save(new Publicacion(
                camiseta("Box Logo Tee", supreme, l, "Blanco", Condicion.NUEVA,
                        "Playera Box Logo, temporada FW.", "Redondo", "/img/tee-blanca.svg"),
                vendedor, TipoPublicacion.SUBASTA, new BigDecimal("2500"), enCincoDias));

        publicaciones.save(new Publicacion(
                camiseta("Stock Logo Tee", stussy, m, "Negro", Condicion.NUEVA,
                        "Algodón pesado, corte relajado.", "Redondo", "/img/tee-negra.svg"),
                vendedor, TipoPublicacion.DIRECTA, new BigDecimal("1100"), null));

        publicaciones.save(new Publicacion(
                camiseta("Adicolor Trefoil Tee", adidas, s, "Verde", Condicion.USADA,
                        "Buen estado, lavada una vez.", "Redondo", "/img/tee-verde.svg"),
                vendedor, TipoPublicacion.OFERTA, new BigDecimal("650"), null));

        publicaciones.save(new Publicacion(
                chamarra("Nuptse 1996 Retro", northFace, xl, "Negro", Condicion.NUEVA,
                        "Plumas de ganso 700, silueta voluminosa.", false, "/img/chamarra-negra.svg"),
                vendedor, TipoPublicacion.OFERTA, new BigDecimal("7800"), null));

        publicaciones.save(new Publicacion(
                chamarra("Detroit Jacket", carhartt, m, "Café", Condicion.COMO_NUEVA,
                        "Lona resistente con forro de manta.", false, "/img/chamarra-cafe.svg"),
                vendedor, TipoPublicacion.DIRECTA, new BigDecimal("3900"), null));

        publicaciones.save(new Publicacion(
                chamarra("Rompevientos Packable", nike, xs, "Azul", Condicion.NUEVA,
                        "Ligera y con capucha.", true, "/img/rompevientos-azul.svg"),
                vendedor, TipoPublicacion.SUBASTA, new BigDecimal("900"), enDosDias));

        // Una puja inicial en la subasta de New Balance, para que la demo no arranque vacía
        publicaciones.findAll().stream()
                .filter(p -> p.getTipo() == TipoPublicacion.SUBASTA
                        && p.getPrenda().getNombre().startsWith("New Balance"))
                .findFirst()
                .ifPresent(p -> p.agregarPuja(new Puja(p, comprador, new BigDecimal("1900"))));
    }

    private Talla talla(String etiqueta) {
        return tallas.findAll().stream()
                .filter(t -> t.getEtiqueta().equals(etiqueta))
                .findFirst()
                .orElseThrow();
    }

    private Prenda calzado(String nombre, Marca marca, Talla talla, String color,
                           Condicion condicion, String descripcion, String silueta, String imagen) {
        Calzado prenda = new Calzado(silueta);
        completar(prenda, nombre, marca, talla, color, condicion, descripcion, imagen);
        return prenda;
    }

    private Prenda camiseta(String nombre, Marca marca, Talla talla, String color,
                            Condicion condicion, String descripcion, String cuello, String imagen) {
        Camiseta prenda = new Camiseta(cuello);
        completar(prenda, nombre, marca, talla, color, condicion, descripcion, imagen);
        return prenda;
    }

    private Prenda chamarra(String nombre, Marca marca, Talla talla, String color,
                            Condicion condicion, String descripcion, boolean impermeable, String imagen) {
        Chamarra prenda = new Chamarra(impermeable);
        completar(prenda, nombre, marca, talla, color, condicion, descripcion, imagen);
        return prenda;
    }

    private void completar(Prenda prenda, String nombre, Marca marca, Talla talla,
                           String color, Condicion condicion, String descripcion, String imagen) {
        prenda.setNombre(nombre);
        prenda.setMarca(marca);
        prenda.setTalla(talla);
        prenda.setColor(color);
        prenda.setCondicion(condicion);
        prenda.setDescripcion(descripcion);
        prenda.setImagenUrl(imagen);
    }
}
