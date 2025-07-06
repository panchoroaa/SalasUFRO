package controlador;

import modelo.BloqueHorario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class CheckReserva {


    /**
     * obtenerIdProfesor
     * <p>
     * Busca en el archivo BaseDeDatosProfesores y rut hasta que coincide la entrada o hay una linea blanca
     * si encuenntra el rut retorna la ID
     * Si No encuentra el rut retorna null y lanza una exeption
     *
     * @param rut
     * @return
     */
    public String obtenerIdProfesor(String rut) throws RutNotFoundException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File("BaseDatosProfesores.json"));
            JsonNode profesoresNode = rootNode.get("profesores");

            if (profesoresNode.isArray()) {
                for (JsonNode profesor : profesoresNode) {
                    if (profesor.get("rut").asText().equals(rut)) {
                        return profesor.get("ID").asText();
                    }
                }
            }

            throw new RutNotFoundException("El RUT " + rut + " no se encuentra en la base de datos");
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de la base de datos", e);
        }
    }

    // Clase para la excepción personalizada
    public class RutNotFoundException extends Exception {
        public RutNotFoundException(String message) {
            super(message);
        }
    }

    public boolean existeProfesor(String rut) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("BaseDatosPrfesores"));
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");

                if (datos.length == 2 && datos[0].trim().equals(rut.trim())) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
            return false;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        }
    }
    public boolean tieneRamo(String ID, String codigoRamo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File("BaseDatosRamosProfesor.json"));

            // Verificar si existe el ID del profesor
            if (!rootNode.has(ID)) {
                return false;
            }

            // Obtener el array de ramos para ese ID
            JsonNode ramosNode = rootNode.get(ID).get("ramos");

            // Verificar si el código del ramo existe en el array
            if (ramosNode.isArray()) {
                for (JsonNode ramo : ramosNode) {
                    if (ramo.asText().equals(codigoRamo)) {
                        return true;
                    }
                }
            }

            return false;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo JSON: " + e.getMessage());
            return false;
        }
    }
    public String reservaLibre(String nombreSala, String dia, BloqueHorario bloque) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File("BaseDatosReservas.json"));

            // Verificar si existe el nodo salas
            JsonNode salasNode = rootNode.get("salas");
            if (salasNode == null || !salasNode.has(nombreSala)) {
                return "Disponible"; // Si la sala no está registrada
            }

            // Obtener el nodo de la sala específica
            JsonNode salaNode = salasNode.get(nombreSala);

            // Verificar si existe el día
            if (!salaNode.has(dia)) {
                return "Disponible"; // Si el día no está registrado
            }

            // Obtener el nodo del día específico
            JsonNode diaNode = salaNode.get(dia);

            // Verificar si existe el bloque horario
            if (!diaNode.has(bloque.name())) {
                return "Disponible"; // Si el bloque no está registrado
            }

            // Retornar el estado del bloque
            return diaNode.get(bloque.name()).asText();

        } catch (IOException e) {
            System.out.println("Error al leer el archivo de reservas: " + e.getMessage());
            return "Error al verificar disponibilidad";
        }
    }
}