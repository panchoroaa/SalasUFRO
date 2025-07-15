package controlador;

import modelo.Asignatura;
import persistencia.JsonDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Comparator;

public class AsignaturaControlador {
    private List<Asignatura> asignaturas;
    private final JsonDataManager dataManager;
    private final Scanner scanner;

    public AsignaturaControlador(JsonDataManager dataManager, Scanner scanner) {
        this.dataManager = dataManager;
        this.asignaturas = dataManager.cargarAsignaturas();
        this.scanner = scanner;
    }

    public List<Asignatura> getAsignaturas() {
        return new ArrayList<>(asignaturas);
    }

    public Optional<Asignatura> getAsignaturaPorCodigo(String codigo) {
        return asignaturas.stream().filter(a -> a.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public void crearAsignatura() {
        System.out.println("\n--- Crear Nueva Asignatura ---");
        System.out.print("Ingrese código de la asignatura: ");
        String codigo = scanner.nextLine().trim();

        if (getAsignaturaPorCodigo(codigo).isPresent()) {
            System.out.println("ERROR: Ya existe una asignatura con el código " + codigo + ".");
            return;
        }

        System.out.print("Ingrese nombre de la asignatura: ");
        String nombre = scanner.nextLine().trim();

        int cantidadAlumnos;
        while (true) {
            try {
                System.out.print("Ingrese cantidad de alumnos: ");
                cantidadAlumnos = scanner.nextInt();
                if (cantidadAlumnos <= 0) {
                    System.out.println("La cantidad de alumnos debe ser un número positivo.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
        scanner.nextLine(); // Consume newline

        Asignatura nuevaAsignatura = new Asignatura(nombre, codigo, cantidadAlumnos);
        asignaturas.add(nuevaAsignatura);
        dataManager.guardarAsignaturas(asignaturas);
        System.out.println("ÉXITO: Asignatura " + nombre + " (" + codigo + ") creada exitosamente.");
    }

    public void listarAsignaturas() {
        System.out.println("\n--- Listado de Asignaturas ---");
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas.");
            return;
        }
        asignaturas.stream()
                .sorted(Comparator.comparing(Asignatura::getNombre))
                .forEach(System.out::println);
    }

    public void actualizarAsignatura() {
        System.out.println("\n--- Actualizar Asignatura ---");
        System.out.print("Ingrese código de la asignatura a actualizar (0 para cancelar): ");
        String codigo = scanner.nextLine().trim();
        if (codigo.equals("0")) {
            System.out.println("Operación cancelada.");
            return;
        }

        Optional<Asignatura> optionalAsignatura = getAsignaturaPorCodigo(codigo);
        if (optionalAsignatura.isPresent()) {
            Asignatura asignaturaAActualizar = optionalAsignatura.get();
            System.out.println("Asignatura actual: " + asignaturaAActualizar);

            System.out.print("Ingrese nuevo nombre (dejar en blanco para no cambiar): ");
            String nuevoNombre = scanner.nextLine().trim();
            if (!nuevoNombre.isEmpty()) {
                asignaturaAActualizar.setNombre(nuevoNombre);
            }

            System.out.print("Ingrese nueva cantidad de alumnos (0 para no cambiar): ");
            String cantidadAlumnosStr = scanner.nextLine().trim();
            if (!cantidadAlumnosStr.isEmpty() && !cantidadAlumnosStr.equals("0")) {
                try {
                    int nuevaCantidad = Integer.parseInt(cantidadAlumnosStr);
                    if (nuevaCantidad > 0) {
                        asignaturaAActualizar.setCantidadAlumnos(nuevaCantidad);
                    } else {
                        System.out.println("Cantidad de alumnos debe ser positiva. No se cambió.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida para cantidad de alumnos. No se cambió.");
                }
            }

            dataManager.guardarAsignaturas(asignaturas);
            System.out.println("ÉXITO: Asignatura actualizada exitosamente.");
        } else {
            System.out.println("ERROR: Asignatura con código " + codigo + " no encontrada.");
        }
    }

    public void eliminarAsignatura() {
        System.out.println("\n--- Eliminar Asignatura ---");
        System.out.print("Ingrese código de la asignatura a eliminar (0 para cancelar): ");
        String codigo = scanner.nextLine().trim();
        if (codigo.equals("0")) {
            System.out.println("Operación cancelada.");
            return;
        }

        // IMPORTANT: Check for active reservations before deleting
        boolean removido = asignaturas.removeIf(a -> a.getCodigo().equalsIgnoreCase(codigo));

        if (removido) {
            dataManager.guardarAsignaturas(asignaturas);
            System.out.println("ÉXITO: Asignatura " + codigo + " eliminada exitosamente.");
        } else {
            System.out.println("ERROR: Asignatura con código " + codigo + " no encontrada.");
        }
    }
}