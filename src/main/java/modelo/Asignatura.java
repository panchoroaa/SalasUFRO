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

    public Asignatura() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getCantidadAlumnos() { return cantidadAlumnos; }
    public void setCantidadAlumnos(int cantidadAlumnos) { this.cantidadAlumnos = cantidadAlumnos; }

    @Override
    public String toString() {
        return String.format("Asignatura: %-30s | Código: %-10s | Alumnos: %d", nombre, codigo, cantidadAlumnos);
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
