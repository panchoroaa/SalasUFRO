package Modelo;

/**Esta es la clase que guardaría las reservas de salas.
 * TODO: Crear una clase que se encargue de "Apartar" las salas que se obtienen de la reserva,
 * es decir, que sea capaz de "bloquear" la sala para que no sea reservada en el mismo horario
 * por distintas materias.
*/

public class Reserva {
    private final Sala sala;
    private final String materia;
    private final int dia;
    private final String hora; //Aplicación de los conceptos de Herencia vistos la clase del 13-06

    public Reserva(Sala sala, String materia, int dia, String hora) { //Constructor para Sala
        this.sala = sala;
        this.materia = materia;
        this.dia = dia;
        this.hora = hora;
    }

    public Sala getSala() { return sala; }
    public String getMateria() { return materia; }
    public int getDia() { return dia; }
    public String getHora() { return hora; }

    @Override
    public String toString() {
        return String.format("Reserva -> Sala: %s, Materia: %s , Día: %d, Hora: %s", sala.getNombre(), materia , dia, hora);
    }
}