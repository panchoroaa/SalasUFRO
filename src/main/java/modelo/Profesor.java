package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Profesor {
    private String nombre;
    private String rut; // Identificador principal
    private String departamento;
    private String id; // Si necesitas un ID adicional que no sea el RUT
    private List<Asignatura> asignaturasImpartidas; // ¡ESTA ES LA LÍNEA CRUCIAL QUE DEBE ESTAR AHÍ!

    // Constructor recomendado: Inicializa todos los datos esenciales
    public Profesor(String nombre, String rut, String departamento, String id) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del profesor no puede estar vacío.");
        }
        if (rut == null || rut.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT del profesor no puede estar vacío.");
        }
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new IllegalArgumentException("El departamento no puede estar vacío.");
        }
        // El 'id' puede ser nulo o vacío si no siempre se proporciona
        // Considera si 'id' es realmente necesario o si 'rut' es suficiente.
        // Si 'id' es el que se usa para cargar desde JSON y 'rut' es más para lógica de negocio,
        // podríamos tener que refactorizar la persistencia. Por ahora, lo incluimos.

        this.nombre = nombre.trim();
        this.rut = rut.trim();
        this.departamento = departamento.trim();
        this.id = (id != null) ? id.trim() : null; // Asigna id o null si es vacío

        this.asignaturasImpartidas = new ArrayList<>();
    }

    // Constructor simplificado si no se usa el 'id' al inicio
    public Profesor(String nombre, String rut, String departamento) {
        this(nombre, rut, departamento, null); // Llama al constructor completo con id=null
    }


    // Métodos para agregar/eliminar asignaturas (protegiendo el encapsulamiento)
    public void agregarAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new IllegalArgumentException("No se puede agregar una asignatura nula.");
        }
        if (!this.asignaturasImpartidas.contains(asignatura)) {
            this.asignaturasImpartidas.add(asignatura);
        }
    }

    public boolean eliminarAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            return false;
        }
        return this.asignaturasImpartidas.remove(asignatura);
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
    public String getDepartamento() { return departamento; }
    public String getId() { return id; } // Getter para el ID

    // Devuelve una copia inmutable de la lista de asignaturas
    public List<Asignatura> getAsignaturasImpartidas() {
        return Collections.unmodifiableList(asignaturasImpartidas);
    }

    // Setters (con validación básica)
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public void setDepartamento(String departamento) {
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new IllegalArgumentException("El departamento no puede estar vacío.");
        }
        this.departamento = departamento.trim();
    }

    public void setRut(String rut) { // Si el RUT es modificable, aunque no es lo ideal para un ID
        if (rut == null || rut.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT no puede estar vacío.");
        }
        this.rut = rut.trim();
    }

    public void setId(String id) { // Setter para el ID adicional
        this.id = (id != null) ? id.trim() : null;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Profesor {")
                .append("rut='").append(rut).append('\'')
                .append(", nombre='").append(nombre).append('\'')
                .append(", departamento='").append(departamento).append('\'');
        if (id != null) {
            sb.append(", id='").append(id).append('\'');
        }
        sb.append(", asignaturas=[");

        if (asignaturasImpartidas.isEmpty()) {
            sb.append("Ninguna");
        } else {
            List<String> tempAsignaturas = new ArrayList<>();
            for (Asignatura asig : asignaturasImpartidas) {
                // Incluimos nombre y código de la asignatura para mejor visibilidad
                tempAsignaturas.add(asig.getNombre() + " (" + asig.getCodigo() + ")");
            }
            sb.append(String.join("; ", tempAsignaturas));
        }
        sb.append("]}");
        return sb.toString();
    }

    // Método para verificar si el profesor tiene una asignatura específica (por código)
    public boolean tieneAsignatura(String codigoAsignatura) { // Renombrado de tieneRamo
        if (codigoAsignatura == null || codigoAsignatura.trim().isEmpty()) {
            return false;
        }
        for (Asignatura asignatura : asignaturasImpartidas) {
            if (asignatura.getCodigo().equalsIgnoreCase(codigoAsignatura.trim())) {
                return true;
            }
        }
        return false;
    }

    // equals y hashCode basados en el RUT (identificador único primario)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profesor profesor = (Profesor) o;
        return rut.equalsIgnoreCase(profesor.rut); // Comparar por RUT (case-insensitive)
    }

    @Override
    public int hashCode() {
        return Objects.hash(rut.toLowerCase()); // Hash del RUT en minúsculas
    }
}