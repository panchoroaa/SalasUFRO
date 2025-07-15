package vista;

import modelo.BloqueHorario;
import modelo.DiaSemana;
import modelo.Horario;

import java.util.List;
import java.util.Optional; // Necesitas esta importación

public class HorarioView {
    private final InputOutputHelper io;

    public HorarioView(InputOutputHelper io) {
        this.io = io;
    }

    public Horario solicitarHorario() {
        io.mostrarMensajeExito("--- Ingrese Horario ---");
        String diaString = solicitarDiaSemana();
        if (diaString == null) return null;

        DiaSemana diaEnum;
        try {
            diaEnum = DiaSemana.valueOf(diaString);
        } catch (IllegalArgumentException e) {
            io.mostrarMensajeError("Error interno: Día no reconocido. " + e.getMessage());
            return null;
        }

        Integer bloqueNumero = solicitarBloqueHorario();
        if (bloqueNumero == null) return null;

        // ** CORRECCIÓN CLAVE AQUÍ: Convertir el número a un objeto BloqueHorario **
        Optional<BloqueHorario> bloqueOpt = BloqueHorario.fromNumeroBloque(bloqueNumero);
        if (bloqueOpt.isEmpty()) {
            io.mostrarMensajeError("Error: El número de bloque '" + bloqueNumero + "' no corresponde a un BloqueHorario válido.");
            return null;
        }
        BloqueHorario bloqueEnum = bloqueOpt.get();

        return new Horario(diaEnum, bloqueEnum); // Pasar el objeto BloqueHorario
    }

    private String solicitarDiaSemana() {
        while (true) {
            String dia = io.solicitarTexto("Ingrese el día (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, 0 para cancelar): ");
            if (dia.equalsIgnoreCase("0")) {
                return null;
            }
            if (List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO").contains(dia.toUpperCase())) {
                return dia.toUpperCase();
            }
            System.out.println("Día no válido. Por favor, ingrese un día válido de la semana.");
        }
    }

    private Integer solicitarBloqueHorario() {
        while (true) {
            System.out.println("\nSeleccione el bloque horario:");
            BloqueHorario[] bloques = BloqueHorario.values();
            for (int i = 0; i < bloques.length; i++) {
                System.out.println((i + 1) + ". " + bloques[i]);
            }
            System.out.println("0. Cancelar");
            int opcion = io.leerOpcion();

            if (opcion == 0) {
                return null;
            }
            if (opcion >= 1 && opcion <= bloques.length) {
                return opcion;
            }
            System.out.println("Opción no válida. Por favor, ingrese un número de la lista.");
        }
    }
}