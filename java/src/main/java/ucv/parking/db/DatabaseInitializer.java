package ucv.parking.db;

import ucv.parking.model.Ticket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS tarifa (" +
            "    id INTEGER PRIMARY KEY CHECK (id = 1)," +
            "    precio_por_hora REAL NOT NULL DEFAULT 2.50," +
            "    precio_fraccion REAL NOT NULL DEFAULT 1.00" +
            ")"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS area (" +
            "    nombre TEXT PRIMARY KEY," +
            "    numero_piso INTEGER NOT NULL DEFAULT 1" +
            ")"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS espacio (" +
            "    id TEXT PRIMARY KEY," +
            "    area_nombre TEXT NOT NULL," +
            "    FOREIGN KEY (area_nombre) REFERENCES area(nombre)" +
            ")"
        );

        stmt.execute(
            "CREATE TABLE IF NOT EXISTS ticket (" +
            "    numero INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    placa TEXT NOT NULL," +
            "    marca TEXT," +
            "    color TEXT," +
            "    espacio_id TEXT NOT NULL," +
            "    entrada INTEGER NOT NULL," +
            "    salida INTEGER DEFAULT 0," +
            "    cobro REAL DEFAULT 0" +
            ")"
        );

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tarifa");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.execute("INSERT INTO tarifa (id, precio_por_hora, precio_fraccion) VALUES (1, 2.50, 1.00)");
        }
        rs.close();

        rs = stmt.executeQuery("SELECT COALESCE(MAX(numero), 0) FROM ticket");
        if (rs.next()) {
            Ticket.contador = rs.getInt(1);
        }
        rs.close();

        stmt.close();
    }
}
