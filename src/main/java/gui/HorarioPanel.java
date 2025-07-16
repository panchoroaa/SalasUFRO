package gui;

import controlador.AsignacionControlador;
import modelo.Asignatura;
import modelo.DiaSemana;
import modelo.Profesor;
import modelo.Reserva;
import modelo.Sala;
import modelo.Horario;
import modelo.BloqueHorario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class HorarioPanel extends JFrame {

    private AsignacionControlador controlador;

    private JComboBox<String> filtroTipoComboBox;
    private JPanel filtroPanelDinamico;

    private JComboBox<Asignatura> asignaturaComboBox;
    private JComboBox<Profesor> profesorComboBox;
    private JComboBox<Sala> salaComboBox;
    private JComboBox<DiaSemana> diaComboBox;

    private JTable horarioTable;
    private DefaultTableModel tableModel;
    private JButton aplicarFiltroButton; // Declarado como miembro de la clase [MODIFICACIÓN CLAVE]

    public HorarioPanel(AsignacionControlador controlador) {
        this.controlador = controlador;
        setTitle("Visualizador de Horarios de Asignaciones");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
        loadInitialData();
    }

    private void initComponents() {
        filtroTipoComboBox = new JComboBox<>(new String[]{"Por Asignatura", "Por Profesor", "Por Sala"});
        filtroPanelDinamico = new JPanel(new CardLayout());

        JPanel filtroAsignaturaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        asignaturaComboBox = new JComboBox<>();
        filtroAsignaturaPanel.add(new JLabel("Asignatura:"));
        filtroAsignaturaPanel.add(asignaturaComboBox);

        JPanel filtroProfesorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profesorComboBox = new JComboBox<>();
        filtroProfesorPanel.add(new JLabel("Profesor:"));
        filtroProfesorPanel.add(profesorComboBox);

        JPanel filtroSalaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salaComboBox = new JComboBox<>();
        filtroSalaPanel.add(new JLabel("Sala:"));
        filtroSalaPanel.add(salaComboBox);

        filtroPanelDinamico.add(filtroAsignaturaPanel, "Por Asignatura");
        filtroPanelDinamico.add(filtroProfesorPanel, "Por Profesor");
        filtroPanelDinamico.add(filtroSalaPanel, "Por Sala");

        diaComboBox = new JComboBox<>();
        diaComboBox.addItem(null);
        diaComboBox.setSelectedItem(null);
        for (DiaSemana dia : DiaSemana.values()) {
            diaComboBox.addItem(dia);
        }
        diaComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todos los Días");
                }
                return this;
            }
        });

        aplicarFiltroButton = new JButton("Aplicar Filtro"); // Inicializado aquí

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(new String[]{"Profesor", "Asignatura", "Sala", "Día", "Bloque"});
        horarioTable = new JTable(tableModel);
        horarioTable.setFillsViewportHeight(true);

        filtroTipoComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(filtroPanelDinamico.getLayout());
                cl.show(filtroPanelDinamico, (String)filtroTipoComboBox.getSelectedItem());
                aplicarFiltro();
            }
        });

        aplicarFiltroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltro();
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Opciones de Filtro"));

        JPanel filtroSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtroSelectorPanel.add(new JLabel("Filtrar por:"));
        filtroSelectorPanel.add(filtroTipoComboBox);
        filtroSelectorPanel.add(new JLabel("Día de la Semana:"));
        filtroSelectorPanel.add(diaComboBox);
        filtroSelectorPanel.add(aplicarFiltroButton);

        topPanel.add(filtroSelectorPanel, BorderLayout.NORTH);
        topPanel.add(filtroPanelDinamico, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(horarioTable), BorderLayout.CENTER);
    }

    private void loadInitialData() {
        controlador.getAsignaturas().forEach(asignaturaComboBox::addItem);
        controlador.getProfesores().forEach(profesorComboBox::addItem);
        controlador.getSalas().forEach(salaComboBox::addItem);

        filtroTipoComboBox.setSelectedItem("Por Asignatura");

        if (asignaturaComboBox.getItemCount() > 0 && asignaturaComboBox.getSelectedItem() == null) {
            asignaturaComboBox.setSelectedIndex(0);
        }
        if (profesorComboBox.getItemCount() > 0 && profesorComboBox.getSelectedItem() == null) {
            profesorComboBox.setSelectedIndex(0);
        }
        if (salaComboBox.getItemCount() > 0 && salaComboBox.getSelectedItem() == null) {
            salaComboBox.setSelectedIndex(0);
        }

        aplicarFiltro();
    }

    private void aplicarFiltro() {
        String rutProfesor = null;
        String nombreSala = null;
        String codigoAsignatura = null;
        DiaSemana diaSeleccionado = (DiaSemana) diaComboBox.getSelectedItem();

        String tipoFiltro = (String) filtroTipoComboBox.getSelectedItem();

        switch (tipoFiltro) {
            case "Por Asignatura":
                if (asignaturaComboBox.getSelectedItem() instanceof Asignatura) {
                    codigoAsignatura = ((Asignatura) asignaturaComboBox.getSelectedItem()).getCodigo();
                }
                break;
            case "Por Profesor":
                if (profesorComboBox.getSelectedItem() instanceof Profesor) {
                    rutProfesor = ((Profesor) profesorComboBox.getSelectedItem()).getRut();
                }
                break;
            case "Por Sala":
                if (salaComboBox.getSelectedItem() instanceof Sala) {
                    nombreSala = ((Sala) salaComboBox.getSelectedItem()).getNombre();
                }
                break;
        }

        List<Reserva> reservasFiltradas = controlador.filtrarReservas(rutProfesor, nombreSala, codigoAsignatura, diaSeleccionado);
        updateTable(reservasFiltradas);
    }

    private void updateTable(List<Reserva> reservas) {
        tableModel.setRowCount(0);

        if (reservas.isEmpty()) {
            tableModel.addRow(new Object[]{"No hay reservas para los filtros seleccionados.", "", "", "", ""});
            return;
        }

        for (Reserva r : reservas) {
            Optional<Profesor> profOpt = controlador.getProfesorPorRut(r.getRutProfesor());
            Optional<Asignatura> asigOpt = controlador.getAsignaturaPorCodigo(r.getCodigoAsignatura());
            Optional<Sala> salaOpt = controlador.getSalaPorNombre(r.getNombreSala());

            String nombreProfesor = profOpt.map(Profesor::getNombre).orElse(r.getRutProfesor() + " (Desconocido)");
            String nombreAsignatura = asigOpt.map(Asignatura::getNombre).orElse(r.getCodigoAsignatura() + " (Desconocida)");
            String nombreSala = salaOpt.map(Sala::getNombre).orElse(r.getNombreSala() + " (Desconocida)");

            tableModel.addRow(new Object[]{
                    nombreProfesor,
                    nombreAsignatura,
                    nombreSala,
                    r.getHorario() != null ? r.getHorario().getDia().toString() : "N/A",
                    r.getHorario() != null ? r.getHorario().getBloqueEnum().toString() : "N/A"
            });
        }
    }
}