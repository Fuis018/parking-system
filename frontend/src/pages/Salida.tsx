import { useState } from "react";
import { useApi } from "@/hooks/useApi";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function Salida() {
  const api = useApi();
  const [idEspacio, setIdEspacio] = useState("");
  const [resultado, setResultado] = useState<{
    placa: string;
    espacioId: string;
    duracion: string;
    totalPagar: number;
  } | null>(null);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setResultado(null);
    setCargando(true);
    try {
      const res = await api.postSalida({ idEspacio });
      setResultado(res);
      setIdEspacio("");
    } catch (e) {
      setError(String(e));
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-bold">Registrar Salida</h1>
      <Card>
        <CardHeader>
          <CardTitle>Espacio a desocupar</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="idEspacio">ID del Espacio</Label>
              <Input
                id="idEspacio"
                value={idEspacio}
                onChange={(e) => setIdEspacio(e.target.value)}
                placeholder="A1"
                required
              />
            </div>
            <Button type="submit" disabled={cargando} className="w-full">
              {cargando ? "Procesando..." : "Registrar Salida"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {resultado && (
        <Card>
          <CardHeader>
            <CardTitle>Comprobante de Salida</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm">
            <p>
              <strong>Vehículo:</strong> {resultado.placa}
            </p>
            <p>
              <strong>Espacio:</strong> {resultado.espacioId}
            </p>
            <p>
              <strong>Duración:</strong> {resultado.duracion}
            </p>
            <p className="text-lg font-bold">
              Total a pagar: S/. {resultado.totalPagar.toFixed(2)}
            </p>
          </CardContent>
        </Card>
      )}
      {error && (
        <p className="text-destructive bg-destructive/10 px-4 py-3 rounded-md text-sm">
          {error}
        </p>
      )}
    </div>
  );
}
