package ucv.parking.model;

public class Tarifa {
    private double precioPorHora;
    private double precioFraccion;

    public Tarifa(double precioPorHora, double precioFraccion) {
        this.precioPorHora = precioPorHora;
        this.precioFraccion = precioFraccion;
    }

    public double getPrecioPorHora() { return precioPorHora; }
    public double getPrecioFraccion() { return precioFraccion; }

    public void setPrecioPorHora(double precioPorHora) { this.precioPorHora = precioPorHora; }
    public void setPrecioFraccion(double precioFraccion) { this.precioFraccion = precioFraccion; }
}
