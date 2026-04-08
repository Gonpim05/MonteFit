package com.example.montefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestorBaseDatos extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "MonfitDB";
        private static final int DATABASE_VERSION = 10;

        // Nombres de tablas
        public static final String TABLE_USUARIOS = "Usuarios";
        public static final String TABLE_EJERCICIOS = "Ejercicios";
        public static final String TABLE_RUTINAS = "Rutinas";
        public static final String TABLE_RUTINA_DETALLE = "Rutina_Detalle";
        public static final String TABLE_COMIDAS = "Comidas";
        public static final String TABLE_RANKINGS = "Rankings_Mensuales";
        public static final String TABLE_LOGROS = "Logros";
        public static final String TABLE_USUARIOS_LOGROS = "Usuarios_Logros";

        // Columnas comunes
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_NOMBRE = "nombre";

        // Columnas Ejercicios
        public static final String COLUMN_EJERCICIO_ID = "ejercicio_id";
        public static final String COLUMN_EJERCICIO_NOMBRE = "nombre";
        public static final String COLUMN_GRUPO_MUSCULAR = "grupo_muscular";

        // Columnas Rutinas
        public static final String COLUMN_RUTINA_ID = "rutina_id";
        public static final String COLUMN_RUTINA_USUARIO_ID = "usuario_id";
        public static final String COLUMN_RUTINA_NOMBRE = "nombre";
        public static final String COLUMN_RUTINA_FECHA = "fecha_creacion";
        public static final String COLUMN_RUTINA_PUBLICA = "es_publico";

        // Columnas Rutina_Detalle
        public static final String COLUMN_DETALLE_RUTINA_ID = "rutina_id";
        public static final String COLUMN_DETALLE_EJERCICIO_ID = "ejercicio_id";
        public static final String COLUMN_SERIES = "series";
        public static final String COLUMN_REPETICIONES = "repeticiones";
        public static final String COLUMN_KILOS = "kilos";

        // Columnas Comidas
        public static final String COLUMN_COMIDA_ID = "comida_id";
        public static final String COLUMN_COMIDA_NOMBRE = "nombre";
        public static final String COLUMN_COMIDA_USUARIO_ID = "usuario_id";
        public static final String COLUMN_CALORIAS = "calorias";
        public static final String COLUMN_CARBOHIDRATOS = "carbohidratos";
        public static final String COLUMN_PROTEINAS = "proteinas";
        public static final String COLUMN_GRASAS = "grasas";

        // Columnas Rankings
        public static final String COLUMN_PESO_MAXIMO = "peso_maximo";
        public static final String COLUMN_MES = "mes";
        public static final String COLUMN_ANIO = "anio";

        // Columnas Logros
        public static final String COLUMN_LOGRO_ID = "logro_id";
        public static final String COLUMN_LOGRO_TITULO = "titulo";
        public static final String COLUMN_LOGRO_DESCRIPCION = "descripcion";

        // Constructor
        public GestorBaseDatos(Context contexto) {
                super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
                // 1. Usuarios (con campos de perfil)
                db.execSQL("CREATE TABLE Usuarios (" +
                                "usuario_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nombre TEXT NOT NULL, " +
                                "correo TEXT UNIQUE NOT NULL, " +
                                "contrasena TEXT NOT NULL, " +
                                "edad INTEGER DEFAULT 0, " +
                                "peso REAL DEFAULT 0.0, " +
                                "sexo TEXT DEFAULT '', " +
                                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP)");

                // 2. Ejercicios
                db.execSQL("CREATE TABLE Ejercicios (" +
                                "ejercicio_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nombre TEXT NOT NULL, " +
                                "grupo_muscular TEXT, " +
                                "descripcion TEXT, " +
                                "video_url TEXT, " +
                                "dificultad TEXT DEFAULT 'Medio')");

                // 3. Rutinas (con campo publico/privado)
                db.execSQL("CREATE TABLE Rutinas (" +
                                "rutina_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "usuario_id INTEGER NOT NULL, " +
                                "nombre TEXT NOT NULL, " +
                                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                "es_publico INTEGER DEFAULT 1, " +
                                "FOREIGN KEY(usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE)");

                // 4. Rutina_Detalle
                db.execSQL("CREATE TABLE Rutina_Detalle (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "rutina_id INTEGER NOT NULL, " +
                                "ejercicio_id INTEGER NOT NULL, " +
                                "series INTEGER, " +
                                "repeticiones INTEGER, " +
                                "kilos REAL, " +
                                "FOREIGN KEY(rutina_id) REFERENCES Rutinas(rutina_id) ON DELETE CASCADE, " +
                                "FOREIGN KEY(ejercicio_id) REFERENCES Ejercicios(ejercicio_id))");

                // 5. Comidas
                db.execSQL("CREATE TABLE Comidas (" +
                                "comida_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nombre TEXT NOT NULL, " +
                                "usuario_id INTEGER NOT NULL, " +
                                "calorias REAL, " +
                                "carbohidratos REAL, " +
                                "proteinas REAL, " +
                                "grasas REAL, " +
                                "codigo_barras TEXT, " +
                                "FOREIGN KEY(usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE)");

                // 6. Rankings Semanales
                db.execSQL("CREATE TABLE Rankings_Mensuales (" +
                                "ranking_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "ejercicio_id INTEGER NOT NULL, " +
                                "usuario_id INTEGER NOT NULL, " +
                                "peso_maximo REAL, " +
                                "semana INTEGER, " +
                                "anio INTEGER, " +
                                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                "FOREIGN KEY(ejercicio_id) REFERENCES Ejercicios(ejercicio_id), " +
                                "FOREIGN KEY(usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE)");

                // 7. Logros
                db.execSQL("CREATE TABLE Logros (" +
                                "logro_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "titulo TEXT, " +
                                "descripcion TEXT)");

                // 8. Usuarios Logros
                db.execSQL("CREATE TABLE Usuarios_Logros (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "usuario_id INTEGER NOT NULL, " +
                                "logro_id INTEGER NOT NULL, " +
                                "fecha_obtenido DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                "FOREIGN KEY(usuario_id) REFERENCES Usuarios(usuario_id) ON DELETE CASCADE, " +
                                "FOREIGN KEY(logro_id) REFERENCES Logros(logro_id), " +
                                "UNIQUE(usuario_id, logro_id))");

                seedData(db);
        }

        private void seedData(SQLiteDatabase db) {
                // =============================================
                // EJERCICIOS - Todos los grupos musculares
                // =============================================

                // Pecho
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Banca', 'Pecho', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Inclinado', 'Pecho', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Aperturas Mancuernas', 'Pecho', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Fondos', 'Pecho', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Cruces en Polea', 'Pecho', 'Facil')");

                // Espalda
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Peso Muerto', 'Espalda', 'Dificil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Dominadas', 'Espalda', 'Dificil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Remo con Barra', 'Espalda', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Jalon al Pecho', 'Espalda', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Remo Mancuerna', 'Espalda', 'Medio')");

                // Hombro
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Militar', 'Hombro', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevaciones Laterales', 'Hombro', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Pajaro', 'Hombro', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Face Pull', 'Hombro', 'Facil')");

                // Biceps
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl con Barra', 'Biceps', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Martillo', 'Biceps', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Concentrado', 'Biceps', 'Facil')");

                // Triceps
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Fondos en Banco', 'Triceps', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extension con Polea', 'Triceps', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Frances', 'Triceps', 'Medio')");

                // Pierna
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Sentadilla', 'Pierna', 'Dificil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Prensa', 'Pierna', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extension de Cuadriceps', 'Pierna', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl Femoral', 'Pierna', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Zancadas', 'Pierna', 'Medio')");

                // Abdomen
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Crunch', 'Abdomen', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Plancha', 'Abdomen', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevacion de Piernas', 'Abdomen', 'Medio')");

                // Gluteos
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Hip Thrust', 'Gluteos', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Patada de Gluteo', 'Gluteos', 'Facil')");

                // Gemelos
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Elevacion de Gemelos', 'Gemelos', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Gemelos en Prensa', 'Gemelos', 'Facil')");

                // Trapecio
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Encogimientos con Barra', 'Trapecio', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Encogimientos con Mancuernas', 'Trapecio', 'Facil')");

                // Antebrazo
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Curl de Muneca', 'Antebrazo', 'Facil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Extensiones de Muneca', 'Antebrazo', 'Facil')");

                // =============================================
                // LOGROS (10 logros)
                // =============================================
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Primer Paso', 'Completa tu primer entrenamiento')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Constancia', 'Entrena 3 veces en una semana')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Bestia', 'Levanta mas de 100kg en un ejercicio')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Maquina', 'Entrena 5 veces en una semana')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Titan', 'Levanta mas de 150kg en un ejercicio')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Maratoniano', 'Completa 20 entrenamientos en total')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Full Body', 'Entrena todos los grupos musculares')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Nutricionista', 'Registra 10 comidas diferentes')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Centurion', 'Supera las 100 repeticiones en un entrenamiento')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Imparable', 'Entrena durante 4 semanas seguidas')");

                // =============================================
                // USUARIOS DE PRUEBA
                // =============================================
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo, fecha_registro) VALUES " +
                                "('Mounir', 'mounir@test.com', '1234', 24, 78.5, 'Hombre', datetime('now'))");
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo, fecha_registro) VALUES " +
                                "('Carlos', 'carlos@test.com', '1234', 22, 85.0, 'Hombre', datetime('now'))");
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, edad, peso, sexo, fecha_registro) VALUES " +
                                "('Ana', 'ana@test.com', '1234', 21, 62.0, 'Mujer', datetime('now'))");

                // Obtener semana y año actual para rankings
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int semanaActual = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                int anioActual = cal.get(java.util.Calendar.YEAR);

                // =============================================
                // RUTINAS DE EJEMPLO
                // =============================================

                // Rutinas para Mounir (usuario_id = 1) - públicas
                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES " +
                                "(1, 'Entrenamiento 1', datetime('now', '-1 day'), 1)");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(1, 1, 4, 10, 80.0)"); // Press Banca
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(1, 21, 3, 12, 100.0)"); // Sentadilla

                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES " +
                                "(1, 'Entrenamiento 2', datetime('now', '-3 day'), 1)");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(2, 6, 5, 5, 120.0)"); // Peso Muerto
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(2, 1, 3, 8, 85.0)"); // Press Banca

                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES " +
                                "(1, 'Entrenamiento 3', datetime('now', '-6 day'), 0)"); // privado
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(3, 21, 4, 10, 110.0)"); // Sentadilla

                // Rutinas para Carlos (usuario_id = 2)
                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion, es_publico) VALUES " +
                                "(2, 'Entrenamiento Carlos', datetime('now', '-2 day'), 1)");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(4, 1, 4, 8, 90.0)"); // Press Banca

                // =============================================
                // RANKINGS SEMANALES
                // =============================================
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(1, 1, 85.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Press Banca
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(1, 2, 90.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Press Banca
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(1, 3, 70.0, " + semanaActual + ", " + anioActual + ")"); // Ana Press Banca

                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(21, 1, 110.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Sentadilla
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(21, 2, 95.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Sentadilla
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(21, 3, 85.0, " + semanaActual + ", " + anioActual + ")"); // Ana Sentadilla

                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(6, 1, 120.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Peso Muerto
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(6, 2, 130.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Peso Muerto
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES " +
                                "(6, 3, 100.0, " + semanaActual + ", " + anioActual + ")"); // Ana Peso Muerto

                // =============================================
                // COMIDAS DE EJEMPLO
                // =============================================
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES " +
                                "('Pollo con arroz', 1, 450, 50.0, 35.0, 8.0)");
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES " +
                                "('Ensalada de atun', 1, 320, 15.0, 28.0, 18.0)");
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES " +
                                "('Batido proteico', 1, 180, 10.0, 25.0, 3.0)");

                // =============================================
                // LOGROS OBTENIDOS
                // =============================================
                db.execSQL("INSERT INTO Usuarios_Logros (usuario_id, logro_id, fecha_obtenido) VALUES " +
                                "(1, 1, datetime('now'))");
                db.execSQL("INSERT INTO Usuarios_Logros (usuario_id, logro_id, fecha_obtenido) VALUES " +
                                "(1, 3, datetime('now'))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // Borrar todo y recrear
                db.execSQL("DROP TABLE IF EXISTS Usuarios_Logros");
                db.execSQL("DROP TABLE IF EXISTS Logros");
                db.execSQL("DROP TABLE IF EXISTS Rankings_Mensuales");
                db.execSQL("DROP TABLE IF EXISTS Comidas");
                db.execSQL("DROP TABLE IF EXISTS Rutina_Detalle");
                db.execSQL("DROP TABLE IF EXISTS Rutinas");
                db.execSQL("DROP TABLE IF EXISTS Ejercicios");
                db.execSQL("DROP TABLE IF EXISTS Usuarios");
                onCreate(db);
        }

        // =============================================
        // USUARIOS
        // =============================================
        public boolean crearUsuario(String nombre, String correo, String contrasena) {
                SQLiteDatabase db = this.getWritableDatabase();
                if (checkEmailExists(correo))
                        return false;

                ContentValues values = new ContentValues();
                values.put("nombre", nombre);
                values.put("correo", correo);
                values.put("contrasena", contrasena);
                return db.insert("Usuarios", null, values) != -1;
        }

        private boolean checkEmailExists(String correo) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT 1 FROM Usuarios WHERE correo = ?", new String[] { correo });
                boolean exists = c.getCount() > 0;
                c.close();
                return exists;
        }

        public boolean checkUserPassword(String correo, String contrasena) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT 1 FROM Usuarios WHERE correo = ? AND contrasena = ?",
                                new String[] { correo, contrasena });
                boolean match = c.getCount() > 0;
                c.close();
                return match;
        }

        public int getUserId(String correo) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT usuario_id FROM Usuarios WHERE correo = ?", new String[] { correo });
                if (c.moveToFirst()) {
                        int id = c.getInt(0);
                        c.close();
                        return id;
                }
                c.close();
                return -1;
        }

        public String getUserName(String correo) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT nombre FROM Usuarios WHERE correo = ?", new String[] { correo });
                if (c.moveToFirst()) {
                        String nombre = c.getString(0);
                        c.close();
                        return nombre;
                }
                c.close();
                return "";
        }

        // PERFIL DE USUARIO (con datos reales)
        public Cursor getUserProfile(String correo) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT nombre, correo, edad as age, peso, sexo as sex FROM Usuarios WHERE correo = ?";
                return db.rawQuery(query, new String[] { correo });
        }

        public boolean updateUserProfile(String correo, String nombre, int age, double peso, String sex) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("nombre", nombre);
                values.put("edad", age);
                values.put("peso", peso);
                values.put("sexo", sex);
                return db.update("Usuarios", values, "correo = ?", new String[] { correo }) > 0;
        }

        public boolean updatePassword(String correo, String newPassword) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("contrasena", newPassword);
                return db.update("Usuarios", values, "correo = ?", new String[] { correo }) > 0;
        }

        public boolean checkUser(String correo) {
                return checkEmailExists(correo);
        }

        public boolean addUser(String correo, String contrasena, String nombre) {
                return crearUsuario(nombre, correo, contrasena);
        }

        // =============================================
        // LOGROS
        // =============================================
        public Cursor getLogros(int userId) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT l." + COLUMN_LOGRO_ID + ", " +
                                "l." + COLUMN_LOGRO_TITULO + ", " +
                                "l." + COLUMN_LOGRO_DESCRIPCION + ", " +
                                "(SELECT COUNT(*) FROM " + TABLE_USUARIOS_LOGROS + " ul " +
                                "WHERE ul." + COLUMN_LOGRO_ID + " = l." + COLUMN_LOGRO_ID + " " +
                                "AND ul." + COLUMN_USUARIO_ID + " = ?) as obtenido " +
                                "FROM " + TABLE_LOGROS + " l";
                return db.rawQuery(query, new String[] { String.valueOf(userId) });
        }

        // =============================================
        // SOCIAL / RANKINGS
        // =============================================
        public Cursor getEstadisticasRanking(int ejercicioId) {
                SQLiteDatabase db = this.getReadableDatabase();

                java.util.Calendar cal = java.util.Calendar.getInstance();
                int semanaActual = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                int anioActual = cal.get(java.util.Calendar.YEAR);

                String query = "SELECT u.nombre as " + COLUMN_NOMBRE + ", " +
                                "r.peso_maximo as " + COLUMN_PESO_MAXIMO + ", " +
                                "r.semana, " +
                                "r.anio, " +
                                "r.fecha_registro as fecha " +
                                "FROM " + TABLE_RANKINGS + " r " +
                                "JOIN " + TABLE_USUARIOS + " u ON r." + COLUMN_USUARIO_ID + " = u." + COLUMN_USUARIO_ID
                                + " " +
                                "WHERE r.ejercicio_id = ? " +
                                "AND r.semana = ? " +
                                "AND r.anio = ? " +
                                "ORDER BY r.peso_maximo DESC";
                return db.rawQuery(query, new String[] {
                                String.valueOf(ejercicioId),
                                String.valueOf(semanaActual),
                                String.valueOf(anioActual)
                });
        }

        public Cursor getAllEjercicios() {
                String query = "SELECT " + COLUMN_EJERCICIO_ID + " as _id, " +
                                COLUMN_EJERCICIO_NOMBRE + " as nombre, " +
                                COLUMN_GRUPO_MUSCULAR + " " +
                                "FROM " + TABLE_EJERCICIOS;
                return this.getReadableDatabase().rawQuery(query, null);
        }

        // =============================================
        // COMIDAS
        // =============================================
        public Cursor getAllFoods() {
                String query = "SELECT " +
                                COLUMN_COMIDA_ID + " as id, " +
                                COLUMN_COMIDA_NOMBRE + " as nombre, " +
                                COLUMN_CALORIAS + " as calorias, " +
                                COLUMN_PROTEINAS + " as proteinas, " +
                                COLUMN_CARBOHIDRATOS + " as carbohidratos, " +
                                COLUMN_GRASAS + " as grasas " +
                                "FROM " + TABLE_COMIDAS;
                return this.getReadableDatabase().rawQuery(query, null);
        }

        public boolean deleteFood(int id) {
                SQLiteDatabase db = this.getWritableDatabase();
                return db.delete(TABLE_COMIDAS, COLUMN_COMIDA_ID + "=?", new String[] { String.valueOf(id) }) > 0;
        }

        public boolean updateFood(int id, String nombre, int calorias, double proteinas, double carbohidratos,
                        double grasas) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_COMIDA_NOMBRE, nombre);
                values.put(COLUMN_CALORIAS, calorias);
                values.put(COLUMN_PROTEINAS, proteinas);
                values.put(COLUMN_CARBOHIDRATOS, carbohidratos);
                values.put(COLUMN_GRASAS, grasas);
                return db.update(TABLE_COMIDAS, values, COLUMN_COMIDA_ID + "=?",
                                new String[] { String.valueOf(id) }) > 0;
        }

        public boolean addFood(String nombre, int calorias, double proteinas, double carbohidratos, double grasas) {
                String correoUsuario = GestorUsuarios.getInstance().getCurrentUserEmail();
                int userId = getUserId(correoUsuario);
                if (userId == -1)
                        userId = 1; // Fallback

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_COMIDA_NOMBRE, nombre);
                values.put(COLUMN_COMIDA_USUARIO_ID, userId);
                values.put(COLUMN_CALORIAS, calorias);
                values.put(COLUMN_PROTEINAS, proteinas);
                values.put(COLUMN_CARBOHIDRATOS, carbohidratos);
                values.put(COLUMN_GRASAS, grasas);
                return db.insert(TABLE_COMIDAS, null, values) != -1;
        }

        // =============================================
        // EJERCICIOS
        // =============================================
        public Cursor getExercisesByBodyPart(String parteCuerpo) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT " + COLUMN_EJERCICIO_ID + " as id, " +
                                COLUMN_EJERCICIO_NOMBRE + " as nombre, " +
                                COLUMN_GRUPO_MUSCULAR + " as parte_cuerpo " +
                                "FROM " + TABLE_EJERCICIOS;
                if (!"Todos".equals(parteCuerpo)) {
                        query += " WHERE " + COLUMN_GRUPO_MUSCULAR + " = ?";
                        return db.rawQuery(query, new String[] { parteCuerpo });
                }
                return db.rawQuery(query, null);
        }

        public boolean deleteExercise(int id) {
                SQLiteDatabase db = this.getWritableDatabase();
                return db.delete(TABLE_EJERCICIOS, COLUMN_EJERCICIO_ID + "=?", new String[] { String.valueOf(id) }) > 0;
        }

        public boolean updateExercise(int id, String nombre, String parteCuerpo) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_EJERCICIO_NOMBRE, nombre);
                values.put(COLUMN_GRUPO_MUSCULAR, parteCuerpo);
                return db.update(TABLE_EJERCICIOS, values, COLUMN_EJERCICIO_ID + "=?",
                                new String[] { String.valueOf(id) }) > 0;
        }

        public boolean addExercise(String nombre, String parteCuerpo) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_EJERCICIO_NOMBRE, nombre);
                values.put(COLUMN_GRUPO_MUSCULAR, parteCuerpo);
                return db.insert(TABLE_EJERCICIOS, null, values) != -1;
        }

        // =============================================
        // RUTINAS / ENTRENAMIENTOS
        // =============================================
        public long addTraining(String date, String correoUsuario) {
                return addTraining(date, correoUsuario, true);
        }

        public long addTraining(String date, String correoUsuario, boolean esPublico) {
                int userId = getUserId(correoUsuario);
                if (userId == -1)
                        return -1;

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_RUTINA_USUARIO_ID, userId);
                values.put(COLUMN_RUTINA_NOMBRE, "Entrenamiento " + date);
                values.put(COLUMN_RUTINA_FECHA, date);
                values.put(COLUMN_RUTINA_PUBLICA, esPublico ? 1 : 0);
                return db.insert(TABLE_RUTINAS, null, values);
        }

        public boolean addTrainingDetail(long routineId, String exerciseName, int series, int repeticiones,
                        double peso) {

                int exerciseId = getExerciseIdByName(exerciseName);
                if (exerciseId == -1)
                        return false;

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_DETALLE_RUTINA_ID, routineId);
                values.put(COLUMN_DETALLE_EJERCICIO_ID, exerciseId);
                values.put(COLUMN_SERIES, series);
                values.put(COLUMN_REPETICIONES, repeticiones);
                values.put(COLUMN_KILOS, (int) peso);
                return db.insert(TABLE_RUTINA_DETALLE, null, values) != -1;
        }

        private int getExerciseIdByName(String nombre) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT " + COLUMN_EJERCICIO_ID + " FROM " + TABLE_EJERCICIOS + " WHERE "
                                + COLUMN_EJERCICIO_NOMBRE + " = ?", new String[] { nombre });
                if (c.moveToFirst()) {
                        int id = c.getInt(0);
                        c.close();
                        return id;
                }
                c.close();
                return -1;
        }

        public boolean deleteTraining(long id) {
                SQLiteDatabase db = this.getWritableDatabase();
                return db.delete(TABLE_RUTINAS, COLUMN_RUTINA_ID + "=?", new String[] { String.valueOf(id) }) > 0;
        }

        public Cursor getUserTrainings(String correoUsuario) {
                int userId = getUserId(correoUsuario);
                SQLiteDatabase db = this.getReadableDatabase();
                return db.rawQuery(
                                "SELECT " + COLUMN_RUTINA_ID + " as id, " + COLUMN_RUTINA_FECHA + " as date, "
                                                + COLUMN_RUTINA_PUBLICA + " as es_publico FROM "
                                                + TABLE_RUTINAS + " WHERE " + COLUMN_RUTINA_USUARIO_ID
                                                + " = ? ORDER BY " + COLUMN_RUTINA_ID + " DESC",
                                new String[] { String.valueOf(userId) });
        }

        // BÚSQUEDA DE USUARIO POR NOMBRE
        public Cursor getUserByName(String nombre) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT " + COLUMN_USUARIO_ID + " as id, nombre, correo FROM " + TABLE_USUARIOS +
                                " WHERE nombre LIKE ?";
                return db.rawQuery(query, new String[] { "%" + nombre + "%" });
        }

        // Obtener entrenamientos PUBLICOS de otro usuario
        public Cursor getUserPublicTrainingsByUserId(int userId) {
                SQLiteDatabase db = this.getReadableDatabase();
                return db.rawQuery(
                                "SELECT " + COLUMN_RUTINA_ID + " as id, " + COLUMN_RUTINA_FECHA + " as date FROM "
                                                + TABLE_RUTINAS + " WHERE " + COLUMN_RUTINA_USUARIO_ID
                                                + " = ? AND " + COLUMN_RUTINA_PUBLICA + " = 1 ORDER BY "
                                                + COLUMN_RUTINA_ID + " DESC",
                                new String[] { String.valueOf(userId) });
        }

        public Cursor getUserTrainingsByUserId(int userId) {
                SQLiteDatabase db = this.getReadableDatabase();
                return db.rawQuery(
                                "SELECT " + COLUMN_RUTINA_ID + " as id, " + COLUMN_RUTINA_FECHA + " as date FROM "
                                                + TABLE_RUTINAS + " WHERE " + COLUMN_RUTINA_USUARIO_ID
                                                + " = ? ORDER BY " + COLUMN_RUTINA_ID + " DESC",
                                new String[] { String.valueOf(userId) });
        }

        public Cursor getTrainingDetails(long routineId) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT " +
                                "e." + COLUMN_EJERCICIO_NOMBRE + " as nombre_ejercicio, " +
                                "rd." + COLUMN_SERIES + " as series, " +
                                "rd." + COLUMN_REPETICIONES + " as repeticiones, " +
                                "rd." + COLUMN_KILOS + " as peso " +
                                "FROM " + TABLE_RUTINA_DETALLE + " rd " +
                                "JOIN " + TABLE_EJERCICIOS + " e ON rd." + COLUMN_DETALLE_EJERCICIO_ID + " = e."
                                + COLUMN_EJERCICIO_ID + " " +
                                "WHERE rd." + COLUMN_DETALLE_RUTINA_ID + " = ?";
                return db.rawQuery(query, new String[] { String.valueOf(routineId) });
        }

        // Toggle visibilidad del entrenamiento
        public boolean toggleEntrenamientoPublico(long rutinaId) {
                SQLiteDatabase db = this.getWritableDatabase();
                Cursor c = db.rawQuery("SELECT es_publico FROM " + TABLE_RUTINAS + " WHERE " + COLUMN_RUTINA_ID + " = ?",
                                new String[] { String.valueOf(rutinaId) });
                if (c.moveToFirst()) {
                        int actual = c.getInt(0);
                        c.close();
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_RUTINA_PUBLICA, actual == 1 ? 0 : 1);
                        return db.update(TABLE_RUTINAS, values, COLUMN_RUTINA_ID + " = ?",
                                        new String[] { String.valueOf(rutinaId) }) > 0;
                }
                c.close();
                return false;
        }

        public boolean isEntrenamientoPublico(long rutinaId) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT es_publico FROM " + TABLE_RUTINAS + " WHERE " + COLUMN_RUTINA_ID + " = ?",
                                new String[] { String.valueOf(rutinaId) });
                if (c.moveToFirst()) {
                        boolean publico = c.getInt(0) == 1;
                        c.close();
                        return publico;
                }
                c.close();
                return true;
        }
}
