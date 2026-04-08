-- =============================================
-- MonteFit - Base de Datos MySQL
-- Equivalente a la base de datos SQLite de la app
-- =============================================

CREATE DATABASE IF NOT EXISTS MonfitDB;
USE MonfitDB;

-- 1. Usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    edad INT DEFAULT 0,
    peso DOUBLE DEFAULT 0.0,
    sexo VARCHAR(20) DEFAULT '',
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Ejercicios
CREATE TABLE IF NOT EXISTS Ejercicios (
    ejercicio_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    grupo_muscular VARCHAR(50),
    descripcion TEXT,
    video_url VARCHAR(255),
    dificultad VARCHAR(20) DEFAULT 'Medio'
) ENGINE=InnoDB;

-- 3. Rutinas (con campo publico/privado)
CREATE TABLE IF NOT EXISTS Rutinas (
    rutina_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    es_publico TINYINT(1) DEFAULT 1,
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Rutina_Detalle
CREATE TABLE IF NOT EXISTS Rutina_Detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rutina_id INT NOT NULL,
    ejercicio_id INT NOT NULL,
    series INT,
    repeticiones INT,
    kilos DOUBLE,
    FOREIGN KEY (rutina_id) REFERENCES Rutinas(rutina_id) ON DELETE CASCADE,
    FOREIGN KEY (ejercicio_id) REFERENCES Ejercicios(ejercicio_id)
) ENGINE=InnoDB;

-- 5. Comidas
CREATE TABLE IF NOT EXISTS Comidas (
    comida_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario_id INT NOT NULL,
    calorias DOUBLE,
    carbohidratos DOUBLE,
    proteinas DOUBLE,
    grasas DOUBLE,
    codigo_barras VARCHAR(50),
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Rankings Semanales
CREATE TABLE IF NOT EXISTS Rankings_Mensuales (
    ranking_id INT AUTO_INCREMENT PRIMARY KEY,
    ejercicio_id INT NOT NULL,
    usuario_id INT NOT NULL,
    peso_maximo DOUBLE,
    semana INT,
    anio INT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ejercicio_id) REFERENCES Ejercicios(ejercicio_id),
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Logros
CREATE TABLE IF NOT EXISTS Logros (
    logro_id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100),
    descripcion TEXT
) ENGINE=InnoDB;

-- 8. Usuarios_Logros
CREATE TABLE IF NOT EXISTS Usuarios_Logros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    logro_id INT NOT NULL,
    fecha_obtenido DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE,
    FOREIGN KEY (logro_id) REFERENCES Logros(logro_id),
    UNIQUE KEY unique_usuario_logro (usuario_id, logro_id)
) ENGINE=InnoDB;


-- =============================================
-- DATOS DE EJEMPLO
-- =============================================

-- Ejercicios (35 ejercicios, 11 grupos musculares)

-- Pecho
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Banca', 'Pecho', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Inclinado', 'Pecho', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Aperturas Mancuernas', 'Pecho', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Fondos', 'Pecho', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Cruces en Polea', 'Pecho', 'Facil');

-- Espalda
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Peso Muerto', 'Espalda', 'Dificil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Dominadas', 'Espalda', 'Dificil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Remo con Barra', 'Espalda', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Jalon al Pecho', 'Espalda', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Remo Mancuerna', 'Espalda', 'Medio');

-- Hombro
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Militar', 'Hombro', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevaciones Laterales', 'Hombro', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Pajaro', 'Hombro', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Face Pull', 'Hombro', 'Facil');

-- Biceps
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl con Barra', 'Biceps', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Martillo', 'Biceps', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Concentrado', 'Biceps', 'Facil');

-- Triceps
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Fondos en Banco', 'Triceps', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extension con Polea', 'Triceps', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Frances', 'Triceps', 'Medio');

-- Pierna
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Sentadilla', 'Pierna', 'Dificil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Prensa', 'Pierna', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extension de Cuadriceps', 'Pierna', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Femoral', 'Pierna', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Zancadas', 'Pierna', 'Medio');

-- Abdomen
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Crunch', 'Abdomen', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Plancha', 'Abdomen', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevacion de Piernas', 'Abdomen', 'Medio');

