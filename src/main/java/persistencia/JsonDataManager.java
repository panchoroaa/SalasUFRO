package persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {
    // La carpeta de datos apunta directamente a src/main/resources
    private static final String DATA_FOLDER = "src/main/resources";
    private static final String PROFESORES_FILE = "BaseDatosProfesores.json";
    private static final String SALAS_FILE = "BaseDatosSalas.json";
    private static final String ASIGNATURAS_FILE = "BaseDatosAsignaturas.json";
    private static final String RESERVAS_FILE = "BaseDatosReservas.json";

    private final ObjectMapper objectMapper;

    public JsonDataManager() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        inicializarArchivosSiNoExisten();
    }

    private void inicializarArchivosSiNoExisten() {
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                System.err.println("Error: No se pudo crear el directorio de datos: " + DATA_FOLDER);
            }
        }
        crearArchivoDesdeRecurso(PROFESORES_FILE);
        crearArchivoDesdeRecurso(SALAS_FILE);
        crearArchivoDesdeRecurso(ASIGNATURAS_FILE);
        crearArchivoDesdeRecurso(RESERVAS_FILE);
    }

    private void crearArchivoDesdeRecurso(String fileName) {
        File file = new File(DATA_FOLDER, fileName);
        boolean shouldInitialize = false;

        if (!file.exists()) {
            shouldInitialize = true;
        } else {
            try {
                String fileContent = Files.readString(file.toPath());
                if (fileContent.trim().equals("[]")) {
                    shouldInitialize = true;
                }
            } catch (IOException e) {
                System.err.println("Error al leer archivo existente " + fileName + ": " + e.getMessage());
                shouldInitialize = true;
            }
        }

        if (shouldInitialize) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (is != null) {
                    Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.err.println("Advertencia: No se encontró el recurso predeterminado para " + fileName + ". Creando archivo vacío.");
                    try {
                        objectMapper.writeValue(file, new ArrayList<>());
                    } catch (IOException e) {
                        System.err.println("Error al crear archivo JSON vacío para " + fileName + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al copiar el recurso " + fileName + ": " + e.getMessage());
            }
        }
    }

    public List<Profesor> cargarProfesores() {
        return cargarDatos(PROFESORES_FILE, new TypeReference<>() {});
    }

    public List<Sala> cargarSalas() {
        return cargarDatos(SALAS_FILE, new TypeReference<>() {});
    }

    public void guardarSalas(List<Sala> data) {
        guardarDatos(SALAS_FILE, data);
    }

    public List<Asignatura> cargarAsignaturas() {
        return cargarDatos(ASIGNATURAS_FILE, new TypeReference<>() {});
    }

    public List<Reserva> cargarReservas() {
        return cargarDatos(RESERVAS_FILE, new TypeReference<>() {});
    }

    public void guardarReservas(List<Reserva> data) {
        guardarDatos(RESERVAS_FILE, data);
    }

    private <T> List<T> cargarDatos(String fileName, TypeReference<List<T>> typeReference) {
        File file = new File(DATA_FOLDER, fileName);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.err.println("Error al cargar " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void guardarDatos(String fileName, List<T> data) {
        File file = new File(DATA_FOLDER, fileName);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("Error al guardar " + fileName + ": " + e.getMessage());
        }
    }
}