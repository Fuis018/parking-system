package ucv.parking.model;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private String nombre;
    private int pisoNumero;
    private List<Espacio> espacios;

    public Area(String nombre) {
        this.nombre = nombre;
        this.espacios = new ArrayList<Espacio>();
    }

    public Area(String nombre, int pisoNumero) {
        this.nombre = nombre;
        this.pisoNumero = pisoNumero;
        this.espacios = new ArrayList<Espacio>();
    }

    public String getNombre() { return nombre; }
    public int getPisoNumero() { return pisoNumero; }
    public void setPisoNumero(int pisoNumero) { this.pisoNumero = pisoNumero; }
    public List<Espacio> getEspacios() { return espacios; }

    public void agregarEspacio(Espacio espacio) {
        espacios.add(espacio);
    }

    public void agregarEspacios(int cantidad, String prefijo) {
        for (int i = 1; i <= cantidad; i++) {
            espacios.add(new Espacio(prefijo + "-" + i));
        }
    }

    public int contarDisponibles() {
        int count = 0;
        for (Espacio e : espacios) {
            if (e.estaDisponible()) count++;
        }
        return count;
    }

    public int contarOcupados() {
        return espacios.size() - contarDisponibles();
    }
}
