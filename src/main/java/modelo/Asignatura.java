package modelo;

import java.util.Objects;

public class Asignatura {
    private String nombre;
    private String codigo;
    private int cantidadAlumnos;

    public Asignatura(String nombre, String codigo, int cantidadAlumnos) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.cantidadAlumnos = cantidadAlumnos;
    }

    public Asignatura() {} // Constructor para Jackson

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getCantidadAlumnos() {
        return cantidadAlumnos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setCantidadAlumnos(int cantidadAlumnos) {
        this.cantidadAlumnos = cantidadAlumnos;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ", " + cantidadAlumnos + " alumnos)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asignatura that = (Asignatura) o;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}