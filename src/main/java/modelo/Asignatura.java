// modelo/Asignatura.java
package modelo;

import java.util.Objects; // Necesario para Objects.hash

public class Asignatura {
    private String nombre;
    private String codigo;
    private String carrera;
    private int semestre;
    private int cantidadAlumnos;

    public Asignatura(String nombre, String codigo, String carrera, int semestre, int cantidadAlumnos) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (semestre <= 0) {
            throw new IllegalArgumentException("El semestre debe ser mayor a 0");
        }
        if (cantidadAlumnos < 0) {
            throw new IllegalArgumentException("La cantidad de alumnos no puede ser negativa");
        }
        this.nombre = nombre.trim();
        this.codigo = codigo != null ? codigo.trim() : "";
        this.carrera = carrera != null ? carrera.trim() : "";
        this.semestre = semestre;
        this.cantidadAlumnos = cantidadAlumnos;
    }

    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }
    public String getCarrera() { return carrera; }
    public int getSemestre() { return semestre; }
    public int getCantidadAlumnos() { return cantidadAlumnos; }

    @Override
    public String toString() {
        return String.format("%s (%s) - Carrera: %s, Semestre: %d, Alumnos: %d",
                nombre, codigo, carrera, semestre, cantidadAlumnos);
    }

    // Nuevos métodos equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asignatura that = (Asignatura) o;
        return codigo.equalsIgnoreCase(that.codigo); // Las asignaturas son iguales si tienen el mismo código
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toLowerCase()); // Hash basado en el código en minúsculas
    }
}