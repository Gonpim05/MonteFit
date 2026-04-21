<?php
// =============================================
// comidas.php  —  Endpoints de Comidas
// =============================================
// GET  ?action=getAll
// POST ?action=add    body: {nombre, calorias, proteinas, carbohidratos, grasas, correo}
// POST ?action=update body: {id, nombre, calorias, proteinas, carbohidratos, grasas}
// POST ?action=delete body: {id}
// =============================================
require_once 'db.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    case 'getAll':
        $res = $conn->query(
            "SELECT comida_id AS id, nombre, calorias, proteinas, carbohidratos, grasas
             FROM Comidas"
        );
        $lista = [];
        while ($row = $res->fetch_assoc()) $lista[] = $row;
        echo json_encode($lista);
        break;

    case 'add':
        $body   = json_decode(file_get_contents('php://input'), true);
        $nombre = $conn->real_escape_string($body['nombre'] ?? '');
        $cal    = (float)($body['calorias']      ?? 0);
        $prot   = (float)($body['proteinas']     ?? 0);
        $carb   = (float)($body['carbohidratos'] ?? 0);
        $gras   = (float)($body['grasas']        ?? 0);
        $correo = $conn->real_escape_string($body['correo'] ?? '');

        // Obtener usuario_id
        $res = $conn->query("SELECT usuario_id FROM Usuarios WHERE correo='$correo'");
        $row = $res->fetch_assoc();
        $uid = $row ? (int)$row['usuario_id'] : 1;

        $ok = $conn->query(
            "INSERT INTO Comidas (nombre, usuario_id, calorias, proteinas, carbohidratos, grasas)
             VALUES ('$nombre', $uid, $cal, $prot, $carb, $gras)"
        );
        echo json_encode(['ok' => $ok]);
        break;

    case 'update':
        $body   = json_decode(file_get_contents('php://input'), true);
        $id     = (int)($body['id']              ?? 0);
        $nombre = $conn->real_escape_string($body['nombre'] ?? '');
        $cal    = (float)($body['calorias']      ?? 0);
        $prot   = (float)($body['proteinas']     ?? 0);
        $carb   = (float)($body['carbohidratos'] ?? 0);
        $gras   = (float)($body['grasas']        ?? 0);
        $ok = $conn->query(
            "UPDATE Comidas SET nombre='$nombre', calorias=$cal, proteinas=$prot,
             carbohidratos=$carb, grasas=$gras WHERE comida_id=$id"
        );
        echo json_encode(['ok' => $ok]);
        break;

    case 'delete':
        $body = json_decode(file_get_contents('php://input'), true);
        $id   = (int)($body['id'] ?? 0);
        $ok   = $conn->query("DELETE FROM Comidas WHERE comida_id=$id");
        echo json_encode(['ok' => $ok]);
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
