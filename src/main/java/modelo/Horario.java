package modelo;

import java.util.Objects;
import java.util.Optional;
import java.util.Arrays;

public class Horario {
    private DiaSemana dia;
    private int bloque;

    public Horario(DiaSemana dia, int bloque) {
        this.dia = dia;
        this.bloque = bloque;
    }

    public Horario() {
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public int getBloque() {
        return bloque;
    }

    public void setBloque(int bloque) {
        this.bloque = bloque;
    }

    @Override
    public String toString() {
        Optional<BloqueHorario> bloqueEnum = Arrays.stream(BloqueHorario.values())
                .filter(b -> (b.ordinal() + 1) == this.bloque)
                .findFirst();

        String descripcionBloque = bloqueEnum.map(BloqueHorario::toString)
                .orElse("Bloque " + this.bloque + " (Horario no definido)");

        return String.format("%s %s", dia.toString(), descripcionBloque);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horario horario = (Horario) o;
        return bloque == horario.bloque && dia == horario.dia;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dia, bloque);
    }
}