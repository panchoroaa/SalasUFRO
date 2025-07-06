package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Profesor {
    private String nombre;
    private String rut;
    private String departamento;
    private String ID;
    private List<Asignatura> asignaturasImpartidas;

    public Profesor(String nombre, String rut, String departamento, String ID) {
        this.nombre = nombre;
        this.rut = rut;
        this.departamento = departamento;
        this.ID = (ID == null || ID.isEmpty()) ? UUID.randomUUID().toString() : ID;
        this.asignaturasImpartidas = new ArrayList<>();
    }

    public Profesor() {
        this.asignaturasImpartidas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Asignatura> getAsignaturasImpartidas() {
        return asignaturasImpartidas;
    }

    public void setAsignaturasImpartidas(List<Asignatura> asignaturasImpartidas) {
        this.asignaturasImpartidas = new ArrayList<>(asignaturasImpartidas);
    }

    public boolean tieneAsignatura(String codigoAsignatura) {
        return asignaturasImpartidas.stream()
                .anyMatch(a -> a.getCodigo().equalsIgnoreCase(codigoAsignatura));
    }

    public void agregarAsignatura(Asignatura asignatura) {
        if (!tieneAsignatura(asignatura.getCodigo())) {
            this.asignaturasImpartidas.add(asignatura);
        }
    }

    public void removerAsignatura(String codigoAsignatura) {
        this.asignaturasImpartidas.removeIf(a -> a.getCodigo().equalsIgnoreCase(codigoAsignatura));
    }

    @Override
    public String toString() {
        return "Profesor [ID=" + ID + ", Nombre=" + nombre + ", RUT=" + rut + ", Departamento=" + departamento + "]";
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