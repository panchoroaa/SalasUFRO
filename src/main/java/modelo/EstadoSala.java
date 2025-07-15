package modelo;

public enum EstadoSala {
    DISPONIBLE("Disponible"),
    NO_DISPONIBLE("No Disponible");

    private final String descripcion;

    EstadoSala(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}