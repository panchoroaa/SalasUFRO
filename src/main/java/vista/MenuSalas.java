// vista/MenuSalas.java
package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.CheckReserva;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuSalas {
    private final Scanner scanner;
    private final ProfesorControlador profesorControlador;
    private final SalaControlador salaControlador;
    private final AsignacionControlador asignacionControlador;
    private final SelectorMenu selector;
    private final CheckReserva checkReserva;

    public MenuSalas() {
        this.scanner = new Scanner(System.in);

        this.profesorControlador = new ProfesorControlador(null);
        this.salaControlador = new SalaControlador(null);

        this.asignacionControlador = new AsignacionControlador(
                this.profesorControlador,
                this.salaControlador
        );

        this.profesorControlador.setAsignacionControlador(this.asignacionControlador);
        this.salaControlador.setAsignacionControlador(this.asignacionControlador);

        this.checkReserva = new CheckReserva();

        this.selector = new SelectorMenu(
                this.scanner,
                this.profesorControlador,
                this.salaControlador,
                this.asignacionControlador,
                this.checkReserva
        );
    }

    public void iniciar() {
        selector.mostrarMenuPrincipal();
    }
}