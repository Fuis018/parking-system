use serde::{Deserialize, Serialize};

const API_BASE: &str = "http://localhost:8080/api";

// ── DTOs ──

#[derive(Debug, Serialize, Deserialize)]
pub struct MallaResponse {
    pub nombre: String,
    #[serde(rename = "totalDisponibles")]
    pub total_disponibles: i32,
    #[serde(rename = "totalOcupados")]
    pub total_ocupados: i32,
    pub pisos: Vec<PisoData>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct PisoData {
    pub numero: i32,
    pub disponibles: i32,
    pub total: i32,
    pub areas: Vec<AreaData>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct TarifaData {
    #[serde(rename = "precioPorHora")]
    pub precio_por_hora: f64,
    #[serde(rename = "precioFraccion")]
    pub precio_fraccion: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct AreaData {
    pub nombre: String,
    pub disponibles: i32,
    pub total: i32,
    pub espacios: Vec<EspacioData>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct EspacioData {
    pub id: String,
    pub estado: String,
    pub placa: Option<String>,
    #[serde(rename = "tiempoMs")]
    pub tiempo_ms: i64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct EntradaRequest {
    pub placa: String,
    pub marca: String,
    pub color: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct EntradaResponse {
    #[serde(rename = "ticketNumero")]
    pub ticket_numero: i32,
    #[serde(rename = "espacioId")]
    pub espacio_id: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SalidaRequest {
    #[serde(rename = "idEspacio")]
    pub id_espacio: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SalidaResponse {
    pub placa: String,
    #[serde(rename = "espacioId")]
    pub espacio_id: String,
    pub duracion: String,
    #[serde(rename = "totalPagar")]
    pub total_pagar: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct TicketResponse {
    pub numero: i32,
    pub placa: String,
    #[serde(rename = "espacioId")]
    pub espacio_id: String,
    #[serde(rename = "entradaMs")]
    pub entrada_ms: i64,
    #[serde(rename = "salidaMs")]
    pub salida_ms: i64,
    pub duracion: String,
    pub cobro: f64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct TarifaUpdate {
    #[serde(rename = "precioPorHora")]
    pub precio_por_hora: f64,
    #[serde(rename = "precioFraccion")]
    pub precio_fraccion: f64,
}

// ── Cliente HTTP ──

pub async fn get_malla() -> Result<MallaResponse, String> {
    let url = format!("{}/malla", API_BASE);
    let resp = reqwest::get(&url).await.map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}

pub async fn post_entrada(req: EntradaRequest) -> Result<EntradaResponse, String> {
    let url = format!("{}/entrada", API_BASE);
    let client = reqwest::Client::new();
    let resp = client
        .post(&url)
        .json(&req)
        .send()
        .await
        .map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}

pub async fn post_salida(req: SalidaRequest) -> Result<SalidaResponse, String> {
    let url = format!("{}/salida", API_BASE);
    let client = reqwest::Client::new();
    let resp = client
        .post(&url)
        .json(&req)
        .send()
        .await
        .map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}

pub async fn get_ticket(numero: i32) -> Result<TicketResponse, String> {
    let url = format!("{}/ticket/{}", API_BASE, numero);
    let resp = reqwest::get(&url).await.map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}

pub async fn get_tarifa() -> Result<TarifaData, String> {
    let url = format!("{}/tarifa", API_BASE);
    let resp = reqwest::get(&url).await.map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}

pub async fn put_tarifa(tarifa: TarifaUpdate) -> Result<TarifaData, String> {
    let url = format!("{}/tarifa", API_BASE);
    let client = reqwest::Client::new();
    let resp = client
        .put(&url)
        .json(&tarifa)
        .send()
        .await
        .map_err(|e| e.to_string())?;
    resp.json().await.map_err(|e| e.to_string())
}
