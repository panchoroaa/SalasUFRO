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
import java.util.function.Function;

public class JsonDataManager {
    private static final String DATA_FOLDER = "Datos";
    private static final ArchivoConfig RESERVAS = new ArchivoConfig("BaseDatosReservas.json");
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private record ArchivoConfig(String ruta, String nombreNodo) {
        public ArchivoConfig(String nombre) {
            this(DATA_FOLDER + File.separator + nombre, nombre.replace(".json", "").toLowerCase());
        }
    }

    private static final ArchivoConfig PROFESORES = new ArchivoConfig("BaseDatosProfesores.json");
    private static final ArchivoConfig SALAS = new ArchivoConfig("BaseDatosSalas.json");
    private static final ArchivoConfig RESERVAS = new ArchivoConfig("BaseDatosReservas.json");
    private static final ArchivoConfig RAMOS = new ArchivoConfig("BaseDatosRamos.json");

    public JsonDataManager() {
        inicializarDirectorio();
        inicializarArchivos();
    }

    private void inicializarDirectorio() {
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists() && dataDir.mkdirs()) {
            System.out.println("Carpeta '" + DATA_FOLDER + "' creada.");
        }
    }

    private void inicializarArchivos() {
        List.of(PROFESORES, SALAS, RESERVAS, RAMOS)
                .forEach(config -> inicializarArchivo(config.ruta(), config.nombreNodo()));
    }

    private void inicializarArchivo(String ruta, String nombreNodo) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists() || archivo.length() == 0) {
                ObjectNode rootNode = objectMapper.createObjectNode();
                rootNode.putArray(nombreNodo);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, rootNode);
                System.out.println("Archivo " + ruta + " inicializado.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar archivo " + ruta, e);
        }
    }

    private <T> List<T> cargarDatos(ArchivoConfig config,
                                    Function<JsonNode, T> convertidor) {
        List<T> datos = new ArrayList<>();
        try {
            File archivo = new File(config.ruta());
            if (!archivo.exists() || archivo.length() == 0) {
                return datos;
            }

            JsonNode rootNode = objectMapper.readTree(archivo);
            JsonNode arrayNode = rootNode.get(config.nombreNodo());

            if (arrayNode != null && arrayNode.isArray()) {
                for (JsonNode nodo : arrayNode) {
                    T elemento = convertidor.apply(nodo);
                    if (elemento != null) {
                        datos.add(elemento);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar " + config.ruta() + ": " + e.getMessage());
        }
        return datos;
    }

    private <T> void guardarDatos(List<T> datos,
                                  ArchivoConfig config,
                                  Function<T, ObjectNode> convertidor) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode arrayNode = rootNode.putArray(config.nombreNodo());

            datos.stream()
                    .map(convertidor)
                    .filter(node -> node != null)
                    .forEach(arrayNode::add);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(config.ruta()), rootNode);

            System.out.println("Datos guardados en " + config.ruta());
        } catch (IOException e) {
            System.err.println("Error al guardar en " + config.ruta() + ": " + e.getMessage());
        }
    }

    // Métodos específicos para Profesor
    public List<Profesor> cargarProfesores() {
        return cargarDatos(PROFESORES, this::crearProfesorDesdeJson);
    }

    public void guardarProfesores(List<Profesor> profesores) {
        guardarDatos(profesores, PROFESORES, this::crearJsonDesdeProfesor);
    }

    private Profesor crearProfesorDesdeJson(JsonNode node) {
        try {
            return new Profesor(
                    node.path("nombre").asText(""),
                    node.path("rut").asText(""),
                    node.path("departamento").asText(""),
                    node.path("ID").asText("")
            );
        } catch (Exception e) {
            System.err.println("Error al crear profesor: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeProfesor(Profesor profesor) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("nombre", profesor.getNombre());
            node.put("rut", profesor.getRut());
            node.put("departamento", profesor.getDepartamento());
            node.put("ID", profesor.getId());
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON de profesor: " + e.getMessage());
            return null;
        }
    }

    // Métodos específicos para Sala
    public List<Sala> cargarSalas() {
        return cargarDatos(SALAS, this::crearSalaDesdeJson);
    }

    public void guardarSalas(List<Sala> salas) {
        guardarDatos(salas, SALAS, this::crearJsonDesdeSala);
    }

    private Sala crearSalaDesdeJson(JsonNode node) {
        try {
            return new Sala(
                    node.path("nombre").asText(""),
                    node.path("capacidad").asInt(0)
            );
        } catch (Exception e) {
            System.err.println("Error al crear sala: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeSala(Sala sala) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("nombre", sala.getNombre());
            node.put("capacidad", sala.getCapacidad());
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON de sala: " + e.getMessage());
            return null;
        }
    }

    // Métodos para manejo de respaldos
    public void respaldarDatos() {
        List.of(PROFESORES, SALAS, RESERVAS, RAMOS)
                .forEach(config -> respaldarArchivo(config.ruta()));
    }

    public void restaurarRespaldos() {
        List.of(PROFESORES, SALAS, RESERVAS, RAMOS)
                .forEach(config -> restaurarArchivo(config.ruta()));
    }

    private void respaldarArchivo(String ruta) {
        try {
            File original = new File(ruta);
            if (original.exists()) {
                File backup = new File(ruta + ".bak");
                java.nio.file.Files.copy(
                        original.toPath(),
                        backup.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            System.err.println("Error al respaldar " + ruta + ": " + e.getMessage());
        }
    }

    private void restaurarArchivo(String ruta) {
        try {
            File backup = new File(ruta + ".bak");
            File actual = new File(ruta);
            if (backup.exists()) {
                java.nio.file.Files.copy(
                        backup.toPath(),
                        actual.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            System.err.println("Error al restaurar " + ruta + ": " + e.getMessage());


        }
    }

    public List<Reserva> cargarReservas(List<Profesor> profesores, List<Sala> salas) {
        return cargarDatos(RESERVAS, node -> crearReservaDesdeJson(node, profesores, salas));
    }

    public void guardarReservas(List<Reserva> reservas) {
        guardarDatos(reservas, RESERVAS, this::crearJsonDesdeReserva);
    }

    private Reserva crearReservaDesdeJson(JsonNode node, List<Profesor> profesores, List<Sala> salas) {
        try {
            String rutProfesor = node.path("rutProfesor").asText();
            String nombreSala = node.path("nombreSala").asText();
            String dia = node.path("dia").asText();
            String bloqueStr = node.path("bloque").asText();

            Profesor profesor = profesores.stream()
                    .filter(p -> p.getRut().equals(rutProfesor))
                    .findFirst()
                    .orElse(null);

            Sala sala = salas.stream()
                    .filter(s -> s.getNombre().equals(nombreSala))
                    .findFirst()
                    .orElse(null);

            if (profesor != null && sala != null) {
                BloqueHorario bloque = BloqueHorario.valueOf(bloqueStr);
                Asignatura asignatura = obtenerAsignaturaDeProfesor(profesor, node);
                return new Reserva(profesor, sala, asignatura, new Horario(dia, bloque));
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear reserva desde JSON: " + e.getMessage());
            return null;
        }
    }

    private Asignatura obtenerAsignaturaDeProfesor(Profesor profesor, JsonNode node) {
        String codigoAsignatura = node.path("codigoAsignatura").asText();
        return profesor.getAsignaturasImpartidas().stream()
                .filter(a -> a.getCodigo().equals(codigoAsignatura))
                .findFirst()
                .orElse(null);
    }

    private ObjectNode crearJsonDesdeReserva(Reserva reserva) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("rutProfesor", reserva.getProfesor().getRut());
            node.put("nombreSala", reserva.getSala().getNombre());
            node.put("codigoAsignatura", reserva.getAsignatura().getCodigo());
            node.put("dia", reserva.getHorario().getDia());
            node.put("bloque", reserva.getHorario().getBloque().name());
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde reserva: " + e.getMessage());
            return null;
        }
    }
}