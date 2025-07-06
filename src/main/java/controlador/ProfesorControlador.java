package controlador;

import modelo.Asignatura;
import modelo.Profesor;
import persistencia.JsonDataManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProfesorControlador {
    private final JsonDataManager jsonDataManager;
    private List<Profesor> profesores;
    private AsignacionControlador asignacionControlador; // Se inyecta después

    public ProfesorControlador(JsonDataManager jsonDataManager) {
        this.jsonDataManager = Objects.requireNonNull(jsonDataManager, "JsonDataManager no puede ser nulo.");
        this.profesores = jsonDataManager.cargarProfesores();
    }

    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Profesor crearProfesor(String nombre, String rut, String departamento) {
        if (buscarProfesorPorRut(rut) != null) {
            System.err.println("Error: Ya existe un profesor con este RUT.");
            return null;
        }
        Profesor nuevoProfesor = new Profesor(nombre, rut, departamento, null);
        profesores.add(nuevoProfesor);
        jsonDataManager.guardarProfesores(profesores);
        return nuevoProfesor;
    }

    public Profesor buscarProfesorPorRut(String rut) {
        return profesores.stream()
                .filter(p -> p.getRut().equalsIgnoreCase(rut))
                .findFirst()
                .orElse(null);
    }

    public boolean actualizarProfesor(Profesor profesor, String nuevoNombre, String nuevoDepartamento) {
        if (profesor == null) {
            System.err.println("Error: Profesor a actualizar no puede ser nulo.");
            return false;
        }
        boolean cambios = false;
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty() && !profesor.getNombre().equals(nuevoNombre)) {
            profesor.setNombre(nuevoNombre.trim());
            cambios = true;
        }
        if (nuevoDepartamento != null && !nuevoDepartamento.trim().isEmpty() && !profesor.getDepartamento().equals(nuevoDepartamento)) {
            profesor.setDepartamento(nuevoDepartamento.trim());
            cambios = true;
        }
        if (cambios) {
            jsonDataManager.guardarProfesores(profesores);
        }
        return cambios;
    }

    public boolean eliminarProfesor(String rut) {
        Profesor profesorAEliminar = buscarProfesorPorRut(rut);
        if (profesorAEliminar == null) {
            System.err.println("Error: Profesor no encontrado.");
            return false;
        }
        // AsignacionControlador se inyecta después, así que la comprobación de nulo es importante
        if (asignacionControlador != null && asignacionControlador.profesorTieneReservasActivas(profesorAEliminar)) {
            System.err.println("Error: No se puede eliminar el profesor porque tiene asignaciones activas.");
            return false;
        }

        boolean eliminado = profesores.remove(profesorAEliminar);
        if (eliminado) {
            jsonDataManager.guardarProfesores(profesores);
        }
        return eliminado;
    }

    public void listarProfesores() {
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }
        System.out.println("\n--- Lista de Profesores ---");
        profesores.forEach(profesor -> {
            System.out.println(profesor.toString());
            if (!profesor.getAsignaturasImpartidas().isEmpty()) {
                System.out.println("  Asignaturas: " + profesor.getAsignaturasImpartidas().stream()
                        .map(Asignatura::getCodigo)
                        .collect(Collectors.joining(", ")));
            }
        });
    }

    public List<Profesor> getProfesoresRegistrados() {
        return new ArrayList<>(profesores);
    }

    public boolean asignarAsignaturaAProfesor(Profesor profesor, Asignatura asignatura) {
        if (profesor == null || asignatura == null) {
            System.err.println("Error: Profesor o asignatura no pueden ser nulos.");
            return false;
        }
        if (profesor.tieneAsignatura(asignatura.getCodigo())) {
            System.err.println("Error: El profesor " + profesor.getNombre() + " ya imparte la asignatura " + asignatura.getNombre() + ".");
            return false;
        }
        profesor.agregarAsignatura(asignatura);
        jsonDataManager.guardarProfesores(profesores); // Guardar cambios en profesores (con la nueva asignatura)
        return true;
    }

    public List<Asignatura> getTodasLasAsignaturasDisponibles() {
        // Ahora se cargan de JsonDataManager directamente como una entidad.
        return jsonDataManager.cargarAsignaturas();
    }

    public List<Asignatura> getAsignaturasDisponiblesParaAsignar(Profesor profesor, List<Asignatura> todasAsignaturas) {
        return todasAsignaturas.stream()
                .filter(a -> !profesor.tieneAsignatura(a.getCodigo()))
                .collect(Collectors.toList());
    }

    public List<Asignatura> getAsignaturasImpartidasPorProfesor(Profesor profesor) {
        if (profesor == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(profesor.getAsignaturasImpartidas());
    }

    public boolean agregarAsignatura(Asignatura asignatura) {
        // Método para agregar una asignatura a la base de datos de asignaturas
        List<Asignatura> asignaturas = jsonDataManager.cargarAsignaturas();
        if (asignaturas.stream().anyMatch(a -> a.getCodigo().equalsIgnoreCase(asignatura.getCodigo()))) {
            System.err.println("Error: Ya existe una asignatura con este código.");
            return false;
        }
        asignaturas.add(asignatura);
        jsonDataManager.guardarAsignaturas(asignaturas);
        return true;
    }
}