// modelo/SalaPequena.java
package modelo;

public class SalaPequena extends Sala {
    public SalaPequena(String id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String getTamano() {
        return "Pequeña";
    }
}
