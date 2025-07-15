package launcher;

import persistencia.JsonDataManager;
import vista.MenuPrincipal;
import controlador.AsignacionControlador; // Correct import for AsignacionControlador

public class Inicio {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Asignación de Salas.");
        JsonDataManager dataManager = new JsonDataManager();
        AsignacionControlador asignacionControlador = new AsignacionControlador(dataManager);
        MenuPrincipal menu = new MenuPrincipal(asignacionControlador);
        menu.iniciar();
        System.out.println("Sistema de Asignación de Salas finalizado.");
    }
}