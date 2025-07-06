package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Horario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dia;
    private BloqueHorario bloque;

    public Horario(String dia, BloqueHorario bloque) {
        this.dia = dia;
        this.bloque = bloque;
    }

    public Horario() {} // Constructor para Jackson

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) { // Setter para Jackson
        this.dia = dia;
    }

    public BloqueHorario getBloque() {
        return bloque;
    }

    public void setBloque(BloqueHorario bloque) { // Setter para Jackson
        this.bloque = bloque;
    }

    @Override
    public String toString() {
        return dia + " " + bloque.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horario horario = (Horario) o;
        return dia.equalsIgnoreCase(horario.dia) && bloque == horario.bloque;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dia, bloque);
    }
}