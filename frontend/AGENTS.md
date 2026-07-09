# AGENTS.md — Frontend

UI desktop del estacionamiento. React 19 + TypeScript + Vite + Tauri 2 + Tailwind v4.

## Entry point

```
src/app/main.tsx    → renderiza <App />
src/app/App.tsx     → componente principal (aún template Tauri)
src/app/Router.tsx  → vacío, sin rutas implementadas
```

## Comandos

```bash
pnpm install         # instalar dependencias
pnpm dev             # Vite dev server (puerto 1420)
pnpm tauri dev       # Vite + ventana Tauri
pnpm build           # tsc && vite build
pnpm tauri build     # empaquetar app desktop
```

- `http://localhost:1420` es el **único puerto** que acepta el dev server (strictPort).
- La API Java debe estar corriendo en `http://localhost:8080` antes de iniciar el frontend.

## Shadcn UI

- Estilo: `new-york`, color base: `neutral`, CSS variables: habilitadas.
- CSS: `src/app/index.css` (Tailwind v4 `@import "tailwindcss"` + tokens de tema).
- Componentes en `src/components/ui/` — instalar con:
  ```bash
  pnpm dlx shadcn@latest add <component-name>
  ```
- `cn()` utility en `src/lib/utils.ts` (clsx + tailwind-merge).
- Sin test runner, sin lint, sin formateo configurado.

## Estructura disponible para features

```
src/pages/       → vacío — crear vistas aquí
src/features/    → vacío — lógica de negocio
src/hooks/       → vacío — custom hooks
src/components/  → ui/ (shadcn), crear componentes de dominio aquí
```

## Tauri / Rust

- Crate: `frontend_lib` (lib), `src-tauri/src/main.rs` → `frontend_lib::run()`.
- Comandos registrados en `src-tauri/src/lib.rs`:
  - `api_get_malla`, `api_post_entrada`, `api_post_salida` — proxy async a Java API
  - `api_get_ticket`, `api_get_tarifa`, `api_put_tarifa` — proxy async a Java API
  - `greet` — placeholder
- Llamar desde TypeScript:
  ```typescript
  import { invoke } from "@tauri-apps/api/core";
  const malla = await invoke("api_get_malla");
  ```
- **No tiene SQLite local** — toda la persistencia está en el backend Java.

## Gotchas

- **App.tsx y Router.tsx son templates** — la UI real del estacionamiento no está construida.
- **Sin tests** — no hay Vitest/Jest/Playwright configurado.
- **Solo TypeScript** para validación (`tsc` corre en `pnpm build`).
