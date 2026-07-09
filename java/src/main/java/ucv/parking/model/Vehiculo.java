package ucv.parking.model;

public class Vehiculo {
    private String placa;
    private String marca;
    private String color;

    public Vehiculo(String placa, String marca, String color) {
        this.placa = placa;
        this.marca = marca;
        this.color = color;
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return placa + " (" + marca + ", " + color + ")";
    }
}
