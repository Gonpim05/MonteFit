<?php
// =============================================
// evaluar_logros.php
// Sistema de validación de logros para usuarios
// =============================================

function evaluarLogros($conn, $usuario_id) {
    if (!$conn || $usuario_id <= 0) return;

    $uid = (int)$usuario_id;
    $logros_obtenidos = [];

    // Obtener logros ya conseguidos
    $res = $conn->query("SELECT logro_id FROM Usuarios_Logros WHERE usuario_id = $uid");
    while ($row = $res->fetch_assoc()) {
        $logros_obtenidos[] = (int)$row['logro_id'];
    }

    $nuevos_logros = [];

    // Verificaciones

    // 1. Primer Paso: 1 entrenamiento
    // 6. Maratoniano: 20 entrenamientos
    $res = $conn->query("SELECT COUNT(*) AS total FROM Rutinas WHERE usuario_id = $uid");
    if ($row = $res->fetch_assoc()) {
        $total = (int)$row['total'];
        if ($total >= 1 && !in_array(1, $logros_obtenidos)) $nuevos_logros[] = 1;
        if ($total >= 20 && !in_array(6, $logros_obtenidos)) $nuevos_logros[] = 6;
    }

    // 2. Constancia: 3 veces en una semana
    // 4. Maquina: 5 veces en una semana
    $res = $conn->query("SELECT COUNT(*) AS semanales FROM Rutinas WHERE usuario_id = $uid AND WEEK(fecha_creacion, 1) = WEEK(NOW(), 1) AND YEAR(fecha_creacion) = YEAR(NOW())");
    if ($row = $res->fetch_assoc()) {
        $semanales = (int)$row['semanales'];
        if ($semanales >= 3 && !in_array(2, $logros_obtenidos)) $nuevos_logros[] = 2;
        if ($semanales >= 5 && !in_array(4, $logros_obtenidos)) $nuevos_logros[] = 4;
    }

    // 3. Bestia: > 100kg
    // 5. Titan: > 150kg
    $res = $conn->query("SELECT MAX(kilos) AS max_peso FROM Rutina_Detalle rd JOIN Rutinas r ON rd.rutina_id = r.rutina_id WHERE r.usuario_id = $uid");
    if ($row = $res->fetch_assoc()) {
        $max_peso = (float)$row['max_peso'];
        if ($max_peso >= 100 && !in_array(3, $logros_obtenidos)) $nuevos_logros[] = 3;
        if ($max_peso >= 150 && !in_array(5, $logros_obtenidos)) $nuevos_logros[] = 5;
    }

    // 8. Nutricionista: 10 comidas
    $res = $conn->query("SELECT COUNT(*) AS comidas FROM Comidas WHERE usuario_id = $uid");
    if ($row = $res->fetch_assoc()) {
        if ((int)$row['comidas'] >= 10 && !in_array(8, $logros_obtenidos)) $nuevos_logros[] = 8;
    }

    // 9. Centurion: > 100 reps en una rutina
    $res = $conn->query("SELECT MAX(total_reps) AS max_reps FROM (SELECT sum(repeticiones) as total_reps FROM Rutina_Detalle rd JOIN Rutinas r ON rd.rutina_id = r.rutina_id WHERE r.usuario_id = $uid GROUP BY r.rutina_id) sub");
    if ($row = $res->fetch_assoc()) {
        if ((int)$row['max_reps'] >= 100 && !in_array(9, $logros_obtenidos)) $nuevos_logros[] = 9;
    }

    // Insertar los nuevos logros
    if (!empty($nuevos_logros)) {
        $sql = "INSERT INTO Usuarios_Logros (usuario_id, logro_id) VALUES ";
        $vals = [];
        foreach ($nuevos_logros as $l_id) {
            $vals[] = "($uid, $l_id)";
        }
        $conn->query($sql . implode(", ", $vals));
    }
}
