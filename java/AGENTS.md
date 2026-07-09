# AGENTS.md — Java API

Backend REST del estacionamiento. Java 17 + Maven, sin frameworks externos.

## Entry point

`src/main/java/ia/parking/App.java` — arranca `ApiController` (puerto 8080) y opcionalmente el menú CLI.

## Comandos

```bash
mvn compile                       # compilar
mvn compile exec:java             # compilar + ejecutar (API + menú CLI)
mvn compile exec:java -Dexec.args="--api-only"   # solo API, sin menú
mvn test                          # tests (placeholder)
```

- `exec-maven-plugin` **no está** en `pom.xml` — requiere el plugin global de Maven.
- `--api-only` evita el menú interactivo (usar para desarrollo con frontend).

## Arquitectura

```
controller/     → ApiController (REST), ParkingController (CLI), Entrada/Salida/Cobro
model/          → Estacionamiento, Piso, Area, Espacio, Ticket, Vehiculo, Tarifa
db/             → DatabaseConnection, DatabaseInitializer, AreaDAO, TicketDAO, TarifaDAO
view/           → MenuView, MallaView, Formulario (solo CLI)
```

## API REST (puerto 8080)

| Método | Ruta | Propósito |
|---|---|---|
| `GET` | `/api/malla` | Malla completa del estacionamiento |
| `POST` | `/api/entrada` | Registrar ingreso `{placa, marca, color}` |
| `POST` | `/api/salida` | Registrar salida `{idEspacio}` |
| `GET` | `/api/ticket/{num}` | Consultar ticket |
| `GET` | `/api/tarifa` | Obtener tarifas |
| `PUT` | `/api/tarifa` | Actualizar tarifas |

CORS `Access-Control-Allow-Origin: *` en todas las respuestas.

## Reglas de negocio

- **3 pisos**: Piso 1 (Áreas A-D, 32 espacios), Piso 2 (Áreas E-H, 32 espacios), Piso 3 (Áreas I-K, 24 espacios) = **88 espacios**.
- Cada área tiene 8 espacios.
- Tarifa base: $2.50/hora + $1.00 fracción.
- Persistencia con **SQLite** a través de `org.xerial:sqlite-jdbc`.
- Los tickets se numeran automáticamente (autoincremental en SQLite).

## Bases de datos

SQLite con las tablas:
- `tarifa` — una fila con precio por hora y fracción.
- `area` — nombre del área y `numero_piso` (1-3).
- `espacio` — cada espacio con su id y referencia al área.
- `ticket` — historial de tickets con datos del vehículo, espacio, timestamps y cobro.

## Gotchas

- **Sin Spring Boot** — usa `com.sun.net.httpserver.HttpServer` del JDK.
- **SQLite** — el archivo `parking.db` se crea en el directorio de trabajo.
- **Test placeholder** — `AppTest.java` solo tiene `assertTrue(true)`.
