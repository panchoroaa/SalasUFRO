package vista;

import controlador.AsignacionControlador;
import controlador.ProfesorControlador;
import controlador.SalaControlador;
import controlador.CheckReserva;
import persistencia.JsonDataManager;

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
        JsonDataManager jsonDataManager = new JsonDataManager();

        this.profesorControlador = new ProfesorControlador(jsonDataManager);
        this.salaControlador = new SalaControlador(jsonDataManager);

        this.asignacionControlador = new AsignacionControlador(
                this.profesorControlador,
                this.salaControlador,
                jsonDataManager
        );

        this.profesorControlador.setAsignacionControlador(this.asignacionControlador);
        this.salaControlador.setAsignacionControlador(this.asignacionControlador);

        this.checkReserva = new CheckReserva(
                this.profesorControlador,
                this.salaControlador,
                this.asignacionControlador
        );

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