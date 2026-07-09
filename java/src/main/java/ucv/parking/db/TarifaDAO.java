package ucv.parking.db;

import ucv.parking.model.Tarifa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TarifaDAO {

    public Tarifa load() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT precio_por_hora, precio_fraccion FROM tarifa WHERE id = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        Tarifa tarifa = null;
        if (rs.next()) {
            tarifa = new Tarifa(rs.getDouble("precio_por_hora"), rs.getDouble("precio_fraccion"));
        }
        rs.close();
        ps.close();
        return tarifa;
    }

    public void update(Tarifa tarifa) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "UPDATE tarifa SET precio_por_hora = ?, precio_fraccion = ? WHERE id = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, tarifa.getPrecioPorHora());
        ps.setDouble(2, tarifa.getPrecioFraccion());
        ps.executeUpdate();
        ps.close();
    }
}
