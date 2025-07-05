package persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import modelo.*; // Importa todas las clases de modelo

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {
    private final ObjectMapper objectMapper;
    // Agregamos la carpeta "Datos" aquí
    private static final String DATA_FOLDER = "Datos"; // Nueva constante para la carpeta
    private static final String PROFESORES_FILE = DATA_FOLDER + File.separator + "BaseDatosProfesores.json";
    private static final String SALAS_FILE = DATA_FOLDER + File.separator + "BaseDatosSalas.json";
    private static final String RESERVAS_FILE = DATA_FOLDER + File.separator + "BaseDatosReservas.json";

    public JsonDataManager() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Asegurarse de que la carpeta 'Datos' exista al inicializar
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Crea el directorio y todos los directorios padre necesarios
            System.out.println("Carpeta '" + DATA_FOLDER + "' creada.");
        }
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
        if (!archivo.exists() || archivo.length() == 0) { // Unificamos la condición
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.putArray(nombreNodo);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, rootNode);
            System.out.println("Archivo " + nombreArchivo + " creado/reinicializado exitosamente.");
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
        File file = new File(PROFESORES_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Archivo de profesores no encontrado o vacío: " + PROFESORES_FILE);
            return profesores;
        }
        try {
            JsonNode rootNode = objectMapper.readTree(file);
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
            System.err.println("Error al cargar profesores desde " + PROFESORES_FILE + ": " + e.getMessage());
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
            if (asignaturasNode != null && asignaturasNode.isArray()) { // Asegurar que es un array y no nulo
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
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PROFESORES_FILE), rootNode);
            System.out.println("Profesores guardados exitosamente en " + PROFESORES_FILE);
        } catch (IOException e) {
            System.err.println("Error al guardar profesores en " + PROFESORES_FILE + ": " + e.getMessage());
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
        File file = new File(SALAS_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Archivo de salas no encontrado o vacío: " + SALAS_FILE);
            return salas;
        }
        try {
            JsonNode rootNode = objectMapper.readTree(file);
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
            System.err.println("Error al cargar salas desde " + SALAS_FILE + ": " + e.getMessage());
        }
        return salas;
    }

    private Sala crearSalaDesdeJson(JsonNode salaNode) {
        try {
            String nombre = salaNode.path("nombre").asText("");
            int capacidad = salaNode.path("capacidad").asInt(0);
            String estado = salaNode.path("estado").asText("Disponible");
            List<Horario> horarios = new ArrayList<>(); // Esta lista se llenará con horarios ocupados

            JsonNode horariosNode = salaNode.path("horariosOcupados");
            if (horariosNode != null && horariosNode.isArray()) {
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
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(SALAS_FILE), rootNode);
            System.out.println("Salas guardadas exitosamente en " + SALAS_FILE);
        } catch (IOException e) {
            System.err.println("Error al guardar salas en " + SALAS_FILE + ": " + e.getMessage());
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

    // **** MÉTODO CARGAR RESERVAS ACTUALIZADO ****
    // Este es crucial y DEBE recibir las listas de profesores y salas cargadas previamente
    public List<Reserva> cargarReservas(List<Profesor> todosProfesores, List<Sala> todasSalas) {
        List<Reserva> reservas = new ArrayList<>();
        File file = new File(RESERVAS_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Archivo de reservas no encontrado o vacío: " + RESERVAS_FILE);
            return reservas;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode reservasNode = rootNode.get("reservas");

            if (reservasNode != null && reservasNode.isArray()) {
                for (JsonNode reservaNode : reservasNode) {
                    // Ahora pasamos las listas completas para buscar las referencias correctas
                    Reserva reserva = crearReservaDesdeJson(reservaNode, todosProfesores, todasSalas);
                    if (reserva != null) {
                        reservas.add(reserva);
                        // IMPORTANTE: Re-ocupar el horario en la sala para mantener su estado
                        // Asegurarse de que el objeto Sala dentro de la reserva sea el mismo que en 'todasSalas'
                        // y así se actualice correctamente.
                        // La lógica de AsignacionControlador.cancelarAsignacion() se basa en esto.
                        reserva.getSala().agregarHorarioOcupado(reserva.getHorario());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar reservas desde " + RESERVAS_FILE + ": " + e.getMessage());
        }
        return reservas;
    }

    // Método auxiliar para crear Reserva desde JSON, ahora con las listas de objetos
    private Reserva crearReservaDesdeJson(JsonNode reservaNode, List<Profesor> todosProfesores, List<Sala> todasSalas) {
        try {
            String rutProfesor = reservaNode.path("rutProfesor").asText();
            String nombreSala = reservaNode.path("nombreSala").asText();
            String codigoAsignatura = reservaNode.path("codigoAsignatura").asText();
            String dia = reservaNode.path("dia").asText();
            String bloqueStr = reservaNode.path("bloque").asText();

            // Buscar la instancia real de Profesor, Sala y Asignatura de las listas cargadas
            Profesor profesor = todosProfesores.stream()
                    .filter(p -> p.getRut().equals(rutProfesor))
                    .findFirst()
                    .orElse(null);
            Sala sala = todasSalas.stream()
                    .filter(s -> s.getNombre().equals(nombreSala))
                    .findFirst()
                    .orElse(null);

            Asignatura asignatura = null;
            if (profesor != null) {
                asignatura = profesor.getAsignaturasImpartidas().stream()
                        .filter(a -> a.getCodigo().equals(codigoAsignatura))
                        .findFirst()
                        .orElse(null);
            }

            BloqueHorario bloque = null;
            try {
                bloque = BloqueHorario.valueOf(bloqueStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Bloque Horario inválido al cargar: " + bloqueStr);
            }

            if (profesor != null && sala != null && asignatura != null && bloque != null) {
                return new Reserva(profesor, sala, asignatura, new Horario(dia, bloque));
            } else {
                System.err.println("Advertencia: No se pudo reconstruir una reserva. Datos faltantes: " +
                        "Profesor (RUT: " + rutProfesor + "): " + (profesor != null) +
                        ", Sala (Nombre: " + nombreSala + "): " + (sala != null) +
                        ", Asignatura (Código: " + codigoAsignatura + "): " + (asignatura != null) +
                        ", Bloque (Str: " + bloqueStr + "): " + (bloque != null));
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear reserva desde JSON (detalle): " + e.getMessage());
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
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(RESERVAS_FILE), rootNode);
            System.out.println("Reservas guardadas exitosamente en " + RESERVAS_FILE);
        } catch (IOException e) {
            System.err.println("Error al guardar reservas en " + RESERVAS_FILE + ": " + e.getMessage());
        }
    }

    private ObjectNode crearJsonDesdeReserva(Reserva reserva) {
        try {
            ObjectNode reservaNode = objectMapper.createObjectNode();
            // Guardamos solo los identificadores para evitar la serialización circular y simplificar la estructura
            reservaNode.put("rutProfesor", reserva.getProfesor().getRut());
            reservaNode.put("nombreSala", reserva.getSala().getNombre());
            reservaNode.put("codigoAsignatura", reserva.getAsignatura().getCodigo());
            reservaNode.put("dia", reserva.getHorario().getDia());
            reservaNode.put("bloque", reserva.getHorario().getBloque().name()); // Guarda el nombre del enum
            return reservaNode;
        } catch (Exception e) {
            System.err.println("Error al crear JSON desde reserva: " + e.getMessage());
            return null;
        }
    }

    // Estos métodos buscarProfesor, buscarSala, buscarAsignatura ya no son necesarios aquí
    // porque cargarReservas ahora recibe las listas completas y realiza la búsqueda en ellas.
    // Los mantendré comentados o los eliminaría si fuera mi código para evitar confusión.
    /*
    private Profesor buscarProfesor(String rut) {
        // Esta implementación cargaría todos los profesores CADA VEZ que se llama
        // Lo correcto es pasarle la lista de profesores ya cargada en memoria.
        for (Profesor p : cargarProfesores()) {
            if (p.getRut().equals(rut)) return p;
        }
        return null;
    }

    private Sala buscarSala(String nombre) {
        // Similar, esto es ineficiente y puede llevar a inconsistencias de objetos.
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
    */

    // Este método ya no es útil porque cargarReservas necesita los parámetros
    // Se elimina o modifica si se necesita una verificación más compleja.
    public boolean existenDatos() {
        // Implementación revisada para que no intente cargar reservas sin listas
        File profesoresFile = new File(PROFESORES_FILE);
        File salasFile = new File(SALAS_FILE);
        File reservasFile = new File(RESERVAS_FILE);

        return profesoresFile.exists() && profesoresFile.length() > 0 ||
                salasFile.exists() && salasFile.length() > 0 ||
                reservasFile.exists() && reservasFile.length() > 0;
    }


    public void respaldaBaseDatos() {
        // ... (Tu código existente aquí. No requiere cambios de ruta si se usa DATA_FOLDER)
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
            // Asegurarse de que el directorio del respaldo exista
            File backupDir = backup.getParentFile();
            if (backupDir != null && !backupDir.exists()) {
                backupDir.mkdirs();
            }
            // Usar Files.copy para copiar el archivo de forma más robusta
            java.nio.file.Files.copy(original.toPath(), backup.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Respaldo de " + nombreArchivo + " creado.");
            // No es necesario reinicializar el archivo original aquí, ya que el original sigue existiendo
        } else {
            System.out.println("Advertencia: Archivo " + nombreArchivo + " no existe para respaldar.");
        }
    }

    public void restaurarRespaldo() {
        // ... (Tu código existente aquí. No requiere cambios de ruta si se usa DATA_FOLDER)
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
                actual.delete(); // Eliminar la versión actual antes de restaurar
            }
            // Asegurarse de que el directorio del archivo actual exista
            File actualDir = actual.getParentFile();
            if (actualDir != null && !actualDir.exists()) {
                actualDir.mkdirs();
            }
            // Usar Files.copy para restaurar el archivo
            java.nio.file.Files.copy(backup.toPath(), actual.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo " + nombreArchivo + " restaurado desde respaldo.");
        } else {
            System.out.println("No se encontró respaldo para " + nombreArchivo);
        }
    }
}