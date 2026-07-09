import { useEffect, useState } from "react";
import { useApi } from "@/hooks/useApi";
import type { MallaResponse } from "@/types/api";
import ParkingGrid from "@/components/ParkingGrid";
import { Badge } from "@/components/ui/badge";

export default function Malla() {
  const api = useApi();
  const [malla, setMalla] = useState<MallaResponse | null>(null);
  const [error, setError] = useState("");

  const cargar = () => {
    setError("");
    api
      .getMalla()
      .then(setMalla)
      .catch((e) => setError(String(e)));
  };

  useEffect(cargar, []);

  if (error) {
    return (
      <div className="space-y-4">
        <h1 className="text-2xl font-bold">Malla del Estacionamiento</h1>
        <p className="text-destructive">Error: {error}</p>
        <button
          onClick={cargar}
          className="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm"
        >
          Reintentar
        </button>
      </div>
    );
  }

  if (!malla) {
    return (
      <div className="flex items-center justify-center h-64 text-muted-foreground">
        Cargando...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">{malla.nombre}</h1>
        <div className="flex gap-2">
          <Badge variant="outline">
            {malla.totalDisponibles} Disponibles
          </Badge>
          <Badge variant="outline">
            {malla.totalOcupados} Ocupados
          </Badge>
        </div>
      </div>
      <ParkingGrid pisos={malla.pisos} />
    </div>
  );
}
