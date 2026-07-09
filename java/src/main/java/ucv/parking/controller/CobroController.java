package ucv.parking.controller;

import ucv.parking.model.Tarifa;
import ucv.parking.model.Ticket;

public class CobroController {

    public double calcularCobro(Ticket ticket, Tarifa tarifa) {
        long duracionMs = ticket.getDuracionMs();
        long duracionMin = duracionMs / 60000;

        if (duracionMin <= 0) duracionMin = 1;

        long horas = duracionMin / 60;
        long minutos = duracionMin % 60;

        double total = (horas * tarifa.getPrecioPorHora());
        if (minutos > 0) {
            total += tarifa.getPrecioFraccion();
        }

        return total;
    }

    public String formatearDuracion(long duracionMs) {
        long totalSeg = duracionMs / 1000;
        long horas = totalSeg / 3600;
        long min = (totalSeg % 3600) / 60;
        long seg = totalSeg % 60;
        return String.format("%02d:%02d:%02d", horas, min, seg);
    }
}
