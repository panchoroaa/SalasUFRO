package vista;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.DiaSemana;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservaView {
    private final InputOutputHelper io;
    private final AsignacionControlador controlador;

    public ReservaView(InputOutputHelper io, AsignacionControlador controlador) {
        this.io = io;
        this.controlador = controlador;
    }

    public void mostrarTodasLasAsignaciones() {
        io.mostrarMensajeExito("--- Todas las Asignaciones ---");
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
                System.out.println(r);
            }
        });
    }

    public void filtrarAsignaciones() {
        io.mostrarMensajeExito("--- Filtrar Asignaciones ---");
        String rutProfesor = null;
        String nombreSala = null;
        DiaSemana dia = null;

        String inputProfesor = io.solicitarTexto("Filtrar por RUT de Profesor (deje vacío para omitir, 0 para cancelar): ");
        if (inputProfesor.equals("0")) { io.mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputProfesor.isEmpty()) {
            rutProfesor = inputProfesor;
            if (controlador.getProfesorPorRut(rutProfesor).isEmpty()) {
                io.mostrarMensajeError("Profesor con RUT '" + rutProfesor + "' no encontrado. Cancelando filtro.");
                return;
            }
        }

        String inputSala = io.solicitarTexto("Filtrar por Nombre de Sala (deje vacío para omitir, 0 para cancelar): ");
        if (inputSala.equals("0")) { io.mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputSala.isEmpty()) {
            nombreSala = inputSala;
            if (controlador.getSalaPorNombre(nombreSala).isEmpty()) {
                io.mostrarMensajeError("Sala '" + nombreSala + "' no encontrada. Cancelando filtro.");
                return;
            }
        }

        String inputDia = io.solicitarTexto("Filtrar por Día de la Semana (LUNES-SABADO, deje vacío para omitir, 0 para cancelar): ");
        if (inputDia.equals("0")) { io.mostrarCancelacionOperacion("filtrar asignaciones"); return; }
        if (!inputDia.isEmpty()) {
            try {
                dia = DiaSemana.valueOf(inputDia.toUpperCase());
            } catch (IllegalArgumentException e) {
                io.mostrarMensajeError("Día de la semana no válido. Debe ser LUNES, MARTES, etc. Cancelando filtro.");
                return;
            }
        }

        if (rutProfesor == null && nombreSala == null && dia == null) {
            System.out.println("No se especificaron criterios de filtro. Mostrando todas las asignaciones.");
            mostrarTodasLasAsignaciones();
            return;
        }

        List<Reserva> resultados = controlador.filtrarReservas(rutProfesor, nombreSala, dia);
        mostrarResultadosBusquedaReservas(resultados);
    }

    public Optional<Reserva> seleccionarAsignacionACancelar() {
        io.mostrarMensajeExito("--- Seleccionar Asignación a Cancelar ---");
        List<Reserva> reservasActuales = controlador.getReservas();
        if (reservasActuales.isEmpty()) {
            System.out.println("No hay asignaciones para cancelar.");
            return Optional.empty();
        }

        // Preparar las reservas para mostrar, incluyendo detalles completos
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

        Integer indiceSeleccionado = io.seleccionarIndiceDeLista(reservasParaMostrar, "asignación a cancelar");

        if (indiceSeleccionado == null) {
            io.mostrarCancelacionOperacion("cancelar asignación");
            return Optional.empty();
        }
        return Optional.of(reservasActuales.get(indiceSeleccionado - 1));
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
                System.out.println(r);
            }
        });
    }

    public void confirmarOperacion(String mensajeConfirmacion, Runnable operacionConfirmada, String nombreOperacion) {
        System.out.print(mensajeConfirmacion + " (S/N): ");
        String confirmacion = io.solicitarTexto("").toUpperCase();
        if (confirmacion.equals("S")) {
            operacionConfirmada.run();
        } else {
            io.mostrarCancelacionOperacion(nombreOperacion);
        }
    }
}