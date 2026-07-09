# Parking API — Backend Java

API RESTful para la gestión de un estacionamiento. Construida con **Java 17**, **Maven** y el servidor HTTP incorporado del JDK (`com.sun.net.httpserver.HttpServer`), sin frameworks externos.

## Tecnologías

| Herramienta | Versión |
|---|---|
| Java | 17 |
| Maven | - |
| Gson | 2.10.1 |
| JUnit | 4.11 |

## Estructura

```
src/main/java/ia/parking/
├── App.java                         → Punto de entrada
├── controller/
│   ├── ApiController.java           → Servidor REST (puerto 8080)
│   ├── ParkingController.java       → Orquestador + menú CLI
│   ├── EntradaController.java       → Lógica de ingreso
│   ├── SalidaController.java        → Lógica de salida
│   └── CobroController.java         → Cálculo de tarifas
├── model/
│   ├── Estacionamiento.java         → Agregado raíz
│   ├── Piso.java                    → Piso con lista de áreas
│   ├── Area.java                    → Área del estacionamiento
│   ├── Espacio.java                 → Espacio individual
│   ├── Ticket.java                  → Ticket de estacionamiento
│   ├── Vehiculo.java                → Vehículo
│   ├── Tarifa.java                  → Configuración de tarifas
│   └── EstadoEspacio.java           → Enum DISPONIBLE / OCUPADO
└── view/
    ├── MenuView.java                → Menú interactivo por consola
    ├── MallaView.java               → Visualización de la malla
    └── FormularioView.java          → Formularios de entrada/salida
```

## Cómo ejecutar

### Solo API (sin interfaz de consola)

```bash
mvn compile exec:java -Dexec.mainClass="ucv.parking.App" -Dexec.args="--api-only"
```

### API + menú interactivo

```bash
mvn compile exec:java -Dexec.mainClass="ucv.parking.App"
```

### Compilar sin ejecutar

```bash
mvn compile
```

### Ejecutar pruebas

```bash
mvn test
```

## API Endpoints

Servidor corriendo en `http://localhost:8080`. Todas las respuestas incluyen `Access-Control-Allow-Origin: *`.

### `GET /api/malla`

Devuelve la distribución completa del estacionamiento con pisos, áreas y espacios.

```json
{
  "nombre": "Parqueo Central",
  "totalDisponibles": 88,
  "totalOcupados": 0,
  "pisos": [
    {
      "numero": 1,
      "disponibles": 32,
      "total": 32,
      "areas": [
        {
          "nombre": "A",
          "disponibles": 8,
          "total": 8,
          "espacios": [
            { "id": "A-1", "estado": "DISPONIBLE", "placa": null, "tiempoMs": 0 }
          ]
        }
      ]
    }
  ]
}
```

### `POST /api/entrada`

Registra el ingreso de un vehículo.

```json
// Request
{ "placa": "ABC-123", "marca": "Toyota", "color": "Rojo" }

// Response 201
{ "mensaje": "Ingreso registrado", "ticket": { "numero": 1, ... } }

// Response 409 (sin espacios)
{ "error": "Estacionamiento lleno" }
```

### `POST /api/salida`

Registra la salida de un vehículo.

```json
// Request
{ "idEspacio": "A-3" }

// Response 200
{ "placa": "ABC-123", "idEspacio": "A-3", "duracion": "02:15:30", "cobro": 6.25 }
```

### `GET /api/ticket/{numero}`

Obtiene los detalles de un ticket.

### `GET /api/tarifa`

Obtiene las tarifas actuales.

```json
{ "precioPorHora": 2.5, "precioFraccion": 1.0 }
```

### `PUT /api/tarifa`

Actualiza las tarifas.

```json
// Request
{ "precioPorHora": 3.0, "precioFraccion": 1.5 }

// Response
{ "precioPorHora": 3.0, "precioFraccion": 1.5 }
```

## Modelo de datos

- **3 pisos**: Piso 1 (Áreas A–D, 32 espacios), Piso 2 (Áreas E–H, 32 espacios), Piso 3 (Áreas I–K, 24 espacios)
- **11 áreas**, cada una con **8 espacios** = **88 espacios** en total
- Tarifa base: $2.50/hora + $1.00 fracción
- Persistencia con **SQLite** (archivo `parking.db`)
