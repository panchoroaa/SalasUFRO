package modelo;

public abstract class Sala {
    protected final String id;
    protected final String nombre;

    public Sala(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }

    public abstract String getTamano();

    @Override
    public String toString() {
        return String.format("ID: %s | Nombre: %s | Tamaño: %s", id, nombre, getTamano());
    }
}
//a