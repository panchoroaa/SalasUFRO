package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.Profesor;
import modelo.Sala;
import modelo.EstadoSala; // Para setear el estado
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException; // Ya usada, mantener

public class SelectorMenu {
    private final Scanner scanner;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;

    public SelectorMenu(Scanner scanner, ProfesorControlador profesorControlador, SalaControlador salaControlador, AsignacionControlador asignacionControlador) {
        this.scanner = scanner;
        this.profesorControlador = profesorControlador;
        this.salaControlador = salaControlador;
        this.asignacionControlador = asignacionControlador;
    }

    public void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1: menuGestionProfesores(); break;
                case 2: menuGestionSalas(); break;
                case 3: realizarAsignacionFlow(); break; // Método para el flujo completo
                case 4: cancelarAsignacionFlow(); break; // Método para el flujo completo
                case 5: profesorControlador.listarProfesores(); // Mostrará listas de ambos
                    salaControlador.listarSalas();
                    asignacionControlador.listarAsignaciones();
                    System.out.println("\nListado completo.");
                    break;
                default: System.out.println("Opción inválida. Error interno.");
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
    }

    // --- Métodos de Orquestación de Sub-Menús (públicos de SelectorMenu, por así decirlo) ---

    private void menuGestionProfesores() {
        int opcion;
        do {
            mostrarMenuGestionProfesores();
            opcion = obtenerOpcionMenuGestion();
            ejecutarOpcionGestionProfesores(opcion);
            if (opcion != 0) pausarYContinuar();
        } while (opcion != 0);
    }

    private void menuGestionSalas() {
        int opcion;
        do {
            mostrarMenuGestionSalas();
            opcion = obtenerOpcionMenuGestion();
            ejecutarOpcionGestionSalas(opcion);
            if (opcion != 0) pausarYContinuar();
        } while (opcion != 0);
    }

    // --- Métodos Atómicos para Mostrar Menús de Gestión ---
    private void mostrarMenuGestionProfesores() {
        limpiarPantalla();
        System.out.println("\n--- Gestión de Profesores ---");
        System.out.println("1. Registrar Profesor");
        System.out.println("2. Listar Profesores");
        System.out.println("3. Actualizar Profesor");
        System.out.println("4. Eliminar Profesor");
        System.out.println("5. Asignar Asignaturas a Profesor");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Ingrese una opción: ");
    }

    private void mostrarMenuGestionSalas() {
        limpiarPantalla();
        System.out.println("\n--- Gestión de Salas ---");
        System.out.println("1. Registrar Sala");
        System.out.println("2. Listar Salas");
        System.out.println("3. Actualizar Sala");
        System.out.println("4. Eliminar Sala");
        System.out.println("0. Volver al Menú Principal");
        System.out.print("Ingrese una opción: ");
    }

    // --- Métodos Atómicos para Obtener y Validar Opciones de Menú de Gestión ---
    private int obtenerOpcionMenuGestion() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                System.out.print("Ingrese una opción: ");
            }
        }
    }

    // --- Métodos Atómicos para Ejecutar Opciones Específicas de Gestión ---
    private void ejecutarOpcionGestionProfesores(int opcion) {
        switch (opcion) {
            case 1: registrarProfesorFlow(); break;
            case 2: profesorControlador.listarProfesores(); break;
            case 3: actualizarProfesorFlow(); break;
            case 4: eliminarProfesorFlow(); break;
            case 5: asignarAsignaturasAProfesorFlow(); break;
            case 0: System.out.println("Volviendo al menú principal..."); break;
            default: System.out.println("Opción inválida.");
        }
    }

    private void ejecutarOpcionGestionSalas(int opcion) {
        switch (opcion) {
            case 1: registrarSalaFlow(); break;
            case 2: salaControlador.listarSalas(); break;
            case 3: actualizarSalaFlow(); break;
            case 4: eliminarSalaFlow(); break;
            case 0: System.out.println("Volviendo al menú principal..."); break;
            default: System.out.println("Opción inválida.");
        }
    }

    // --- Flujos Completos para Operaciones CRUD y Asignaciones ---

    private void registrarProfesorFlow() {
        System.out.println("\n=== Registro de Profesor ===");
        String nombre = solicitarNombreProfesor();
        if (nombre == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }

        String rut = solicitarRutProfesor();
        if (rut == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }
        if (profesorControlador.buscarProfesorPorRut(rut) != null) {
            System.out.println("Ya existe un profesor con este RUT. Ingrese uno diferente.");
            return;
        }

        String departamento = solicitarDepartamentoProfesor();
        if (departamento == null) { mostrarCancelacionOperacion("Registro de profesor"); return; }

        Profesor nuevoProfesor = profesorControlador.crearProfesor(nombre, rut, departamento);
        if (nuevoProfesor != null) {
            mostrarMensajeExito("Profesor registrado exitosamente:\n" + nuevoProfesor.toString());
        } else {
            mostrarMensajeError("Error al registrar el profesor.");
        }
    }

    private void actualizarProfesorFlow() {
        System.out.println("\n=== Actualizar Profesor ===");
        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        if (profesores.isEmpty()) { mostrarNoHayElementosPara("profesores"); return; }
        profesorControlador.listarProfesores();

        Profesor profesorAActualizar = seleccionarProfesorDeLista(profesores, "actualizar");
        if (profesorAActualizar == null) { mostrarCancelacionOperacion("Actualización de profesor"); return; }

        System.out.println("Profesor actual: " + profesorAActualizar.toString());

        String nuevoNombre = solicitarNuevoNombreProfesor();
        if (nuevoNombre == null) { mostrarCancelacionOperacion("Actualización de profesor"); return; }

        String nuevoDepartamento = solicitarNuevoDepartamentoProfesor();
        if (nuevoDepartamento == null) { mostrarCancelacionOperacion("Actualización de profesor"); return; }

        boolean actualizado = profesorControlador.actualizarProfesor(profesorAActualizar, nuevoNombre, nuevoDepartamento);
        if (actualizado) {
            mostrarMensajeExito("Profesor actualizado exitosamente:\n" + profesorAActualizar.toString());
        } else {
            mostrarMensajeError("No se pudo actualizar el profesor.");
        }
    }

    private void eliminarProfesorFlow() {
        System.out.println("\n=== Eliminar Profesor ===");
        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        if (profesores.isEmpty()) { mostrarNoHayElementosPara("profesores"); return; }
        profesorControlador.listarProfesores();

        String rutAEliminar = solicitarRutProfesorParaEliminar();
        if (rutAEliminar == null) { mostrarCancelacionOperacion("Eliminación de profesor"); return; }

        boolean eliminado = profesorControlador.eliminarProfesor(rutAEliminar);
        if (eliminado) {
            mostrarMensajeExito("Profesor eliminado exitosamente.");
        } else {
            mostrarMensajeError("No se pudo eliminar el profesor (puede tener asignaciones activas o no fue encontrado).");
        }
    }

    private void asignarAsignaturasAProfesorFlow() {
        System.out.println("\n=== Asignar Asignaturas a Profesor ===");
        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        if (profesores.isEmpty()) { mostrarNoHayElementosPara("profesores"); return; }

        Profesor profesor = seleccionarProfesorDeLista(profesores, "asignar asignaturas");
        if (profesor == null) { mostrarCancelacionOperacion("Asignación de asignatura"); return; }

        List<Asignatura> todasLasAsignaturas = profesorControlador.getTodasLasAsignaturasDisponibles();
        if (todasLasAsignaturas.isEmpty()) { System.out.println("No hay asignaturas disponibles para asignar."); return; }

        List<Asignatura> asignaturasDisponiblesParaProfesor = profesorControlador.getAsignaturasDisponiblesParaAsignar(profesor, todasLasAsignaturas);
        if (asignaturasDisponiblesParaProfesor.isEmpty()) {
            System.out.println("El profesor ya tiene todas las asignaturas disponibles o no hay asignaturas nuevas para asignar.");
            return;
        }

        System.out.println("Asignaturas disponibles para " + profesor.getNombre() + ":");
        Asignatura asignaturaSeleccionada = seleccionarAsignaturaDeLista(asignaturasDisponiblesParaProfesor, "asignar");
        if (asignaturaSeleccionada == null) { mostrarCancelacionOperacion("Asignación de asignatura"); return; }

        boolean asignada = profesorControlador.asignarAsignaturaAProfesor(profesor, asignaturaSeleccionada);
        if (asignada) {
            mostrarMensajeExito("Asignatura '" + asignaturaSeleccionada.getNombre() + "' asignada a " + profesor.getNombre() + " exitosamente.");
        } else {
            mostrarMensajeError("No se pudo asignar la asignatura.");
        }
    }

    private void registrarSalaFlow() {
        System.out.println("\n=== Registro de Sala ===");

        String nombre = solicitarNombreSala();
        if (nombre == null) { mostrarCancelacionOperacion("Registro de sala"); return; }
        if (salaControlador.buscarSalaPorNombre(nombre) != null) {
            System.out.println("Ya existe una sala con este nombre. Ingrese uno diferente.");
            return;
        }

        Integer capacidad = solicitarCapacidadSala();
        if (capacidad == null) { mostrarCancelacionOperacion("Registro de sala"); return; }

        Sala nuevaSala = salaControlador.crearSala(nombre, capacidad);
        if (nuevaSala != null) {
            mostrarMensajeExito("Sala registrada exitosamente:\n" + nuevaSala.toString());
        } else {
            mostrarMensajeError("Error al registrar la sala.");
        }
    }

    private void actualizarSalaFlow() {
        System.out.println("\n=== Actualizar Sala ===");
        List<Sala> salas = salaControlador.getSalasRegistradas();
        if (salas.isEmpty()) { mostrarNoHayElementosPara("salas"); return; }
        salaControlador.listarSalas();

        Sala salaAActualizar = seleccionarSalaDeLista(salas, "actualizar");
        if (salaAActualizar == null) { mostrarCancelacionOperacion("Actualización de sala"); return; }

        System.out.println("Sala actual: " + salaAActualizar.toString() + ", Estado: " + salaControlador.getEstadoSala(salaAActualizar));

        EstadoSala nuevoEstado = solicitarEstadoSala();
        if (nuevoEstado == null) { mostrarCancelacionOperacion("Actualización de sala"); return; }

        boolean actualizado = salaControlador.actualizarEstadoSala(salaAActualizar, nuevoEstado);
        if (actualizado) {
            mostrarMensajeExito("Sala actualizada exitosamente:\n" + salaAActualizar.toString() + ", Estado: " + nuevoEstado);
        } else {
            mostrarMensajeError("No se pudo actualizar la sala.");
        }
    }

    private void eliminarSalaFlow() {
        System.out.println("\n=== Eliminar Sala ===");
        List<Sala> salas = salaControlador.getSalasRegistradas();
        if (salas.isEmpty()) { mostrarNoHayElementosPara("salas"); return; }
        salaControlador.listarSalas();

        String nombreAEliminar = solicitarNombreSalaParaEliminar();
        if (nombreAEliminar == null) { mostrarCancelacionOperacion("Eliminación de sala"); return; }

        boolean eliminado = salaControlador.eliminarSala(nombreAEliminar);
        if (eliminado) {
            mostrarMensajeExito("Sala eliminada exitosamente.");
        } else {
            mostrarMensajeError("No se pudo eliminar la sala (puede tener asignaciones activas o no fue encontrada).");
        }
    }

    private void realizarAsignacionFlow() {
        System.out.println("\n=== Realizar Asignación de Sala ===");

        List<Profesor> profesores = profesorControlador.getProfesoresRegistrados();
        if (profesores.isEmpty()) { mostrarNoHayElementosPara("profesores"); return; }
        Profesor profesor = seleccionarProfesorDeLista(profesores, "asignar sala");
        if (profesor == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        List<Asignatura> asignaturasProfesor = profesorControlador.getAsignaturasImpartidasPorProfesor(profesor);
        if (asignaturasProfesor.isEmpty()) { System.out.println("El profesor seleccionado no imparte asignaturas. No se puede realizar la asignación."); return; }
        Asignatura asignatura = seleccionarAsignaturaDeLista(asignaturasProfesor, "asignar sala");
        if (asignatura == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        List<Sala> salas = salaControlador.getSalasRegistradas();
        if (salas.isEmpty()) { mostrarNoHayElementosPara("salas"); return; }

        List<Sala> salasConCapacidad = salas.stream()
                .filter(s -> s.getCapacidad() >= asignatura.getCantidadAlumnos())
                .toList();
        if (salasConCapacidad.isEmpty()) { System.out.println("No hay salas disponibles con capacidad suficiente para " + asignatura.getCantidadAlumnos() + " alumnos."); return; }
        Sala sala = seleccionarSalaDeLista(salasConCapacidad, "asignar");
        if (sala == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        String dia = solicitarDiaSemana();
        if (dia == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        BloqueHorario bloque = solicitarBloqueHorario();
        if (bloque == null) { mostrarCancelacionOperacion("Asignación de sala"); return; }

        String resultado = asignacionControlador.realizarAsignacion(profesor, sala, asignatura, dia, bloque);
        System.out.println(resultado); // El controlador devuelve el mensaje de resultado
    }

    private void cancelarAsignacionFlow() {
        System.out.println("\n=== Cancelar Asignación ===");
        asignacionControlador.listarAsignaciones();

        List<String> reservasStr = asignacionControlador.getReservasParaUI();
        if (reservasStr.isEmpty()) { System.out.println("No hay asignaciones para cancelar."); return; }

        for (int i = 0; i < reservasStr.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, reservasStr.get(i));
        }

        Integer indiceReserva = solicitarIndiceReserva(reservasStr.size());
        if (indiceReserva == null) { mostrarCancelacionOperacion("Cancelación de asignación"); return; }

        String resultado = asignacionControlador.cancelarAsignacion(indiceReserva - 1); // Pasa el índice 0-basado
        System.out.println(resultado);
    }

    // --- Métodos Atómicos para Solicitar y Validar Datos Específicos ---

    private String solicitarNombreProfesor() {
        return getValidatedNonEmptyString("Nombre del Profesor (0 para cancelar): ");
    }

    private String solicitarRutProfesor() {
        return getValidatedRut("RUT del Profesor (0 para cancelar): ");
    }

    private String solicitarDepartamentoProfesor() {
        return getValidatedNonEmptyString("Departamento (0 para cancelar): ");
    }

    private String solicitarNuevoNombreProfesor() {
        return getOptionalNonEmptyString("Nuevo Nombre del Profesor (deje vacío para no cambiar, 0 para cancelar): ");
    }

    private String solicitarNuevoDepartamentoProfesor() {
        return getOptionalNonEmptyString("Nuevo Departamento (deje vacío para no cambiar, 0 para cancelar): ");
    }

    private String solicitarRutProfesorParaEliminar() {
        return getValidatedRut("Ingrese el RUT del profesor a eliminar (0 para cancelar): ");
    }

    private String solicitarNombreSala() {
        return getValidatedNonEmptyString("Nombre de la Sala (0 para cancelar): ");
    }

    private Integer solicitarCapacidadSala() {
        return getPositiveIntInput("Capacidad de la Sala (0 para cancelar): ");
    }

    private EstadoSala solicitarEstadoSala() {
        while (true) {
            String statusStr = getStringInput("Estado de la Sala (Disponible/Mantenimiento, 0 para cancelar): ");
            if (statusStr == null) return null;
            if (statusStr.equalsIgnoreCase("DISPONIBLE")) return EstadoSala.DISPONIBLE;
            if (statusStr.equalsIgnoreCase("MANTENIMIENTO")) return EstadoSala.MANTENIMIENTO;
            System.out.println("Estado inválido. Use 'Disponible' o 'Mantenimiento'.");
        }
    }

    private String solicitarNombreSalaParaEliminar() {
        return getValidatedNonEmptyString("Ingrese el nombre de la sala a eliminar (0 para cancelar): ");
    }

    private String solicitarDiaSemana() {
        while (true) {
            String day = getValidatedNonEmptyString("Ingrese el día de la semana para la asignación (Lunes, Martes, ..., Viernes. 0 para cancelar): ");
            if (day == null) return null;
            String normalizedDay = day.toLowerCase();
            if (normalizedDay.equals("lunes") || normalizedDay.equals("martes") ||
                    normalizedDay.equals("miercoles") || normalizedDay.equals("jueves") ||
                    normalizedDay.equals("viernes")) {
                return normalizedDay.substring(0, 1).toUpperCase() + normalizedDay.substring(1);
            }
            System.out.println("Día inválido. Por favor, ingrese un día de la semana válido.");
        }
    }

    private BloqueHorario solicitarBloqueHorario() {
        System.out.println("Seleccione un bloque horario (0 para cancelar):");
        BloqueHorario[] bloques = BloqueHorario.values();
        for (int i = 0; i < bloques.length; i++) {
            System.out.printf("%d. %s%n", i + 1, bloques[i]);
        }
        while (true) {
            Integer opcion = getPositiveIntInput("Opción: ");
            if (opcion == null) return null;
            if (opcion >= 1 && opcion <= bloques.length) {
                return bloques[opcion - 1];
            }
            System.out.println("Opción fuera de rango. Intente nuevamente.");
        }
    }

    private Integer solicitarIndiceReserva(int maxIndex) {
        return getPositiveIntInput("Ingrese el número de la asignación a cancelar (0 para cancelar): ");
    }

    // --- Métodos Atómicos para Seleccionar Elementos de Listas ---

    private Profesor seleccionarProfesorDeLista(List<Profesor> profesores, String accion) {
        System.out.println("\n--- Seleccionar Profesor para " + accion + " ---");
        for (int i = 0; i < profesores.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, profesores.get(i).getNombre());
        }
        while (true) {
            Integer opcion = getPositiveIntInput("Ingrese el número del profesor (0 para cancelar): ");
            if (opcion == null) return null;
            if (opcion >= 1 && opcion <= profesores.size()) {
                return profesores.get(opcion - 1);
            }
            System.out.println("Opción inválida.");
        }
    }

    private Sala seleccionarSalaDeLista(List<Sala> salas, String accion) {
        System.out.println("\n--- Seleccionar Sala para " + accion + " ---");
        for (int i = 0; i < salas.size(); i++) {
            System.out.printf("%d. %s (Capacidad: %d, Estado: %s)%n", i + 1, salas.get(i).getNombre(), salas.get(i).getCapacidad(), salaControlador.getEstadoSala(salas.get(i)));
        }
        while (true) {
            Integer opcion = getPositiveIntInput("Ingrese el número de la sala (0 para cancelar): ");
            if (opcion == null) return null;
            if (opcion >= 1 && opcion <= salas.size()) {
                return salas.get(opcion - 1);
            }
            System.out.println("Opción inválida.");
        }
    }

    private Asignatura seleccionarAsignaturaDeLista(List<Asignatura> asignaturas, String accion) {
        System.out.println("\n--- Seleccionar Asignatura para " + accion + " ---");
        for (int i = 0; i < asignaturas.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, asignaturas.get(i).getNombre(), asignaturas.get(i).getCodigo());
        }
        while (true) {
            Integer opcion = getPositiveIntInput("Ingrese el número de la asignatura (0 para cancelar): ");
            if (opcion == null) return null;
            if (opcion >= 1 && opcion <= asignaturas.size()) {
                return asignaturas.get(opcion - 1);
            }
            System.out.println("Opción inválida.");
        }
    }

    // --- Métodos de E/S y Validación Básica (Reutilizables, son la base de SelectorMenu) ---

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.equals("0") ? null : input; // '0' para cancelar
    }

    private Integer getPositiveIntInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null) return null; // Usuario canceló
            try {
                int num = Integer.parseInt(input);
                if (num > 0) return num;
                System.out.println("El número debe ser positivo.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private String getValidatedNonEmptyString(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null) return null; // Usuario canceló
            if (input.isEmpty()) {
                System.out.println("El campo no puede estar vacío.");
                continue;
            }
            return input;
        }
    }

    private String getOptionalNonEmptyString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null; // Cancelar
        if (input.isEmpty()) return ""; // Dejar vacío para no cambiar (distinto de null para cancelar)
        return input;
    }

    private String getValidatedRut(String prompt) {
        while (true) {
            String rut = getValidatedNonEmptyString(prompt);
            if (rut == null) return null;
            // Aquí puedes añadir tu lógica de validación de RUT más estricta si es necesario
            return rut;
        }
    }

    // --- Métodos para Mensajes de UI Comunes ---
    private void mostrarCancelacionOperacion(String operacion) {
        System.out.println(operacion + " cancelada.");
    }

    private void mostrarMensajeExito(String mensaje) {
        System.out.println("\nÉxito: " + mensaje);
    }

    private void mostrarMensajeError(String mensaje) {
        System.out.println("\nError: " + mensaje);
    }

    private void mostrarNoHayElementosPara(String tipoElemento) {
        System.out.println("No hay " + tipoElemento + " registrados.");
    }

    // --- Métodos de Utilidad de UI Compartidos ---
    private void limpiarPantalla() {
        // Implementación idéntica a la de MenuSalas
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            for (int i = 0; i < 50; ++i) System.out.println();
        }
    }

    private void pausarYContinuar() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }
}