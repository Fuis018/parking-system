package ucv.parking.view;

import ucv.parking.model.Area;
import ucv.parking.model.Espacio;
import ucv.parking.model.Estacionamiento;
import ucv.parking.model.Piso;

public class MallaView {

    public void mostrarMalla(Estacionamiento estacionamiento) {
        System.out.println("\n========== MALLA DE ESPACIOS ==========");
        System.out.println("Estacionamiento: " + estacionamiento.getNombre());
        System.out.println("Disponibles: " + estacionamiento.totalDisponibles()
                + " | Ocupados: " + estacionamiento.totalOcupados()
                + " | Total: " + estacionamiento.totalEspacios());
        System.out.println("========================================");

        for (Piso piso : estacionamiento.getPisos()) {
            System.out.println("\n======== Piso " + piso.getNumero()
                    + " (Disp: " + piso.contarDisponibles()
                    + "/" + piso.totalEspacios() + ") ========");

            for (Area area : piso.getAreas()) {
                System.out.println("\n--- Area: " + area.getNombre()
                        + " (Disp: " + area.contarDisponibles()
                        + "/" + area.getEspacios().size() + ") ---");

                int cont = 0;
                for (Espacio espacio : area.getEspacios()) {
                    String marca = espacio.estaDisponible() ? "[ ]" : "[X]";
                    String etiqueta = espacio.estaDisponible()
                            ? espacio.getId()
                            : espacio.getId() + "(" + espacio.getVehiculo().getPlaca() + ")";
                    System.out.printf("%-22s", marca + " " + etiqueta);
                    cont++;
                    if (cont % 4 == 0) System.out.println();
                }
                if (cont % 4 != 0) System.out.println();
            }
        }
        System.out.println("\n[ ] Disponible    [X] Ocupado");
    }
}
