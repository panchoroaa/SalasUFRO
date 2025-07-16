package launcher;

import persistencia.JsonDataManager;
import controlador.AsignacionControlador;
import gui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Inicio {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("No se pudo establecer el Look and Feel Nimbus. Se usará el predeterminado.");
        }
        System.out.println("Iniciando Sistema de Asignación de Salas (GUI).");
        JsonDataManager dataManager = new JsonDataManager();
        AsignacionControlador asignacionControlador = new AsignacionControlador(dataManager);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(asignacionControlador);
            mainFrame.setVisible(true);
        });
    }
}