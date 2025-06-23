package modelo;

public class Sala {
    private String nombre;
    private int capacidad;
    private String estado; // Por ejemplo: "Disponible", "Ocupada", etc.
    private BloqueHorario bloqueHorario;

    public Sala(String nombre, int capacidad, String estado, BloqueHorario bloqueHorario) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = estado;
        this.bloqueHorario = bloqueHorario;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public BloqueHorario getBloqueHorario() {
        return bloqueHorario;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setBloqueHorario(BloqueHorario bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
    }
    @Override
    public String toString() {
        return "Sala{" +
                "nombre='" + nombre + '\'' +
                ", capacidad=" + capacidad +
                ", estado='" + estado + '\'' +
                ", bloqueHorario=" + bloqueHorario +
                '}';
    }
}
//a