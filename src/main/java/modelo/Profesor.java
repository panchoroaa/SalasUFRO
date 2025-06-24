package modelo;

import java.util.ArrayList;
import java.util.List;

public class Profesor {
    private String rut;
    private String nombre;
    private String departamento;
    private List<String> asignaturas;

    public Profesor(String nombre, String departamento) {
        this.rut = rut;
        this.nombre = nombre;
        this.departamento = departamento;
        this.asignaturas = new ArrayList<>();
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public List<String> getAsignaturas() {
        return new ArrayList<>(asignaturas);
    }

    public void agregarAsignatura(String asignatura) {
        if (!asignaturas.contains(asignatura)) {
            asignaturas.add(asignatura);
        }
    }

    public void eliminarAsignatura(String asignatura) {
        asignaturas.remove(asignatura);
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "Rut=" + rut + '\'' +
                "nombre='" + nombre + '\'' +
                ", departamento='" + departamento + '\'' +
                ", asignaturas=" + asignaturas +
                '}';
    }
}
