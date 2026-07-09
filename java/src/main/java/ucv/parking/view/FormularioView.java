package ucv.parking.view;

import ucv.parking.model.Vehiculo;

public class FormularioView {
    private MenuView menuView;

    public FormularioView(MenuView menuView) {
        this.menuView = menuView;
    }

    public Vehiculo formularioEntrada() {
        System.out.println("\n===== REGISTRO DE ENTRADA =====");
        String placa = menuView.leerString("Placa del vehiculo: ");
        String marca = menuView.leerString("Marca: ");
        String color = menuView.leerString("Color: ");
        return new Vehiculo(placa, marca, color);
    }

    public String formularioSalida() {
        System.out.println("\n===== REGISTRO DE SALIDA =====");
        return menuView.leerString("ID del espacio a desocupar: ");
    }

    public int formularioTicket() {
        System.out.println("\n===== CONSULTAR TICKET =====");
        return menuView.leerEntero();
    }
}
