# Parking System

Sistema de gestión de estacionamiento con una **API REST en Java** (backend) y un **frontend desktop** construido con React + Tauri.

## Arquitectura

```
parking/
├── java/         → Backend: API REST + lógica de negocio + persistencia SQLite
└── frontend/     → UI desktop (React 19, TypeScript, Vite, Tauri 2)
```

### Separación de responsabilidades

- **`java/`** — Contiene toda la lógica de negocio y persistencia. Expone una API REST en `http://localhost:8080`. Usa SQLite (`org.xerial:sqlite-jdbc`) para almacenar áreas, espacios, tickets y tarifas.
- **`frontend/`** — Aplicación de escritorio construida con Tauri. Se comunica exclusivamente con la API Java mediante comandos Rust que actúan como proxy HTTP. **No tiene base de datos local** — toda la persistencia está en el backend Java.

## Requisitos

- **Java 17+** y **Maven** (para el backend)
- **Node.js 18+** y **pnpm** (para el frontend)
- **Rust** (solo si usas Tauri para empaquetado desktop)

## Inicio rápido

### 1. Ejecutar la API

```bash
cd java
mvn compile exec:java -Dexec.mainClass="ucv.parking.App" -Dexec.args="--api-only"
```

Esto inicia el servidor en `http://localhost:8080` sin la consola interactiva.

### 2. Ejecutar el frontend

```bash
cd frontend
pnpm install
pnpm tauri dev
```

El frontend se abre en `http://localhost:1420`.

> Asegúrate de que la API esté corriendo antes de usar el frontend.

## Modos de ejecución

| Modo | Comando | Descripción |
|---|---|---|
| API + CLI | `mvn compile exec:java` | API en 8080 + menú interactivo en terminal |
| Solo API | `mvn compile exec:java -Dexec.args="--api-only"` | Solo servidor REST |
| Frontend dev | `pnpm dev` | Servidor Vite (sin Tauri) |
| Frontend + Tauri | `pnpm tauri dev` | Servidor Vite + ventana Tauri |

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/malla` | Malla completa del estacionamiento |
| `POST` | `/api/entrada` | Registrar ingreso de vehículo |
| `POST` | `/api/salida` | Registrar salida de vehículo |
| `GET` | `/api/ticket/{num}` | Consultar ticket por número |
| `GET` | `/api/tarifa` | Obtener tarifas actuales |
| `PUT` | `/api/tarifa` | Actualizar tarifas |

## Capacidades

- 3 áreas (A: 8, B: 8, C: 4 espacios) = **20 espacios** en total
- Cálculo de tarifa por horas + fracción
- Tickets con número autoincremental (SQLite)
- Persistencia con **SQLite** — áreas, espacios, tickets y tarifas se guardan en `parking.db`
