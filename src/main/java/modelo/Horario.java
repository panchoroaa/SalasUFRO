package modelo;

import java.util.Objects;
import java.util.Optional; // Importar Optional

public class Horario {
    private DiaSemana dia;
    private BloqueHorario bloqueEnum;

    public Horario(DiaSemana dia, BloqueHorario bloqueEnum) {
        this.dia = dia;
        this.bloqueEnum = bloqueEnum;
    }

    public Horario() {
    }

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

    // Mantener estos si los usas para alguna lógica que requiera el int del bloque
    public int getBloque() {
        return bloqueEnum != null ? bloqueEnum.getNumeroBloque() : 0;
    }

    public void setBloque(int bloque) {
        this.bloqueEnum = BloqueHorario.fromNumeroBloque(bloque).orElse(null);
    }

    // --- Métodos para serialización/deserialización como clave de String ---
    public String toKeyString() {
        if (dia == null || bloqueEnum == null) {
            // Manejar caso donde el horario no está completamente inicializado
            return null; // O lanzar una excepción, dependiendo de tu lógica de negocio
        }
        return this.dia.name() + "_" + this.bloqueEnum.name();
    }

    public static Horario fromKeyString(String keyString) {
        if (keyString == null || keyString.isEmpty() || !keyString.contains("_")) {
            throw new IllegalArgumentException("Formato de clave de horario inválido: " + keyString);
        }
        String[] parts = keyString.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de clave de horario inválido (esperado 'DIA_BLOQUE'): " + keyString);
        }
        try {
            DiaSemana parsedDia = DiaSemana.valueOf(parts[0]);
            BloqueHorario parsedBloque = BloqueHorario.valueOf(parts[1]);
            return new Horario(parsedDia, parsedBloque);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valores de DiaSemana o BloqueHorario inválidos en la clave: " + keyString, e);
        }
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