package modelo;

import java.util.Objects; // Necesario para Objects.hash

public class Horario {
    private final String dia; // Más adelante, podríamos cambiar a un enum DiaSemana
    private final BloqueHorario bloque;

    public Horario(String dia, BloqueHorario bloque) {
        if (dia == null || dia.isBlank()) {
            throw new IllegalArgumentException("El día no puede estar vacío.");
        }
        if (bloque == null) {
            throw new IllegalArgumentException("Debe seleccionar un bloque horario válido.");
        }

        this.dia = dia.trim();
        this.bloque = bloque;
    }

    public String getDia() {
        return dia;
    }

    public BloqueHorario getBloque() {
        return bloque;
    }

    // Este método ya lo tenías y está correcto.
    public boolean conflictuaCon(Horario otro) {
        if (otro == null) return false;
        // Compara el día (ignorando caso) y si el bloque horario es el mismo objeto (enum)
        return this.dia.equalsIgnoreCase(otro.dia) && this.bloque == otro.bloque;
    }

    @Override
    public String toString() {
        return String.format("Día: %s, Bloque: %s", dia, bloque);
    }

    // --- Métodos equals() y hashCode() ---
    // Dos objetos Horario son iguales si tienen el mismo día y el mismo bloque.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horario horario = (Horario) o;
        // La comparación de día debe ser insensible a mayúsculas/minúsculas.
        // La comparación de bloque para enums es con '=='
        return dia.equalsIgnoreCase(horario.dia) && bloque == horario.bloque;
    }

    @Override
    public int hashCode() {
        // Usa Objects.hash para combinar los hashes de los atributos
        return Objects.hash(dia.toLowerCase(), bloque);
    }
}