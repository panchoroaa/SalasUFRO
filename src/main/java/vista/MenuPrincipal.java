package vista;

import controlador.AsignacionControlador;
import modelo.*; // Asegúrate de que DiaSemana y BloqueHorario estén aquí o en otro paquete importado
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MenuPrincipal {
    private final Scanner scanner;
    private final AsignacionControlador controlador;

    public MenuPrincipal(AsignacionControlador controlador) {
        this.scanner = new Scanner(System.in);
        this.controlador = controlador;
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    mostrarMenuGestionAsignaciones();
                    break;
                case 2:
                    mostrarMenuGestionProfesores();
                    break;
                case 3:
                    mostrarMenuGestionSalas();
                    break;
                case 4:
                    mostrarMenuGestionAsignaturas();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         SISTEMA DE ASIGNACIÓN DE SALAS  ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║ 1. Gestión de Asignaciones              ║"); // Nuevo submenú
        System.out.println("║ 2. Gestión de Profesores                ║");
        System.out.println("║ 3. Gestión de Salas                     ║");
        System.out.println("║ 4. Gestión de Asignaturas               ║");
        System.out.println("║ 0. Salir                                ║");
        System.out.println("╚═════════════════════════════════════════╝");
        System.out.print("Ingrese su opción: ");
    }

    // --- SUBMENÚS DE GESTIÓN ---

    private void mostrarMenuGestionAsignaciones() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Asignaciones ---");
            System.out.println("1. Realizar Nueva Asignación");
            System.out.println("2. Cancelar Asignación");
            System.out.println("3. Ver Todas las Asignaciones");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Ingrese su opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    realizarNuevaAsignacion();
                    break;
                case 2:
                    cancelarAsignacion();
                    break;
                case 3:
                    verTodasLasAsignaciones();
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionProfesores() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Profesores ---");
            System.out.println("1. Ver todos los Profesores");
            System.out.println("2. Ver Asignaturas Impartidas por un Profesor");
            System.out.println("3. Ver Horario de un Profesor");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Ingrese su opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    verTodosProfesores();
                    break;
                case 2:
                    verAsignaturasImpartidasPorProfesor();
                    break;
                case 3:
                    verHorarioDeUnProfesor();
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionSalas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Salas ---");
            System.out.println("1. Ver todas las Salas");
            System.out.println("2. Ver Horario de una Sala");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Ingrese su opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    verTodasSalas();
                    break;
                case 2:
                    verHorarioDeUnaSala();
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionAsignaturas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Asignaturas ---");
            System.out.println("1. Ver todas las Asignaturas");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Ingrese su opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    verTodasAsignaturas();
                    break;
                case 0:
                    System.out.println("Volviendo al Menú Principal...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // --- LÓGICA DE NEGOCIO Y FLUJO DE ASIGNACIONES ---

    private void realizarNuevaAsignacion() {
        System.out.println("\n--- Realizar Nueva Asignación ---");

        Optional<Asignatura> asignaturaOpt = seleccionarAsignatura();
        if (asignaturaOpt.isEmpty()) {
            mostrarCancelacionOperacion("realizar asignación");
            return;
        }
        Asignatura asignaturaSeleccionada = asignaturaOpt.get();

        Horario horarioSeleccionado = solicitarHorario();
        if (horarioSeleccionado == null) {
            mostrarCancelacionOperacion("realizar asignación");
            return;
        }

        // Obtener y seleccionar profesor filtrado
        List<Profesor> profesoresDisponibles = controlador.getProfesoresDisponibles(asignaturaSeleccionada, horarioSeleccionado);
        if (profesoresDisponibles.isEmpty()) {
            mostrarMensajeError("No hay profesores disponibles que impartan '" + asignaturaSeleccionada.getNombre() + "' y estén libres en el horario seleccionado.");
            return;
        }
        Profesor profesorSeleccionado = (Profesor) seleccionarElemento(profesoresDisponibles, "profesor");
        if (profesorSeleccionado == null) {
            mostrarCancelacionOperacion("realizar asignación");
            return;
        }

        // Obtener y seleccionar sala filtrada
        List<Sala> salasDisponibles = controlador.getSalasDisponiblesEnHorario(horarioSeleccionado);
        if (salasDisponibles.isEmpty()) {
            mostrarMensajeError("No hay salas disponibles en el horario seleccionado.");
            return;
        }
        Sala salaSeleccionada = (Sala) seleccionarElemento(salasDisponibles, "sala");
        if (salaSeleccionada == null) {
            mostrarCancelacionOperacion("realizar asignación");
            return;
        }

        String resultado = controlador.realizarAsignacion(profesorSeleccionado, salaSeleccionada, asignaturaSeleccionada, horarioSeleccionado);
        procesarResultadoOperacion(resultado);
    }

    private void cancelarAsignacion() {
        System.out.println("\n--- Cancelar Asignación ---");
        List<Reserva> reservasActuales = controlador.getReservas();
        if (reservasActuales.isEmpty()) {
            System.out.println("No hay asignaciones para cancelar.");
            return;
        }

        List<String> reservasParaMostrar = reservasActuales.stream()
                .map(r -> {
                    Optional<Profesor> p = controlador.getProfesorPorRut(r.getRutProfesor());
                    Optional<Sala> s = controlador.getSalaPorNombre(r.getNombreSala());
                    Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());
                    if (p.isPresent() && s.isPresent() && a.isPresent()) {
                        return r.toStringCompleto(p.get(), s.get(), a.get());
                    } else {
                        return r.toString();
                    }
                })
                .collect(Collectors.toList());

        Integer indiceSeleccionado = seleccionarIndiceElemento(reservasParaMostrar, "asignación a cancelar");

        if (indiceSeleccionado == null) {
            mostrarCancelacionOperacion("cancelar asignación");
            return;
        }

        Reserva reservaACancelar = reservasActuales.get(indiceSeleccionado - 1);

        // --- Confirmación de la acción ---
        System.out.println("\nVa a cancelar la siguiente asignación:");
        Optional<Profesor> p = controlador.getProfesorPorRut(reservaACancelar.getRutProfesor());
        Optional<Sala> s = controlador.getSalaPorNombre(reservaACancelar.getNombreSala());
        Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(reservaACancelar.getCodigoAsignatura());
        if (p.isPresent() && s.isPresent() && a.isPresent()) {
            System.out.println(reservaACancelar.toStringCompleto(p.get(), s.get(), a.get()));
        } else {
            System.out.println(reservaACancelar.toString());
        }

        System.out.print("¿Está seguro que desea cancelar esta asignación? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S")) {
            mostrarCancelacionOperacion("cancelar asignación");
            return;
        }
        // --- Fin de confirmación ---

        String resultado = controlador.cancelarAsignacion(reservaACancelar);
        procesarResultadoOperacion(resultado);
    }

    private void verTodasLasAsignaciones() {
        System.out.println("\n--- Todas las Asignaciones ---");
        List<Reserva> reservas = controlador.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay asignaciones registradas.");
            return;
        }

        reservas.forEach(r -> {
            Optional<Profesor> p = controlador.getProfesorPorRut(r.getRutProfesor());
            Optional<Sala> s = controlador.getSalaPorNombre(r.getNombreSala());
            Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());

            if (p.isPresent() && s.isPresent() && a.isPresent()) {
                System.out.println(r.toStringCompleto(p.get(), s.get(), a.get()));
            } else {
                System.out.println(r.toString());
            }
        });
    }

    // --- MÉTODOS DE VISUALIZACIÓN ESPECÍFICOS PARA ENTIDADES ---

    private void verTodosProfesores() {
        System.out.println("\n--- Todos los Profesores ---");
        List<Profesor> profesores = controlador.getProfesores();
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }
        profesores.forEach(System.out::println);
    }

    private void verAsignaturasImpartidasPorProfesor() {
        System.out.println("\n--- Asignaturas Impartidas por Profesor ---");
        Optional<Profesor> profesorOpt = seleccionarProfesor();
        if (profesorOpt.isEmpty()) {
            mostrarCancelacionOperacion("ver asignaturas de profesor");
            return;
        }
        mostrarAsignaturasDeProfesor(profesorOpt.get());
    }

    private void verHorarioDeUnProfesor() {
        System.out.println("\n--- Ver Horario de un Profesor ---");
        Optional<Profesor> profesorOpt = seleccionarProfesor();
        if (profesorOpt.isEmpty()) {
            mostrarCancelacionOperacion("ver horario de profesor");
            return;
        }
        mostrarHorarioDeProfesor(profesorOpt.get());
    }

    private void verTodasSalas() {
        System.out.println("\n--- Todas las Salas ---");
        List<Sala> salas = controlador.getSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }
        salas.forEach(System.out::println);
    }

    private void verHorarioDeUnaSala() {
        System.out.println("\n--- Ver Horario de una Sala ---");
        Optional<Sala> salaOpt = seleccionarSala();
        if (salaOpt.isEmpty()) {
            mostrarCancelacionOperacion("ver horario de sala");
            return;
        }
        mostrarHorarioDeSala(salaOpt.get());
    }

    private void verTodasAsignaturas() {
        System.out.println("\n--- Todas las Asignaturas ---");
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas.");
            return;
        }
        asignaturas.forEach(System.out::println);
    }


    // --- MÉTODOS AUXILIARES PARA SELECCIÓN Y VISUALIZACIÓN ---

    private Object seleccionarElemento(List<?> lista, String tipoElemento) {
        if (lista.isEmpty()) {
            System.out.println("No hay " + tipoElemento + "s disponibles.");
            return null;
        }
        System.out.println("Seleccione un " + tipoElemento + ":");
        AtomicInteger index = new AtomicInteger(1);
        lista.forEach(item -> System.out.println(index.getAndIncrement() + ". " + item.toString()));
        System.out.println("0. Cancelar");

        int opcion = leerOpcion();
        if (opcion > 0 && opcion <= lista.size()) {
            return lista.get(opcion - 1);
        }
        return null;
    }

    private Integer seleccionarIndiceElemento(List<?> lista, String tipoElemento) {
        if (lista.isEmpty()) {
            System.out.println("No hay " + tipoElemento + "s disponibles para seleccionar.");
            return null;
        }
        System.out.println("Seleccione el " + tipoElemento + " a cancelar:");
        AtomicInteger index = new AtomicInteger(1);
        lista.forEach(item -> System.out.println(index.getAndIncrement() + ". " + item.toString()));
        System.out.println("0. Cancelar");

        int opcion = leerOpcion();
        if (opcion > 0 && opcion <= lista.size()) {
            return opcion;
        }
        return null;
    }

    private Optional<Profesor> seleccionarProfesor() {
        List<Profesor> profesores = controlador.getProfesores();
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return Optional.empty();
        }
        return Optional.ofNullable((Profesor) seleccionarElemento(profesores, "profesor"));
    }

    private Optional<Sala> seleccionarSala() {
        List<Sala> salas = controlador.getSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return Optional.empty();
        }
        return Optional.ofNullable((Sala) seleccionarElemento(salas, "sala"));
    }

    private Optional<Asignatura> seleccionarAsignatura() {
        List<Asignatura> asignaturas = controlador.getAsignaturas();
        if (asignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas.");
            return Optional.empty();
        }
        return Optional.ofNullable((Asignatura) seleccionarElemento(asignaturas, "asignatura"));
    }


    private void mostrarAsignaturasDeProfesor(Profesor profesor) {
        List<Asignatura> asignaturasImpartidas = profesor.getAsignaturasImpartidas();
        if (asignaturasImpartidas.isEmpty()) {
            System.out.println("El profesor " + profesor.getNombre() + " no tiene asignaturas registradas para impartir.");
        } else {
            System.out.println("\nAsignaturas impartidas por " + profesor.getNombre() + ":");
            asignaturasImpartidas.forEach(System.out::println);
        }
    }

    private void mostrarHorarioDeProfesor(Profesor profesor) {
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
                    r.getHorario().toString(),
                    s.map(Sala::getNombre).orElse("Desconocida"),
                    a.map(Asignatura::getNombre).orElse("Desconocida"));
        });
    }

    private void mostrarHorarioDeSala(Sala sala) {
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
                    r.getHorario().toString(),
                    p.map(Profesor::getNombre).orElse("Desconocido"),
                    a.map(Asignatura::getNombre).orElse("Desconocida"));
        });
    }

    // --- MÉTODOS GENERALES DE UTILIDAD ---

    private Horario solicitarHorario() {
        System.out.println("\n--- Ingrese Horario ---");
        String diaString = solicitarDiaSemana();
        if (diaString == null) return null;

        // Convertir el String del día a un enum DiaSemana
        DiaSemana diaEnum;
        try {
            diaEnum = DiaSemana.valueOf(diaString); // Asume que DiaSemana tiene los nombres exactos (LUNES, MARTES, etc.)
        } catch (IllegalArgumentException e) {
            // Esto no debería ocurrir si solicitarDiaSemana() ya valida, pero es una buena práctica
            mostrarMensajeError("Error interno: Día no reconocido. " + e.getMessage());
            return null;
        }

        Integer bloqueIndex = solicitarBloqueHorario();
        if (bloqueIndex == null) return null;

        return new Horario(diaEnum, bloqueIndex);
    }

    private String solicitarDiaSemana() {
        while (true) {
            System.out.print("Ingrese el día (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, 0 para cancelar): ");
            String dia = scanner.nextLine().trim().toUpperCase();
            if (dia.equals("0")) {
                return null; // El usuario eligió cancelar
            }
            // Validar que el día ingresado sea uno de los permitidos
            if (List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO").contains(dia)) {
                return dia; // Día válido, se retorna
            }
            // Si el día no es válido y no es "0", se imprime el mensaje y se repite el bucle
            System.out.println("Día no válido. Por favor, ingrese un día válido de la semana.");
        }
    }

    private Integer solicitarBloqueHorario() {
        while (true) {
            System.out.println("\nSeleccione el bloque horario:");
            BloqueHorario[] bloques = BloqueHorario.values();
            for (int i = 0; i < bloques.length; i++) {
                System.out.println((i + 1) + ". " + bloques[i].toString());
            }
            System.out.println("0. Cancelar");
            System.out.print("Ingrese su opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir newline

                if (opcion == 0) {
                    return null; // El usuario eligió cancelar
                }
                if (opcion >= 1 && opcion <= bloques.length) {
                    return opcion; // Retorna el índice (sumándole 1 para coincidir con la lista visible)
                }
                System.out.println("Opción no válida. Por favor, ingrese un número de la lista.");
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Consumir entrada incorrecta
            }
        }
    }

    private int leerOpcion() {
        try {
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir newline
            return opcion;
        } catch (InputMismatchException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número.");
            scanner.nextLine(); // Consumir la entrada incorrecta
            return -1; // Retorna un valor no válido para que se repita el ciclo
        }
    }

    private void procesarResultadoOperacion(String resultado) {
        if (resultado.startsWith("Error")) {
            mostrarMensajeError(resultado);
        } else {
            mostrarMensajeExito(resultado);
        }
    }

    private void mostrarMensajeExito(String mensaje) {
        System.out.println("\n¡ÉXITO! " + mensaje);
    }

    private void mostrarMensajeError(String mensaje) {
        System.err.println("\nERROR: " + mensaje);
    }

    private void mostrarCancelacionOperacion(String operacion) {
        System.out.println("Operación de " + operacion + " cancelada.");
    }
}