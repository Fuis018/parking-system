import type { PisoData } from "@/types/api";

interface ParkingGridProps {
  pisos: PisoData[];
}

export default function ParkingGrid({ pisos }: ParkingGridProps) {
  return (
    <div className="space-y-8">
      {pisos.map((piso) => (
        <div key={piso.numero}>
          <h2 className="text-xl font-bold mb-4">
            Piso {piso.numero} — {piso.disponibles}/{piso.total} libres
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {piso.areas.map((area) => (
              <div
                key={area.nombre}
                className="border rounded-xl p-4 bg-card shadow-sm"
              >
                <div className="flex items-center justify-between mb-3">
                  <h3 className="font-semibold text-lg">Área {area.nombre}</h3>
                  <span className="text-sm text-muted-foreground">
                    {area.disponibles}/{area.total} libres
                  </span>
                </div>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {area.espacios.map((esp) => {
                    const ocupado = esp.estado === "OCUPADO";
                    return (
                      <div
                        key={esp.id}
                        className={`aspect-square rounded-lg border-2 flex flex-col items-center justify-center text-xs font-medium transition-colors ${
                          ocupado
                            ? "border-red-400 bg-red-50 text-red-700 dark:bg-red-950 dark:text-red-300 dark:border-red-800"
                            : "border-green-400 bg-green-50 text-green-700 dark:bg-green-950 dark:text-green-300 dark:border-green-800"
                        }`}
                      >
                        <span className="text-sm font-bold">{esp.id}</span>
                        {ocupado && esp.placa && (
                          <span className="mt-0.5 truncate w-full text-center px-1">
                            {esp.placa}
                          </span>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