-- Gluteos
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Hip Thrust', 'Gluteos', 'Medio');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Patada de Gluteo', 'Gluteos', 'Facil');

-- Gemelos
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevacion de Gemelos', 'Gemelos', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Gemelos en Prensa', 'Gemelos', 'Facil');

-- Trapecio
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Encogimientos con Barra', 'Trapecio', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Encogimientos con Mancuernas', 'Trapecio', 'Facil');

-- Antebrazo
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl de Muneca', 'Antebrazo', 'Facil');
INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extensiones de Muneca', 'Antebrazo', 'Facil');


-- Logros (10)
INSERT INTO Logros (titulo, descripcion) VALUES ('Primer Paso', 'Completa tu primer entrenamiento');
INSERT INTO Logros (titulo, descripcion) VALUES ('Constancia', 'Entrena 3 veces en una semana');
INSERT INTO Logros (titulo, descripcion) VALUES ('Bestia', 'Levanta mas de 100kg en un ejercicio');
INSERT INTO Logros (titulo, descripcion) VALUES ('Maquina', 'Entrena 5 veces en una semana');
INSERT INTO Logros (titulo, descripcion) VALUES ('Titan', 'Levanta mas de 150kg en un ejercicio');
INSERT INTO Logros (titulo, descripcion) VALUES ('Maratoniano', 'Completa 20 entrenamientos en total');
INSERT INTO Logros (titulo, descripcion) VALUES ('Full Body', 'Entrena todos los grupos musculares');
INSERT INTO Logros (titulo, descripcion) VALUES ('Nutricionista', 'Registra 10 comidas diferentes');
INSERT INTO Logros (titulo, descripcion) VALUES ('Centurion', 'Supera las 100 repeticiones en un entrenamiento');
INSERT INTO Logros (titulo, descripcion) VALUES ('Imparable', 'Entrena durante 4 semanas seguidas');


-- Usuarios de prueba
INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo) VALUES ('Mounir', 'mounir@test.com', '1234', 24, 78.5, 'Hombre');
INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo) VALUES ('Carlos', 'carlos@test.com', '1234', 22, 85.0, 'Hombre');
INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo) VALUES ('Ana', 'ana@test.com', '1234', 21, 62.0, 'Mujer');


-- Rutinas de ejemplo
INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES (1, 'Entrenamiento 1', DATE_SUB(NOW(), INTERVAL 1 DAY), 1);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (1, 1, 4, 10, 80.0);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (1, 21, 3, 12, 100.0);

INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES (1, 'Entrenamiento 2', DATE_SUB(NOW(), INTERVAL 3 DAY), 1);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (2, 6, 5, 5, 120.0);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (2, 1, 3, 8, 85.0);

INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES (1, 'Entrenamiento 3', DATE_SUB(NOW(), INTERVAL 6 DAY), 0);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (3, 21, 4, 10, 110.0);

INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES (2, 'Entrenamiento Carlos', DATE_SUB(NOW(), INTERVAL 2 DAY), 1);
INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES (4, 1, 4, 8, 90.0);


-- Rankings semanales (usar WEEK y YEAR de MySQL)
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (1, 1, 85.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (1, 2, 90.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (1, 3, 70.0, WEEK(NOW()), YEAR(NOW()));

INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (21, 1, 110.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (21, 2, 95.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (21, 3, 85.0, WEEK(NOW()), YEAR(NOW()));

INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (6, 1, 120.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (6, 2, 130.0, WEEK(NOW()), YEAR(NOW()));
INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES (6, 3, 100.0, WEEK(NOW()), YEAR(NOW()));


-- Comidas de ejemplo
INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES ('Pollo con arroz', 1, 450, 50.0, 35.0, 8.0);
INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES ('Ensalada de atun', 1, 320, 15.0, 28.0, 18.0);
INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES ('Batido proteico', 1, 180, 10.0, 25.0, 3.0);


-- Logros obtenidos
INSERT INTO Usuarios_Logros (usuario_id, logro_id) VALUES (1, 1);
INSERT INTO Usuarios_Logros (usuario_id, logro_id) VALUES (1, 3);
