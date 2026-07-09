use std::process::{Child, Command};
use std::sync::Mutex;

pub struct JavaSidecar {
    child: Mutex<Option<Child>>,
}

impl JavaSidecar {
    pub fn start(jar_path: &std::path::Path, data_dir: &std::path::Path) -> Result<Self, String> {
        if !jar_path.exists() {
            return Err(format!("parking.jar not found at {:?}", jar_path));
        }

        println!("[sidecar] Starting Java API from: {:?}", jar_path);

        let child = Command::new("java")
            .arg("-jar")
            .arg(jar_path)
            .arg("--api-only")
            .current_dir(data_dir)
            .spawn()
            .map_err(|e| format!("Cannot start Java: {}", e))?;

        Ok(JavaSidecar {
            child: Mutex::new(Some(child)),
        })
    }

    pub fn stop(&self) {
        if let Ok(mut guard) = self.child.lock() {
            if let Some(mut child) = guard.take() {
                let _ = child.kill();
                let _ = child.wait();
                println!("[sidecar] Java API stopped");
            }
        }
    }
}

impl Drop for JavaSidecar {
    fn drop(&mut self) {
        self.stop();
    }
}
