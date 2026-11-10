package com.fearclothes.dto;

import com.fearclothes.model.Calzado;
import com.fearclothes.model.Camiseta;
import com.fearclothes.model.Categoria;
import com.fearclothes.model.Chamarra;
import com.fearclothes.model.Condicion;
import com.fearclothes.model.Prenda;
import com.fearclothes.model.Publicacion;
import com.fearclothes.model.TipoPublicacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/** Datos del formulario "Vender una prenda" (también se usa para editar). */
public class PublicacionForm {

    /** Rellena el formulario con los datos actuales de una publicación (para editarla). */
    public static PublicacionForm desde(Publicacion publicacion) {
        PublicacionForm f = new PublicacionForm();
        Prenda prenda = publicacion.getPrenda();
        f.nombre = prenda.getNombre();
        f.descripcion = prenda.getDescripcion();
        f.color = prenda.getColor();
        f.imagenUrl = prenda.getImagenUrl();
        f.categoria = prenda.getCategoria();
        f.condicion = prenda.getCondicion();
        f.marcaId = prenda.getMarca().getId();
        f.tallaId = prenda.getTalla().getId();
        f.tipo = publicacion.getTipo();
        f.precio = publicacion.getPrecio();
        if (prenda instanceof Calzado calzado) {
            f.silueta = calzado.getSilueta();
        } else if (prenda instanceof Camiseta camiseta) {
            f.tipoCuello = camiseta.getTipoCuello();
        } else if (prenda instanceof Chamarra chamarra) {
            f.impermeable = Boolean.TRUE.equals(chamarra.getImpermeable());
        }
        return f;
    }

    @NotBlank(message = "Escribe el nombre de la prenda")
    @Size(max = 120)
    private String nombre;

    @Size(max = 1000)
    private String descripcion;

    private String color;

    /** Imagen subida desde el equipo del vendedor (tiene prioridad sobre imagenUrl). */
    private MultipartFile imagen;

    private String imagenUrl;

    @NotNull(message = "Elige una categoría")
    private Categoria categoria = Categoria.CAMISETA;

    @NotNull(message = "Elige la condición")
    private Condicion condicion = Condicion.NUEVA;

    @NotNull(message = "Elige una marca")
    private Long marcaId;

    @NotNull(message = "Elige una talla")
    private Long tallaId;

    @NotNull(message = "Elige el tipo de venta")
    private TipoPublicacion tipo = TipoPublicacion.DIRECTA;

    @NotNull(message = "Escribe un precio")
    @DecimalMin(value = "1", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Min(1)
    @Max(14)
    private Integer duracionDias = 3;

    // Datos específicos por categoría
    private String silueta;
    private String tipoCuello;
    private boolean impermeable;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public MultipartFile getImagen() {
        return imagen;
    }

    public void setImagen(MultipartFile imagen) {
        this.imagen = imagen;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Condicion getCondicion() {
        return condicion;
    }

    public void setCondicion(Condicion condicion) {
        this.condicion = condicion;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getTallaId() {
        return tallaId;
    }

    public void setTallaId(Long tallaId) {
        this.tallaId = tallaId;
    }

    public TipoPublicacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoPublicacion tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getSilueta() {
        return silueta;
    }

    public void setSilueta(String silueta) {
        this.silueta = silueta;
    }

    public String getTipoCuello() {
        return tipoCuello;
    }

    public void setTipoCuello(String tipoCuello) {
        this.tipoCuello = tipoCuello;
    }

    public boolean isImpermeable() {
        return impermeable;
    }

    public void setImpermeable(boolean impermeable) {
        this.impermeable = impermeable;
    }
}
