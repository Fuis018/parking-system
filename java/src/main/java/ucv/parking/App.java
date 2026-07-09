package ucv.parking;

import ucv.parking.controller.ApiController;
import ucv.parking.controller.ParkingController;

public class App {
    public static void main(String[] args) throws Exception {
        ParkingController controller = new ParkingController();

        ApiController api = new ApiController(
                controller.getEstacionamiento(),
                controller.getEntradaController(),
                controller.getCobroController()
        );
        api.iniciar(8080);

        if (args.length == 0 || !args[0].equals("--api-only")) {
            controller.iniciar();
        } else {
            System.out.println("Modo API solamente. Presione Ctrl+C para detener.");
            Thread.currentThread().join();
        }
    }
}
