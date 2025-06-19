package vista;

import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.AsignacionControlador;

public class SelectorMenu {
    private final ProfesorControlador profesorControlador = new ProfesorControlador();
    private final SalaControlador salaControlador = new SalaControlador();
    private final AsignacionControlador asignacionControlador = new AsignacionControlador();

    public void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> profesorControlador.registrarProfesor();
            case 2 -> salaControlador.registrarSala();
            case 3 -> asignacionControlador.asignarSala();
            case 4 -> asignacionControlador.verDisponibilidad();
            case 5 -> asignacionControlador.modificarAsignacion();
            case 6 -> asignacionControlador.verAsignaciones();
            case 7 -> System.out.println("Saliendo del sistema...");
            default -> System.out.println("Opción inválida. Intente de nuevo.");
        }
    }
}
