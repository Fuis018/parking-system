package ucv.parking.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import ucv.parking.db.TarifaDAO;
import ucv.parking.db.TicketDAO;
import ucv.parking.model.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class ApiController {
    private final Estacionamiento estacionamiento;
    private final EntradaController entradaController;
    private final CobroController cobroController;
    private final TicketDAO ticketDAO;
    private final TarifaDAO tarifaDAO;
    private final Gson gson;
    private HttpServer server;

    public ApiController(Estacionamiento estacionamiento,
                         EntradaController entradaController,
                         CobroController cobroController) {
        this.estacionamiento = estacionamiento;
        this.entradaController = entradaController;
        this.cobroController = cobroController;
        this.ticketDAO = new TicketDAO();
        this.tarifaDAO = new TarifaDAO();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void iniciar(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/malla", this::handleMalla);
        server.createContext("/api/entrada", this::handleEntrada);
        server.createContext("/api/salida", this::handleSalida);
        server.createContext("/api/ticket", this::handleTicket);
        server.createContext("/api/tarifa", this::handleTarifa);
        server.setExecutor(null);
        server.start();
        System.out.println("[API] Servidor REST en http://localhost:" + port);
    }

    public void detener() {
        if (server != null) server.stop(0);
    }

    private void handleMalla(HttpExchange e) {
        MallaDTO dto = new MallaDTO(estacionamiento);
        responderJson(e, 200, dto);
    }

    private void handleEntrada(HttpExchange e) {
        if (!"POST".equals(e.getRequestMethod())) {
            responderJson(e, 405, new ErrorDTO("Metodo no permitido"));
            return;
        }
        EntradaRequest req = leerBody(e, EntradaRequest.class);
        if (req == null || req.placa == null || req.placa.isEmpty()) {
            responderJson(e, 400, new ErrorDTO("placa requerida"));
            return;
        }

        Vehiculo vehiculo = new Vehiculo(req.placa, req.marca, req.color);
        Espacio espacio = estacionamiento.buscarEspacioDisponible();
        if (espacio == null) {
            responderJson(e, 409, new ErrorDTO("No hay espacios disponibles"));
            return;
        }

        espacio.ocupar(vehiculo);
        Ticket ticket = new Ticket(espacio, vehiculo);

        try {
            ticketDAO.insert(ticket);
        } catch (SQLException ex) {
            espacio.desocupar();
            responderJson(e, 500, new ErrorDTO("Error al guardar ticket: " + ex.getMessage()));
            return;
        }

        responderJson(e, 201, new EntradaResponse(ticket, espacio));
    }

    private void handleSalida(HttpExchange e) {
        if (!"POST".equals(e.getRequestMethod())) {
            responderJson(e, 405, new ErrorDTO("Metodo no permitido"));
            return;
        }
        SalidaRequest req = leerBody(e, SalidaRequest.class);
        if (req == null || req.idEspacio == null) {
            responderJson(e, 400, new ErrorDTO("idEspacio requerido"));
            return;
        }

        Espacio espacio = estacionamiento.buscarEspacioPorId(req.idEspacio);
        if (espacio == null) {
            responderJson(e, 404, new ErrorDTO("Espacio no encontrado"));
            return;
        }
        if (espacio.estaDisponible()) {
            responderJson(e, 409, new ErrorDTO("El espacio ya esta disponible"));
            return;
        }

        Vehiculo vehiculo = espacio.getVehiculo();

        try {
            Ticket ticketAbierto = buscarTicketAbiertoPorEspacio(req.idEspacio);
            if (ticketAbierto != null) {
                long duracionMs = ticketAbierto.getDuracionMs();
                double monto = cobroController.calcularCobro(ticketAbierto, estacionamiento.getTarifa());
                long ahora = System.currentTimeMillis();
                ticketDAO.updateSalida(ticketAbierto.getNumero(), ahora, monto);
                espacio.desocupar();
                responderJson(e, 200, new SalidaResponse(vehiculo, req.idEspacio,
                        cobroController.formatearDuracion(duracionMs), monto));
            } else {
                espacio.desocupar();
                responderJson(e, 200, new SalidaResponse(vehiculo, req.idEspacio,
                        "00:00:00", 0));
            }
        } catch (SQLException ex) {
            responderJson(e, 500, new ErrorDTO("Error al registrar salida: " + ex.getMessage()));
        }
    }

    private void handleTicket(HttpExchange e) {
        String path = e.getRequestURI().getPath();
        String numStr = path.replace("/api/ticket/", "").trim();

        Ticket ticket = null;
        try {
            int num = Integer.parseInt(numStr);
            ticket = ticketDAO.findById(num);
        } catch (NumberFormatException | SQLException ignored) {}

        if (ticket == null) {
            responderJson(e, 404, new ErrorDTO("Ticket no encontrado"));
            return;
        }
        responderJson(e, 200, new TicketDTO(ticket, cobroController));
    }

    private void handleTarifa(HttpExchange e) {
        if ("GET".equals(e.getRequestMethod())) {
            responderJson(e, 200, estacionamiento.getTarifa());
        } else if ("PUT".equals(e.getRequestMethod())) {
            TarifaReq req = leerBody(e, TarifaReq.class);
            if (req != null) {
                if (req.precioPorHora > 0) estacionamiento.getTarifa().setPrecioPorHora(req.precioPorHora);
                if (req.precioFraccion > 0) estacionamiento.getTarifa().setPrecioFraccion(req.precioFraccion);
                try {
                    tarifaDAO.update(estacionamiento.getTarifa());
                } catch (SQLException ex) {
                    responderJson(e, 500, new ErrorDTO("Error al guardar tarifa: " + ex.getMessage()));
                    return;
                }
            }
            responderJson(e, 200, estacionamiento.getTarifa());
        } else {
            responderJson(e, 405, new ErrorDTO("Metodo no permitido"));
        }
    }

    private Ticket buscarTicketAbiertoPorEspacio(String espacioId) throws SQLException {
        List<Ticket> abiertos = ticketDAO.findAbiertos();
        for (Ticket t : abiertos) {
            if (t.getEspacio().getId().equals(espacioId)) {
                return t;
            }
        }
        return null;
    }

    private void responderJson(HttpExchange e, int status, Object data) {
        try {
            byte[] json = gson.toJson(data).getBytes(StandardCharsets.UTF_8);
            e.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            e.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            e.sendResponseHeaders(status, json.length);
            OutputStream os = e.getResponseBody();
            os.write(json);
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private <T> T leerBody(HttpExchange e, Class<T> clase) {
        try (InputStream is = e.getRequestBody();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clase);
        } catch (IOException ex) {
            return null;
        }
    }

    static class MallaDTO {
        String nombre;
        int totalDisponibles;
        int totalOcupados;
        java.util.List<PisoDTO> pisos;

        MallaDTO(Estacionamiento e) {
            this.nombre = e.getNombre();
            this.totalDisponibles = e.totalDisponibles();
            this.totalOcupados = e.totalOcupados();
            this.pisos = new java.util.ArrayList<>();
            for (Piso p : e.getPisos()) pisos.add(new PisoDTO(p));
        }
    }

    static class PisoDTO {
        int numero;
        int disponibles;
        int total;
        java.util.List<AreaDTO> areas;

        PisoDTO(Piso p) {
            this.numero = p.getNumero();
            this.disponibles = p.contarDisponibles();
            this.total = p.totalEspacios();
            this.areas = new java.util.ArrayList<>();
            for (Area a : p.getAreas()) areas.add(new AreaDTO(a));
        }
    }

    static class AreaDTO {
        String nombre;
        int disponibles;
        int total;
        java.util.List<EspacioDTO> espacios;

        AreaDTO(Area a) {
            this.nombre = a.getNombre();
            this.disponibles = a.contarDisponibles();
            this.total = a.getEspacios().size();
            this.espacios = new java.util.ArrayList<>();
            for (Espacio es : a.getEspacios()) espacios.add(new EspacioDTO(es));
        }
    }

    static class EspacioDTO {
        String id;
        String estado;
        String placa;
        long tiempoMs;

        EspacioDTO(Espacio e) {
            this.id = e.getId();
            this.estado = e.estaDisponible() ? "DISPONIBLE" : "OCUPADO";
            this.placa = e.getVehiculo() != null ? e.getVehiculo().getPlaca() : null;
            this.tiempoMs = e.getTiempoTranscurridoMs();
        }
    }

    static class EntradaRequest {
        String placa;
        String marca;
        String color;
    }

    static class EntradaResponse {
        int ticketNumero;
        String espacioId;

        EntradaResponse(Ticket t, Espacio e) {
            this.ticketNumero = t.getNumero();
            this.espacioId = e.getId();
        }
    }

    static class SalidaRequest {
        String idEspacio;
    }

    static class SalidaResponse {
        String placa;
        String espacioId;
        String duracion;
        double totalPagar;

        SalidaResponse(Vehiculo v, String espacioId, String duracion, double totalPagar) {
            this.placa = v.getPlaca();
            this.espacioId = espacioId;
            this.duracion = duracion;
            this.totalPagar = totalPagar;
        }
    }

    static class TicketDTO {
        int numero;
        String placa;
        String espacioId;
        long entradaMs;
        long salidaMs;
        String duracion;
        double cobro;

        TicketDTO(Ticket t, CobroController cc) {
            this.numero = t.getNumero();
            this.placa = t.getVehiculo().getPlaca();
            this.espacioId = t.getEspacio().getId();
            this.entradaMs = t.getEntrada();
            this.salidaMs = t.getSalida();
            this.duracion = cc.formatearDuracion(t.getDuracionMs());
            this.cobro = t.getCobro();
        }
    }

    static class TarifaReq {
        double precioPorHora;
        double precioFraccion;
    }

    static class ErrorDTO {
        String error;
        ErrorDTO(String error) { this.error = error; }
    }
}
