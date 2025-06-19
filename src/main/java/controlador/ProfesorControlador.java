package controlador;

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

        System.out.print("Asignatura: ");
        String asignatura = scanner.nextLine();

        Profesor profe = new Profesor(nombre, departamento, asignatura);

        // Más adelante: guardar en archivo o lista
        System.out.println("Profesor registrado: " + profe.getNombre());
    }
}
