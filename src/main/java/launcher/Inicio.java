package launcher;

import persistencia.JsonDataManager;
import controlador.AsignacionControlador;
import gui.MainFrame;
import javax.swing.SwingUtilities;

public class Inicio {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Asignación de Salas (GUI).");
        JsonDataManager dataManager = new JsonDataManager();
        AsignacionControlador asignacionControlador = new AsignacionControlador(dataManager);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(asignacionControlador);
            mainFrame.setVisible(true);
        });
    }
}