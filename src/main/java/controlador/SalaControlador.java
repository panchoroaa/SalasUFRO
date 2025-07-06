package controlador;

import modelo.GestionSala;
import modelo.Sala;
import modelo.EstadoSala;
import persistencia.JsonDataManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SalaControlador {
    private final JsonDataManager jsonDataManager;
    private List<GestionSala> gestionesSalas;
    private AsignacionControlador asignacionControlador; // Se inyecta después

    public SalaControlador(JsonDataManager jsonDataManager) {
        this.jsonDataManager = Objects.requireNonNull(jsonDataManager, "JsonDataManager no puede ser nulo.");
        this.gestionesSalas = jsonDataManager.cargarGestionesSalas(); // Ahora carga directamente GestionSala
        if (this.gestionesSalas == null) {
            this.gestionesSalas = new ArrayList<>();
        }
    }

    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Sala crearSala(String nombre, int capacidad) {
        if (buscarSalaPorNombre(nombre) != null) {
            System.err.println("Error: Ya existe una sala con este nombre.");
            return null;
        }
        Sala nuevaSala = new Sala(nombre, capacidad);
        GestionSala nuevaGestionSala = new GestionSala(nuevaSala);
        gestionesSalas.add(nuevaGestionSala);
        jsonDataManager.guardarGestionesSalas(gestionesSalas); // Guarda lista de GestionSala
        return nuevaSala;
    }

    public Sala buscarSalaPorNombre(String nombre) {
        return gestionesSalas.stream()
                .map(GestionSala::getSala)
                .filter(s -> s.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public GestionSala getGestionSalaPara(Sala sala) {
        if (sala == null) return null;
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().equals(sala))
                .findFirst()
                .orElse(null);
    }

    public boolean actualizarEstadoSala(Sala sala, EstadoSala nuevoEstado) {
        GestionSala gestionSala = getGestionSalaPara(sala);
        if (gestionSala == null) {
            System.err.println("Error: Sala no encontrada para actualizar estado.");
            return false;
        }
        if (gestionSala.getEstado() == nuevoEstado) {
            System.out.println("La sala ya tiene ese estado. No se realizan cambios.");
            return false;
        }
        gestionSala.setEstado(nuevoEstado);
        jsonDataManager.guardarGestionesSalas(gestionesSalas); // Guarda lista de GestionSala
        return true;
    }

    public boolean eliminarSala(String nombre) {
        Sala salaAEliminar = buscarSalaPorNombre(nombre);
        if (salaAEliminar == null) {
            System.err.println("Error: Sala no encontrada.");
            return false;
        }
        // AsignacionControlador se inyecta después, importante el null check
        if (asignacionControlador != null && asignacionControlador.salaTieneReservasActivas(salaAEliminar)) {
            System.err.println("Error: No se puede eliminar la sala porque tiene asignaciones activas.");
            return false;
        }

        boolean eliminado = gestionesSalas.removeIf(gs -> gs.getSala().equals(salaAEliminar));
        if (eliminado) {
            jsonDataManager.guardarGestionesSalas(gestionesSalas); // Guarda lista de GestionSala
        }
        return eliminado;
    }

    public void listarSalas() {
        if (gestionesSalas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }
        System.out.println("\n--- Lista de Salas ---");
        gestionesSalas.forEach(gs -> System.out.println("Sala: " + gs.getSala().getNombre() +
                ", Capacidad: " + gs.getSala().getCapacidad() +
                ", Estado: " + gs.getEstado()));
    }

    public List<Sala> getSalasRegistradasPuras() { // Devuelve solo objetos Sala
        return gestionesSalas.stream()
                .map(GestionSala::getSala)
                .collect(Collectors.toList());
    }

    public List<GestionSala> getGestionesSalasRegistradas() { // Devuelve las GestionSala completas
        return new ArrayList<>(gestionesSalas);
    }

    public EstadoSala getEstadoSala(Sala sala) {
        return Optional.ofNullable(getGestionSalaPara(sala))
                .map(GestionSala::getEstado)
                .orElse(null);
    }

    public void guardarCambiosEnGestionSala(GestionSala gestionSala) {
        // Encuentra la gestionSala en la lista y la actualiza
        // Esto es importante porque si no, solo se modifica la referencia local
        int index = gestionesSalas.indexOf(gestionSala);
        if (index != -1) {
            gestionesSalas.set(index, gestionSala); // Actualiza la instancia en la lista
            jsonDataManager.guardarGestionesSalas(this.gestionesSalas);
        } else {
            System.err.println("Advertencia: GestionSala no encontrada en la lista para guardar. Puede haber inconsistencia.");
        }
    }
}