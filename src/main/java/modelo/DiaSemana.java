package modelo;

public enum DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO;
    public static DiaSemana fromString(String text) {
        for (DiaSemana d : DiaSemana.values()) {
            if (d.name().equalsIgnoreCase(text)) {
                return d;
            }
        }
        throw new IllegalArgumentException("No hay día de la semana con el nombre: " + text);
    }
}