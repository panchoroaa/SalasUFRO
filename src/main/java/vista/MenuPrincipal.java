package vista;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.BloqueHorario;
import modelo.DiaSemana;
import modelo.Horario;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MenuPrincipal {
    private final AsignacionControlador asignacionControlador;
    private final InputOutputHelper io;
    private final ProfesorView profesorView;
    private final SalaView salaView;
    private final AsignaturaView asignaturaView;
    private final HorarioView horarioView;
    private final ReservaView reservaView;

    public MenuPrincipal(AsignacionControlador asignacionControlador) {
        // Un solo Scanner compartido para toda la aplicación
        Scanner sharedScanner = new Scanner(System.in);

        this.asignacionControlador = asignacionControlador;
        this.io = new InputOutputHelper(sharedScanner); // Instancia InputOutputHelper con el scanner
        this.profesorView = new ProfesorView(io, asignacionControlador);
        this.salaView = new SalaView(io, asignacionControlador);
        this.asignaturaView = new AsignaturaView(io, asignacionControlador);
        this.horarioView = new HorarioView(io);
        this.reservaView = new ReservaView(io, asignacionControlador);
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = io.leerOpcion(); // Usamos InputOutputHelper para leer la opción

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
                    io.mostrarMensajeError("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n╔═════════════════════════════════════════╗");
        System.out.println("║         SISTEMA DE ASIGNACIÓN DE SALAS  ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║                                         ║");
        System.out.println("║      1. Gestión de Asignaciones         ║");
        System.out.println("║      2. Gestión de Profesores           ║");
        System.out.println("║      3. Gestión de Salas                ║");
        System.out.println("║      4. Gestión de Asignaturas          ║");
        System.out.println("║                                         ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║            0. Salir                     ║");
        System.out.println("╚═════════════════════════════════════════╝");
        System.out.print("  ▶ Ingrese su opción: ");
    }

    private void mostrarMenuGestionAsignaciones() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Asignaciones ---");
            System.out.println("1. Realizar nueva asignación");
            System.out.println("2. Cancelar asignación existente");
            System.out.println("3. Ver todas las asignaciones");
            System.out.println("4. Filtrar asignaciones");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = io.leerOpcion();

            switch (opcion) {
                case 1:
                    realizarNuevaAsignacion();
                    break;
                case 2:
                    cancelarAsignacion();
                    break;
                case 3:
                    reservaView.mostrarTodasLasAsignaciones();
                    break;
                case 4:
                    reservaView.filtrarAsignaciones();
                    break;
                case 0:
                    io.mostrarCancelacionOperacion("volver");
                    break;
                default:
                    io.mostrarMensajeError("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionProfesores() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Profesores ---");
            System.out.println("1. Ver todos los profesores");
            System.out.println("2. Buscar profesor");
            System.out.println("3. Ver asignaturas impartidas por profesor");
            System.out.println("4. Ver horario de un profesor");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = io.leerOpcion();

            switch (opcion) {
                case 1:
                    profesorView.mostrarTodosProfesores();
                    break;
                case 2:
                    profesorView.buscarProfesor();
                    break;
                case 3:
                    profesorView.verAsignaturasImpartidasPorProfesor();
                    break;
                case 4:
                    profesorView.verHorarioDeUnProfesor();
                    break;
                case 0:
                    io.mostrarCancelacionOperacion("volver");
                    break;
                default:
                    io.mostrarMensajeError("Opción no válida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionSalas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Salas ---");
            System.out.println("1. Ver todas las salas");
            System.out.println("2. Buscar sala");
            System.out.println("3. Ver horario de una sala");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = io.leerOpcion();

            switch (opcion) {
                case 1:
                    salaView.mostrarTodasSalas();
                    break;
                case 2:
                    salaView.buscarSala();
                    break;
                case 3:
                    salaView.verHorarioDeUnaSala();
                    break;
                case 0:
                    io.mostrarCancelacionOperacion("volver");
                    break;
                default:
                    io.mostrarMensajeError("Opción no válida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 0);
    }

    private void mostrarMenuGestionAsignaturas() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Asignaturas ---");
            System.out.println("1. Ver todas las asignaturas");
            System.out.println("2. Buscar asignatura");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = io.leerOpcion();

            switch (opcion) {
                case 1:
                    asignaturaView.mostrarTodasAsignaturas();
                    break;
                case 2:
                    asignaturaView.buscarAsignatura();
                    break;
                case 0:
                    io.mostrarCancelacionOperacion("volver");
                    break;
                default:
                    io.mostrarMensajeError("Opción no válida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 0);
    }

    private void realizarNuevaAsignacion() {
        io.mostrarMensajeExito("--- Realizar Nueva Asignación ---");

        Horario horario = horarioView.solicitarHorario();
        if (horario == null) {
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }

        List<Asignatura> asignaturasDisponibles = asignacionControlador.getAsignaturas();
        if (asignaturasDisponibles.isEmpty()) {
            io.mostrarMensajeError("No hay asignaturas disponibles para asignar.");
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Optional<Asignatura> asignaturaOpt = asignaturaView.seleccionarAsignatura();
        if (asignaturaOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Asignatura asignaturaSeleccionada = asignaturaOpt.get();

        List<Profesor> profesoresDisponibles = asignacionControlador.getProfesoresDisponibles(asignaturaSeleccionada, horario);
        if (profesoresDisponibles.isEmpty()) {
            io.mostrarMensajeError("No hay profesores disponibles que impartan la asignatura en ese horario.");
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Optional<Profesor> profesorOpt = profesorView.seleccionarProfesor();
        if (profesorOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Profesor profesorSeleccionado = profesorOpt.get();

        List<Sala> salasDisponibles = asignacionControlador.getSalasDisponiblesEnHorario(horario)
                .stream()
                .filter(s -> s.getCapacidad() >= asignaturaSeleccionada.getCantidadAlumnos())
                .collect(Collectors.toList());

        if (salasDisponibles.isEmpty()) {
            io.mostrarMensajeError("No hay salas disponibles con capacidad suficiente en ese horario.");
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Optional<Sala> salaOpt = salaView.seleccionarSala();
        if (salaOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("nueva asignación");
            return;
        }
        Sala salaSeleccionada = salaOpt.get();

        String resultado = asignacionControlador.crearAsignacion(
                profesorSeleccionado.getRut(),
                salaSeleccionada.getNombre(),
                asignaturaSeleccionada.getCodigo(),
                horario
        );

        if (resultado.startsWith("Error")) {
            io.mostrarMensajeError(resultado);
        } else {
            io.mostrarMensajeExito(resultado);
        }
    }

    private void cancelarAsignacion() {
        io.mostrarMensajeExito("--- Cancelar Asignación ---");
        Optional<Reserva> reservaOpt = reservaView.seleccionarAsignacionACancelar();

        if (reservaOpt.isEmpty()) {
            io.mostrarCancelacionOperacion("cancelar asignación");
            return;
        }

        Reserva reservaACancelar = reservaOpt.get();
        io.mostrarMensajeExito("Confirmar cancelación de la siguiente asignación:");

        Optional<Profesor> p = asignacionControlador.getProfesorPorRut(reservaACancelar.getRutProfesor());
        Optional<Sala> s = asignacionControlador.getSalaPorNombre(reservaACancelar.getNombreSala());
        Optional<Asignatura> a = asignacionControlador.getAsignaturaPorCodigo(reservaACancelar.getCodigoAsignatura());

        if (p.isPresent() && s.isPresent() && a.isPresent()) {
            System.out.println(reservaACancelar.toStringCompleto(p.get(), s.get(), a.get()));
        } else {
            System.out.println(reservaACancelar);
        }

        reservaView.confirmarOperacion("¿Está seguro de que desea cancelar esta asignación?",
                () -> {
                    String resultado = asignacionControlador.cancelarAsignacion(reservaACancelar);
                    if (resultado.startsWith("¡")) {
                        io.mostrarMensajeExito(resultado);
                    } else {
                        io.mostrarMensajeError(resultado);
                    }
                }, "cancelación de asignación");
    }
}