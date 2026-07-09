import { useEffect, useState } from "react";
import { useApi } from "@/hooks/useApi";
import type { MallaResponse, TarifaData } from "@/types/api";
import ParkingGrid from "@/components/ParkingGrid";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

export default function Dashboard() {
  const api = useApi();
  const [malla, setMalla] = useState<MallaResponse | null>(null);
  const [tarifa, setTarifa] = useState<TarifaData | null>(null);

  useEffect(() => {
    api.getMalla().then(setMalla).catch(console.error);
    api.getTarifa().then(setTarifa).catch(console.error);
  }, []);

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
          <Badge variant="outline" className="text-sm">
            {malla.totalDisponibles} Disponibles
          </Badge>
          <Badge variant="outline" className="text-sm">
            {malla.totalOcupados} Ocupados
          </Badge>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {malla.pisos.map((piso) => (
          <Card key={piso.numero}>
            <CardHeader className="pb-2">
              <CardTitle className="text-lg">Piso {piso.numero}</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold">{piso.disponibles}</p>
              <p className="text-sm text-muted-foreground">
                de {piso.total} espacios libres
              </p>
            </CardContent>
          </Card>
        ))}
      </div>

      <ParkingGrid pisos={malla.pisos} />

      {tarifa && (
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-lg">Tarifa Actual</CardTitle>
          </CardHeader>
          <CardContent className="flex gap-6 text-sm">
            <span>
              <strong>Hora:</strong> S/. {tarifa.precioPorHora.toFixed(2)}
            </span>
            <span>
              <strong>Fracción:</strong> S/. {tarifa.precioFraccion.toFixed(2)}
            </span>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
