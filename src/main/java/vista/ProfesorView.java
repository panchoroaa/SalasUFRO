package vista;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;
import java.util.List;
import java.util.Optional;

public class ProfesorView {
    private final InputOutputHelper io;
    private final AsignacionControlador controlador;

    public ProfesorView(InputOutputHelper io, AsignacionControlador controlador) {
        this.io = io;
        this.controlador = controlador;
    }

    public void mostrarTodosProfesores() {
        io.mostrarMensajeExito("--- Todos los Profesores ---");
        List<Profesor> profesores = controlador.getProfesores();
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }
        profesores.forEach(System.out::println);
    }

    public void verAsignaturasImpartidasPorProfesor() {
        io.mostrarMensajeExito("--- Asignaturas Impartidas por Profesor ---");
        Optional<Profesor> profesorOpt = seleccionarProfesor();
        if (profesorOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("ver asignaturas de profesor");
            return;
        }
        Profesor profesor = profesorOpt.get();
        List<Asignatura> asignaturasImpartidas = profesor.getAsignaturasImpartidas();

        if (asignaturasImpartidas.isEmpty()) {
            System.out.println("El profesor " + profesor.getNombre() + " no tiene asignaturas registradas para impartir.");
        } else {
            System.out.println("\nAsignaturas impartidas por " + profesor.getNombre() + ":");
            asignaturasImpartidas.forEach(System.out::println);
        }
    }

    public void verHorarioDeUnProfesor() {
        io.mostrarMensajeExito("--- Ver Horario de un Profesor ---");
        Optional<Profesor> profesorOpt = seleccionarProfesor();
        if (profesorOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("ver horario de profesor");
            return;
        }
        Profesor profesor = profesorOpt.get();
        List<Reserva> reservasProfesor = controlador.getReservasPorProfesor(profesor.getRut());
        if (reservasProfesor.isEmpty()) {
            System.out.println("El profesor " + profesor.getNombre() + " no tiene asignaciones registradas.");
            return;
        }

        System.out.println("\nHorario del profesor " + profesor.getNombre() + ":");
        reservasProfesor.forEach(r -> {
            Optional<Sala> s = controlador.getSalaPorNombre(r.getNombreSala());
            Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());
            System.out.printf("  - %s: Sala: %s, Asignatura: %s%n",
                    r.getHorario(),
                    s.map(Sala::getNombre).orElse("Desconocida"),
                    a.map(Asignatura::getNombre).orElse("Desconocida"));
        });
    }

    public void buscarProfesor() {
        io.mostrarMensajeExito("--- Buscar Profesor ---");
        String query = io.solicitarTexto("Ingrese nombre o RUT del profesor a buscar (0 para cancelar): ");
        if (query.equals("0")) { io.mostrarCancelacionOperacion("búsqueda de profesor"); return; }

        List<Profesor> resultados = controlador.buscarProfesores(query);
        io.mostrarResultadosBusqueda(resultados, "profesor");
    }

    public Optional<Profesor> seleccionarProfesor() {
        List<Profesor> profesores = controlador.getProfesores();
        Integer indiceSeleccionado = io.seleccionarIndiceDeLista(profesores, "profesor");
        if (indiceSeleccionado != null) {
            return Optional.of(profesores.get(indiceSeleccionado - 1));
        }
        return Optional.empty();
    }
}