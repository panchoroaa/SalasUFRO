// modelo/SalaGrande.java
package modelo;

public class SalaGrande extends Sala {
    public SalaGrande(String id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String getTamano() {
        return "Grande";
    }
}
//a