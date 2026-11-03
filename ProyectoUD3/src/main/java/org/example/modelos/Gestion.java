package org.example.modelos;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "gestiones")
public class Gestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private int cantidad;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_codigo", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "pieza_codigo", nullable = false)
    private Pieza pieza;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "proyecto_codigo", nullable = false)
    private Proyecto proyecto;

    public Gestion() {
    }

    public Gestion(LocalDate fecha, int cantidad, Proveedor proveedor, Pieza pieza, Proyecto proyecto) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
        this.pieza = pieza;
        this.proyecto = proyecto;
    }

    public void validar() {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        if (proveedor == null || pieza == null || proyecto == null) {
            throw new IllegalArgumentException("Debes elegir proveedor, pieza y proyecto");
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    @Override
    public String toString() {
        return "Gestion{" + "id=" + id + ", fecha=" + fecha + ", cantidad=" + cantidad + '}';
    }
}
