package controlador;

import modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistencia.JsonDataManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionControladorTest {

    private static final String TEST_DATA_DIR = "test_data_temp";
    private static final String TEST_PROFESORES_FILE = TEST_DATA_DIR + File.separator + "test_BaseDatosProfesores.json";
    private static final String TEST_SALAS_FILE = TEST_DATA_DIR + File.separator + "test_BaseDatosSalas.json";
    private static final String TEST_ASIGNATURAS_FILE = TEST_DATA_DIR + File.separator + "test_BaseDatosAsignaturas.json";
    private static final String TEST_RESERVAS_FILE = TEST_DATA_DIR + File.separator + "test_BaseDatosReservas.json";
    private static final String TEST_HORARIOS_OCUPADOS_FILE = TEST_DATA_DIR + File.separator + "test_BaseDatosHorariosOcupados.json";

    private JsonDataManager testDataManager;
    private AsignacionControlador controlador;

    private Profesor prof1;
    private Profesor prof2;
    private Sala sala1;
    private Sala sala2;
    private Asignatura asig1;
    private Asignatura asig2;
    private Horario horario1;
    private Horario horario2;

    @BeforeEach
    void setUp() throws IOException {
        Path testDirPath = Paths.get(TEST_DATA_DIR);
        if (!Files.exists(testDirPath)) {
            Files.createDirectories(testDirPath);
        }
        try (Stream<Path> paths = Files.walk(testDirPath)) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
        }

        testDataManager = new JsonDataManager(
                TEST_PROFESORES_FILE,
                TEST_SALAS_FILE,
                TEST_ASIGNATURAS_FILE,
                TEST_RESERVAS_FILE,
                TEST_HORARIOS_OCUPADOS_FILE
        );

        prof1 = new Profesor("Juan Perez", "11111111-1", "Informatica", UUID.randomUUID().toString());
        asig1 = new Asignatura("Programacion I", "COMP101", 25);
        List<Asignatura> asignaturasProf1 = new ArrayList<>();
        asignaturasProf1.add(asig1);
        prof1.setAsignaturasImpartidas(asignaturasProf1);

        prof2 = new Profesor("Maria Lopez", "22222222-2", "Matematicas", UUID.randomUUID().toString());
        List<Asignatura> asignaturasProf2 = new ArrayList<>();
        asignaturasProf2.add(asig1);
        prof2.setAsignaturasImpartidas(asignaturasProf2);

        sala1 = new Sala("Laboratorio A", 30);
        sala1.setEstado(EstadoSala.DISPONIBLE);
        sala2 = new Sala("Sala Multimedia", 30);
        sala2.setEstado(EstadoSala.DISPONIBLE);

        horario1 = new Horario(DiaSemana.LUNES, BloqueHorario.BLOQUE_1);
        horario2 = new Horario(DiaSemana.LUNES, BloqueHorario.BLOQUE_2);

        // Inicialización de asig2
        asig2 = new Asignatura("Inteligencia Artificial", "IA201", 35);

        List<Profesor> profesoresIniciales = new ArrayList<>();
        profesoresIniciales.add(prof1);
        profesoresIniciales.add(prof2);
        testDataManager.guardarProfesores(profesoresIniciales);

        List<Sala> salasIniciales = new ArrayList<>();
        salasIniciales.add(sala1);
        salasIniciales.add(sala2);
        testDataManager.guardarSalas(salasIniciales);

        List<Asignatura> asignaturasIniciales = new ArrayList<>();
        asignaturasIniciales.add(asig1);
        asignaturasIniciales.add(asig2);
        testDataManager.guardarAsignaturas(asignaturasIniciales);

        controlador = new AsignacionControlador(testDataManager);
    }

    @Test
    void testCrearAsignacionExitosa() {
        String resultado = controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("¡Asignación realizada con éxito!"), "La primera asignación debería ser exitosa. Mensaje: " + resultado);

        List<Reserva> reservasActuales = testDataManager.cargarReservas();
        assertFalse(reservasActuales.isEmpty());
        assertEquals(1, reservasActuales.size());
        assertEquals(prof1.getRut(), reservasActuales.get(0).getRutProfesor());
        assertEquals(sala1.getNombre(), reservasActuales.get(0).getNombreSala());
        assertEquals(asig1.getCodigo(), reservasActuales.get(0).getCodigoAsignatura());
        assertEquals(horario1, reservasActuales.get(0).getHorario());

        List<Sala> salasActualizadas = testDataManager.cargarSalas();
        Sala salaActualizada = salasActualizadas.stream()
                .filter(s -> s.getNombre().equals(sala1.getNombre()))
                .findFirst().orElseThrow();
        assertFalse(salaActualizada.estaDisponibleEn(horario1));
    }

    @Test
    void testCrearAsignacionProfesorYaOcupado() {
        controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        controlador = new AsignacionControlador(testDataManager);

        String resultado = controlador.crearAsignacion(prof1.getRut(), sala2.getNombre(), asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: El profesor " + prof1.getNombre() + " ya tiene una asignación en ese horario."), "Mensaje: " + resultado);
        assertEquals(1, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionSalaYaOcupada() {
        controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        controlador = new AsignacionControlador(testDataManager);

        String resultado = controlador.crearAsignacion(prof2.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: La sala " + sala1.getNombre() + " no está disponible en el horario"), "Mensaje: " + resultado);
        assertEquals(1, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionProfesorNoEncontrado() {
        String resultado = controlador.crearAsignacion("RUT_INEXISTENTE", sala1.getNombre(), asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: Profesor con RUT RUT_INEXISTENTE no encontrado."), "Mensaje: " + resultado);
        assertEquals(0, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionSalaNoEncontrada() {
        String resultado = controlador.crearAsignacion(prof1.getRut(), "SALA_INEXISTENTE", asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: Sala SALA_INEXISTENTE no encontrada."), "Mensaje: " + resultado);
        assertEquals(0, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionAsignaturaNoEncontrada() {
        String resultado = controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), "ASIGNATURA_INEXISTENTE", horario1);
        assertTrue(resultado.contains("Error: Asignatura con código ASIGNATURA_INEXISTENTE no encontrada."), "Mensaje: " + resultado);
        assertEquals(0, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionAsignaturaNoImpartidaPorProfesor() {
        Profesor profSinAsig1 = new Profesor("Pedro Sin Asignatura", "33333333-3", "Artes", UUID.randomUUID().toString());
        profSinAsig1.setAsignaturasImpartidas(new ArrayList<>());

        List<Profesor> profesoresActuales = testDataManager.cargarProfesores();
        profesoresActuales.add(profSinAsig1);
        testDataManager.guardarProfesores(profesoresActuales);

        controlador = new AsignacionControlador(testDataManager);

        String resultado = controlador.crearAsignacion(profSinAsig1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: El profesor " + profSinAsig1.getNombre() + " no imparte la asignatura " + asig1.getNombre()), "Mensaje: " + resultado);
        assertEquals(0, testDataManager.cargarReservas().size());
    }

    @Test
    void testCrearAsignacionCapacidadExcedida() {
        List<Asignatura> asignaturasProf1Actualizadas = new ArrayList<>(prof1.getAsignaturasImpartidas());
        if (!asignaturasProf1Actualizadas.contains(asig2)) {
            asignaturasProf1Actualizadas.add(asig2);
            prof1.setAsignaturasImpartidas(asignaturasProf1Actualizadas);

            List<Profesor> profesoresActuales = testDataManager.cargarProfesores();
            profesoresActuales.removeIf(p -> p.getRut().equals(prof1.getRut()));
            profesoresActuales.add(prof1);
            testDataManager.guardarProfesores(profesoresActuales);

            controlador = new AsignacionControlador(testDataManager);
        }

        String resultado = controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig2.getCodigo(), horario1);
        assertTrue(resultado.contains("Error: La cantidad de alumnos de la asignatura (" + asig2.getCantidadAlumnos() +
                ") excede la capacidad de la sala (" + sala1.getCapacidad() + ")."), "Mensaje: " + resultado);
        assertEquals(0, testDataManager.cargarReservas().size());
    }

    @Test
    void testGetProfesoresDisponibles() {
        controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        controlador = new AsignacionControlador(testDataManager);

        List<Profesor> profesoresDisponiblesHorario1 = controlador.getProfesoresDisponibles(asig1, horario1);
        assertEquals(1, profesoresDisponiblesHorario1.size());
        assertTrue(profesoresDisponiblesHorario1.stream().anyMatch(p -> p.getRut().equals(prof2.getRut())));
        assertFalse(profesoresDisponiblesHorario1.stream().anyMatch(p -> p.getRut().equals(prof1.getRut())));

        List<Profesor> profesoresDisponiblesHorario2 = controlador.getProfesoresDisponibles(asig1, horario2);
        assertEquals(2, profesoresDisponiblesHorario2.size());
        assertTrue(profesoresDisponiblesHorario2.stream().anyMatch(p -> p.getRut().equals(prof1.getRut())));
        assertTrue(profesoresDisponiblesHorario2.stream().anyMatch(p -> p.getRut().equals(prof2.getRut())));
    }

    @Test
    void testGetSalasDisponiblesEnHorario() {
        controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        controlador = new AsignacionControlador(testDataManager);

        List<Sala> salasDisponiblesHorario1 = controlador.getSalasDisponiblesEnHorario(horario1);
        assertEquals(1, salasDisponiblesHorario1.size());
        assertTrue(salasDisponiblesHorario1.stream().anyMatch(s -> s.getNombre().equals(sala2.getNombre())));
        assertFalse(salasDisponiblesHorario1.stream().anyMatch(s -> s.getNombre().equals(sala1.getNombre())));

        List<Sala> salasDisponiblesHorario2 = controlador.getSalasDisponiblesEnHorario(horario2);
        assertEquals(2, salasDisponiblesHorario2.size());
        assertTrue(salasDisponiblesHorario2.stream().anyMatch(s -> s.getNombre().equals(sala1.getNombre())));
        assertTrue(salasDisponiblesHorario2.stream().anyMatch(s -> s.getNombre().equals(sala2.getNombre())));
    }

    @Test
    void testCancelarAsignacion() {
        controlador.crearAsignacion(prof1.getRut(), sala1.getNombre(), asig1.getCodigo(), horario1);
        controlador = new AsignacionControlador(testDataManager);

        List<Reserva> reservasACancelar = controlador.getReservas();
        assertFalse(reservasACancelar.isEmpty());
        Reserva reservaACancelar = reservasACancelar.get(0);

        String resultadoCancelacion = controlador.cancelarAsignacion(reservaACancelar);
        assertTrue(resultadoCancelacion.contains("¡Asignación cancelada con éxito!"), "Mensaje: " + resultadoCancelacion);

        List<Reserva> reservasDespues = testDataManager.cargarReservas();
        assertTrue(reservasDespues.isEmpty());

        List<Sala> salasActualizadas = testDataManager.cargarSalas();
        Sala salaActualizada = salasActualizadas.stream()
                .filter(s -> s.getNombre().equals(sala1.getNombre()))
                .findFirst().orElseThrow();
        assertTrue(salaActualizada.estaDisponibleEn(horario1));
    }
}