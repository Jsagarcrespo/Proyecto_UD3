package org.example.modelos;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/*
 * ENTIDAD JPA: PROYECTO
 * @Entity indica que Hibernate guardara los objetos de esta clase en una tabla.
 * Los getters y setters sirven para leer y modificar los atributos Java.
 */
@Entity
@Table(name = "proyectos")
public class Proyecto {

    // @Id marca la clave primaria que identifica cada registro.
    @Id
    @Column(length = 20)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ciudad;

    // Un registro puede aparecer en muchos suministros (relacion 1:N).
    // mappedBy indica que el lado que tiene la clave ajena es Gestion.
    // LAZY significa que la lista no se carga hasta que hace falta.
    @OneToMany(mappedBy = "proyecto", fetch = FetchType.LAZY)
    private List<Gestion> gestiones = new ArrayList<>();

    public Proyecto() {
    }

    public Proyecto(String codigo, String nombre, String ciudad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    // Esta validacion se ejecuta antes de guardar o modificar.
    // Si hay un dato incorrecto, lanzamos una excepcion que la ventana captura.
    public void validar() {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del proyecto es obligatorio");
        }
        // En la tabla el codigo tiene como maximo 20 caracteres.
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El codigo no puede superar 20 caracteres");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("La ciudad del proyecto es obligatoria");
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<Gestion> getGestiones() {
        return gestiones;
    }

    public void setGestiones(List<Gestion> gestiones) {
        this.gestiones = gestiones;
    }

    @Override
    public String toString() {
        return "Proyecto{" + "codigo='" + codigo + '\'' + ", nombre='" + nombre + '\'' + ", ciudad='" + ciudad + '\'' + '}';
    }
}
