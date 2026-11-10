package com.fearclothes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "chamarras")
public class Chamarra extends Prenda {

    private Boolean impermeable;

    protected Chamarra() {
    }

    public Chamarra(boolean impermeable) {
        super(Categoria.CHAMARRA);
        this.impermeable = impermeable;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Impermeable: " + (Boolean.TRUE.equals(impermeable) ? "sí" : "no");
    }

    public Boolean getImpermeable() {
        return impermeable;
    }

    public void setImpermeable(Boolean impermeable) {
        this.impermeable = impermeable;
    }
}
