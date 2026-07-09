# Parking Frontend — UI Desktop

Interfaz gráfica para el sistema de estacionamiento. Construida con **React 19**, **TypeScript**, **Vite** y empaquetada como aplicación de escritorio con **Tauri 2**.

## Tecnologías

| Herramienta | Propósito |
|---|---|
| React 19 | Librería UI |
| TypeScript 5.8 | Lenguaje |
| Vite 7 | Dev server / bundler |
| Tauri 2 | Shell de escritorio (Rust) |
| pnpm | Gestor de paquetes |
| Java 17 + Maven | Backend API (empaquetado como sidecar Uber-Jar) |

## Estructura

```
frontend/
├── index.html                     → Entry point HTML
├── vite.config.ts                 → Configuración de Vite (puerto 1420)
├── package.json                   → Dependencias npm + script build:jar
├── tsconfig.json                  → Configuración TypeScript
├── src/                           → Código React
│   ├── app/
│   │   ├── main.tsx               → Punto de entrada React
│   │   ├── App.tsx                → Componente principal
│   │   └── Router.tsx             → Definición de rutas
│   ├── components/                → Componentes UI
│   ├── lib/                       → Utilidades (cn, etc.)
│   ├── pages/                     → Vistas
│   ├── features/                  → Lógica de negocio
│   └── hooks/                     → Custom hooks
└── src-tauri/                     → Shell de escritorio Tauri (Rust)
    ├── tauri.conf.json            → Configuración de ventana, bundle y recursos
    ├── Cargo.toml                 → Dependencias Rust
    └── src/
        ├── main.rs                → Entry point Rust
        ├── lib.rs                 → Comandos Tauri + sidecar Java
        ├── api.rs                 → Proxy HTTP a la API Java
        └── sidecar.rs             → Manejo del proceso Java (solo release)
```

## Cómo ejecutar

### Desarrollo

La API Java se ejecuta **manualmente** (el sidecar solo se activa en producción):

```bash
# Terminal 1: API Java
cd ../java
mvn compile exec:java -Dexec.args="--api-only"

# Terminal 2: Frontend
pnpm install
pnpm dev              # solo Vite (navegador)
pnpm tauri dev        # Vite + ventana Tauri
```

### Producción

El build compila el Uber-Jar automáticamente y lo empaqueta como recurso:

```bash
pnpm tauri build      # compila Uber-Jar, TypeScript, y empaqueta app
```

Al abrir la app empaquetada, Tauri inicia automáticamente `java -jar parking.jar --api-only` como proceso sidecar y lo detiene al cerrar la aplicación.

## Conexión con la API

El frontend se comunica con la API de Java en `http://localhost:8080` a través de comandos Tauri (Rust) que actúan como proxy HTTP.

```typescript
import { invoke } from "@tauri-apps/api/core";
const malla = await invoke("api_get_malla");
```

## Scripts disponibles

| Comando | Descripción |
|---|---|
| `pnpm dev` | Inicia Vite en modo desarrollo |
| `pnpm tauri dev` | Inicia Vite + ventana Tauri |
| `pnpm build:jar` | Compila el Uber-Jar de la API Java |
| `pnpm build` | Compila Uber-Jar + TypeScript + Vite |
| `pnpm tauri build` | Empaqueta la app de escritorio (con sidecar Java) |
| `pnpm preview` | Previsualiza la compilación de producción |
