package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profesor {
    private String nombre;
    private String departamento;
    private List<Asignatura> asignaturasImpartidas;

    public Profesor(String nombre, String departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.asignaturasImpartidas = new ArrayList<>();
    }

    public void agregarAsignatura(Asignatura asignatura) {
        this.asignaturasImpartidas.add(asignatura);
    }

    public String getNombre() {
        return nombre;
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

    public void agregarAsignatura(String nombreAsignatura, int cantidadAlumnos) {
        Asignatura nuevaAsignatura = new Asignatura(
                nombreAsignatura,  // nombre
                "",               // código (vacío por ahora)
                "",               // carrera (vacía por ahora)
                1,                // semestre por defecto
                cantidadAlumnos
        );
        this.asignaturasImpartidas.add(nuevaAsignatura);
    }


    public String getDepartamento() {
        return departamento;
    }

    public List<Asignatura> getAsignaturasImpartidas() {
        return asignaturasImpartidas;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre)
                .append(", Departamento: ").append(departamento)
                .append(", Asignaturas: ");

        if (asignaturasImpartidas.isEmpty()) {
            sb.append("Ninguna");
        } else {
            List<String> tempAsignaturas = new ArrayList<>();
            for (Asignatura asig : asignaturasImpartidas) {
                tempAsignaturas.add(asig.getNombre() + " (" + asig.getCantidadAlumnos() + " alumnos)");
            }
            sb.append(String.join("; ", tempAsignaturas));
        }
        return sb.toString();
    }
}