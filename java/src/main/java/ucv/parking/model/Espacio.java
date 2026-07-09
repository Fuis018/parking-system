package ucv.parking.model;

public class Espacio {
    private String id;
    private EstadoEspacio estado;
    private Vehiculo vehiculo;
    private long timestampEntrada;

    public Espacio(String id) {
        this.id = id;
        this.estado = EstadoEspacio.DISPONIBLE;
        this.vehiculo = null;
        this.timestampEntrada = 0;
    }

    public String getId() { return id; }
    public EstadoEspacio getEstado() { return estado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public long getTimestampEntrada() { return timestampEntrada; }

    public boolean estaDisponible() {
        return estado == EstadoEspacio.DISPONIBLE;
    }

    public void ocupar(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
        this.estado = EstadoEspacio.OCUPADO;
        this.timestampEntrada = System.currentTimeMillis();
    }

    public Vehiculo desocupar() {
        Vehiculo v = this.vehiculo;
        this.vehiculo = null;
        this.estado = EstadoEspacio.DISPONIBLE;
        this.timestampEntrada = 0;
        return v;
    }

    public long getTiempoTranscurridoMs() {
        if (estado == EstadoEspacio.DISPONIBLE) return 0;
        return System.currentTimeMillis() - timestampEntrada;
    }
}
