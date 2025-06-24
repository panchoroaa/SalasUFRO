package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profesor {
    private String nombre;
    private String departamento;
    private Map<String, Integer> asignaturasConAlumnos;

    public Profesor(String nombre, String departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.asignaturasConAlumnos = new HashMap<>(); // Inicializa el mapa
    }


    public void agregarAsignatura(String asignatura, int cantidadAlumnos) {
        this.asignaturasConAlumnos.put(asignatura, cantidadAlumnos);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public Map<String, Integer> getAsignaturasConAlumnos() {
        return asignaturasConAlumnos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Retorna una representación en cadena del Profesor, incluyendo
     * su nombre, departamento y una lista de asignaturas con la cantidad de alumnos.
     * Ejemplo: "Nombre: Juan Perez, Departamento: Ciencias, Asignaturas: Matematicas (30 alumnos); Fisica (25 alumnos)"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre)
                .append(", Departamento: ").append(departamento)
                .append(", Asignaturas: ");

        if (asignaturasConAlumnos.isEmpty()) {
            sb.append("Ninguna");
        } else {
            List<String> tempAsignaturas = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : asignaturasConAlumnos.entrySet()) {

                tempAsignaturas.add(entry.getKey() + " (" + entry.getValue() + " alumnos)");
            }

            sb.append(String.join("; ", tempAsignaturas));
        }
        return sb.toString();
    }
}