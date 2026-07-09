package ucv.parking.controller;

import ucv.parking.db.AreaDAO;
import ucv.parking.db.DatabaseInitializer;
import ucv.parking.db.DatabaseConnection;
import ucv.parking.db.TarifaDAO;
import ucv.parking.db.TicketDAO;
import ucv.parking.model.*;
import ucv.parking.view.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParkingController {
    private Estacionamiento estacionamiento;
    private MenuView menuView;
    private MallaView mallaView;
    private EntradaController entradaController;
    private SalidaController salidaController;
    private CobroController cobroController;
    private TicketDAO ticketDAO;
    private TarifaDAO tarifaDAO;
    private AreaDAO areaDAO;

    public ParkingController() {
        this.menuView = new MenuView();
        this.mallaView = new MallaView();
        this.cobroController = new CobroController();
        this.tarifaDAO = new TarifaDAO();
        this.ticketDAO = new TicketDAO();
        this.areaDAO = new AreaDAO();
        inicializarBaseDeDatos();
        this.entradaController = new EntradaController(menuView);
        this.salidaController = new SalidaController(menuView, cobroController);
        inicializarEstacionamiento();
        restaurarEspaciosOcupados();
    }

    public Estacionamiento getEstacionamiento() { return estacionamiento; }
    public EntradaController getEntradaController() { return entradaController; }
    public CobroController getCobroController() { return cobroController; }

    private void inicializarBaseDeDatos() {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo inicializar la base de datos: " + e.getMessage());
            System.exit(1);
        }
    }

    private void inicializarEstacionamiento() {
        Tarifa tarifa;
        try {
            tarifa = tarifaDAO.load();
        } catch (SQLException e) {
            tarifa = new Tarifa(2.50, 1.00);
        }

        estacionamiento = new Estacionamiento("Parqueo Central", tarifa);

        try {
            if (areaDAO.isEmpty()) {
                // Piso 1: 4 areas (A, B, C, D)
                Piso piso1 = new Piso(1);
                for (char c = 'A'; c <= 'D'; c++) {
                    String nombre = String.valueOf(c);
                    Area area = new Area(nombre, 1);
                    area.agregarEspacios(8, nombre);
                    areaDAO.insert(area);
                    piso1.agregarArea(area);
                }
                estacionamiento.agregarPiso(piso1);

                // Piso 2: 4 areas (E, F, G, H)
                Piso piso2 = new Piso(2);
                for (char c = 'E'; c <= 'H'; c++) {
                    String nombre = String.valueOf(c);
                    Area area = new Area(nombre, 2);
                    area.agregarEspacios(8, nombre);
                    areaDAO.insert(area);
                    piso2.agregarArea(area);
                }
                estacionamiento.agregarPiso(piso2);

                // Piso 3: 3 areas (I, J, K)
                Piso piso3 = new Piso(3);
                for (char c = 'I'; c <= 'K'; c++) {
                    String nombre = String.valueOf(c);
                    Area area = new Area(nombre, 3);
                    area.agregarEspacios(8, nombre);
                    areaDAO.insert(area);
                    piso3.agregarArea(area);
                }
                estacionamiento.agregarPiso(piso3);
            } else {
                List<Area> areas = areaDAO.findAll();
                Map<Integer, Piso> pisosMap = new LinkedHashMap<>();
                for (Area a : areas) {
                    int pisoNum = a.getPisoNumero();
                    Piso piso = pisosMap.get(pisoNum);
                    if (piso == null) {
                        piso = new Piso(pisoNum);
                        pisosMap.put(pisoNum, piso);
                        estacionamiento.agregarPiso(piso);
                    }
                    piso.agregarArea(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudieron cargar las areas: " + e.getMessage());
            System.exit(1);
        }
    }

    private void restaurarEspaciosOcupados() {
        try {
            List<Ticket> abiertos = ticketDAO.findAbiertos();
            for (Ticket t : abiertos) {
                Espacio espacio = estacionamiento.buscarEspacioPorId(t.getEspacio().getId());
                if (espacio != null) {
                    Vehiculo v = t.getVehiculo();
                    espacio.ocupar(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("[WARN] No se pudieron restaurar espacios ocupados: " + e.getMessage());
        }
    }

    public void iniciar() {
        int opcion;
        do {
            mallaView.mostrarMalla(estacionamiento);
            opcion = menuView.mostrarMenuPrincipal();

            switch (opcion) {
                case 1:
                    mallaView.mostrarMalla(estacionamiento);
                    menuView.pausa();
                    break;
                case 2:
                    entradaController.registrarEntrada(estacionamiento);
                    menuView.pausa();
                    break;
                case 3:
                    salidaController.registrarSalida(estacionamiento, entradaController);
                    menuView.pausa();
                    break;
                case 4:
                    consultarTicket();
                    menuView.pausa();
                    break;
                case 5:
                    configurarTarifas();
                    menuView.pausa();
                    break;
                case 0:
                    menuView.mostrarMensaje("Saliendo del sistema...");
                    break;
                default:
                    menuView.mostrarMensaje("Opcion no valida.");
                    menuView.pausa();
            }
        } while (opcion != 0);

        menuView.cerrar();
        DatabaseConnection.close();
    }

    private void consultarTicket() {
        int num = menuView.leerEntero();
        Ticket t = entradaController.buscarTicket(num);
        if (t == null) {
            menuView.mostrarMensaje("Ticket #" + num + " no encontrado.");
            return;
        }
        menuView.mostrarMensaje("Ticket #" + t.getNumero()
                + " | Vehiculo: " + t.getVehiculo()
                + " | Espacio: " + t.getEspacio().getId()
                + " | Duracion: " + cobroController.formatearDuracion(t.getDuracionMs())
                + " | Cobro: $" + String.format("%.2f", t.getCobro()));
    }

    private void configurarTarifas() {
        menuView.mostrarMensaje("\n----- CONFIGURAR TARIFAS -----");
        double ph = menuView.leerDouble("Precio por hora actual ($"
                + estacionamiento.getTarifa().getPrecioPorHora() + "): ");
        if (ph > 0) estacionamiento.getTarifa().setPrecioPorHora(ph);

        double pf = menuView.leerDouble("Precio fraccion actual ($"
                + estacionamiento.getTarifa().getPrecioFraccion() + "): ");
        if (pf > 0) estacionamiento.getTarifa().setPrecioFraccion(pf);

        try {
            tarifaDAO.update(estacionamiento.getTarifa());
            menuView.mostrarMensaje("Tarifas actualizadas y guardadas.");
        } catch (SQLException e) {
            menuView.mostrarMensaje("Error al guardar tarifas: " + e.getMessage());
        }
    }
}
