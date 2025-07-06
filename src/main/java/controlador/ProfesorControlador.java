package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import modelo.Profesor;
import modelo.Asignatura;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class ProfesorControlador {
    private final Scanner scanner = new Scanner(System.in);
    private static final String NOMBRE_ARCHIVO = "BaseDatosProfesores.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registrarProfesor() {
        System.out.println("\n=== Registro de Profesor ===");

        System.out.print("RUT del Profesor: ");
        String rut = scanner.nextLine();

        System.out.print("Nombre del Profesor: ");
        String nombre = scanner.nextLine();

        System.out.print("Departamento: ");
        String departamento = scanner.nextLine();

        // Generar ID único
        String id = UUID.randomUUID().toString().substring(0, 8);

        Profesor profe = new Profesor(nombre, departamento);
        profe.setRut(rut);
        profe.setId(id);

        // Resto del código para agregar asignaturas...

        guardarProfesorEnArchivo(profe);

        System.out.println("\nProfesor registrado exitosamente:");
        System.out.println(profe);
    }

    private void guardarProfesorEnArchivo(Profesor profesor) {
        try {
            File archivo = new File(NOMBRE_ARCHIVO);
            ObjectNode rootNode;
            ArrayNode profesoresArray;

            if (archivo.exists()) {
                rootNode = (ObjectNode) objectMapper.readTree(archivo);
                profesoresArray = (ArrayNode) rootNode.get("profesores");
            } else {
                rootNode = objectMapper.createObjectNode();
                profesoresArray = objectMapper.createArrayNode();
                rootNode.set("profesores", profesoresArray);
            }

            ObjectNode profesorNode = objectMapper.createObjectNode();
            profesorNode.put("rut", profesor.getRut());
            profesorNode.put("nombre", profesor.getNombre());
            profesorNode.put("departamento", profesor.getDepartamento());
            profesorNode.put("ID", profesor.getId());

            profesoresArray.add(profesorNode);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivo, rootNode);
            System.out.println("Datos del profesor guardados en " + NOMBRE_ARCHIVO);

        } catch (IOException e) {
            System.err.println("Error al guardar el profesor en el archivo: " + e.getMessage());
        }
    }
}