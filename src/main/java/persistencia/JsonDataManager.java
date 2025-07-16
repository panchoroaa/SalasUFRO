package persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDataManager {

    private static final String BASE_DATOS_DIR = System.getProperty("user.dir") + File.separator + "Datos" + File.separator;
    private static final String BACKUP_DIR = System.getProperty("user.dir") + File.separator + "Backup" + File.separator;

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

        // Asegurarse de que los directorios base existan
        crearDirectoriosBase();
    }

    // Constructor para pruebas (opcional)
    public JsonDataManager(String profesoresPath, String salasPath, String asignaturasPath, String reservasPath, String horariosOcupadosPath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        crearDirectoriosBase();
    }


    private void crearDirectoriosBase() {
        new File(BASE_DATOS_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
    }


    private <T> List<T> cargarListaDatos(String filePath, TypeReference<List<T>> typeReference) {
        if (filePath == null) return new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            File backupFile = new File(BACKUP_DIR + file.getName());
            if (backupFile.exists()) {
                System.out.println("INFO: Archivo no encontrado en 'Datos'. Restaurando desde 'Backups': " + file.getName());
                copiarArchivo(backupFile, file);
            } else {
                System.out.println("ADVERTENCIA: No se encontró el archivo ni el backup. Creando archivo vacío: " + file.getName());
                crearArchivoVacio(filePath, "[]");
                return new ArrayList<>();
            }
        }

        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("ERROR: Error al cargar lista desde " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }


    private <K, V> Map<K, V> cargarMapaDatos(String filePath, TypeReference<Map<K, V>> typeReference) {
        if (filePath == null) return new HashMap<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            File backupFile = new File(BACKUP_DIR + file.getName());
            if (backupFile.exists()) {
                System.out.println("INFO: Archivo no encontrado en 'Datos'. Restaurando desde 'Backups': " + file.getName());
                copiarArchivo(backupFile, file);
            } else {
                System.out.println("ADVERTENCIA: No se encontró el archivo ni el backup. Creando archivo vacío: " + file.getName());
                crearArchivoVacio(filePath, "{}");
                return new HashMap<>();
            }
        }

        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("ERROR: Error al cargar mapa desde " + filePath + ": " + e.getMessage());
            return new HashMap<>();
        }
    }


    private void copiarArchivo(File origen, File destino) {
        try {
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("ÉXITO: Archivo restaurado desde " + origen.getPath() + " a " + destino.getPath());
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo copiar el archivo de backup: " + e.getMessage());
        }
    }


    private void crearArchivoVacio(String filePath, String defaultContent) {
        try {
            File file = new File(filePath);
            objectMapper.writeValue(file, objectMapper.readTree(defaultContent));
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo crear el archivo JSON vacío en " + filePath + ": " + e.getMessage());
        }
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

    private <T> void guardarDatos(String filePath, List<T> data) {
        try {
            objectMapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            System.err.println("Error al guardar lista en " + filePath + ": " + e.getMessage());
        }
    }

    private <K, V> void guardarMapaDatos(String filePath, Map<K, V> data) {
        try {
            objectMapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            System.err.println("Error al guardar mapa en " + filePath + ": " + e.getMessage());
        }
    }
}