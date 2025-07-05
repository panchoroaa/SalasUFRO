package vista;

import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.AsignacionControlador;

public class SelectorMenu {
    private final ProfesorControlador profesorControlador = new ProfesorControlador();
    private final SalaControlador salaControlador = new SalaControlador();
    private final AsignacionControlador asignacionControlador = new AsignacionControlador();

    public void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> profesorControlador.registrarProfesor();
                case 2 -> salaControlador.registrarSala();
                case 3 -> asignacionControlador.asignarSalaAProfesor();
                case 4 -> asignacionControlador.cancelarAsignacion();
                case 5 -> asignacionControlador.verAsignaciones();
                case 6 -> System.out.println("¡Gracias por usar el sistema!");
                default -> {
                    if (opcion != -1) {
                        System.out.println("Opción inválida. Por favor, intente de nuevo.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
        }
    }
}