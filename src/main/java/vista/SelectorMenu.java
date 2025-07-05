package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;

public class SelectorMenu {
    // Estas no deben ser 'final' si las vas a asignar en el constructor
    private ProfesorControlador profesorControlador;
    private SalaControlador salaControlador;
    private AsignacionControlador asignacionControlador;

    // Constructor que recibe las instancias de los controladores
    public SelectorMenu(ProfesorControlador profesorControlador, SalaControlador salaControlador, AsignacionControlador asignacionControlador) {
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.asignacionControlador = asignacionControlador;
    }

    public void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> profesorControlador.registrarProfesor();
                case 2 -> salaControlador.registrarSala();
                case 3 -> asignacionControlador.asignarSalaAProfesor();
                case 4 -> asignacionControlador.cancelarAsignacion(); // Como lo tienes ahora
                case 5 -> asignacionControlador.verAsignaciones();
                case 6 -> System.out.println("¡Gracias por usar el sistema!"); // Esto es manejado por MenuSalas, aquí no debería haber un "salir"
                default -> {
                    // La validación de opciones inválidas es mejor manejarla en MenuSalas para controlar el bucle
                    // Pero si llega aquí, significa que MenuSalas ya lo validó o es una opción fuera de rango.
                    // Tu lógica de `if (opcion != -1)` es un poco redundante si MenuSalas ya devuelve -1 para errores.
                    System.out.println("Opción inválida. Por favor, intente de nuevo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
            e.printStackTrace(); // Para depuración, muestra la traza completa del error
        }
    }
}