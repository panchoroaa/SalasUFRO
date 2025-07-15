package vista;

import controlador.AsignacionControlador;
import modelo.Asignatura;

import java.util.List;
import java.util.Optional;

public class AsignaturaView {
    private final InputOutputHelper io;
    private final AsignacionControlador controlador;

    public AsignaturaView(InputOutputHelper io, AsignacionControlador controlador) {
        this.io = io;
        this.controlador = controlador;
    }

    public void mostrarTodasAsignaturas() {
        io.mostrarMensajeExito("--- Todas las Asignaturas ---");
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas.");
            return;
        }
        asignaturas.forEach(System.out::println);
    }

    public void buscarAsignatura() {
        io.mostrarMensajeExito("--- Buscar Asignatura ---");
        String query = io.solicitarTexto("Ingrese nombre o código de la asignatura a buscar (0 para cancelar): ");
        if (query.equals("0")) { io.mostrarCancelacionOperacion("búsqueda de asignatura"); return; }

        List<Asignatura> resultados = controlador.buscarAsignaturas(query);
        io.mostrarResultadosBusqueda(resultados, "asignatura"); // Usar el método de InputOutputHelper
    }

    public Optional<Asignatura> seleccionarAsignatura() {
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        Integer indiceSeleccionado = io.seleccionarIndiceDeLista(asignaturas, "asignatura");
        if (indiceSeleccionado != null) {
            return Optional.of(asignaturas.get(indiceSeleccionado - 1));
        }
        return Optional.empty();
    }
}