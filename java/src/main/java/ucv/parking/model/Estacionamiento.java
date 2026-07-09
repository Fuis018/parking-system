package ucv.parking.model;

import java.util.ArrayList;
import java.util.List;

public class Estacionamiento {
    private String nombre;
    private List<Piso> pisos;
    private Tarifa tarifa;

    public Estacionamiento(String nombre, Tarifa tarifa) {
        this.nombre = nombre;
        this.tarifa = tarifa;
        this.pisos = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public List<Piso> getPisos() { return pisos; }
    public Tarifa getTarifa() { return tarifa; }

    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }

    public void agregarPiso(Piso piso) {
        pisos.add(piso);
    }

    public List<Area> getAreas() {
        List<Area> todas = new ArrayList<>();
        for (Piso p : pisos) todas.addAll(p.getAreas());
        return todas;
    }

    public Espacio buscarEspacioDisponible() {
        for (Piso p : pisos) {
            for (Area a : p.getAreas()) {
                for (Espacio e : a.getEspacios()) {
                    if (e.estaDisponible()) return e;
                }
            }
        }
        return null;
    }

    public Espacio buscarEspacioPorId(String idCompleto) {
        for (Piso p : pisos) {
            for (Area a : p.getAreas()) {
                for (Espacio e : a.getEspacios()) {
                    if (e.getId().equals(idCompleto)) return e;
                }
            }
        }
        return null;
    }

    public int totalEspacios() {
        int total = 0;
        for (Piso p : pisos) total += p.totalEspacios();
        return total;
    }

    public int totalDisponibles() {
        int total = 0;
        for (Piso p : pisos) total += p.contarDisponibles();
        return total;
    }

    public int totalOcupados() {
        return totalEspacios() - totalDisponibles();
    }
}
