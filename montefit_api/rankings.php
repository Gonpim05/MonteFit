<?php
// =============================================
// rankings.php  —  Endpoints de Rankings
// =============================================
// GET ?action=getRanking&ejercicio_id=X
// =============================================
require_once 'db.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    case 'getRanking':
        $eid = (int)($_GET['ejercicio_id'] ?? 0);
        $res = $conn->query(
            "SELECT u.nombre,
                    r.peso_maximo,
                    r.semana,
                    r.anio,
                    r.fecha_registro AS fecha
             FROM Rankings_Mensuales r
             JOIN Usuarios u ON r.usuario_id = u.usuario_id
             WHERE r.ejercicio_id = $eid
               AND r.semana = WEEK(NOW())
               AND r.anio   = YEAR(NOW())
             ORDER BY r.peso_maximo DESC
             LIMIT 10"
        );
        $lista = [];
        while ($row = $res->fetch_assoc()) $lista[] = $row;
        echo json_encode($lista);
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
