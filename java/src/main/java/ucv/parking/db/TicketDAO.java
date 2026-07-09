package ucv.parking.db;

import ucv.parking.model.Espacio;
import ucv.parking.model.Ticket;
import ucv.parking.model.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public void insert(Ticket ticket) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO ticket (numero, placa, marca, color, espacio_id, entrada, salida, cobro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, ticket.getNumero());
        ps.setString(2, ticket.getVehiculo().getPlaca());
        ps.setString(3, ticket.getVehiculo().getMarca());
        ps.setString(4, ticket.getVehiculo().getColor());
        ps.setString(5, ticket.getEspacio().getId());
        ps.setLong(6, ticket.getEntrada());
        ps.setLong(7, ticket.getSalida());
        ps.setDouble(8, ticket.getCobro());
        ps.executeUpdate();
        ps.close();
    }

    public void updateSalida(int numero, long salida, double cobro) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE ticket SET salida = ?, cobro = ? WHERE numero = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, salida);
        ps.setDouble(2, cobro);
        ps.setInt(3, numero);
        ps.executeUpdate();
        ps.close();
    }

    public Ticket findById(int numero) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM ticket WHERE numero = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, numero);
        ResultSet rs = ps.executeQuery();
        Ticket ticket = null;
        if (rs.next()) {
            ticket = mapTicket(rs);
        }
        rs.close();
        ps.close();
        return ticket;
    }

    public List<Ticket> findAbiertos() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM ticket WHERE salida = 0 ORDER BY numero";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Ticket> tickets = new ArrayList<>();
        while (rs.next()) {
            tickets.add(mapTicket(rs));
        }
        rs.close();
        ps.close();
        return tickets;
    }

    private Ticket mapTicket(ResultSet rs) throws SQLException {
        int numero = rs.getInt("numero");
        String placa = rs.getString("placa");
        String marca = rs.getString("marca");
        String color = rs.getString("color");
        String espacioId = rs.getString("espacio_id");
        long entrada = rs.getLong("entrada");
        long salida = rs.getLong("salida");
        double cobro = rs.getDouble("cobro");

        Vehiculo vehiculo = new Vehiculo(placa, marca, color);
        Espacio espacio = new Espacio(espacioId);
        return new Ticket(numero, espacio, vehiculo, entrada, salida, cobro);
    }
}
