package ucv.parking.view;

import java.util.Scanner;

public class MenuView {
    private Scanner scanner;

    public MenuView() {
        this.scanner = new Scanner(System.in);
    }

    public int mostrarMenuPrincipal() {
        System.out.println("\n===== SISTEMA DE PARQUEO INTELIGENTE =====");
        System.out.println("1. Ver malla de espacios");
        System.out.println("2. Registrar entrada de vehiculo");
        System.out.println("3. Registrar salida de vehiculo");
        System.out.println("4. Consultar ticket");
        System.out.println("5. Configurar tarifas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
        return leerEntero();
    }

    public String leerString(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double leerDouble(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void pausa() {
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }

    public void cerrar() {
        scanner.close();
    }
}
