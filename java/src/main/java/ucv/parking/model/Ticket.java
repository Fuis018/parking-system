package ucv.parking.model;

public class Ticket {
    public static int contador = 0;
    private int numero;
    private Espacio espacio;
    private Vehiculo vehiculo;
    private long entrada;
    private long salida;
    private double cobro;

    public Ticket(Espacio espacio, Vehiculo vehiculo) {
        this.numero = ++contador;
        this.espacio = espacio;
        this.vehiculo = vehiculo;
        this.entrada = System.currentTimeMillis();
        this.salida = 0;
        this.cobro = 0;
    }

    public Ticket(int numero, Espacio espacio, Vehiculo vehiculo, long entrada, long salida, double cobro) {
        this.numero = numero;
        this.espacio = espacio;
        this.vehiculo = vehiculo;
        this.entrada = entrada;
        this.salida = salida;
        this.cobro = cobro;
    }

    public int getNumero() { return numero; }
    public Espacio getEspacio() { return espacio; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public long getEntrada() { return entrada; }
    public long getSalida() { return salida; }
    public double getCobro() { return cobro; }

    public void registrarSalida(double cobro) {
        this.salida = System.currentTimeMillis();
        this.cobro = cobro;
    }

    public long getDuracionMs() {
        long fin = (salida == 0) ? System.currentTimeMillis() : salida;
        return fin - entrada;
    }
}
