<?php
// =============================================
// rutinas.php  —  Endpoints de Rutinas/Entrenamientos
// =============================================
// GET  ?action=getMias&correo=X
// GET  ?action=getPublicas&usuario_id=X
// GET  ?action=getDetalles&rutina_id=X
// GET  ?action=isPublico&rutina_id=X
// POST ?action=add       body: {fecha, correo, es_publico}
// POST ?action=addDetalle body: {rutina_id, ejercicio_nombre, series, repeticiones, peso}
// POST ?action=delete    body: {id}
// POST ?action=toggle    body: {rutina_id}
// =============================================
require_once 'db.php';
require_once 'evaluar_logros.php';

$action = $_GET['action'] ?? '';
$conn   = getConexion();

switch ($action) {

    // ── MIS ENTRENAMIENTOS ───────────────────
    case 'getMias':
        $correo = $conn->real_escape_string($_GET['correo'] ?? '');
        $res = $conn->query(
            "SELECT u.usuario_id FROM Usuarios u WHERE u.correo='$correo'"
        );
        $row = $res->fetch_assoc();
        if (!$row) { echo json_encode([]); break; }
        $uid = (int)$row['usuario_id'];
        $res2 = $conn->query(
            "SELECT rutina_id AS id, fecha_creacion AS date, es_publico
             FROM Rutinas WHERE usuario_id=$uid ORDER BY rutina_id DESC"
        );
        $lista = [];
        while ($r = $res2->fetch_assoc()) $lista[] = $r;
        echo json_encode($lista);
        break;

    // ── ENTRENS PÚBLICOS DE OTRO USUARIO ─────
    case 'getPublicas':
        $uid = (int)($_GET['usuario_id'] ?? 0);
        $res = $conn->query(
            "SELECT rutina_id AS id, fecha_creacion AS date
             FROM Rutinas WHERE usuario_id=$uid AND es_publico=1
             ORDER BY rutina_id DESC"
        );
        $lista = [];
        while ($r = $res->fetch_assoc()) $lista[] = $r;
        echo json_encode($lista);
        break;

    // ── DETALLES DE UN ENTRENAMIENTO ─────────
    case 'getDetalles':
        $rid = (int)($_GET['rutina_id'] ?? 0);
        $res = $conn->query(
            "SELECT e.nombre AS nombre_ejercicio,
                    rd.series, rd.repeticiones, rd.kilos AS peso
             FROM Rutina_Detalle rd
             JOIN Ejercicios e ON rd.ejercicio_id = e.ejercicio_id
             WHERE rd.rutina_id=$rid"
        );
        $lista = [];
        while ($r = $res->fetch_assoc()) $lista[] = $r;
        echo json_encode($lista);
        break;

    // ── ¿ES PÚBLICO? ─────────────────────────
    case 'isPublico':
        $rid = (int)($_GET['rutina_id'] ?? 0);
        $res = $conn->query("SELECT es_publico FROM Rutinas WHERE rutina_id=$rid");
        $row = $res->fetch_assoc();
        echo json_encode(['es_publico' => $row ? (bool)$row['es_publico'] : true]);
        break;

    // ── CREAR ENTRENAMIENTO ──────────────────
    case 'add':
        $body      = json_decode(file_get_contents('php://input'), true);
        $fecha     = $conn->real_escape_string($body['fecha']     ?? date('Y-m-d H:i:s'));
        $correo    = $conn->real_escape_string($body['correo']    ?? '');
        $esPublico = isset($body['es_publico']) ? (int)$body['es_publico'] : 1;

        $res = $conn->query("SELECT usuario_id FROM Usuarios WHERE correo='$correo'");
        $row = $res->fetch_assoc();
        if (!$row) { echo json_encode(['ok' => false, 'rutina_id' => -1]); break; }
        $uid = (int)$row['usuario_id'];

        $nombre = $conn->real_escape_string("Entrenamiento $fecha");
        $conn->query(
            "INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico)
             VALUES ($uid, '$nombre', '$fecha', $esPublico)"
        );
        echo json_encode(['ok' => true, 'rutina_id' => (int)$conn->insert_id]);
        break;

    // ── AÑADIR DETALLE A ENTRENAMIENTO ───────
    case 'addDetalle':
        $body      = json_decode(file_get_contents('php://input'), true);
        $rid       = (int)($body['rutina_id']        ?? 0);
        $ejercNombre = $conn->real_escape_string($body['ejercicio_nombre'] ?? '');
        $series    = (int)($body['series']           ?? 0);
        $reps      = (int)($body['repeticiones']     ?? 0);
        $peso      = (float)($body['peso']           ?? 0);

        // Buscar ejercicio_id por nombre
        $res = $conn->query(
            "SELECT ejercicio_id FROM Ejercicios WHERE nombre='$ejercNombre'"
        );
        $row = $res->fetch_assoc();
        if (!$row) { echo json_encode(['ok' => false, 'error' => 'Ejercicio no encontrado']); break; }
        $eid = (int)$row['ejercicio_id'];

        $ok = $conn->query(
            "INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos)
             VALUES ($rid, $eid, $series, $reps, $peso)"
        );
        
        if ($ok) {
            // Actualizar Rankings si es un nuevo máximo semanal
            $uidRes = $conn->query("SELECT usuario_id FROM Rutinas WHERE rutina_id = $rid");
            if ($uidRow = $uidRes->fetch_assoc()) {
                $uid = (int)$uidRow['usuario_id'];
                
                // Actualizar Ranking Mensual/Semanal
                $rankRes = $conn->query("SELECT ranking_id, peso_maximo FROM Rankings_Mensuales WHERE usuario_id = $uid AND ejercicio_id = $eid AND semana = WEEK(NOW(), 1) AND anio = YEAR(NOW())");
                if ($rankRow = $rankRes->fetch_assoc()) {
                    if ($peso > (float)$rankRow['peso_maximo']) {
                        $conn->query("UPDATE Rankings_Mensuales SET peso_maximo = $peso WHERE ranking_id = " . $rankRow['ranking_id']);
                    }
                } else {
                    $conn->query("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES ($eid, $uid, $peso, WEEK(NOW(), 1), YEAR(NOW()))");
                }
                
                // Evaluar todos los logros posibles
                evaluarLogros($conn, $uid);
            }
        }
        
        echo json_encode(['ok' => $ok]);
        break;

    // ── BORRAR ENTRENAMIENTO ─────────────────
    case 'delete':
        $body = json_decode(file_get_contents('php://input'), true);
        $id   = (int)($body['id'] ?? 0);
        $ok   = $conn->query("DELETE FROM Rutinas WHERE rutina_id=$id");
        echo json_encode(['ok' => $ok]);
        break;

    // ── TOGGLE PÚBLICO/PRIVADO ───────────────
    case 'toggle':
        $body = json_decode(file_get_contents('php://input'), true);
        $rid  = (int)($body['rutina_id'] ?? 0);
        $res  = $conn->query("SELECT es_publico FROM Rutinas WHERE rutina_id=$rid");
        $row  = $res->fetch_assoc();
        if ($row) {
            $nuevo = $row['es_publico'] ? 0 : 1;
            $conn->query("UPDATE Rutinas SET es_publico=$nuevo WHERE rutina_id=$rid");
            echo json_encode(['ok' => true, 'es_publico' => (bool)$nuevo]);
        } else {
            echo json_encode(['ok' => false]);
        }
        break;

    default:
        http_response_code(400);
        echo json_encode(['error' => 'Accion no reconocida']);
}

$conn->close();
