package ucv.parking.model;

import java.util.ArrayList;
import java.util.List;

public class Piso {
    private int numero;
    private List<Area> areas;

    public Piso(int numero) {
        this.numero = numero;
        this.areas = new ArrayList<>();
    }

    public int getNumero() { return numero; }
    public List<Area> getAreas() { return areas; }

    public void agregarArea(Area area) {
        areas.add(area);
    }

    public int totalEspacios() {
        int total = 0;
        for (Area a : areas) total += a.getEspacios().size();
        return total;
    }

    public int contarDisponibles() {
        int total = 0;
        for (Area a : areas) total += a.contarDisponibles();
        return total;
    }

    public int contarOcupados() {
        return totalEspacios() - contarDisponibles();
    }
}
