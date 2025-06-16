package Modelo;


public class Sala {
    private final String ID;
    private final String nombre;
    private final String tamano; //Aplicación de los conceptos de Herencia vistos la clase del 13-06

    public Sala(String id, String nombre, String tamano) {
        this.ID = id;
        this.nombre = nombre;
        this.tamano = tamano;
    }

    public String getId() { return ID; }
    public String getNombre() { return nombre; }
    public String getTamano() { return tamano; }

    @Override
    public String toString() {
        return String.format("ID: %s | Nombre: %s | Tamaño: %s", ID, nombre, tamano);
    }
}
