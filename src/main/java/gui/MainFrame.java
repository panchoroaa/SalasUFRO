package gui;

import controlador.AsignacionControlador;
import persistencia.JsonDataManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private AsignacionControlador controlador;
    private JPanel mainContentPanel;

    public MainFrame(AsignacionControlador controlador) {
        this.controlador = controlador;
        setTitle("Sistema de Asignación de Salas");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setupWindowListener();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());
        add(mainContentPanel, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Seleccione una opción del menú.", SwingConstants.CENTER);
        mainContentPanel.add(welcomeLabel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu profesorMenu = new JMenu("Profesores");
        JMenuItem viewProfesoresItem = new JMenuItem("Ver/Gestionar Profesores");
        viewProfesoresItem.addActionListener(e -> showPanel(new ProfesorPanel(controlador)));
        profesorMenu.add(viewProfesoresItem);
        menuBar.add(profesorMenu);

        JMenu salaMenu = new JMenu("Salas");
        JMenuItem viewSalasItem = new JMenuItem("Ver/Gestionar Salas");
        viewSalasItem.addActionListener(e -> showPanel(new SalaPanel(controlador)));
        salaMenu.add(viewSalasItem);
        menuBar.add(salaMenu);

        JMenu asignaturaMenu = new JMenu("Asignaturas");
        JMenuItem viewAsignaturasItem = new JMenuItem("Ver/Gestionar Asignaturas");
        viewAsignaturasItem.addActionListener(e -> showPanel(new AsignaturaPanel(controlador)));
        asignaturaMenu.add(viewAsignaturasItem);
        menuBar.add(asignaturaMenu);

        JMenu asignacionMenu = new JMenu("Asignaciones");
        JMenuItem newAsignacionItem = new JMenuItem("Crear Asignación");
        JMenuItem cancelAsignacionItem = new JMenuItem("Cancelar Asignación");
        // Opción "Filtrar Asignaciones" ha sido eliminada de aquí
        JMenuItem viewAllAndFilterAsignacionesItem = new JMenuItem("Ver/Filtrar Asignaciones");


        newAsignacionItem.addActionListener(e -> showPanel(new CrearAsignacionPanel(controlador)));
        cancelAsignacionItem.addActionListener(e -> {
            CancelarAsignacionPanel panel = new CancelarAsignacionPanel(controlador);
            panel.loadReservations();
            showPanel(panel);
        });
        // Unificada la acción de "Ver Todas" y "Filtrar"
        viewAllAndFilterAsignacionesItem.addActionListener(e -> {
            VerTodasAsignacionesPanel panel = new VerTodasAsignacionesPanel(controlador);
            panel.loadReservations();
            showPanel(panel);
        });

        asignacionMenu.add(newAsignacionItem);
        asignacionMenu.add(cancelAsignacionItem);
        asignacionMenu.addSeparator();
        asignacionMenu.add(viewAllAndFilterAsignacionesItem); // Solo una opción para ver/filtrar
        menuBar.add(asignacionMenu);
    }

    private void showPanel(JPanel panel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Lógica de guardado al cerrar la aplicación, si es necesaria.
            }
        });
    }
}