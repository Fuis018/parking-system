export interface MallaResponse {
  nombre: string;
  totalDisponibles: number;
  totalOcupados: number;
  pisos: PisoData[];
}

export interface PisoData {
  numero: number;
  disponibles: number;
  total: number;
  areas: AreaData[];
}

export interface AreaData {
  nombre: string;
  disponibles: number;
  total: number;
  espacios: EspacioData[];
}

export interface EspacioData {
  id: string;
  estado: "DISPONIBLE" | "OCUPADO";
  placa: string | null;
  tiempoMs: number;
}

export interface TarifaData {
  precioPorHora: number;
  precioFraccion: number;
}

export interface EntradaRequest {
  placa: string;
  marca: string;
  color: string;
}

export interface EntradaResponse {
  ticketNumero: number;
  espacioId: string;
}

export interface SalidaRequest {
  idEspacio: string;
}

export interface SalidaResponse {
  placa: string;
  espacioId: string;
  duracion: string;
  totalPagar: number;
}

export interface TicketResponse {
  numero: number;
  placa: string;
  espacioId: string;
  entradaMs: number;
  salidaMs: number;
  duracion: string;
  cobro: number;
}
