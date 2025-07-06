// controlador/AsignacionControlador.java
package controlador;

import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.GestionSala;
import modelo.Horario;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AsignacionControlador {
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final JsonDataManager jsonDataManager;
    private List<Reserva> reservas;

    public AsignacionControlador(ProfesorControlador profesorControlador, SalaControlador salaControlador) {
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.jsonDataManager = new JsonDataManager();
        this.reservas = jsonDataManager.cargarReservas(
                profesorControlador.getProfesoresRegistrados(),
                salaControlador.getGestionesSalasRegistradas()
        );
        reAsociarGestionesSalasEnReservas();
    }

    private void reAsociarGestionesSalasEnReservas() {
        for (Reserva reserva : reservas) {
            GestionSala gestionSala = salaControlador.getGestionSalaPara(reserva.getSala());
            if (gestionSala != null) {
                reserva.setGestionSalaAsociada(gestionSala);
            } else {
                System.err.println("Advertencia: No se encontró GestionSala en memoria para la sala " + reserva.getSala().getNombre() + " de la reserva cargada.");
            }
        }
    }

    public String realizarAsignacion(Profesor profesor, Sala salaInformativa, Asignatura asignatura, String dia, BloqueHorario bloque) {
        Horario horario = new Horario(dia, bloque);
        GestionSala gestionSala = salaControlador.getGestionSalaPara(salaInformativa);
        if (gestionSala == null) {
            return "Error interno: No se encontró la gestión de sala en memoria para la sala seleccionada.";
        }

        try {
            gestionSala.agregarHorarioOcupado(horario);
            Reserva nuevaReserva = new Reserva(profesor, salaInformativa, asignatura, horario, gestionSala);
            reservas.add(nuevaReserva);

            jsonDataManager.guardarReservas(reservas);
            salaControlador.guardarTodasLasGestionesSalasEnArchivo();
            return "\n¡Reserva realizada exitosamente!\n" + nuevaReserva.toString();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "Error al procesar la asignación: " + e.getMessage();
        }
    }

    public void listarAsignaciones() {
        if (reservas.isEmpty()) {
            System.out.println("No hay asignaciones de salas registradas.");
            return;
        }
        System.out.println("\n=== Listado de Asignaciones de Salas ===");
        for (int i = 0; i < reservas.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, reservas.get(i).toString());
        }
    }

    public List<String> getReservasParaUI() {
        return reservas.stream()
                .map(Reserva::toString)
                .collect(Collectors.toList());
    }

    public String cancelarAsignacion(int index) {
        if (index < 0 || index >= reservas.size()) {
            return "Índice de asignación inválido.";
        }
        Reserva reservaACancelar = reservas.remove(index);
        reservaACancelar.cancelar();
        jsonDataManager.guardarReservas(reservas);
        salaControlador.guardarTodasLasGestionesSalasEnArchivo();
        return "Asignación cancelada exitosamente: " + reservaACancelar.toString();
    }
}