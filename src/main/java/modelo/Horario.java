package modelo;

public class Horario {
    private final String dia;
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

    public boolean conflictuaCon(Horario otro) {
        if (otro == null) return false;
        return this.dia.equalsIgnoreCase(otro.dia) && this.bloque == otro.bloque;
    }

    @Override
    public String toString() {
        return String.format("Día: %s, Bloque: %s", dia, bloque);
    }
}
//a