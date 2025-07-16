package persistencia;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

class JsonDataManagerTest {

    private JsonDataManager dataManager;
    private final String TEST_DATA_DIR = "test_data_temp_json";
    private String profesorFilePath;
    private String salaFilePath;
    private String asignaturaFilePath;
    private String reservaFilePath;
    private String horarioOcupadoFilePath;

    @BeforeEach
    void setUp() throws IOException {
        Path testDirPath = new File(TEST_DATA_DIR).toPath();
        if (Files.exists(testDirPath)) {
            try (Stream<Path> walk = Files.walk(testDirPath)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Error al eliminar el archivo/directorio de prueba: " + path + " - " + e.getMessage());
                            }
                        });
            }
        }
        Files.createDirectories(testDirPath);

        profesorFilePath = TEST_DATA_DIR + File.separator + "test_profesores.json";
        salaFilePath = TEST_DATA_DIR + File.separator + "test_salas.json";
        asignaturaFilePath = TEST_DATA_DIR + File.separator + "test_asignaturas.json";
        reservaFilePath = TEST_DATA_DIR + File.separator + "test_reservas.json";
        horarioOcupadoFilePath = TEST_DATA_DIR + File.separator + "test_horarios_ocupados.json";

        dataManager = new JsonDataManager(
                profesorFilePath,
                salaFilePath,
                asignaturaFilePath,
                reservaFilePath,
                horarioOcupadoFilePath
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        Path testDirPath = new File(TEST_DATA_DIR).toPath();
        if (Files.exists(testDirPath)) {
            try (Stream<Path> walk = Files.walk(testDirPath)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Error al eliminar el archivo/directorio de prueba en tearDown: " + path + " - " + e.getMessage());
                            }
                        });
            }
        }
    }

    @Test
    void testGuardarYCargarProfesores() {
        List<Profesor> profesores = new ArrayList<>();
        // Constructor de Profesor corregido a 4 argumentos
        profesores.add(new Profesor("Juan Perez", "123-4", "Informatica", UUID.randomUUID().toString()));
        profesores.add(new Profesor("Maria Lopez", "567-8", "Matematicas", UUID.randomUUID().toString()));

        dataManager.guardarProfesores(profesores);
        List<Profesor> cargados = dataManager.cargarProfesores();

        assertNotNull(cargados);
        assertEquals(2, cargados.size());
        assertEquals("Juan Perez", cargados.get(0).getNombre());
    }

    @Test
    void testGuardarYCargarSalas() {
        List<Sala> salas = new ArrayList<>();
        salas.add(new Sala("Lab B", 20));
        salas.add(new Sala("Auditorio Principal", 100));

        dataManager.guardarSalas(salas);
        List<Sala> cargadas = dataManager.cargarSalas();

        assertNotNull(cargadas);
        assertEquals(2, cargadas.size());
        assertEquals("Lab B", cargadas.get(0).getNombre());
    }

    @Test
    void testGuardarYCargarReservas() {
        List<Reserva> reservas = new ArrayList<>();
        String rutProfesor = "P-001";
        String nombreSala = "S-AULA";
        String codigoAsignatura = "ASG-MATH";
        Horario horarioReserva = new Horario(DiaSemana.LUNES, BloqueHorario.BLOQUE_1);

        // Constructor de Reserva corregido a Strings y Horario
        reservas.add(new Reserva(rutProfesor, nombreSala, codigoAsignatura, horarioReserva));

        dataManager.guardarReservas(reservas);
        List<Reserva> cargadas = dataManager.cargarReservas();

        assertNotNull(cargadas);
        assertEquals(1, cargadas.size());
        assertEquals(rutProfesor, cargadas.get(0).getRutProfesor());
        assertEquals(nombreSala, cargadas.get(0).getNombreSala());
        assertEquals(codigoAsignatura, cargadas.get(0).getCodigoAsignatura());
        assertEquals(horarioReserva, cargadas.get(0).getHorario());
    }

    @Test
    void testGuardarYCargarHorariosOcupados() {
        Map<String, Map<String, String>> horariosOcupados = new HashMap<>(); // Ahora la clave es String
        Horario h1 = new Horario(DiaSemana.LUNES, BloqueHorario.BLOQUE_1);
        Horario h2 = new Horario(DiaSemana.MARTES, BloqueHorario.BLOQUE_2);

        // Usar toKeyString() del Horario para la clave
        horariosOcupados.put(h1.toKeyString(), new HashMap<>());
        horariosOcupados.get(h1.toKeyString()).put("profesor", "111-1");
        horariosOcupados.get(h1.toKeyString()).put("sala", "Sala A");

        horariosOcupados.put(h2.toKeyString(), new HashMap<>());
        horariosOcupados.get(h2.toKeyString()).put("sala", "Sala B");

        dataManager.guardarHorariosOcupados(horariosOcupados);
        Map<String, Map<String, String>> cargados = dataManager.cargarHorariosOcupados(); // Cargar con clave String

        assertNotNull(cargados);
        assertEquals(2, cargados.size());

        // Verificar usando las claves de String
        assertTrue(cargados.containsKey(h1.toKeyString()));
        assertEquals("111-1", cargados.get(h1.toKeyString()).get("profesor"));

        assertTrue(cargados.containsKey(h2.toKeyString()));
        assertEquals("Sala B", cargados.get(h2.toKeyString()).get("sala"));
    }
}