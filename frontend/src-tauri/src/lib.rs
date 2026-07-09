mod api;
#[cfg(not(debug_assertions))]
mod sidecar;

#[cfg(not(debug_assertions))]
use sidecar::JavaSidecar;
#[cfg(not(debug_assertions))]
use tauri::Manager;

// ── Comandos de comunicación con la API Java ──

#[tauri::command]
async fn api_get_malla() -> Result<api::MallaResponse, String> {
    api::get_malla().await
}

#[tauri::command]
async fn api_post_entrada(req: api::EntradaRequest) -> Result<api::EntradaResponse, String> {
    api::post_entrada(req).await
}

#[tauri::command]
async fn api_post_salida(req: api::SalidaRequest) -> Result<api::SalidaResponse, String> {
    api::post_salida(req).await
}

#[tauri::command]
async fn api_get_ticket(numero: i32) -> Result<api::TicketResponse, String> {
    api::get_ticket(numero).await
}

#[tauri::command]
async fn api_get_tarifa() -> Result<api::TarifaData, String> {
    api::get_tarifa().await
}

#[tauri::command]
async fn api_put_tarifa(tarifa: api::TarifaUpdate) -> Result<api::TarifaData, String> {
    api::put_tarifa(tarifa).await
}

#[tauri::command]
fn greet(name: &str) -> String {
    format!("Hello, {}! You've been greeted from Rust!", name)
}

// ── Punto de entrada ──

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    let app = tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .invoke_handler(tauri::generate_handler![
            greet,
            api_get_malla,
            api_post_entrada,
            api_post_salida,
            api_get_ticket,
            api_get_tarifa,
            api_put_tarifa,
        ])
        .build(tauri::generate_context!())
        .expect("error while building tauri application");

    #[cfg(not(debug_assertions))]
    {
        let sidecar = start_java_sidecar(app.handle());
        if let Some(sidecar) = sidecar {
            app.manage(sidecar);
        }
    }

    app.run(|_app_handle, event| {
        if let tauri::RunEvent::Exit = event {
            // JavaSidecar se detiene via Drop
        }
    });
}

#[cfg(not(debug_assertions))]
fn resolve_jar_path(handle: &tauri::AppHandle) -> Option<std::path::PathBuf> {
    if let Ok(dir) = handle.path().resource_dir() {
        let p = dir.join("parking.jar");
        if p.exists() {
            return Some(p);
        }
    }
    if let Ok(exe) = std::env::current_exe() {
        if let Some(dir) = exe.parent() {
            let p = dir.join("parking.jar");
            if p.exists() {
                return Some(p);
            }
            let p = dir.join("../lib/frontend/parking.jar");
            if p.exists() {
                return Some(p);
            }
        }
    }
    None
}

#[cfg(not(debug_assertions))]
fn start_java_sidecar(handle: &tauri::AppHandle) -> Option<JavaSidecar> {
    let jar_path = match resolve_jar_path(handle) {
        Some(p) => p,
        None => {
            eprintln!("[sidecar] parking.jar not found");
            return None;
        }
    };

    let data_dir = match handle.path().app_data_dir() {
        Ok(d) => d,
        Err(e) => {
            eprintln!("[sidecar] Cannot get app data dir: {}", e);
            return None;
        }
    };

    if let Err(e) = std::fs::create_dir_all(&data_dir) {
        eprintln!("[sidecar] Cannot create data dir: {}", e);
        return None;
    }

    match JavaSidecar::start(&jar_path, &data_dir) {
        Ok(sidecar) => {
            println!("[sidecar] Java API started successfully");
            Some(sidecar)
        }
        Err(e) => {
            eprintln!("[sidecar] WARNING: {}", e);
            None
        }
    }
}
