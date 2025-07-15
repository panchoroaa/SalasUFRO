package vista;

import controlador.AsignacionControlador;
import modelo.*;
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
        System.out.println("║ 1. Gestión de Asignaciones              ║");
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
            System.out.println("4. Filtrar Asignaciones"); // NUEVO: Filtro
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
                case 4:
                    filtrarAsignaciones(); // NUEVO MÉTODO
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
            System.out.println("4. Buscar Profesor"); // NUEVO: Búsqueda
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
                case 4:
                    buscarProfesor(); // NUEVO MÉTODO
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
            System.out.println("3. Buscar Sala"); // NUEVO: Búsqueda
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
                case 3:
                    buscarSala(); // NUEVO MÉTODO
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
            System.out.println("2. Buscar Asignatura"); // NUEVO: Búsqueda
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Ingrese su opción: ");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    verTodasAsignaturas();
                    break;
                case 2:
                    buscarAsignatura(); // NUEVO MÉTODO
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

        String resultado = controlador.crearAsignacion(profesorSeleccionado.getRut(), salaSeleccionada.getNombre(), asignaturaSeleccionada.getCodigo(), horarioSeleccionado);
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
                        // CORRECCIÓN: Eliminar .toString() redundante si es la única cosa en el println
                        return r.toString(); // Dejar .toString() aquí si Reserva no es el tipo base esperado por println
                    }
                })
                .collect(Collectors.toList());

        Integer indiceSeleccionado = seleccionarIndiceElemento(reservasParaMostrar, "asignación a cancelar");

        if (indiceSeleccionado == null) {
            mostrarCancelacionOperacion("cancelar asignación");
            return;
        }

        Reserva reservaACancelar = reservasActuales.get(indiceSeleccionado - 1);

        System.out.println("\nVa a cancelar la siguiente asignación:");
        Optional<Profesor> p = controlador.getProfesorPorRut(reservaACancelar.getRutProfesor());
        Optional<Sala> s = controlador.getSalaPorNombre(reservaACancelar.getNombreSala());
        Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(reservaACancelar.getCodigoAsignatura());
        if (p.isPresent() && s.isPresent() && a.isPresent()) {
            System.out.println(reservaACancelar.toStringCompleto(p.get(), s.get(), a.get()));
        } else {
            // CORRECCIÓN: Eliminar .toString() redundante si es la única cosa en el println
            System.out.println(reservaACancelar); // Java llamará a toString() automáticamente
        }

        System.out.print("¿Está seguro que desea cancelar esta asignación? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S")) {
            mostrarCancelacionOperacion("cancelar asignación");
            return;
        }

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
                // CORRECCIÓN: Eliminar .toString() redundante si es la única cosa en el println
                System.out.println(r); // Java llamará a toString() automáticamente
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

    // --- NUEVOS MÉTODOS DE BÚSQUEDA Y FILTRO (I.3) ---

    private void buscarProfesor() {
        System.out.println("\n--- Buscar Profesor ---");
        System.out.print("Ingrese nombre o RUT del profesor a buscar (0 para cancelar): ");
        String query = scanner.nextLine();
        if (query.equals("0")) { mostrarCancelacionOperacion("búsqueda de profesor"); return; }

        List<Profesor> resultados = controlador.buscarProfesores(query);
        mostrarResultadosBusqueda(resultados, "profesor");
    }

    private void buscarSala() {
        System.out.println("\n--- Buscar Sala ---");
        System.out.print("Ingrese nombre de la sala a buscar (0 para cancelar): ");
        String query = scanner.nextLine();
        if (query.equals("0")) { mostrarCancelacionOperacion("búsqueda de sala"); return; }

        List<Sala> resultados = controlador.buscarSalas(query);
        mostrarResultadosBusqueda(resultados, "sala");
    }

    private void buscarAsignatura() {
        System.out.println("\n--- Buscar Asignatura ---");
        System.out.print("Ingrese nombre o código de la asignatura a buscar (0 para cancelar): ");
        String query = scanner.nextLine();
        if (query.equals("0")) { mostrarCancelacionOperacion("búsqueda de asignatura"); return; }

        List<Asignatura> resultados = controlador.buscarAsignaturas(query);
        mostrarResultadosBusqueda(resultados, "asignatura");
    }

    private void filtrarAsignaciones() {
        System.out.println("\n--- Filtrar Asignaciones ---");
        String rutProfesor = null;
        String nombreSala = null;
        DiaSemana dia = null;

        System.out.print("Filtrar por RUT de Profesor (deje vacío para omitir, 0 para cancelar): ");
        String inputProfesor = scanner.nextLine().trim();
        if (inputProfesor.equals("0")) { mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputProfesor.isEmpty()) {
            rutProfesor = inputProfesor;
            if (controlador.getProfesorPorRut(rutProfesor).isEmpty()) {
                mostrarMensajeError("Profesor con RUT '" + rutProfesor + "' no encontrado. Cancelando filtro.");
                return;
            }
        }

        System.out.print("Filtrar por Nombre de Sala (deje vacío para omitir, 0 para cancelar): ");
        String inputSala = scanner.nextLine().trim();
        if (inputSala.equals("0")) { mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputSala.isEmpty()) {
            nombreSala = inputSala;
            if (controlador.getSalaPorNombre(nombreSala).isEmpty()) {
                mostrarMensajeError("Sala '" + nombreSala + "' no encontrada. Cancelando filtro.");
                return;
            }
        }

        System.out.print("Filtrar por Día de la Semana (LUNES-SABADO, deje vacío para omitir, 0 para cancelar): ");
        String inputDia = scanner.nextLine().trim().toUpperCase();
        if (inputDia.equals("0")) { mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputDia.isEmpty()) {
            try {
                dia = DiaSemana.valueOf(inputDia);
            } catch (IllegalArgumentException e) {
                mostrarMensajeError("Día de la semana no válido. Debe ser LUNES, MARTES, etc. Cancelando filtro.");
                return;
            }
        }

        // CORRECCIÓN: Simplificación de la condición
        // La condición 'isEmpty()' después de '!input.isEmpty()' es redundante.
        if (rutProfesor == null && nombreSala == null && dia == null) {
            System.out.println("No se especificaron criterios de filtro. Mostrando todas las asignaciones.");
            verTodasLasAsignaciones();
            return;
        }

        List<Reserva> resultados = controlador.filtrarReservas(rutProfesor, nombreSala, dia);
        mostrarResultadosBusquedaReservas(resultados);
    }

    private <T> void mostrarResultadosBusqueda(List<T> resultados, String tipoElemento) {
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron " + tipoElemento + "es que coincidan con la búsqueda.");
            return;
        }
        System.out.println("\n--- Resultados de la búsqueda de " + tipoElemento + "es ---");
        resultados.forEach(System.out::println); // Java llamará a toString() automáticamente
    }

    private void mostrarResultadosBusquedaReservas(List<Reserva> resultados) {
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron asignaciones que coincidan con los filtros.");
            return;
        }
        System.out.println("\n--- Resultados de Asignaciones Filtradas ---");
        resultados.forEach(r -> {
            Optional<Profesor> p = controlador.getProfesorPorRut(r.getRutProfesor());
            Optional<Sala> s = controlador.getSalaPorNombre(r.getNombreSala());
            Optional<Asignatura> a = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());
            if (p.isPresent() && s.isPresent() && a.isPresent()) {
                System.out.println(r.toStringCompleto(p.get(), s.get(), a.get()));
            } else {
                // CORRECCIÓN: Eliminar .toString() redundante si es la única cosa en el println
                System.out.println(r); // Java llamará a toString() automáticamente
            }
        });
    }

    // --- MÉTODOS AUXILIARES PARA SELECCIÓN Y VISUALIZACIÓN ---

    // NUEVO MÉTODO AUXILIAR para evitar duplicación de código
    private Integer pedirIndiceDeLista(List<?> lista, String tipoElemento) {
        if (lista.isEmpty()) {
            System.out.println("No hay " + tipoElemento + "s disponibles para seleccionar.");
            return null;
        }
        System.out.println("Seleccione un " + tipoElemento + ":");
        AtomicInteger index = new AtomicInteger(1);
        // CORRECCIÓN: Eliminar .toString() redundante
        lista.forEach(item -> System.out.println(index.getAndIncrement() + ". " + item)); // Java llamará a toString() automáticamente
        System.out.println("0. Cancelar");

        int opcion = leerOpcion();
        if (opcion > 0 && opcion <= lista.size()) {
            return opcion;
        }
        return null; // El usuario canceló o ingresó una opción inválida
    }

    // Método refactorizado para usar el nuevo auxiliar
    private Object seleccionarElemento(List<?> lista, String tipoElemento) {
        Integer opcionIndex = pedirIndiceDeLista(lista, tipoElemento);
        if (opcionIndex != null) {
            return lista.get(opcionIndex - 1);
        }
        return null;
    }

    // Método refactorizado para usar el nuevo auxiliar
    private Integer seleccionarIndiceElemento(List<?> lista, String tipoElemento) {
        return pedirIndiceDeLista(lista, tipoElemento);
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
                    r.getHorario(), // toString() se llama implícitamente en printf
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
                    r.getHorario(), // toString() se llama implícitamente en printf
                    p.map(Profesor::getNombre).orElse("Desconocido"),
                    a.map(Asignatura::getNombre).orElse("Desconocida"));
        });
    }

    // --- MÉTODOS GENERALES DE UTILIDAD ---

    private Horario solicitarHorario() {
        System.out.println("\n--- Ingrese Horario ---");
        String diaString = solicitarDiaSemana();
        if (diaString == null) return null;

        DiaSemana diaEnum;
        try {
            diaEnum = DiaSemana.valueOf(diaString);
        } catch (IllegalArgumentException e) {
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
                return null;
            }
            if (List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO").contains(dia)) {
                return dia;
            }
            System.out.println("Día no válido. Por favor, ingrese un día válido de la semana.");
        }
    }

    private Integer solicitarBloqueHorario() {
        while (true) {
            System.out.println("\nSeleccione el bloque horario:");
            BloqueHorario[] bloques = BloqueHorario.values();
            for (int i = 0; i < bloques.length; i++) {
                // CORRECCIÓN: Eliminar .toString() redundante
                System.out.println((i + 1) + ". " + bloques[i]); // Java llamará a toString() automáticamente
            }
            System.out.println("0. Cancelar");
            System.out.print("Ingrese su opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                if (opcion == 0) {
                    return null;
                }
                if (opcion >= 1 && opcion <= bloques.length) {
                    return opcion;
                }
                System.out.println("Opción no válida. Por favor, ingrese un número de la lista.");
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
    }

    private int leerOpcion() {
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