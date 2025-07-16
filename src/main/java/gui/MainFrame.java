package gui;

import controlador.AsignacionControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private AsignacionControlador controlador;
    private JPanel mainContentPanel;
    private JPanel navigationPanel;

    public MainFrame(AsignacionControlador controlador) {
        this.controlador = controlador;
        setTitle("Sistema de Asignación de Salas");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setupWindowListener();
        showPanel(new JLabel("Bienvenido al Sistema de Asignación de Salas UFRO. Seleccione una opción.", SwingConstants.CENTER));
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(0, 1, 10, 10));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navigationPanel.setBackground(new Color(240, 240, 240));

        JButton btnCrearAsignacion = new JButton("<html>Crear<br>Asignación</html>");
        JButton btnVerFiltrarAsignaciones = new JButton("<html>Ver / Filtrar<br>Asignaciones</html>");
        JButton btnGestionarProfesores = new JButton("<html>Profesores</html>");
        JButton btnGestionarSalas = new JButton("<html>Salas</html>");
        JButton btnGestionarAsignaturas = new JButton("<html>Asignaturas</html>");
        JButton btnSalir = new JButton("Salir");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        btnCrearAsignacion.setFont(buttonFont);
        btnVerFiltrarAsignaciones.setFont(buttonFont);

        btnGestionarProfesores.setFont(buttonFont);
        btnGestionarSalas.setFont(buttonFont);
        btnGestionarAsignaturas.setFont(buttonFont);
        btnSalir.setFont(buttonFont);

        btnCrearAsignacion.addActionListener(e -> showPanel(new HorarioVisualPanel(controlador)));

        btnVerFiltrarAsignaciones.addActionListener(e -> {
            VerTodasAsignacionesPanel panel = new VerTodasAsignacionesPanel(controlador);
            panel.loadReservations();
            showPanel(panel);
        });
        btnGestionarProfesores.addActionListener(e -> showPanel(new ProfesorPanel(controlador)));
        btnGestionarSalas.addActionListener(e -> showPanel(new SalaPanel(controlador)));
        btnGestionarAsignaturas.addActionListener(e -> showPanel(new AsignaturaPanel(controlador)));
        btnSalir.addActionListener(e -> System.exit(0));

        navigationPanel.add(btnCrearAsignacion);
        navigationPanel.add(btnVerFiltrarAsignaciones);
        navigationPanel.add(btnGestionarProfesores);
        navigationPanel.add(btnGestionarSalas);
        navigationPanel.add(btnGestionarAsignaturas);
        navigationPanel.add(btnSalir);

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());

        add(navigationPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void showPanel(JPanel panel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showPanel(JLabel label) {
        mainContentPanel.removeAll();
        mainContentPanel.add(label, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
    }
}