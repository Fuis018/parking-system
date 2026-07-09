# AGENTS.md

## Project structure

Two independent modules in one repo — no monorepo tooling:

```
parking/
├── java/          → Backend: REST API + lógica de negocio + persistencia SQLite
└── frontend/      → Desktop UI (React 19, Vite, Tauri 2, Tailwind v4)
```

### Separación de responsabilidades

- **`java/`** — Toda la lógica de negocio y persistencia. Expone API REST en puerto 8080.
  Usa SQLite (`org.xerial:sqlite-jdbc`) para almacenar áreas, espacios, tickets y tarifas.
- **`frontend/`** — Aplicación de escritorio Tauri. Se comunica con la API Java mediante
  comandos Rust como proxy HTTP. **No tiene base de datos local**.

## Java API

**Entry:** `java/src/main/java/ia/parking/App.java`

```bash
# API-only mode (no CLI menu):
mvn compile exec:java -Dexec.mainClass="ia.parking.App" -Dexec.args="--api-only"

# API + interactive console:
mvn compile exec:java -Dexec.mainClass="ia.parking.App"

mvn test
```

- No `exec-maven-plugin` in `pom.xml` — relies on global Maven Exec Plugin.
- Persistencia con **SQLite** — tablas: `tarifa`, `area`, `espacio`, `ticket`.
- REST server on port 8080, CORS `*` on every response.
- 6 endpoints: `GET /api/malla`, `POST /api/entrada`, `POST /api/salida`, `GET /api/ticket/{num}`, `GET /api/tarifa`, `PUT /api/tarifa`.
- Start the API **before** the frontend dev server.

## Frontend

**Dev server:** `http://localhost:1420` (strict port)

```bash
pnpm install
pnpm dev         # Vite only
pnpm tauri dev   # Vite + Tauri desktop window
pnpm build       # tsc && vite build
```

- `@/*` maps to `./src/*` (tsconfig.json).
- Entry: `src/app/main.tsx` renders `<App />`.
- `App.tsx` and `Router.tsx` are **still the Tauri starter template** — actual parking UI not built.
- `src/pages/`, `src/features/`, `src/hooks/` are empty — structure is ready for feature modules.

### Shadcn UI

- Style: `new-york`, base color: `neutral`, CSS variables: enabled.
- CSS: `src/app/index.css` (Tailwind v4 `@import "tailwindcss"` + theme tokens).
- Components live in `src/components/ui/`, installed via:
  ```bash
  pnpm dlx shadcn@latest add <component-name>
  ```
- `cn()` utility in `src/lib/utils.ts` (clsx + tailwind-merge).
- No test runner, no lint config, no formatting config.

## Tauri / Rust

- Crate name: `frontend_lib` (lib), entry: `src-tauri/src/main.rs` calls `frontend_lib::run()`.
- App identifier: `ucv.grupo3.parking`.

### Registered Tauri commands (`src-tauri/src/lib.rs`)

| Command | Type | Purpose |
|---|---|---|
| `api_get_malla`, `api_post_entrada`, `api_post_salida`, `api_get_ticket`, `api_get_tarifa`, `api_put_tarifa` | async | Proxy HTTP calls to Java API on `localhost:8080` |
| `greet` | sync | Template placeholder |

- **No SQLite local** — toda la persistencia está en el backend Java.

### Rust dependencies

Key ones beyond defaults: `reqwest` (rustls-tls, json), `tokio` (full).

## Gotchas

- **No lint/format/test scripts** on frontend side. Only `tsc` runs during `pnpm build`.
- **Java test is a placeholder** (`assertTrue(true)`).
- **No CI**, **no pre-commit**, **no gitignore at root** (only in frontend/).
- **No commits yet** — `.git/` exists but no history.
