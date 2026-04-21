<?php
// =============================================
// usuarios.php  —  Endpoints de Usuarios
// =============================================
// GET  ?action=login&correo=X&contrasena=X
// GET  ?action=check&correo=X
// GET  ?action=getId&correo=X
// GET  ?action=getName&correo=X
// GET  ?action=getProfile&correo=X
// GET  ?action=buscar&nombre=X
// POST ?action=register   body: {nombre, correo, contrasena}
// POST ?action=update      body: {correo, nombre, edad, peso, sexo}
// POST ?action=updatePass  body: {correo, contrasena}
// =============================================
require_once 'db.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    // ── LOGIN ────────────────────────────────
    case 'login':
        $correo    = $conn->real_escape_string($_GET['correo'] ?? '');
        $contrasena = $conn->real_escape_string($_GET['contrasena'] ?? '');
        $res = $conn->query(
            "SELECT 1 FROM Usuarios WHERE correo='$correo' AND contrasena='$contrasena'"
        );
        echo json_encode(['ok' => $res->num_rows > 0]);
        break;

    // ── CHECK EMAIL ──────────────────────────
    case 'check':
        $correo = $conn->real_escape_string($_GET['correo'] ?? '');
        $res = $conn->query("SELECT 1 FROM Usuarios WHERE correo='$correo'");
        echo json_encode(['existe' => $res->num_rows > 0]);
        break;

    // ── GET ID ───────────────────────────────
    case 'getId':
        $correo = $conn->real_escape_string($_GET['correo'] ?? '');
        $res = $conn->query("SELECT usuario_id FROM Usuarios WHERE correo='$correo'");
        $row = $res->fetch_assoc();
        echo json_encode(['usuario_id' => $row ? (int)$row['usuario_id'] : -1]);
        break;

    // ── GET NAME ─────────────────────────────
    case 'getName':
        $correo = $conn->real_escape_string($_GET['correo'] ?? '');
        $res = $conn->query("SELECT nombre FROM Usuarios WHERE correo='$correo'");
        $row = $res->fetch_assoc();
        echo json_encode(['nombre' => $row ? $row['nombre'] : '']);
        break;

    // ── GET PROFILE ──────────────────────────
    case 'getProfile':
        $correo = $conn->real_escape_string($_GET['correo'] ?? '');
        $res = $conn->query(
            "SELECT nombre, correo, edad AS age, peso, sexo AS sex
             FROM Usuarios WHERE correo='$correo'"
        );
        $row = $res->fetch_assoc();
        echo json_encode($row ?: null);
        break;

    // ── BUSCAR USUARIO ───────────────────────
    case 'buscar':
        $nombre = $conn->real_escape_string($_GET['nombre'] ?? '');
        $res = $conn->query(
            "SELECT usuario_id AS id, nombre, correo
             FROM Usuarios WHERE nombre LIKE '%$nombre%'"
        );
        $lista = [];
        while ($row = $res->fetch_assoc()) $lista[] = $row;
        echo json_encode($lista);
        break;

    // ── REGISTER ─────────────────────────────
    case 'register':
        $body      = json_decode(file_get_contents('php://input'), true);
        $nombre    = $conn->real_escape_string($body['nombre']    ?? '');
        $correo    = $conn->real_escape_string($body['correo']    ?? '');
        $contrasena = $conn->real_escape_string($body['contrasena'] ?? '');

        // Verificar si ya existe
        $check = $conn->query("SELECT 1 FROM Usuarios WHERE correo='$correo'");
        if ($check->num_rows > 0) {
            echo json_encode(['ok' => false, 'error' => 'El correo ya existe']);
            break;
        }
        $ok = $conn->query(
            "INSERT INTO Usuarios (nombre, correo, contrasena)
             VALUES ('$nombre', '$correo', '$contrasena')"
        );
        echo json_encode(['ok' => $ok]);
        break;

    // ── UPDATE PROFILE ───────────────────────
    case 'update':
        $body   = json_decode(file_get_contents('php://input'), true);
        $correo = $conn->real_escape_string($body['correo'] ?? '');
        $nombre = $conn->real_escape_string($body['nombre'] ?? '');
        $edad   = (int)($body['edad'] ?? 0);
        $peso   = (float)($body['peso'] ?? 0);
        $sexo   = $conn->real_escape_string($body['sexo'] ?? '');
        $ok = $conn->query(
            "UPDATE Usuarios SET nombre='$nombre', edad=$edad, peso=$peso, sexo='$sexo'
             WHERE correo='$correo'"
        );
        echo json_encode(['ok' => $ok && $conn->affected_rows >= 0]);
        break;

    // ── UPDATE PASSWORD ──────────────────────
    case 'updatePass':
        $body       = json_decode(file_get_contents('php://input'), true);
        $correo     = $conn->real_escape_string($body['correo']    ?? '');
        $contrasena = $conn->real_escape_string($body['contrasena'] ?? '');
        $ok = $conn->query(
            "UPDATE Usuarios SET contrasena='$contrasena' WHERE correo='$correo'"
        );
        echo json_encode(['ok' => $ok]);
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
