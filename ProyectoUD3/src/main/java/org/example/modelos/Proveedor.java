package org.example.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/*
 * ENTIDAD JPA: PROVEEDOR
 * @Entity indica que Hibernate guardara los objetos de esta clase en una tabla.
 * Los getters y setters sirven para leer y modificar los atributos Java.
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {

    // @Id marca la clave primaria que identifica cada registro.
    @Id
    @Column(length = 20)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String direccion;

    // Un registro puede aparecer en muchos suministros (relacion 1:N).
    // mappedBy indica que el lado que tiene la clave ajena es Gestion.
    // LAZY significa que la lista no se carga hasta que hace falta.
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<Gestion> gestiones = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(String codigo, String nombre, String apellidos, String direccion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
    }

    // Esta validacion se ejecuta antes de guardar o modificar.
    // Si hay un dato incorrecto, lanzamos una excepcion que la ventana captura.
    public void validar() {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del proveedor es obligatorio");
        }
        // En la tabla el codigo tiene como maximo 20 caracteres.
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El codigo no puede superar 20 caracteres");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos del proveedor son obligatorios");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La direccion del proveedor es obligatoria");
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Gestion> getGestiones() {
        return gestiones;
    }

    public void setGestiones(List<Gestion> gestiones) {
        this.gestiones = gestiones;
    }

    @Override
    public String toString() {
        return "Proveedor{" + "codigo='" + codigo + '\'' + ", nombre='" + nombre + '\'' + ", apellidos='" + apellidos + '\'' + '}';
    }
}
