package persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDataManager {

    private static final String PROFESORES_FILE_NAME = "BaseDatosProfesores.json";
    private static final String SALAS_FILE_NAME = "BaseDatosSalas.json";
    private static final String ASIGNATURAS_FILE_NAME = "BaseDatosAsignaturas.json";
    private static final String RESERVAS_FILE_NAME = "BaseDatosReservas.json";
    private static final String HORARIOS_OCUPADOS_FILE_NAME = "BaseDatosHorariosOcupados.json";

    private final ObjectMapper objectMapper;
    private final String profesoresFilePath;
    private final String salasFilePath;
    private final String asignaturasFilePath;
    private final String reservasFilePath;
    private final String horariosOcupadosFilePath;

    public JsonDataManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());

        this.profesoresFilePath = getResourceAbsolutePath(PROFESORES_FILE_NAME);
        this.salasFilePath = getResourceAbsolutePath(SALAS_FILE_NAME);
        this.asignaturasFilePath = getResourceAbsolutePath(ASIGNATURAS_FILE_NAME);
        this.reservasFilePath = getResourceAbsolutePath(RESERVAS_FILE_NAME);
        this.horariosOcupadosFilePath = getResourceAbsolutePath(HORARIOS_OCUPADOS_FILE_NAME);

        createFilesIfNotExist();
    }

    public JsonDataManager(String profesoresPath, String salasPath, String asignaturasPath, String reservasPath, String horariosOcupadosPath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());

        this.profesoresFilePath = profesoresPath;
        this.salasFilePath = salasPath;
        this.asignaturasFilePath = asignaturasPath;
        this.reservasFilePath = reservasPath;
        this.horariosOcupadosFilePath = horariosOcupadosPath;

        createTestFileParentDirectories();
    }

    private String getResourceAbsolutePath(String resourceName) {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
            if (resourceUrl != null) {
                return new File(resourceUrl.toURI()).getAbsolutePath();
            } else {
                System.err.println("Advertencia: Recurso '" + resourceName + "' no encontrado en el classpath. Intentando ruta relativa.");
                return resourceName;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener la ruta absoluta para el recurso '" + resourceName + "': " + e.getMessage());
            return resourceName;
        }
    }

    private void createFilesIfNotExist() {
        createFileIfNotExist(profesoresFilePath, "[]");
        createFileIfNotExist(salasFilePath, "[]");
        createFileIfNotExist(asignaturasFilePath, "[]");
        createFileIfNotExist(reservasFilePath, "[]");
        createFileIfNotExist(horariosOcupadosFilePath, "{}");
    }

    private void createFileIfNotExist(String filePath, String defaultContent) {
        if (filePath == null) return;
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    if (!parentDir.mkdirs()) {
                        System.err.println("Error: No se pudo crear el directorio padre para " + filePath);
                        return;
                    }
                }
                objectMapper.writeValue(file, objectMapper.readTree(defaultContent));
            } catch (IOException e) {
                System.err.println("Error al crear archivo JSON vacío en " + filePath + ": " + e.getMessage());
            }
        }
    }

    private void createTestFileParentDirectories() {
        String[] paths = {profesoresFilePath, salasFilePath, asignaturasFilePath, reservasFilePath, horariosOcupadosFilePath};
        for (String path : paths) {
            File parentDir = new File(path).getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Error: No se pudo crear el directorio para tests/rutas personalizadas: " + parentDir.getAbsolutePath());
                }
            }
        }
    }

    public void guardarProfesores(List<Profesor> data) {
        guardarDatos(profesoresFilePath, data);
    }

    public void guardarSalas(List<Sala> data) {
        guardarDatos(salasFilePath, data);
    }

    public void guardarAsignaturas(List<Asignatura> data) {
        guardarDatos(asignaturasFilePath, data);
    }

    public void guardarReservas(List<Reserva> data) {
        guardarDatos(reservasFilePath, data);
    }

    public void guardarHorariosOcupados(Map<String, Map<String, String>> data) {
        guardarMapaDatos(horariosOcupadosFilePath, data);
    }

    public List<Profesor> cargarProfesores() {
        return cargarListaDatos(profesoresFilePath, new TypeReference<>() {});
    }

    public List<Sala> cargarSalas() {
        return cargarListaDatos(salasFilePath, new TypeReference<>() {});
    }

    public List<Asignatura> cargarAsignaturas() {
        return cargarListaDatos(asignaturasFilePath, new TypeReference<>() {});
    }

    public List<Reserva> cargarReservas() {
        return cargarListaDatos(reservasFilePath, new TypeReference<>() {});
    }

    public Map<String, Map<String, String>> cargarHorariosOcupados() {
        return cargarMapaDatos(horariosOcupadosFilePath, new TypeReference<>() {});
    }

    private <T> List<T> cargarListaDatos(String filePath, TypeReference<List<T>> typeReference) {
        if (filePath == null) return new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Error al cargar lista de " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void guardarDatos(String filePath, List<T> data) {
        if (filePath == null) return;
        File file = new File(filePath);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error al guardar lista en " + filePath + ": " + e.getMessage());
        }
    }

    private <K, V> Map<K, V> cargarMapaDatos(String filePath, TypeReference<Map<K, V>> typeReference) {
        if (filePath == null) return new HashMap<>();
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Error al cargar mapa de " + filePath + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    private <K, V> void guardarMapaDatos(String filePath, Map<K, V> data) {
        if (filePath == null) return;
        File file = new File(filePath);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error al guardar mapa en " + filePath + ": " + e.getMessage());
        }
    }

    public void limpiarDatos() {
        try {
            if (profesoresFilePath != null) new File(profesoresFilePath).delete();
            if (salasFilePath != null) new File(salasFilePath).delete();
            if (asignaturasFilePath != null) new File(asignaturasFilePath).delete();
            if (reservasFilePath != null) new File(reservasFilePath).delete();
            if (horariosOcupadosFilePath != null) new File(horariosOcupadosFilePath).delete();
        } catch (Exception e) {
            System.err.println("Error al intentar limpiar archivos de datos: " + e.getMessage());
        }
    }
}