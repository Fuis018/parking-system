import { useEffect, useState } from "react";
import { useApi } from "@/hooks/useApi";
import type { TarifaData } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function Tarifa() {
  const api = useApi();
  const [precioPorHora, setPrecioPorHora] = useState("");
  const [precioFraccion, setPrecioFraccion] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  useEffect(() => {
    api
      .getTarifa()
      .then((t) => {
        setPrecioPorHora(String(t.precioPorHora));
        setPrecioFraccion(String(t.precioFraccion));
      })
      .catch(console.error);
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setMensaje("");
    setCargando(true);
    try {
      const tarifa: TarifaData = {
        precioPorHora: Number(precioPorHora),
        precioFraccion: Number(precioFraccion),
      };
      await api.putTarifa(tarifa);
      setMensaje("Tarifa actualizada correctamente");
    } catch (e) {
      setError(String(e));
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-bold">Configurar Tarifas</h1>
      <Card>
        <CardHeader>
          <CardTitle>Precios (S/.)</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="hora">Precio por Hora</Label>
              <Input
                id="hora"
                type="number"
                step="0.01"
                min="0"
                value={precioPorHora}
                onChange={(e) => setPrecioPorHora(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="fraccion">Precio por Fracción</Label>
              <Input
                id="fraccion"
                type="number"
                step="0.01"
                min="0"
                value={precioFraccion}
                onChange={(e) => setPrecioFraccion(e.target.value)}
                required
              />
            </div>
            <Button type="submit" disabled={cargando} className="w-full">
              {cargando ? "Guardando..." : "Guardar Tarifa"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {mensaje && (
        <p className="text-green-600 bg-green-50 dark:bg-green-950 dark:text-green-400 px-4 py-3 rounded-md text-sm">
          {mensaje}
        </p>
      )}
      {error && (
        <p className="text-destructive bg-destructive/10 px-4 py-3 rounded-md text-sm">
          {error}
        </p>
      )}
    </div>
  );
}
