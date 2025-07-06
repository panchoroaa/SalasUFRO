package launcher;

import vista.MenuSalas;

public class Inicio {
    public static void main(String[] args) {
        MenuSalas menu = new MenuSalas();
        menu.iniciar();
        System.out.println("Ejecutando main.");
    }
}
