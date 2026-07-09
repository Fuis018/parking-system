import { useState } from "react";
import { useApi } from "@/hooks/useApi";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function Entrada() {
  const api = useApi();
  const [placa, setPlaca] = useState("");
  const [marca, setMarca] = useState("");
  const [color, setColor] = useState("");
  const [resultado, setResultado] = useState<string | null>(null);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setResultado(null);
    setCargando(true);
    try {
      const res = await api.postEntrada({ placa, marca, color });
      setResultado(
        `Ticket #${res.ticketNumero} — Espacio ${res.espacioId}`
      );
      setPlaca("");
      setMarca("");
      setColor("");
    } catch (e) {
      setError(String(e));
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-bold">Registrar Entrada</h1>
      <Card>
        <CardHeader>
          <CardTitle>Datos del Vehículo</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="placa">Placa</Label>
              <Input
                id="placa"
                value={placa}
                onChange={(e) => setPlaca(e.target.value)}
                placeholder="ABC-123"
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="marca">Marca</Label>
              <Input
                id="marca"
                value={marca}
                onChange={(e) => setMarca(e.target.value)}
                placeholder="Toyota"
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="color">Color</Label>
              <Input
                id="color"
                value={color}
                onChange={(e) => setColor(e.target.value)}
                placeholder="Rojo"
                required
              />
            </div>
            <Button type="submit" disabled={cargando} className="w-full">
              {cargando ? "Registrando..." : "Registrar Entrada"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {resultado && (
        <p className="text-green-600 bg-green-50 dark:bg-green-950 dark:text-green-400 px-4 py-3 rounded-md text-sm">
          {resultado}
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
