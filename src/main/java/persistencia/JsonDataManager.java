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

    private static final String BASE_DATOS_DIR = System.getProperty("user.dir") + File.separator + "Datos" + File.separator;

    private static final String PROFESORES_FILE_NAME = BASE_DATOS_DIR + "BaseDatosProfesores.json";
    private static final String SALAS_FILE_NAME = BASE_DATOS_DIR + "BaseDatosSalas.json";
    private static final String ASIGNATURAS_FILE_NAME = BASE_DATOS_DIR + "BaseDatosAsignaturas.json";
    private static final String RESERVAS_FILE_NAME = BASE_DATOS_DIR + "BaseDatosReservas.json";
    private static final String HORARIOS_OCUPADOS_FILE_NAME = BASE_DATOS_DIR + "BaseDatosHorariosOcupados.json";

    private final ObjectMapper objectMapper;
    public JsonDataManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());

        createFilesIfNotExist();
    }

    public JsonDataManager(String profesoresPath, String salasPath, String asignaturasPath, String reservasPath, String horariosOcupadosPath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());

        createTestFileParentDirectories();
    }


    private void createFilesIfNotExist() {
        createFileIfNotExist(PROFESORES_FILE_NAME, "[]");
        createFileIfNotExist(SALAS_FILE_NAME, "[]");
        createFileIfNotExist(ASIGNATURAS_FILE_NAME, "[]");
        createFileIfNotExist(RESERVAS_FILE_NAME, "[]");
        createFileIfNotExist(HORARIOS_OCUPADOS_FILE_NAME, "{}");
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
        String[] paths = {PROFESORES_FILE_NAME, SALAS_FILE_NAME, ASIGNATURAS_FILE_NAME, RESERVAS_FILE_NAME, HORARIOS_OCUPADOS_FILE_NAME};
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
        guardarDatos(PROFESORES_FILE_NAME, data);
    }

    public void guardarSalas(List<Sala> data) {
        guardarDatos(SALAS_FILE_NAME, data);
    }

    public void guardarAsignaturas(List<Asignatura> data) {
        guardarDatos(ASIGNATURAS_FILE_NAME, data);
    }

    public void guardarReservas(List<Reserva> data) {
        guardarDatos(RESERVAS_FILE_NAME, data);
    }

    public void guardarHorariosOcupados(Map<String, Map<String, String>> data) {
        guardarMapaDatos(HORARIOS_OCUPADOS_FILE_NAME, data);
    }

    public List<Profesor> cargarProfesores() {
        return cargarListaDatos(PROFESORES_FILE_NAME, new TypeReference<>() {});
    }

    public List<Sala> cargarSalas() {
        return cargarListaDatos(SALAS_FILE_NAME, new TypeReference<>() {});
    }

    public List<Asignatura> cargarAsignaturas() {
        return cargarListaDatos(ASIGNATURAS_FILE_NAME, new TypeReference<>() {});
    }

    public List<Reserva> cargarReservas() {
        return cargarListaDatos(RESERVAS_FILE_NAME, new TypeReference<>() {});
    }

    public Map<String, Map<String, String>> cargarHorariosOcupados() {
        return cargarMapaDatos(HORARIOS_OCUPADOS_FILE_NAME, new TypeReference<>() {});
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
            if (PROFESORES_FILE_NAME != null) new File(PROFESORES_FILE_NAME).delete();
            if (SALAS_FILE_NAME != null) new File(SALAS_FILE_NAME).delete();
            if (ASIGNATURAS_FILE_NAME != null) new File(ASIGNATURAS_FILE_NAME).delete();
            if (RESERVAS_FILE_NAME != null) new File(RESERVAS_FILE_NAME).delete();
            if (HORARIOS_OCUPADOS_FILE_NAME != null) new File(HORARIOS_OCUPADOS_FILE_NAME).delete();
        } catch (Exception e) {
            System.err.println("Error al intentar limpiar archivos de datos: " + e.getMessage());
        }
    }
}