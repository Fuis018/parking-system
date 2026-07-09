package ucv.parking.controller;

import ucv.parking.db.TicketDAO;
import ucv.parking.model.Espacio;
import ucv.parking.model.Estacionamiento;
import ucv.parking.model.Ticket;
import ucv.parking.model.Vehiculo;
import ucv.parking.view.FormularioView;
import ucv.parking.view.MenuView;

import java.sql.SQLException;
import java.util.List;

public class SalidaController {
    private MenuView menuView;
    private FormularioView formularioView;
    private CobroController cobroController;
    private TicketDAO ticketDAO;

    public SalidaController(MenuView menuView, CobroController cobroController) {
        this.menuView = menuView;
        this.formularioView = new FormularioView(menuView);
        this.cobroController = cobroController;
        this.ticketDAO = new TicketDAO();
    }

    public void registrarSalida(Estacionamiento estacionamiento, EntradaController entradaController) {
        String idEspacio = formularioView.formularioSalida();

        Espacio espacio = estacionamiento.buscarEspacioPorId(idEspacio);
        if (espacio == null) {
            menuView.mostrarMensaje("El ID \"" + idEspacio + "\" no existe.");
            return;
        }

        if (espacio.estaDisponible()) {
            menuView.mostrarMensaje("El espacio \"" + idEspacio + "\" ya esta disponible.");
            return;
        }

        Vehiculo vehiculo = espacio.getVehiculo();
        long duracionMs = espacio.getTiempoTranscurridoMs();
        double monto = cobroController.calcularCobro(
                new Ticket(espacio, vehiculo), estacionamiento.getTarifa());

        try {
            Ticket ticketAbierto = buscarTicketPorEspacio(idEspacio);
            if (ticketAbierto != null) {
                long ahora = System.currentTimeMillis();
                ticketDAO.updateSalida(ticketAbierto.getNumero(), ahora, monto);
            }
        } catch (SQLException e) {
            menuView.mostrarMensaje("Error al registrar salida: " + e.getMessage());
            return;
        }

        Vehiculo saliente = espacio.desocupar();

        menuView.mostrarMensaje("\n----- COMPROBANTE DE SALIDA -----");
        menuView.mostrarMensaje("Vehiculo: " + saliente);
        menuView.mostrarMensaje("Espacio: " + espacio.getId());
        menuView.mostrarMensaje("Tiempo: " + cobroController.formatearDuracion(duracionMs));
        menuView.mostrarMensaje("Total a pagar: $" + String.format("%.2f", monto));
        menuView.mostrarMensaje("----------------------------------");
    }

    private Ticket buscarTicketPorEspacio(String espacioId) throws SQLException {
        List<Ticket> abiertos = ticketDAO.findAbiertos();
        for (Ticket t : abiertos) {
            if (t.getEspacio().getId().equals(espacioId)) {
                return t;
            }
        }
        return null;
    }
}
