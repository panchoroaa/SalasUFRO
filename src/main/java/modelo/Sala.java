// modelo/Sala.java
package modelo;

import java.util.Objects;

public class Sala {
    private final String nombre;
    private final int capacidad;

    public Sala(String nombre, int capacidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sala no puede estar vacío");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.nombre = nombre.trim();
        this.capacidad = capacidad;
    }

    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }

    @Override
    public String toString() {
        return String.format("Sala {nombre='%s', capacidad=%d}", getNombre(), getCapacidad());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sala otra = (Sala) o;
        return nombre.equalsIgnoreCase(otra.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
}