package controlador;

import modelo.Asignatura;
import modelo.Profesor;
import persistencia.JsonDataManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfesorControlador {
    private final JsonDataManager jsonDataManager; // Asumimos que es correcto y funcional
    private List<Profesor> profesores;
    private AsignacionControlador asignacionControlador;

    // Constructor
    public ProfesorControlador(AsignacionControlador asignacionControlador) {
        this.jsonDataManager = new JsonDataManager();
        // Asumimos que cargarProfesores funciona
        this.profesores = jsonDataManager.cargarProfesores();
        this.asignacionControlador = asignacionControlador;
    }

    // Setter para inyectar la dependencia circular
    public void setAsignacionControlador(AsignacionControlador asignacionControlador) {
        this.asignacionControlador = asignacionControlador;
    }

    public Profesor crearProfesor(String nombre, String rut, String departamento) {
        // La validación de unicidad de RUT ya la haces en SelectorMenu antes de llamar aquí.
        // Este if es un "fallback" pero si la UI ya validó, no debería ejecutarse.
        if (buscarProfesorPorRut(rut) != null) {
            System.err.println("Error de lógica: RUT duplicado recibido en controlador.");
            return null;
        }

        try {
            String id = UUID.randomUUID().toString();
            Profesor nuevoProfesor = new Profesor(nombre, rut, departamento, id);
            profesores.add(nuevoProfesor);
            // Asumimos que guardarProfesores funciona
            jsonDataManager.guardarProfesores(profesores);
            return nuevoProfesor;
        } catch (IllegalArgumentException e) {
            System.err.println("Error interno al crear profesor: " + e.getMessage());
            return null;
        }
    }

    public void listarProfesores() {
        if (profesores.isEmpty()) {
            System.out.println("No hay profesores registrados."); // OK, controlador muestra info simple.
            return;
        }
        System.out.println("\n=== Listado de Profesores ===");
        for (int i = 0; i < profesores.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, profesores.get(i).toString());
        }
    }

    public boolean actualizarProfesor(Profesor profesor, String nuevoNombre, String nuevoDepartamento) {
        try {
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                profesor.setNombre(nuevoNombre);
            }
            if (nuevoDepartamento != null && !nuevoDepartamento.isEmpty()) {
                profesor.setDepartamento(nuevoDepartamento);
            }
            // Asumimos que guardarProfesores funciona
            jsonDataManager.guardarProfesores(profesores);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al actualizar profesor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProfesor(String rut) {
        Profesor profesor = buscarProfesorPorRut(rut);
        if (profesor == null) {
            return false; // Profesor no encontrado
        }

        if (asignacionControlador != null && asignacionControlador.profesorTieneReservasActivas(profesor)) {
            return false; // No se puede eliminar si tiene reservas activas
        }

        profesores.remove(profesor);
        // Asumimos que guardarProfesores funciona
        jsonDataManager.guardarProfesores(profesores);
        return true;
    }

    public boolean asignarAsignaturaAProfesor(Profesor profesor, Asignatura asignatura) {
        if (profesor.tieneAsignatura(asignatura.getCodigo())) {
            return false; // Ya tiene la asignatura
        }
        profesor.agregarAsignatura(asignatura);
        // Asumimos que guardarProfesores funciona
        jsonDataManager.guardarProfesores(profesores);
        return true;
    }

    public Profesor buscarProfesorPorRut(String rut) {
        for (Profesor p : profesores) {
            if (p.getRut().equalsIgnoreCase(rut)) {
                return p;
            }
        }
        return null;
    }

    public List<Profesor> getProfesoresRegistrados() {
        return new ArrayList<>(profesores);
    }

    public List<Asignatura> getAsignaturasImpartidasPorProfesor(Profesor profesor) {
        return new ArrayList<>(profesor.getAsignaturasImpartidas());
    }

    public List<Asignatura> getTodasLasAsignaturasDisponibles() {
        // Asumimos que cargarAsignaturas en JsonDataManager funciona y devuelve todas.
        return jsonDataManager.cargarAsignaturas();
    }

    public List<Asignatura> getAsignaturasDisponiblesParaAsignar(Profesor profesor, List<Asignatura> todasLasAsignaturas) {
        List<Asignatura> asignaturasDisponibles = new ArrayList<>();
        for (Asignatura asig : todasLasAsignaturas) {
            if (!profesor.tieneAsignatura(asig.getCodigo())) {
                asignaturasDisponibles.add(asig);
            }
        }
        return asignaturasDisponibles;
    }
}