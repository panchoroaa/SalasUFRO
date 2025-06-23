// modelo/SalaMediana.java
package modelo;

public class SalaMediana extends Sala {
    public SalaMediana(String id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String getTamano() {
        return "Mediana";
    }
}
//a