// controlador/SalaControlador.java
package controlador;

import modelo.Sala;
import modelo.GestionSala;
import modelo.EstadoSala;
import persistencia.JsonDataManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalaControlador {
    private final JsonDataManager jsonDataManager;
    private List<GestionSala> gestionesSalas;
    private AsignacionControlador asignacionControlador;

    public SalaControlador(AsignacionControlador asignacionControlador) {
        this.jsonDataManager = new JsonDataManager();
        this.gestionesSalas = jsonDataManager.cargarGestionesSalas();
        if (this.gestionesSalas == null) {
            this.gestionesSalas = new ArrayList<>();
        }
        this.asignacionControlador = asignacionControlador;
    }

    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Sala crearSala(String nombre, int capacidad) {
        try {
            Sala nuevaSalaInformativa = new Sala(nombre, capacidad);
            GestionSala nuevaGestionSala = new GestionSala(nuevaSalaInformativa);
            gestionesSalas.add(nuevaGestionSala);
            jsonDataManager.guardarGestionesSalas(gestionesSalas);
            return nuevaSalaInformativa;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear sala: " + e.getMessage());
            return null;
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
        gestionesSalas.remove(gestionSalaAEliminar);
        jsonDataManager.guardarGestionesSalas(gestionesSalas);
        return true;
    }

    public boolean actualizarEstadoSala(Sala sala, EstadoSala nuevoEstado) {
        if (sala == null || nuevoEstado == null) return false;
        GestionSala gestionSala = getGestionSalaPara(sala);
        if (gestionSala == null) return false;

        if (gestionSala.getEstado() != nuevoEstado) {
            gestionSala.setEstado(nuevoEstado);
            jsonDataManager.guardarGestionesSalas(gestionesSalas);
            return true;
        }
        return false;
    }

    public EstadoSala getEstadoSala(Sala sala) {
        GestionSala gestionSala = getGestionSalaPara(sala);
        return (gestionSala != null) ? gestionSala.getEstado() : null;
    }

    public List<Sala> getSalasRegistradas() {
        return gestionesSalas.stream()
                .map(GestionSala::getSala)
                .collect(Collectors.toList());
    }

    public void listarSalas() {
        if (gestionesSalas.isEmpty()) {
            System.out.println("No hay salas registradas.");
            return;
        }
        System.out.println("\n=== Listado de Salas ===");
        for (int i = 0; i < gestionesSalas.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, gestionesSalas.get(i).getSala().toString());
            System.out.println("   Estado: " + gestionesSalas.get(i).getEstado());
            System.out.println("   Horarios Ocupados: " + gestionesSalas.get(i).getHorariosOcupados().size());
        }
    }

    public Sala buscarSalaPorNombre(String nombre) {
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().getNombre().equalsIgnoreCase(nombre))
                .map(GestionSala::getSala)
                .findFirst()
                .orElse(null);
    }

    public GestionSala getGestionSalaPara(Sala salaInformativa) {
        return gestionesSalas.stream()
                .filter(gs -> gs.getSala().equals(salaInformativa))
                .findFirst()
                .orElse(null);
    }

    public List<GestionSala> getGestionesSalasRegistradas() {
        return new ArrayList<>(gestionesSalas);
    }

    public void guardarTodasLasGestionesSalasEnArchivo() {
        jsonDataManager.guardarGestionesSalas(gestionesSalas);
    }
}