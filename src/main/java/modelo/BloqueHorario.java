package modelo;

import java.util.Arrays;
import java.util.Optional;

public enum BloqueHorario {
    BLOQUE_1(1, "08:30", "09:30"),
    BLOQUE_2(2, "09:40", "10:40"),
    BLOQUE_3(3, "10:50", "11:50"),
    BLOQUE_4(4, "12:00", "13:00"),
    BLOQUE_5(5, "14:30", "15:30"),
    BLOQUE_6(6, "15:40", "16:40"),
    BLOQUE_7(7, "16:50", "17:50"),
    BLOQUE_8(8, "18:00", "19:00"),
    BLOQUE_9(9, "19:10", "20:10");

    private final int numeroBloque;
    private final String horaInicio;
    private final String horaFin;

    BloqueHorario(int numeroBloque, String horaInicio, String horaFin) {
        this.numeroBloque = numeroBloque;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getNumeroBloque() {
        return numeroBloque;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    @Override
    public String toString() {
        return "Bloque " + numeroBloque + " (" + horaInicio + " - " + horaFin + ")";
    }

    public static Optional<BloqueHorario> fromNumeroBloque(int numero) {
        return Arrays.stream(BloqueHorario.values())
                .filter(b -> b.numeroBloque == numero)
                .findFirst();
    }
}