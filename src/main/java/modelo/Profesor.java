package modelo;

public class Profesor {
    private String nombre;
    private String departamento;
    private String asignatura;

    public Profesor(String nombre, String departamento, String asignatura) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.asignatura = asignatura;
    }

    public String getNombre() { return nombre; }
    public String getDepartamento() { return departamento; }
    public String getAsignatura() { return asignatura; }
}
