package controlador;

import modelo.Horario;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;

import java.util.List;
import java.util.Optional;

public class CheckReserva {

    private final List<Reserva> reservas;
    private final List<Sala> salas; // Needed to check sala availability state
    private final List<Profesor> profesores; // Needed for prof conflict by RUT

    public CheckReserva(List<Reserva> reservas, List<Sala> salas, List<Profesor> profesores) {
        this.reservas = reservas;
        this.salas = salas;
        this.profesores = profesores;
    }

    /**
     * Verifica si una sala específica está disponible en un horario dado.
     * @param nombreSala Nombre de la sala a verificar.
     * @param horario Horario a verificar.
     * @return true si la sala está disponible, false en caso contrario.
     */
    public boolean isSalaDisponible(String nombreSala, Horario horario) {
        Optional<Sala> salaOptional = salas.stream()
                .filter(s -> s.getNombre().equalsIgnoreCase(nombreSala))
                .findFirst();

        if (salaOptional.isEmpty()) {
            // This should ideally not happen if selection is from existing list
            System.err.println("Advertencia: Sala " + nombreSala + " no encontrada en el sistema.");
            return false;
        }

        Sala sala = salaOptional.get();
        // Check if sala's general state is available and if the specific horario is not occupied
        return sala.estaDisponibleEn(horario);
    }

    /**
     * Verifica si un profesor está disponible en un horario dado.
     * @param rutProfesor RUT del profesor a verificar.
     * @param horario Horario a verificar.
     * @return true si el profesor está disponible, false en caso contrario.
     */
    public boolean isProfesorDisponible(String rutProfesor, Horario horario) {
        // Check if any existing reservation conflicts with this professor and horario
        return reservas.stream()
                .noneMatch(r -> r.getRutProfesor().equalsIgnoreCase(rutProfesor) &&
                        r.getHorario().equals(horario));
    }
}