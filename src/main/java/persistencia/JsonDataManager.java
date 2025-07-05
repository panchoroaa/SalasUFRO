package persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import modelo.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {
    private final ObjectMapper objectMapper;
    private static final String PROFESORES_FILE = "BaseDatosProfesores.json";
    private static final String SALAS_FILE = "BaseDatosSalas.json";
    private static final String RESERVAS_FILE = "BaseDatosReservas.json";

    public JsonDataManager() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        inicializarArchivosJson();
    }

    private void inicializarArchivosJson() {
        try {
            inicializarArchivo(PROFESORES_FILE, "profesores");
            inicializarArchivo(SALAS_FILE, "salas");
            inicializarArchivo(RESERVAS_FILE, "reservas");
        } catch (IOException e) {
            System.err.println("Error crítico al inicializar archivos JSON: " + e.getMessage());
            throw new RuntimeException("No se pudieron inicializar los archivos de base de datos", e);
        }
    }

    private void inicializarArchivo(String nombreArchivo, String nombreNodo) throws IOException {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.putArray(nombreNodo);
            objectMapper.writeValue(archivo, rootNode);
            System.out.println("Archivo " + nombreArchivo + " creado exitosamente.");
        } else if (archivo.length() == 0) {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.putArray(nombreNodo);
            objectMapper.writeValue(archivo, rootNode);
            System.out.println("Archivo " + nombreArchivo + " reinicializado.");
        }
    }

    public void verificarEstadoBD() {
        System.out.println("\n=== Estado de las Bases de Datos ===");
        verificarArchivo(PROFESORES_FILE, "Profesores");
        verificarArchivo(SALAS_FILE, "Salas");
        verificarArchivo(RESERVAS_FILE, "Reservas");
    }

    private void verificarArchivo(String nombreArchivo, String tipo) {
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            try {
                JsonNode rootNode = objectMapper.readTree(archivo);
                JsonNode arrayNode = rootNode.get(tipo.toLowerCase());
                int cantidadRegistros = arrayNode != null ? arrayNode.size() : 0;
                System.out.printf("%s: %d registros (%d bytes)%n",
                        tipo, cantidadRegistros, archivo.length());
            } catch (IOException e) {
                System.out.printf("%s: Error al leer archivo (%s)%n",
                        tipo, e.getMessage());
            }
        } else {
            System.out.printf("%s: Archivo no existe%n", tipo);
        }
    }

    public List<Profesor> cargarProfesores() {
        List<Profesor> profesores = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(PROFESORES_FILE));
            JsonNode profesoresNode = rootNode.get("profesores");

            if (profesoresNode != null && profesoresNode.isArray()) {
                for (JsonNode profesorNode : profesoresNode) {
                    Profesor profesor = crearProfesorDesdeJson(profesorNode);
                    if (profesor != null) {
                        profesores.add(profesor);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar profesores: " + e.getMessage());
        }
        return profesores;
    }

    private Profesor crearProfesorDesdeJson(JsonNode profesorNode) {
        try {
            String nombre = profesorNode.path("nombre").asText("");
            String rut = profesorNode.path("rut").asText("");
            String departamento = profesorNode.path("departamento").asText("");
            String id = profesorNode.path("id").asText("");

            Profesor profesor = new Profesor(nombre, rut, departamento, id);

            JsonNode asignaturasNode = profesorNode.path("asignaturasImpartidas");
            if (asignaturasNode.isArray()) {
                for (JsonNode asigNode : asignaturasNode) {
                    Asignatura asignatura = crearAsignaturaDesdeJson(asigNode);
                    if (asignatura != null) {
                        profesor.agregarAsignatura(asignatura);
                    }
                }
            }
            return profesor;
        } catch (Exception e) {
            System.err.println("Error al crear profesor desde JSON: " + e.getMessage());
            return null;
        }
    }

    private Asignatura crearAsignaturaDesdeJson(JsonNode asigNode) {
        try {
            return new Asignatura(
                    asigNode.path("nombre").asText(""),
                    asigNode.path("codigo").asText(""),
                    asigNode.path("carrera").asText(""),
                    asigNode.path("semestre").asInt(1),
                    asigNode.path("cantidadAlumnos").asInt(0)
            );
        } catch (Exception e) {
            System.err.println("Error al crear asignatura desde JSON: " + e.getMessage());
            return null;
        }
    }

    public void guardarProfesores(List<Profesor> profesores) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode profesoresArray = rootNode.putArray("profesores");

            for (Profesor profesor : profesores) {
                ObjectNode profesorNode = crearJsonDesdeProfesor(profesor);
                if (profesorNode != null) {
                    profesoresArray.add(profesorNode);
                }
            }

            objectMapper.writeValue(new File(PROFESORES_FILE), rootNode);
        } catch (IOException e) {
            System.err.println("Error al guardar profesores: " + e.getMessage());
        }
    }

    private ObjectNode crearJsonDesdeProfesor(Profesor profesor) {
        try {
            ObjectNode profesorNode = objectMapper.createObjectNode();
            profesorNode.put("nombre", profesor.getNombre());
            profesorNode.put("rut", profesor.getRut());
            profesorNode.put("departamento", profesor.getDepartamento());
            profesorNode.put("id", profesor.getId());

            ArrayNode asignaturasArray = profesorNode.putArray("asignaturasImpartidas");
            for (Asignatura asig : profesor.getAsignaturasImpartidas()) {
                ObjectNode asignaturaNode = crearJsonDesdeAsignatura(asig);
                if (asignaturaNode != null) {
                    asignaturasArray.add(asignaturaNode);
                }
            }

            return profesorNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde profesor: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeAsignatura(Asignatura asig) {
        try {
            ObjectNode asignaturaNode = objectMapper.createObjectNode();
            asignaturaNode.put("nombre", asig.getNombre());
            asignaturaNode.put("codigo", asig.getCodigo());
            asignaturaNode.put("carrera", asig.getCarrera());
            asignaturaNode.put("semestre", asig.getSemestre());
            asignaturaNode.put("cantidadAlumnos", asig.getCantidadAlumnos());
            return asignaturaNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde asignatura: " + e.getMessage());
            return null;
        }
    }

    public List<Sala> cargarSalas() {
        List<Sala> salas = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(SALAS_FILE));
            JsonNode salasNode = rootNode.get("salas");

            if (salasNode != null && salasNode.isArray()) {
                for (JsonNode salaNode : salasNode) {
                    Sala sala = crearSalaDesdeJson(salaNode);
                    if (sala != null) {
                        salas.add(sala);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar salas: " + e.getMessage());
        }
        return salas;
    }

    private Sala crearSalaDesdeJson(JsonNode salaNode) {
        try {
            String nombre = salaNode.path("nombre").asText("");
            int capacidad = salaNode.path("capacidad").asInt(0);
            String estado = salaNode.path("estado").asText("Disponible");
            List<Horario> horarios = new ArrayList<>();

            JsonNode horariosNode = salaNode.path("horariosOcupados");
            if (horariosNode.isArray()) {
                for (JsonNode horarioNode : horariosNode) {
                    Horario horario = crearHorarioDesdeJson(horarioNode);
                    if (horario != null) {
                        horarios.add(horario);
                    }
                }
            }

            return new Sala(nombre, capacidad, estado, horarios);
        } catch (Exception e) {
            System.err.println("Error al crear sala desde JSON: " + e.getMessage());
            return null;
        }
    }

    private Horario crearHorarioDesdeJson(JsonNode horarioNode) {
        try {
            String dia = horarioNode.path("dia").asText("");
            String bloqueStr = horarioNode.path("bloque").asText("");
            BloqueHorario bloque = BloqueHorario.valueOf(bloqueStr);
            return new Horario(dia, bloque);
        } catch (Exception e) {
            System.err.println("Error al crear horario desde JSON: " + e.getMessage());
            return null;
        }
    }

    public void guardarSalas(List<Sala> salas) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode salasArray = rootNode.putArray("salas");

            for (Sala sala : salas) {
                ObjectNode salaNode = crearJsonDesdeSala(sala);
                if (salaNode != null) {
                    salasArray.add(salaNode);
                }
            }

            objectMapper.writeValue(new File(SALAS_FILE), rootNode);
        } catch (IOException e) {
            System.err.println("Error al guardar salas: " + e.getMessage());
        }
    }

    private ObjectNode crearJsonDesdeSala(Sala sala) {
        try {
            ObjectNode salaNode = objectMapper.createObjectNode();
            salaNode.put("nombre", sala.getNombre());
            salaNode.put("capacidad", sala.getCapacidad());
            salaNode.put("estado", sala.getEstado());

            ArrayNode horariosArray = salaNode.putArray("horariosOcupados");
            for (Horario horario : sala.getHorariosOcupados()) {
                ObjectNode horarioNode = crearJsonDesdeHorario(horario);
                if (horarioNode != null) {
                    horariosArray.add(horarioNode);
                }
            }

            return salaNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde sala: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeHorario(Horario horario) {
        try {
            ObjectNode horarioNode = objectMapper.createObjectNode();
            horarioNode.put("dia", horario.getDia());
            horarioNode.put("bloque", horario.getBloque().name());
            return horarioNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde horario: " + e.getMessage());
            return null;
        }
    }

    public List<Reserva> cargarReservas() {
        List<Reserva> reservas = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(RESERVAS_FILE));
            JsonNode reservasNode = rootNode.get("reservas");

            if (reservasNode != null && reservasNode.isArray()) {
                for (JsonNode reservaNode : reservasNode) {
                    Reserva reserva = crearReservaDesdeJson(reservaNode);
                    if (reserva != null) {
                        reservas.add(reserva);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar reservas: " + e.getMessage());
        }
        return reservas;
    }

    private Reserva crearReservaDesdeJson(JsonNode reservaNode) {
        try {
            String rutProfesor = reservaNode.path("rutProfesor").asText();
            String nombreSala = reservaNode.path("nombreSala").asText();
            String codigoAsignatura = reservaNode.path("codigoAsignatura").asText();
            String dia = reservaNode.path("dia").asText();
            String bloqueStr = reservaNode.path("bloque").asText();

            Profesor profesor = buscarProfesor(rutProfesor);
            Sala sala = buscarSala(nombreSala);
            Asignatura asignatura = buscarAsignatura(profesor, codigoAsignatura);
            BloqueHorario bloque = BloqueHorario.valueOf(bloqueStr);

            if (profesor != null && sala != null && asignatura != null && bloque != null) {
                return new Reserva(profesor, sala, asignatura, new Horario(dia, bloque));
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear reserva desde JSON: " + e.getMessage());
            return null;
        }
    }

    public void guardarReservas(List<Reserva> reservas) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode reservasArray = rootNode.putArray("reservas");

            for (Reserva reserva : reservas) {
                ObjectNode reservaNode = crearJsonDesdeReserva(reserva);
                if (reservaNode != null) {
                    reservasArray.add(reservaNode);
                }
            }

            objectMapper.writeValue(new File(RESERVAS_FILE), rootNode);
        } catch (IOException e) {
            System.err.println("Error al guardar reservas: " + e.getMessage());
        }
    }

    private ObjectNode crearJsonDesdeReserva(Reserva reserva) {
        try {
            ObjectNode reservaNode = objectMapper.createObjectNode();
            reservaNode.put("rutProfesor", reserva.getProfesor().getRut());
            reservaNode.put("nombreSala", reserva.getSala().getNombre());
            reservaNode.put("codigoAsignatura", reserva.getAsignatura().getCodigo());
            reservaNode.put("dia", reserva.getHorario().getDia());
            reservaNode.put("bloque", reserva.getHorario().getBloque().name());
            return reservaNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde reserva: " + e.getMessage());
            return null;
        }
    }

    private Profesor buscarProfesor(String rut) {
        for (Profesor p : cargarProfesores()) {
            if (p.getRut().equals(rut)) return p;
        }
        return null;
    }

    private Sala buscarSala(String nombre) {
        for (Sala s : cargarSalas()) {
            if (s.getNombre().equals(nombre)) return s;
        }
        return null;
    }

    private Asignatura buscarAsignatura(Profesor profesor, String codigo) {
        if (profesor != null) {
            for (Asignatura a : profesor.getAsignaturasImpartidas()) {
                if (a.getCodigo().equals(codigo)) return a;
            }
        }
        return null;
    }

    public boolean existenDatos() {
        try {
            return !cargarProfesores().isEmpty() ||
                    !cargarSalas().isEmpty() ||
                    !cargarReservas().isEmpty();
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de datos: " + e.getMessage());
            return false;
        }
    }

    public void respaldaBaseDatos() {
        try {
            respaldarArchivo(PROFESORES_FILE);
            respaldarArchivo(SALAS_FILE);
            respaldarArchivo(RESERVAS_FILE);
            System.out.println("Respaldo de bases de datos completado exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al respaldar bases de datos: " + e.getMessage());
        }
    }

    private void respaldarArchivo(String nombreArchivo) throws IOException {
        File original = new File(nombreArchivo);
        if (original.exists()) {
            File backup = new File(nombreArchivo + ".bak");
            if (backup.exists()) {
                backup.delete();
            }
            if (!original.renameTo(backup)) {
                throw new IOException("No se pudo crear el respaldo de " + nombreArchivo);
            }
            inicializarArchivo(nombreArchivo,
                    nombreArchivo.replace("BaseDatos", "").replace(".json", "").toLowerCase());
        }
    }

    public void restaurarRespaldo() {
        try {
            restaurarArchivo(PROFESORES_FILE);
            restaurarArchivo(SALAS_FILE);
            restaurarArchivo(RESERVAS_FILE);
            System.out.println("Restauración de respaldo completada exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al restaurar respaldo: " + e.getMessage());
        }
    }

    private void restaurarArchivo(String nombreArchivo) throws IOException {
        File backup = new File(nombreArchivo + ".bak");
        File actual = new File(nombreArchivo);
        if (backup.exists()) {
            if (actual.exists()) {
                actual.delete();
            }
            if (!backup.renameTo(actual)) {
                throw new IOException("No se pudo restaurar el archivo " + nombreArchivo);
            }
        }
    }
}