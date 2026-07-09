import { Routes, Route } from "react-router-dom";
import NavBar from "@/components/NavBar";
import Dashboard from "@/pages/Dashboard";
import Malla from "@/pages/Malla";
import Entrada from "@/pages/Entrada";
import Salida from "@/pages/Salida";
import Ticket from "@/pages/Ticket";
import Tarifa from "@/pages/Tarifa";

export default function Router() {
  return (
    <div className="min-h-screen flex flex-col">
      <NavBar />
      <main className="flex-1 p-6">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/malla" element={<Malla />} />
          <Route path="/entrada" element={<Entrada />} />
          <Route path="/salida" element={<Salida />} />
          <Route path="/ticket" element={<Ticket />} />
          <Route path="/tarifa" element={<Tarifa />} />
        </Routes>
      </main>
    </div>
  );
}
