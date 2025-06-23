package modelo;

public class Horario {
    private final String dia;
    private final String horaInicio;
    private final String horaFin;

    public Horario(String dia, String horaInicio, String horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getDia() {
        return dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public boolean conflictuaCon(Horario otro) {
        if (!this.dia.equals(otro.dia)) {
            return false;
        }

        int inicio1 = Integer.parseInt(this.horaInicio.replace(":", ""));
        int fin1 = Integer.parseInt(this.horaFin.replace(":", ""));
        int inicio2 = Integer.parseInt(otro.horaInicio.replace(":", ""));
        int fin2 = Integer.parseInt(otro.horaFin.replace(":", ""));

        return (inicio1 < fin2 && fin1 > inicio2);
    }

    @Override
    public String toString() {
        return "Día: " + dia + ", Desde: " + horaInicio + " hasta: " + horaFin;
    }
}
