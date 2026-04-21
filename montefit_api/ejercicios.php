<?php
// =============================================
// ejercicios.php  —  Endpoints de Ejercicios
// =============================================
// GET  ?action=getAll
// GET  ?action=getByGrupo&grupo=X   (o grupo=Todos)
// POST ?action=add    body: {nombre, grupo_muscular}
// POST ?action=update body: {id, nombre, grupo_muscular}
// POST ?action=delete body: {id}
// =============================================
require_once 'db.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    case 'getAll':
        $res = $conn->query(
            "SELECT ejercicio_id AS _id, nombre, grupo_muscular FROM Ejercicios"
        );
        $lista = [];
        while ($row = $res->fetch_assoc()) $lista[] = $row;
        echo json_encode($lista);
        break;

    case 'getByGrupo':
        $grupo = $conn->real_escape_string($_GET['grupo'] ?? 'Todos');
        if ($grupo === 'Todos') {
            $res = $conn->query(
                "SELECT ejercicio_id AS id, nombre, grupo_muscular AS parte_cuerpo FROM Ejercicios"
            );
        } else {
            $res = $conn->query(
                "SELECT ejercicio_id AS id, nombre, grupo_muscular AS parte_cuerpo
                 FROM Ejercicios WHERE grupo_muscular='$grupo'"
            );
        }
        $lista = [];
        while ($row = $res->fetch_assoc()) $lista[] = $row;
        echo json_encode($lista);
        break;

    case 'add':
        $body   = json_decode(file_get_contents('php://input'), true);
        $nombre = $conn->real_escape_string($body['nombre']        ?? '');
        $grupo  = $conn->real_escape_string($body['grupo_muscular'] ?? '');
        $ok = $conn->query(
            "INSERT INTO Ejercicios (nombre, grupo_muscular) VALUES ('$nombre', '$grupo')"
        );
        echo json_encode(['ok' => $ok]);
        break;

    case 'update':
        $body   = json_decode(file_get_contents('php://input'), true);
        $id     = (int)($body['id'] ?? 0);
        $nombre = $conn->real_escape_string($body['nombre']        ?? '');
        $grupo  = $conn->real_escape_string($body['grupo_muscular'] ?? '');
        $ok = $conn->query(
            "UPDATE Ejercicios SET nombre='$nombre', grupo_muscular='$grupo'
             WHERE ejercicio_id=$id"
        );
        echo json_encode(['ok' => $ok]);
        break;

    case 'delete':
        $body = json_decode(file_get_contents('php://input'), true);
        $id   = (int)($body['id'] ?? 0);
        $ok   = $conn->query("DELETE FROM Ejercicios WHERE ejercicio_id=$id");
        echo json_encode(['ok' => $ok]);
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
