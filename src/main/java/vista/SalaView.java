package vista;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.Profesor;
import modelo.Sala;
import modelo.Reserva;

import java.util.List;
import java.util.Optional;

public class SalaView {
    private final InputOutputHelper io;
    private final AsignacionControlador controlador;

    public SalaView(InputOutputHelper io, AsignacionControlador controlador) {
        this.io = io;
        this.controlador = controlador;
    }

    public void mostrarTodasSalas() {
        io.mostrarMensajeExito("--- Todas las Salas ---");
        List<Sala> salas = controlador.getSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }
        salas.forEach(System.out::println);
    }

    public void verHorarioDeUnaSala() {
        io.mostrarMensajeExito("--- Ver Horario de una Sala ---");
        Optional<Sala> salaOpt = seleccionarSala();
        if (salaOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("ver horario de sala");
            return;
        }
        Sala sala = salaOpt.get();
        List<Reserva> reservasSala = controlador.getReservasPorSala(sala.getNombre());
        if (reservasSala.isEmpty()) {
            System.out.println("La sala " + sala.getNombre() + " no tiene asignaciones registradas.");
            return;
        }

        System.out.println("\nHorario de la sala " + sala.getNombre() + ":");
        reservasSala.forEach(r -> {
            Optional<Profesor> p = controlador.getProfesorPorRut(r.getRutProfesor());
            Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());
            System.out.printf("  - %s: Profesor: %s, Asignatura: %s%n",
                    r.getHorario(),
                    p.map(Profesor::getNombre).orElse("Desconocido"),
                    a.map(Asignatura::getNombre).orElse("Desconocida"));
        });
    }

    public void buscarSala() {
        io.mostrarMensajeExito("--- Buscar Sala ---");
        String query = io.solicitarTexto("Ingrese nombre de la sala a buscar (0 para cancelar): ");
        if (query.equals("0")) { io.mostrarCancelacionOperacion("búsqueda de sala"); return; }

        List<Sala> resultados = controlador.buscarSalas(query);
        io.mostrarResultadosBusqueda(resultados, "sala"); // Usar el método de InputOutputHelper
    }

    public Optional<Sala> seleccionarSala() {
        List<Sala> salas = controlador.getSalas();
        Integer indiceSeleccionado = io.seleccionarIndiceDeLista(salas, "sala");
        if (indiceSeleccionado != null) {
            return Optional.of(salas.get(indiceSeleccionado - 1));
        }
        return Optional.empty();
    }
}