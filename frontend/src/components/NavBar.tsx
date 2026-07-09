import { NavLink } from "react-router-dom";
import { cn } from "@/lib/utils";

const links = [
  { to: "/", label: "Dashboard" },
  { to: "/malla", label: "Malla" },
  { to: "/entrada", label: "Registrar Entrada" },
  { to: "/salida", label: "Registrar Salida" },
  { to: "/ticket", label: "Consultar Ticket" },
  { to: "/tarifa", label: "Tarifas" },
];

export default function NavBar() {
  return (
    <nav className="flex items-center gap-6 px-6 h-14 border-b bg-card shrink-0">
      <span className="font-bold text-lg tracking-tight">Parking</span>
      <div className="flex gap-1">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.to === "/"}
            className={({ isActive }) =>
              cn(
                "px-3 py-1.5 rounded-md text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:text-foreground hover:bg-accent"
              )
            }
          >
            {link.label}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
