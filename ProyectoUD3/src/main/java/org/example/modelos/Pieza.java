package org.example.modelos;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
 * ENTIDAD JPA: PIEZA
 * @Entity indica que Hibernate guardara los objetos de esta clase en una tabla.
 * Los getters y setters sirven para leer y modificar los atributos Java.
 */
@Entity
@Table(name = "piezas")
public class Pieza {

    // @Id marca la clave primaria que identifica cada registro.
    @Id
    @Column(length = 20)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    private String descripcion;

    // Un registro puede aparecer en muchos suministros (relacion 1:N).
    // mappedBy indica que el lado que tiene la clave ajena es Gestion.
    // LAZY significa que la lista no se carga hasta que hace falta.
    @OneToMany(mappedBy = "pieza", fetch = FetchType.LAZY)
    private List<Gestion> gestiones = new ArrayList<>();

    public Pieza() {
    }

    public Pieza(String codigo, String nombre, BigDecimal precio, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    // Esta validacion se ejecuta antes de guardar o modificar.
    // Si hay un dato incorrecto, lanzamos una excepcion que la ventana captura.
    public void validar() {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la pieza es obligatorio");
        }
        // En la tabla el codigo tiene como maximo 20 caracteres.
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El codigo no puede superar 20 caracteres");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la pieza es obligatorio");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        // La columna de la base de datos guarda solo dos decimales.
        if (precio.scale() > 2) {
            throw new IllegalArgumentException("El precio puede tener como maximo 2 decimales");
        }
        // precision=10 y scale=2 permiten hasta 8 cifras enteras y 2 decimales.
        if (precio.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("El precio supera el maximo permitido");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Gestion> getGestiones() {
        return gestiones;
    }

    public void setGestiones(List<Gestion> gestiones) {
        this.gestiones = gestiones;
    }

    @Override
    public String toString() {
        return "Pieza{" + "codigo='" + codigo + '\'' + ", nombre='" + nombre + '\'' + ", precio=" + precio + '}';
    }
}
