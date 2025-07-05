package modelo;

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
}