// controlador/ProfesorControlador.java
package controlador;

import modelo.Asignatura;
import modelo.Profesor;
import persistencia.JsonDataManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProfesorControlador {
    private final JsonDataManager jsonDataManager;
    private List<Profesor> profesores;
    private AsignacionControlador asignacionControlador;

    public ProfesorControlador(AsignacionControlador asignacionControlador) {
        this.jsonDataManager = new JsonDataManager();
        this.profesores = jsonDataManager.cargarProfesores();
        this.asignacionControlador = asignacionControlador;
    }

    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Profesor crearProfesor(String nombre, String rut, String departamento) {
        if (buscarProfesorPorRut(rut) != null) {
            System.err.println("Error de lógica (interna): RUT duplicado recibido para creación.");
            return null;
        }
        try {
            String id = UUID.randomUUID().toString();
            Profesor nuevoProfesor = new Profesor(nombre, rut, departamento, id);
            profesores.add(nuevoProfesor);
            jsonDataManager.guardarProfesores(profesores);
            return nuevoProfesor;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear profesor: " + e.getMessage());
            return null;
        }
    }

    public boolean eliminarProfesor(String rut) {
        Profesor profesor = buscarProfesorPorRut(rut);
        if (profesor == null) {
            return false;
        }
        profesores.remove(profesor);
        jsonDataManager.guardarProfesores(profesores);
        return true;
    }

    public boolean actualizarProfesor(Profesor profesor, String nuevoNombre, String nuevoDepartamento) {
        if (profesor == null) return false;
        boolean cambiado = false;
        if (nuevoNombre != null && !nuevoNombre.isEmpty() && !nuevoNombre.equals(profesor.getNombre())) {
            profesor.setNombre(nuevoNombre);
            cambiado = true;
        }
        if (nuevoDepartamento != null && !nuevoDepartamento.isEmpty() && !nuevoDepartamento.equals(profesor.getDepartamento())) {
            profesor.setDepartamento(nuevoDepartamento);
            cambiado = true;
        }
        if (cambiado) {
            jsonDataManager.guardarProfesores(profesores);
        }
        return cambiado;
    }

    public Profesor buscarProfesorPorRut(String rut) {
        for (Profesor p : profesores) {
            if (p.getRut().equalsIgnoreCase(rut)) {
                return p;
            }
        }
        return null;
    }

    public void listarProfesores() {
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }
        System.out.println("\n=== Listado de Profesores ===");
        for (int i = 0; i < profesores.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, profesores.get(i).toString());
        }
    }

    public List<Profesor> getProfesoresRegistrados() {
        return new ArrayList<>(profesores);
    }

    public List<Asignatura> getTodasLasAsignaturasDisponibles() {
        return jsonDataManager.cargarAsignaturas();
    }

    public boolean asignarAsignaturaAProfesor(Profesor profesor, Asignatura asignatura) {
        if (profesor == null || asignatura == null) {
            return false;
        }
        if (profesor.tieneAsignatura(asignatura.getCodigo())) {
            return false;
        }
        profesor.agregarAsignatura(asignatura);
        jsonDataManager.guardarProfesores(profesores);
        return true;
    }

    public List<Asignatura> getAsignaturasImpartidasPorProfesor(Profesor profesor) {
        if (profesor == null) {
            return new ArrayList<>();
        }
        return profesor.getAsignaturasImpartidas();
    }

    public List<Asignatura> getAsignaturasDisponiblesParaAsignar(Profesor profesor, List<Asignatura> todasAsignaturas) {
        if (profesor == null || todasAsignaturas == null) {
            return new ArrayList<>();
        }
        List<String> codigosImpartidos = profesor.getAsignaturasImpartidas().stream()
                .map(Asignatura::getCodigo)
                .collect(Collectors.toList());

        return todasAsignaturas.stream()
                .filter(asignatura -> !codigosImpartidos.contains(asignatura.getCodigo()))
                .collect(Collectors.toList());
    }
}