package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profesor {
    private String nombre;
    private String departamento;
    private String rut;
    private String id;
    private List<Asignatura> asignaturasImpartidas;

    // Constructor principal
    public Profesor(String nombre, String departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.asignaturasImpartidas = new ArrayList<>();
    }

    // Constructor completo para JSON
    public Profesor(String nombre, String departamento, String rut, String id) {
        this(nombre, departamento);
        this.rut = rut;
        this.id = id;
    }

    // Getters y setters necesarios
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public List<Asignatura> getAsignaturasImpartidas() {
        return asignaturasImpartidas;
    }

    public void agregarAsignatura(Asignatura asignatura) {
        this.asignaturasImpartidas.add(asignatura);
    }

    // Método auxiliar para agregar asignatura por nombre y cantidad de alumnos
    public void agregarAsignatura(String nombre, int cantidadAlumnos) {
        Asignatura asignatura = new Asignatura(nombre, "", "", 1, cantidadAlumnos);
        this.asignaturasImpartidas.add(asignatura);
    }

    public Map<String, Integer> getAsignaturasConAlumnos() {
        Map<String, Integer> asignaturasConAlumnos = new java.util.HashMap<>();
        for (Asignatura asignatura : asignaturasImpartidas) {
            asignaturasConAlumnos.put(asignatura.getNombre(), asignatura.getCantidadAlumnos());
        }
        return asignaturasConAlumnos;
    }

    public int getNumeroAsignaturasImpartidas() {
        return asignaturasImpartidas.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Profesor{")
                .append("rut='").append(rut).append('\'')
                .append(", nombre='").append(nombre).append('\'')
                .append(", departamento='").append(departamento).append('\'')
                .append(", id='").append(id).append('\'')
                .append(", asignaturas=[");

        if (asignaturasImpartidas.isEmpty()) {
            sb.append("Ninguna");
        } else {
            List<String> tempAsignaturas = new ArrayList<>();
            for (Asignatura asig : asignaturasImpartidas) {
                tempAsignaturas.add(asig.getNombre() + " (" + asig.getCantidadAlumnos() + " alumnos)");
            }
            sb.append(String.join("; ", tempAsignaturas));
        }

        sb.append("]}");
        return sb.toString();
    }

    // Método para verificar si el profesor tiene un ramo específico
    public boolean tieneRamo(String codigoRamo) {
        for (Asignatura asignatura : asignaturasImpartidas) {
            if (asignatura.getCodigo().equals(codigoRamo)) {
                return true;
            }
        }
        return false;
    }
}