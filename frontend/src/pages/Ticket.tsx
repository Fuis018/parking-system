import { useState } from "react";
import { useApi } from "@/hooks/useApi";
import type { TicketResponse } from "@/types/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function Ticket() {
  const api = useApi();
  const [numero, setNumero] = useState("");
  const [ticket, setTicket] = useState<TicketResponse | null>(null);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setTicket(null);
    setCargando(true);
    try {
      const res = await api.getTicket(Number(numero));
      setTicket(res);
    } catch (e) {
      setError(String(e));
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-bold">Consultar Ticket</h1>
      <Card>
        <CardHeader>
          <CardTitle>Número de Ticket</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="numero">Ticket #</Label>
              <Input
                id="numero"
                type="number"
                value={numero}
                onChange={(e) => setNumero(e.target.value)}
                placeholder="1"
                required
              />
            </div>
            <Button type="submit" disabled={cargando} className="w-full">
              {cargando ? "Buscando..." : "Consultar"}
            </Button>
          </form>
        </CardContent>
      </Card>

      {ticket && (
        <Card>
          <CardHeader>
            <CardTitle>Ticket #{ticket.numero}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm">
            <p>
              <strong>Placa:</strong> {ticket.placa}
            </p>
            <p>
              <strong>Espacio:</strong> {ticket.espacioId}
            </p>
            <p>
              <strong>Ingreso:</strong>{" "}
              {new Date(ticket.entradaMs).toLocaleString()}
            </p>
            <p>
              <strong>Salida:</strong>{" "}
              {ticket.salidaMs
                ? new Date(ticket.salidaMs).toLocaleString()
                : "—"}
            </p>
            <p>
              <strong>Duración:</strong> {ticket.duracion}
            </p>
            <p>
              <strong>Cobro:</strong> S/. {ticket.cobro.toFixed(2)}
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
