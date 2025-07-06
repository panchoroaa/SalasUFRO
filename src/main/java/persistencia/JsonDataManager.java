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
import java.util.Optional;
import java.util.function.Function;

public class JsonDataManager {
    private static final String DATA_FOLDER = "Datos";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private record ArchivoConfig(String ruta, String nombreNodo) {
        public ArchivoConfig(String nombre) {
            this(DATA_FOLDER + File.separator + nombre, nombre.replace(".json", "").toLowerCase());
        }
    }

    private static final ArchivoConfig PROFESORES = new ArchivoConfig("BaseDatosProfesores.json");
    // Ahora Sala manejará GestionSala para persistencia
    private static final ArchivoConfig GESTIONES_SALAS = new ArchivoConfig("BaseDatosGestionesSalas.json");
    private static final ArchivoConfig RESERVAS = new ArchivoConfig("BaseDatosReservas.json");
    private static final ArchivoConfig ASIGNATURAS = new ArchivoConfig("BaseDatosAsignaturas.json"); // Para gestionar asignaturas independientemente

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
        // Asegúrate de usar GESTIONES_SALAS en lugar de SALAS aquí
        List.of(PROFESORES, GESTIONES_SALAS, RESERVAS, ASIGNATURAS)
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

    // Sobrecarga para cargar datos que requieren referencias a otras entidades
    private <T, R1, R2, R3> List<T> cargarDatos(ArchivoConfig config,
                                                Function<JsonNode, T> convertidor,
                                                List<R1> refList1, List<R2> refList2, List<R3> refList3) {
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
                    T elemento = convertidor.apply(nodo); // El convertidor debe manejar las listas de referencia
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

    // --- Métodos específicos para Profesor ---
    public List<Profesor> cargarProfesores() {
        return cargarDatos(PROFESORES, this::crearProfesorDesdeJson);
    }

    public void guardarProfesores(List<Profesor> profesores) {
        guardarDatos(profesores, PROFESORES, this::crearJsonDesdeProfesor);
    }

    private Profesor crearProfesorDesdeJson(JsonNode node) {
        try {
            // Carga las asignaturas impartidas por el profesor
            List<Asignatura> asignaturasImpartidas = new ArrayList<>();
            JsonNode asignaturasNode = node.path("asignaturasImpartidas");
            if (asignaturasNode.isArray()) {
                for (JsonNode asigNode : asignaturasNode) {
                    asignaturasImpartidas.add(crearAsignaturaDesdeJson(asigNode));
                }
            }

            Profesor profesor = new Profesor(
                    node.path("nombre").asText(""),
                    node.path("rut").asText(""),
                    node.path("departamento").asText(""),
                    node.path("ID").asText("")
            );
            profesor.setAsignaturasImpartidas(asignaturasImpartidas);
            return profesor;
        } catch (Exception e) {
            System.err.println("Error al crear profesor desde JSON: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeProfesor(Profesor profesor) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("nombre", profesor.getNombre());
            node.put("rut", profesor.getRut());
            node.put("departamento", profesor.getDepartamento());
            node.put("ID", profesor.getID()); // Usar getID()
            // Guarda las asignaturas impartidas por el profesor
            ArrayNode asignaturasArray = node.putArray("asignaturasImpartidas");
            profesor.getAsignaturasImpartidas().stream()
                    .map(this::crearJsonDesdeAsignatura)
                    .forEach(asignaturasArray::add);
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde profesor: " + e.getMessage());
            return null;
        }
    }

    // --- Métodos específicos para Asignatura ---
    public List<Asignatura> cargarAsignaturas() {
        return cargarDatos(ASIGNATURAS, this::crearAsignaturaDesdeJson);
    }

    public void guardarAsignaturas(List<Asignatura> asignaturas) {
        guardarDatos(asignaturas, ASIGNATURAS, this::crearJsonDesdeAsignatura);
    }

    private Asignatura crearAsignaturaDesdeJson(JsonNode node) {
        try {
            return new Asignatura(
                    node.path("nombre").asText(""),
                    node.path("codigo").asText(""),
                    node.path("cantidadAlumnos").asInt(0)
            );
        } catch (Exception e) {
            System.err.println("Error al crear asignatura: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeAsignatura(Asignatura asignatura) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("nombre", asignatura.getNombre());
            node.put("codigo", asignatura.getCodigo());
            node.put("cantidadAlumnos", asignatura.getCantidadAlumnos());
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON de asignatura: " + e.getMessage());
            return null;
        }
    }

    // --- Métodos específicos para GestionSala ---
    public List<GestionSala> cargarGestionesSalas() {
        // Al cargar GestionSala, se necesitan Sala y los horarios ocupados.
        // Sala es parte de GestionSala, se puede crear al momento.
        // Horarios ocupados también se crean al momento.
        return cargarDatos(GESTIONES_SALAS, this::crearGestionSalaDesdeJson);
    }

    public void guardarGestionesSalas(List<GestionSala> gestionesSalas) {
        guardarDatos(gestionesSalas, GESTIONES_SALAS, this::crearJsonDesdeGestionSala);
    }

    private GestionSala crearGestionSalaDesdeJson(JsonNode node) {
        try {
            // Crea la Sala primero
            Sala sala = crearSalaDesdeJson(node.path("sala"));
            EstadoSala estado = EstadoSala.valueOf(node.path("estado").asText(EstadoSala.DISPONIBLE.name()));

            GestionSala gestionSala = new GestionSala(sala);
            gestionSala.setEstado(estado);

            // Carga los Horarios ocupados
            JsonNode horariosNode = node.path("horariosOcupados");
            if (horariosNode.isArray()) {
                List<Horario> horariosOcupados = new ArrayList<>();
                for (JsonNode horarioNode : horariosNode) {
                    horariosOcupados.add(crearHorarioDesdeJson(horarioNode));
                }
                gestionSala.setHorariosOcupados(horariosOcupados);
            }
            return gestionSala;
        } catch (Exception e) {
            System.err.println("Error al crear GestionSala desde JSON: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeGestionSala(GestionSala gestionSala) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.set("sala", crearJsonDesdeSala(gestionSala.getSala())); // Guarda la sala dentro
            node.put("estado", gestionSala.getEstado().name()); // Guarda el nombre del enum

            // Guarda los horarios ocupados
            ArrayNode horariosArray = node.putArray("horariosOcupados");
            gestionSala.getHorariosOcupados().stream()
                    .map(this::crearJsonDesdeHorario)
                    .forEach(horariosArray::add);
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde GestionSala: " + e.getMessage());
            return null;
        }
    }

    // Métodos auxiliares para Sala (usados dentro de GestionSala)
    private Sala crearSalaDesdeJson(JsonNode node) {
        try {
            return new Sala(
                    node.path("nombre").asText(""),
                    node.path("capacidad").asInt(0)
            );
        } catch (Exception e) {
            System.err.println("Error al crear sala (aux): " + e.getMessage());
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
            System.err.println("Error al crear JSON de sala (aux): " + e.getMessage());
            return null;
        }
    }

    // Métodos auxiliares para Horario (usados dentro de GestionSala y Reserva)
    private Horario crearHorarioDesdeJson(JsonNode node) {
        try {
            String dia = node.path("dia").asText("");
            // Utiliza el nombre del enum para BloqueHorario
            BloqueHorario bloque = BloqueHorario.valueOf(node.path("bloque").asText(""));
            return new Horario(dia, bloque);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al parsear BloqueHorario: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear horario desde JSON: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeHorario(Horario horario) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("dia", horario.getDia());
            node.put("bloque", horario.getBloque().name()); // Guarda el nombre del enum
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde horario: " + e.getMessage());
            return null;
        }
    }

    // --- Métodos específicos para Reserva ---
    // Ahora recibe todas las listas de referencia para resolver dependencias
    public List<Reserva> cargarReservas(List<Profesor> profesores, List<Sala> salas, List<Asignatura> asignaturas) {
        return cargarDatos(RESERVAS, node -> crearReservaDesdeJson(node, profesores, salas, asignaturas));
    }

    public void guardarReservas(List<Reserva> reservas) {
        guardarDatos(reservas, RESERVAS, this::crearJsonDesdeReserva);
    }

    private Reserva crearReservaDesdeJson(JsonNode node, List<Profesor> profesores, List<Sala> salas, List<Asignatura> asignaturas) {
        try {
            String rutProfesor = node.path("rutProfesor").asText();
            String nombreSala = node.path("nombreSala").asText();
            String codigoAsignatura = node.path("codigoAsignatura").asText();
            JsonNode horarioNode = node.path("horario"); // El horario ahora es un objeto anidado

            Profesor profesor = profesores.stream()
                    .filter(p -> p.getRut().equals(rutProfesor))
                    .findFirst()
                    .orElse(null);

            Sala sala = salas.stream()
                    .filter(s -> s.getNombre().equals(nombreSala))
                    .findFirst()
                    .orElse(null);

            Asignatura asignatura = asignaturas.stream()
                    .filter(a -> a.getCodigo().equals(codigoAsignatura))
                    .findFirst()
                    .orElse(null);

            Horario horario = null;
            if (horarioNode.isObject()) {
                horario = crearHorarioDesdeJson(horarioNode);
            }

            if (profesor != null && sala != null && asignatura != null && horario != null) {
                return new Reserva(profesor, sala, asignatura, horario);
            } else {
                System.err.println("Advertencia: No se pudo cargar una reserva debido a datos faltantes/inválidos (profesor, sala, asignatura u horario).");
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear reserva desde JSON: " + e.getMessage());
            return null;
        }
    }

    private ObjectNode crearJsonDesdeReserva(Reserva reserva) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("rutProfesor", reserva.getProfesor().getRut());
            node.put("nombreSala", reserva.getSala().getNombre());
            node.put("codigoAsignatura", reserva.getAsignatura().getCodigo());
            // Guarda el horario como un objeto anidado
            node.set("horario", crearJsonDesdeHorario(reserva.getHorario()));
            return node;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde reserva: " + e.getMessage());
            return null;
        }
    }

    // Métodos para manejo de respaldos
    public void respaldarDatos() {
        List.of(PROFESORES, GESTIONES_SALAS, RESERVAS, ASIGNATURAS)
                .forEach(config -> respaldarArchivo(config.ruta()));
    }

    public void restaurarRespaldos() {
        List.of(PROFESORES, GESTIONES_SALAS, RESERVAS, ASIGNATURAS)
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
}