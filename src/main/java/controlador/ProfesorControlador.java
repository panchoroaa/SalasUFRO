package controlador;

import java.util.List;
import java.util.ArrayList;
import modelo.Profesor;
import java.util.Scanner;

public class ProfesorControlador {
    private final Scanner scanner = new Scanner(System.in);

    public void registrarProfesor() {
        System.out.println("Registro de Profesor");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Departamento: ");
        String departamento = scanner.nextLine();

        System.out.println("Ingresa las asignaturas separadas por coma (ej. Matematicas, Fisica, Programacion):");
        String asignaturasInput = scanner.nextLine();

        // Separar asignaturas y limpiar espacios
        String[] asignaturasArray = asignaturasInput.split(",");
        List<String> asignaturas = new ArrayList<>();
        for (String asignatura : asignaturasArray) {
            asignaturas.add(asignatura.trim());
        }

        // Crear profesor
        Profesor profe = new Profesor(nombre, departamento);
        for (String asignatura : asignaturas) {
            profe.agregarAsignatura(asignatura);
        }

        // Confirmación
        System.out.println("Profesor registrado exitosamente:");
        System.out.println(profe);
    }
}
