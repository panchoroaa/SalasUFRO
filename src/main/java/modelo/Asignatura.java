package modelo;

public class Asignatura {
    private String nombre;
    private String codigo;
    private String carrera;
    private int semestre;
    private int cantidadAlumnos;

    public Asignatura(String nombre, String codigo, String carrera, int semestre, int cantidadAlumnos) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.carrera = carrera;
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
}