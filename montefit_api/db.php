<?php
// =============================================
// db.php  —  Conexión a MySQL (XAMPP local)
// =============================================
// Pon esta carpeta en:  C:\xampp\htdocs\montefit_api\
// URL base desde el móvil/emulador:
//   Emulador Android:  http://10.0.2.2/montefit_api/
//   Dispositivo real:  http://<IP-de-tu-PC>/montefit_api/
// =============================================

define('DB_HOST', '127.0.0.1');
define('DB_PORT', 3301);         // Puerto específico que estás usando en WAMP
define('DB_USER', 'root');       // usuario XAMPP por defecto
define('DB_PASS', '');           // contraseña XAMPP por defecto (vacía)
define('DB_NAME', 'MonfitDB');

function getConexion() {
    try {
        // Pasamos DB_PORT como quinto parámetro para que envíe la conexión al 3301
        $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);
        if ($conn->connect_error) {
            http_response_code(500);
            die(json_encode(['error' => 'Error de conexión: ' . $conn->connect_error]));
        }
        $conn->set_charset('utf8');
        return $conn;
    } catch (Exception $e) {
        http_response_code(500);
        die(json_encode(['ok' => false, 'error' => 'MySQL denegó la conexión en el puerto ' . DB_PORT . '. Detalle: ' . $e->getMessage()]));
    }
}

// Cabeceras CORS para que Android pueda llamar sin bloqueos
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Preflight OPTIONS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}
