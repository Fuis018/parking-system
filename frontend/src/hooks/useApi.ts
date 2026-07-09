import { invoke } from "@tauri-apps/api/core";
import type {
  MallaResponse,
  EntradaRequest,
  EntradaResponse,
  SalidaRequest,
  SalidaResponse,
  TicketResponse,
  TarifaData,
} from "@/types/api";

export function useApi() {
  return {
    getMalla: () => invoke<MallaResponse>("api_get_malla"),

    postEntrada: (req: EntradaRequest) =>
      invoke<EntradaResponse>("api_post_entrada", { req }),

    postSalida: (req: SalidaRequest) =>
      invoke<SalidaResponse>("api_post_salida", { req }),

    getTicket: (numero: number) =>
      invoke<TicketResponse>("api_get_ticket", { numero }),

    getTarifa: () => invoke<TarifaData>("api_get_tarifa"),

    putTarifa: (tarifa: TarifaData) =>
      invoke<TarifaData>("api_put_tarifa", { tarifa }),
  };
}
