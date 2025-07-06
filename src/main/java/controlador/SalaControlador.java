package controlador;

import modelo.Sala;
import modelo.GestionSala;
import modelo.EstadoSala;
import persistencia.JsonDataManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalaControlador {
    private final JsonDataManager jsonDataManager; // Asumimos que es correcto y funcional
    private List<GestionSala> gestionesSalas;
    private AsignacionControlador asignacionControlador;

    // Constructor
    public SalaControlador(AsignacionControlador asignacionControlador) {
        this.jsonDataManager = new JsonDataManager();
        // Asumimos que cargarGestionesSalas funciona
        this.gestionesSalas = jsonDataManager.cargarGestionesSalas();
        if (this.gestionesSalas == null) {
            this.gestionesSalas = new ArrayList<>();
        }
        this.asignacionControlador = asignacionControlador;
    }

    // Setter para inyectar la dependencia circular
    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Sala crearSala(String nombre, int capacidad) {
        // Asumimos que la unicidad del nombre se valida en SelectorMenu antes de llamar aquí.
        try {
            Sala nuevaSalaInformativa = new Sala(nombre, capacidad);
            GestionSala nuevaGestionSala = new GestionSala(nuevaSalaInformativa);
            gestionesSalas.add(nuevaGestionSala);
            // Asumimos que guardarGestionesSalas funciona
            jsonDataManager.guardarGestionesSalas(gestionesSalas);
            return nuevaSalaInformativa;
        } catch (IllegalArgumentException e) {
            System.err.println("Error interno al crear sala: " + e.getMessage());
            return null;
        }
    }

    public void listarSalas() {
        if (gestionesSalas.isEmpty()) {
            System.out.println("No hay salas registradas."); // OK, controlador muestra info simple.
            return;
        }
        System.out.println("\n=== Listado de Salas ===");
        for (int i = 0; i < gestionesSalas.size(); i++) {
            GestionSala gs = gestionesSalas.get(i);
            System.out.printf("%d. %s, Estado: %s%n", i + 1, gs.getSala().toString(), gs.getEstado().toString());
        }
    }

    public boolean actualizarEstadoSala(Sala salaInformativa, EstadoSala nuevoEstado) {
        GestionSala gestionSala = getGestionSalaPara(salaInformativa);
        if (gestionSala == null) {
            return false; // Sala no encontrada
        }
        try {
            gestionSala.setEstado(nuevoEstado);
            // Asumimos que guardarGestionesSalas funciona
            jsonDataManager.guardarGestionesSalas(gestionesSalas);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al actualizar estado de sala: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarSala(String nombre) {
        GestionSala gestionSalaAEliminar = gestionesSalas.stream()
                .filter(gs -> gs.getSala().getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
        if (gestionSalaAEliminar == null) {
            return false;
        }

        if (asignacionControlador != null && asignacionControlador.salaTieneReservasActivas(gestionSalaAEliminar.getSala())) {
            return false; // No se puede eliminar si tiene reservas activas
        }

        gestionesSalas.remove(gestionSalaAEliminar);
        // Asumimos que guardarGestionesSalas funciona
        jsonDataManager.guardarGestionesSalas(gestionesSalas);
        return true;
    }

    public List<Sala> getSalasRegistradas() {
        return gestionesSalas.stream()
                .map(GestionSala::getSala)
                .collect(Collectors.toList());
    }

    public Sala buscarSalaPorNombre(String nombre) {
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().getNombre().equalsIgnoreCase(nombre))
                .map(GestionSala::getSala)
                .findFirst()
                .orElse(null);
    }

    public GestionSala getGestionSalaPara(Sala salaInformativa) {
        // Asume que Sala.equals() está bien implementado (por nombre)
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().equals(salaInformativa))
                .findFirst()
                .orElse(null);
    }

    public EstadoSala getEstadoSala(Sala salaInformativa) {
        GestionSala gs = getGestionSalaPara(salaInformativa);
        return (gs != null) ? gs.getEstado() : null;
    }

    public List<GestionSala> getGestionesSalasRegistradas() {
        return new ArrayList<>(gestionesSalas);
    }

    public void guardarTodasLasGestionesSalasEnArchivo() {
        // Asumimos que guardarGestionesSalas funciona
        jsonDataManager.guardarGestionesSalas(gestionesSalas);
    }
}