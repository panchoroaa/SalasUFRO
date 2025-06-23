package modelo;

public enum
BloqueHorario {
    BLOQUE_1("08:30", "09:30"),
    BLOQUE_2("09:40", "10:40"),
    BLOQUE_3("10:50", "11:50"),
    BLOQUE_4("12:00", "13:00"),
    BLOQUE_5("14:30", "15:30"),
    BLOQUE_6("15:40", "16:40"),
    BLOQUE_7("16:50", "17:50"),
    BLOQUE_8("18:00", "19:00"),
    BLOQUE_9("19:10", "20:10");

    private final String horaInicio;
    private final String horaFin;

    BloqueHorario(String horaInicio, String horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    @Override
    public String toString() {
        return horaInicio + " - " + horaFin;
    }
}
//a