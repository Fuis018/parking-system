package ucv.parking.db;

import ucv.parking.model.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public void insert(Area area) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO area (nombre, numero_piso) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, area.getNombre());
        ps.setInt(2, area.getPisoNumero());
        ps.executeUpdate();
        ps.close();

        insertEspacios(area);
    }

    public void insertEspacios(Area area) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO espacio (id, area_nombre) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (var e : area.getEspacios()) {
            ps.setString(1, e.getId());
            ps.setString(2, area.getNombre());
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    public List<Area> findAll() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<Area> areas = new ArrayList<>();

        String sqlArea = "SELECT nombre, numero_piso FROM area ORDER BY numero_piso, nombre";
        PreparedStatement ps = conn.prepareStatement(sqlArea);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String nombre = rs.getString("nombre");
            int piso = rs.getInt("numero_piso");
            Area area = new Area(nombre, piso);
            loadEspacios(area, conn);
            areas.add(area);
        }
        rs.close();
        ps.close();
        return areas;
    }

    private void loadEspacios(Area area, Connection conn) throws SQLException {
        String sql = "SELECT id FROM espacio WHERE area_nombre = ? ORDER BY id";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, area.getNombre());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            area.agregarEspacio(new ucv.parking.model.Espacio(rs.getString("id")));
        }
        rs.close();
        ps.close();
    }

    public boolean isEmpty() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM area");
        ResultSet rs = ps.executeQuery();
        boolean empty = rs.next() && rs.getInt(1) == 0;
        rs.close();
        ps.close();
        return empty;
    }
}
