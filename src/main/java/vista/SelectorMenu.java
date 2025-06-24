package vista;

import controlador.ProfesorControlador;
import controlador.AsignacionControlador;

public class SelectorMenu {
    private final ProfesorControlador profesorControlador = new ProfesorControlador();
    private final AsignacionControlador asignacionControlador = new AsignacionControlador();

    public void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> profesorControlador.registrarProfesor();
            case 2 -> asignacionControlador.asignarSala();
            case 3 -> asignacionControlador.verDisponibilidad();
            case 4 -> asignacionControlador.modificarAsignacion();
            case 5 -> asignacionControlador.verAsignaciones();
            case 6 -> System.out.println("Saliendo del sistema...");
            default -> System.out.println("Opción inválida. Intente de nuevo.");
        }
    }
}
//a