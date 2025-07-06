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

    // Constructor: No recibe InputHandler
    public AsignacionControlador(ProfesorControlador profesorControlador, SalaControlador salaControlador) {
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.jsonDataManager = new JsonDataManager();
        // Cargar reservas, reasociando con objetos completos de profesor y gestionSala
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
                System.err.println("Advertencia: No se encontró GestionSala para la sala " + reserva.getSala().getNombre() + " de la reserva cargada.");
            }
        }
    }

    // Método para realizar la asignación. Recibe parámetros ya validados.
    public String realizarAsignacion(Profesor profesor, Sala salaInformativa, Asignatura asignatura, String dia, BloqueHorario bloque) {
        Horario horario = new Horario(dia, bloque);

        GestionSala gestionSala = salaControlador.getGestionSalaPara(salaInformativa);
        if (gestionSala == null) {
            return "Error: No se encontró la gestión de sala para la sala seleccionada.";
        }

        if (!gestionSala.estaDisponible(horario)) {
            return "La sala " + salaInformativa.getNombre() + " no está disponible en el horario " + horario + " o está en mantenimiento.";
        }

        if (profesorTieneConflictoHorario(profesor, horario)) {
            return "El profesor " + profesor.getNombre() + " ya tiene una asignación en el horario " + horario + ".";
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

    private boolean profesorTieneConflictoHorario(Profesor profesor, Horario horario) {
        return reservas.stream()
                .filter(r -> r.getProfesor().equals(profesor))
                .anyMatch(r -> r.getHorario().conflictuaCon(horario));
    }

    public void listarAsignaciones() {
        if (reservas.isEmpty()) {
            System.out.println("No hay asignaciones de salas registradas."); // El controlador imprime directamente aquí
            return;
        }
        System.out.println("\n=== Listado de Asignaciones de Salas ===");
        for (int i = 0; i < reservas.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, reservas.get(i).toString());
        }
    }

    // Nuevo método para que SelectorMenu obtenga las reservas como strings
    public List<String> getReservasParaUI() {
        return reservas.stream()
                .map(Reserva::toString)
                .collect(Collectors.toList());
    }

    // Método para cancelar la asignación. Recibe el índice (0-basado)
    public String cancelarAsignacion(int index) {
        if (index < 0 || index >= reservas.size()) {
            return "Índice de asignación inválido.";
        }
        Reserva reservaACancelar = reservas.remove(index);
        reservaACancelar.cancelar(); // Esto llama al método cancelar de Reserva, que actualiza GestionSala
        jsonDataManager.guardarReservas(reservas);
        salaControlador.guardarTodasLasGestionesSalasEnArchivo(); // Persiste la sala con el horario liberado
        return "Asignación cancelada exitosamente: " + reservaACancelar.toString();
    }

    public boolean profesorTieneReservasActivas(Profesor profesor) {
        return reservas.stream().anyMatch(r -> r.getProfesor().equals(profesor));
    }

    public boolean salaTieneReservasActivas(Sala sala) {
        return reservas.stream().anyMatch(r -> r.getSala().equals(sala));
    }
}