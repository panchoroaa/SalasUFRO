// Ubicación: gui/HorarioVisualPanel.java
package gui;

import controlador.AsignacionControlador;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HorarioVisualPanel extends JPanel {
    private AsignacionControlador controlador;
    private JTable horarioTable;
    private HorarioVisualTableModel tableModel;

    private JComboBox<String> filtroTipoComboBox;
    private JPanel filtroPanelDinamico;
    private JComboBox<Asignatura> asignaturaComboBox;
    private JComboBox<Profesor> profesorComboBox;
    private JComboBox<Sala> salaComboBox;
    private JButton aplicarFiltroButton;

    public HorarioVisualPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        setupTableMouseListener();
        loadFilterData();
        aplicarFiltro();
    }

    private void initComponents() {
        JPanel topContainerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Horario Interactivo de Asignaciones", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topContainerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));

        filtroTipoComboBox = new JComboBox<>(new String[]{"Todas", "Por Asignatura", "Por Profesor", "Por Sala"});
        aplicarFiltroButton = new JButton("Aplicar Filtro");

        filtroPanelDinamico = new JPanel(new CardLayout());
        asignaturaComboBox = new JComboBox<>();
        profesorComboBox = new JComboBox<>();
        salaComboBox = new JComboBox<>();

        filtroPanelDinamico.add(new JPanel(), "Todas");
        filtroPanelDinamico.add(asignaturaComboBox, "Por Asignatura");
        filtroPanelDinamico.add(profesorComboBox, "Por Profesor");
        filtroPanelDinamico.add(salaComboBox, "Por Sala");

        filterPanel.add(new JLabel("Filtrar por:"));
        filterPanel.add(filtroTipoComboBox);
        filterPanel.add(filtroPanelDinamico);
        filterPanel.add(aplicarFiltroButton);

        topContainerPanel.add(filterPanel, BorderLayout.CENTER);
        add(topContainerPanel, BorderLayout.NORTH);

        tableModel = new HorarioVisualTableModel();
        horarioTable = new JTable(tableModel);
        horarioTable.setRowHeight(60);
        horarioTable.setShowGrid(true);
        horarioTable.setGridColor(Color.LIGHT_GRAY);
        horarioTable.setDefaultRenderer(Object.class, new HorarioCellRenderer(controlador));

        JScrollPane scrollPane = new JScrollPane(horarioTable);
        add(scrollPane, BorderLayout.CENTER);


        filtroTipoComboBox.addActionListener(e -> {
            CardLayout cl = (CardLayout) (filtroPanelDinamico.getLayout());
            cl.show(filtroPanelDinamico, (String) filtroTipoComboBox.getSelectedItem());
        });

        aplicarFiltroButton.addActionListener(e -> aplicarFiltro());
    }

    private void loadFilterData() {
        controlador.getAsignaturas().forEach(asignaturaComboBox::addItem);
        controlador.getProfesores().forEach(profesorComboBox::addItem);
        controlador.getSalas().forEach(salaComboBox::addItem);
    }

    private void aplicarFiltro() {
        List<Reserva> reservasFiltradas;
        String tipoFiltro = (String) filtroTipoComboBox.getSelectedItem();

        switch (tipoFiltro) {
            case "Por Asignatura":
                Asignatura asig = (Asignatura) asignaturaComboBox.getSelectedItem();
                reservasFiltradas = controlador.filtrarReservas(null, null, asig.getCodigo(), null);
                break;
            case "Por Profesor":
                Profesor prof = (Profesor) profesorComboBox.getSelectedItem();
                reservasFiltradas = controlador.filtrarReservas(prof.getRut(), null, null, null);
                break;
            case "Por Sala":
                Sala sala = (Sala) salaComboBox.getSelectedItem();
                reservasFiltradas = controlador.filtrarReservas(null, sala.getNombre(), null, null);
                break;
            default: // "Todas"
                reservasFiltradas = controlador.getReservas();
                break;
        }
        tableModel.setReservas(reservasFiltradas);
    }

    private void setupTableMouseListener() {
        horarioTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = horarioTable.rowAtPoint(e.getPoint());
                int col = horarioTable.columnAtPoint(e.getPoint());
                if (col == 0 || row < 0) return;

                Object value = tableModel.getValueAt(row, col);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(HorarioVisualPanel.this);

                if (value instanceof Reserva) {
                    // --- CELDA OCUPADA: Mostrar diálogo de detalles ---
                    DialogoDetalleAsignacion dialogoDetalle = new DialogoDetalleAsignacion(topFrame, controlador, (Reserva) value);
                    dialogoDetalle.setVisible(true);
                    if (dialogoDetalle.isCancelacionExitosa()) {
                        aplicarFiltro();
                    }
                } else {
                    // --- CELDA VACÍA: Mostrar diálogo para crear ---
                    DiaSemana dia = DiaSemana.values()[col - 1];
                    BloqueHorario bloque = BloqueHorario.values()[row];
                    DialogoCrearAsignacion dialogoCrear = new DialogoCrearAsignacion(topFrame, controlador, dia, bloque);
                    dialogoCrear.setVisible(true);
                    if (dialogoCrear.isAsignacionCreada()) {
                        aplicarFiltro();
                    }
                }
            }
        });
    }
}