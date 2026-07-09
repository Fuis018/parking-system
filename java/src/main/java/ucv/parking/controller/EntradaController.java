package ucv.parking.controller;

import ucv.parking.db.TicketDAO;
import ucv.parking.model.Espacio;
import ucv.parking.model.Estacionamiento;
import ucv.parking.model.Ticket;
import ucv.parking.model.Vehiculo;
import ucv.parking.view.FormularioView;
import ucv.parking.view.MenuView;

import java.sql.SQLException;

public class EntradaController {
    private TicketDAO ticketDAO;
    private MenuView menuView;
    private FormularioView formularioView;

    public EntradaController(MenuView menuView) {
        this.ticketDAO = new TicketDAO();
        this.menuView = menuView;
        this.formularioView = new FormularioView(menuView);
    }

    public Ticket registrarEntrada(Estacionamiento estacionamiento) {
        Vehiculo vehiculo = formularioView.formularioEntrada();

        Espacio disponible = estacionamiento.buscarEspacioDisponible();
        if (disponible == null) {
            menuView.mostrarMensaje("No hay espacios disponibles.");
            return null;
        }

        disponible.ocupar(vehiculo);
        Ticket ticket = new Ticket(disponible, vehiculo);

        try {
            ticketDAO.insert(ticket);
        } catch (SQLException e) {
            menuView.mostrarMensaje("Error al guardar ticket: " + e.getMessage());
            disponible.desocupar();
            return null;
        }

        menuView.mostrarMensaje("Entrada registrada. Espacio: " + disponible.getId()
                + " | Ticket #" + ticket.getNumero());
        return ticket;
    }

    public Ticket buscarTicket(int numero) {
        try {
            return ticketDAO.findById(numero);
        } catch (SQLException e) {
            menuView.mostrarMensaje("Error al consultar ticket: " + e.getMessage());
            return null;
        }
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }
}
