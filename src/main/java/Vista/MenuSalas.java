/** Gestor de Salas SUFRO
* TODO: Hay que reescribir esto; debería encargarse de la parte del menú solamente,
 * esta cosa debería solo recibir los scanner.nextLine y tirarselo a las otras clases.
 *
 *TODO: Unit testing.
 */

package Vista;
import Modelo.Sala;
import java.util.Scanner;

public class MenuSalas {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("====== MENÚ PRINCIPAL ======");
            System.out.println("1. Filtrar Salas");
            System.out.println("2. Agendar Salas");
            System.out.println("3. Listar Salas");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1 -> {
                    filtrarSalas(scanner);
                    break;
                }
                case 2 -> {
                    break;
                }
                case 3 -> {
                    break;
                }
                case 4 -> {
                    System.out.println("Saliendo del programa...");
                    break;
            }
                default -> System.out.println("Opción inválida, intenta de nuevo.");
            }

            System.out.println();
        } while (opcion != 4);

        scanner.close();
    }

    public static String obtenerDiaSemana(int dia) {
        switch (dia) {
            case 1: return "Lunes";
            case 2: return "Martes";
            case 3: return "Miércoles";
            case 4: return "Jueves";
            case 5: return "Viernes";
            default: return "Día inválido";
        }
    }

    public static void filtrarSalas(Scanner scanner) {
        System.out.println("====== FILTRAR SALAS ======");
        System.out.println("Selecciona un día de la semana (1=Lunes ... 5=Viernes): ");
        int dia = scanner.nextInt();
        scanner.nextLine();

        if (dia < 1 || dia > 5) {
            System.out.println("Solo puedes agendar de lunes a viernes.");
            return;
        }

        System.out.print("Ingresa la hora (formato HH:MM): ");
        String hora = scanner.nextLine();

        System.out.print("¿Qué tamaño de sala deseas? (pequeña/mediana/grande): ");
        String tamano = scanner.nextLine();
        System.out.println("Buscando salas disponibles para:");
        System.out.println("Día: " + obtenerDiaSemana(dia));
        System.out.println("Hora: " + hora);
        System.out.println("Tamaño solicitado: " + tamano);
        System.out.println("Salas disponibles:");

            }

        }