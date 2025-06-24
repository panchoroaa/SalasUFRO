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
     * """""FALTA PONERR SI EL ARCHIVO ESTA O NO""""""""""""""""
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
            BufferedReader reader = new BufferedReader(new FileReader("BaseDatosProfesores"));
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");

                if (datos.length == 2 && datos[0].trim().equals(rut.trim())) {
                    reader.close();
                    return datos[1].trim();
                }
            }
            reader.close();
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
            BufferedReader reader = new BufferedReader(new FileReader("BaseDatosProfesores"));
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
            JsonNode rootNode = mapper.readTree(new File("BaseDatosRamos.json"));

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
    public String reservaLibre(String nombreSala, BloqueHorario bloque) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File("BaseDatosReservas.json"));

            // Verificar si existe la sala
            if (!rootNode.has(nombreSala)) {
                return "Disponible"; // Si la sala no está en el registro, está disponible
            }

            // Obtener las reservas de la sala
            JsonNode salaNode = rootNode.get(nombreSala);

            // Verificar el estado para el bloque específico
            if (salaNode.has(bloque.toString())) {
                return salaNode.get(bloque.toString()).asText();
            }

            return "Disponible"; // Si no hay registro para ese bloque, está disponible

        } catch (IOException e) {
            System.out.println("Error al leer el archivo de reservas: " + e.getMessage());
            return "Error al verificar disponibilidad";
        }
    }

}
