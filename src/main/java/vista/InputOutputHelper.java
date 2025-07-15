package vista;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class InputOutputHelper {
    private final Scanner scanner;

    public InputOutputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public int leerOpcion() {
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine();
            return opcion;
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número.");
            scanner.nextLine();
            return -1;
        }
    }

    public <T> Integer seleccionarIndiceDeLista(List<T> lista, String tipoElemento) {
        if (lista.isEmpty()) {
            System.out.println("No hay " + tipoElemento + "s disponibles para seleccionar.");
            return null;
        }
        System.out.println("Seleccione un " + tipoElemento + ":");
        AtomicInteger index = new AtomicInteger(1);
        lista.forEach(item -> System.out.println(index.getAndIncrement() + ". " + item));
        System.out.println("0. Cancelar");

        int opcion = leerOpcion();
        if (opcion > 0 && opcion <= lista.size()) {
            return opcion;
        }
        return null;
    }

    public void mostrarMensajeExito(String mensaje) {
        System.out.println("\n¡ÉXITO! " + mensaje);
    }

    public void mostrarMensajeError(String mensaje) {
        System.err.println("\nERROR: " + mensaje);
    }

    public void mostrarCancelacionOperacion(String operacion) {
        System.out.println("Operación de " + operacion + " cancelada.");
    }

    public String solicitarTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Muestra una lista de resultados de búsqueda para un tipo de elemento dado.
     * @param <T> El tipo de los elementos en la lista.
     * @param resultados La lista de elementos encontrados.
     * @param tipoElemento Una cadena que describe el tipo de elemento (ej. "profesor", "sala").
     */
    public <T> void mostrarResultadosBusqueda(List<T> resultados, String tipoElemento) {
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron " + tipoElemento + "es que coincidan con la búsqueda.");
            return;
        }
        System.out.println("\n--- Resultados de la búsqueda de " + tipoElemento + "es ---");
        resultados.forEach(System.out::println);
    }
}