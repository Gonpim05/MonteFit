<?php
// =============================================
// logros.php  —  Endpoints de Logros
// =============================================
// GET ?action=getLogros&usuario_id=X
// =============================================
require_once 'db.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    case 'getLogros':
        $uid = (int)($_GET['usuario_id'] ?? 0);
        $res = $conn->query(
            "SELECT l.logro_id, l.titulo, l.descripcion,
                    (SELECT COUNT(*) FROM Usuarios_Logros ul
                     WHERE ul.logro_id = l.logro_id AND ul.usuario_id = $uid) AS obtenido
             FROM Logros l"
        );
        $lista = [];
        while ($row = $res->fetch_assoc()) {
            $row['obtenido'] = (int)$row['obtenido'];
            $lista[] = $row;
        }
        echo json_encode($lista);
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
