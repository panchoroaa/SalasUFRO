package modelo;

public enum EstadoSala {
    DISPONIBLE("Disponible"),
    MANTENIMIENTO("Mantenimiento");

    private final String descripcion;

    EstadoSala(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}