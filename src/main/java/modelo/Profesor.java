package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Profesor {
    private String nombre;
    private String rut;
    private String departamento;
    private String id;
    private List<Asignatura> asignaturasImpartidas;

    public Profesor(String nombre, String rut, String departamento, String id) {
        this.nombre = nombre;
        this.rut = rut;
        this.departamento = departamento;
        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString() : id;
        this.asignaturasImpartidas = new ArrayList<>();
    }

    public Profesor() {
        this.asignaturasImpartidas = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Asignatura> getAsignaturasImpartidas() { return asignaturasImpartidas; }
    public void setAsignaturasImpartidas(List<Asignatura> asignaturasImpartidas) { this.asignaturasImpartidas = asignaturasImpartidas; }

    public boolean imparteAsignatura(String codigoAsignatura) {
        return asignaturasImpartidas.stream().anyMatch(a -> a.getCodigo().equalsIgnoreCase(codigoAsignatura));
    }

    @Override
    public String toString() {
        return String.format("Profesor: %-25s | RUT: %-12s | Depto: %s", nombre, rut, departamento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profesor profesor = (Profesor) o;
        return rut.equals(profesor.rut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rut);
    }
}
