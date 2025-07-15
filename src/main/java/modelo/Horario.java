package modelo;

import java.util.Objects;


public class Horario {
    private DiaSemana dia;
    private BloqueHorario bloqueEnum;
    public Horario(DiaSemana dia, BloqueHorario bloqueEnum) {
        this.dia = dia;
        this.bloqueEnum = bloqueEnum;
    }

    public Horario() {
    }

    // --- Getters y Setters ---
    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public BloqueHorario getBloqueEnum() {
        return bloqueEnum;
    }

    public void setBloqueEnum(BloqueHorario bloqueEnum) {
        this.bloqueEnum = bloqueEnum;
    }

    public int getBloque() {
        return bloqueEnum != null ? bloqueEnum.getNumeroBloque() : 0;
    }

    public void setBloque(int bloque) {
        this.bloqueEnum = BloqueHorario.fromNumeroBloque(bloque).orElse(null);
    }


    @Override
    public String toString() {
        return String.format("%s %s", dia.toString(), bloqueEnum != null ? bloqueEnum.toString() : "Bloque N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horario horario = (Horario) o;
        return Objects.equals(dia, horario.dia) &&
                Objects.equals(bloqueEnum, horario.bloqueEnum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dia, bloqueEnum);
    }
}