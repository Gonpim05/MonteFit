package com.example.montefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestorBaseDatos extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "MonfitDB";
        private static final int DATABASE_VERSION = 9;

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
                // 1. Usuarios
                db.execSQL("CREATE TABLE Usuarios (" +
                                "usuario_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nombre TEXT NOT NULL, " +
                                "correo TEXT UNIQUE NOT NULL, " +
                                "contrasena TEXT NOT NULL, " +
                                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP)");

                // 2. Ejercicios
                db.execSQL("CREATE TABLE Ejercicios (" +
                                "ejercicio_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nombre TEXT NOT NULL, " +
                                "grupo_muscular TEXT, " +
                                "descripcion TEXT, " +
                                "video_url TEXT, " +
                                "dificultad TEXT DEFAULT 'Medio')");

                // 3. Rutinas
                db.execSQL("CREATE TABLE Rutinas (" +
                                "rutina_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "usuario_id INTEGER NOT NULL, " +
                                "nombre TEXT NOT NULL, " +
                                "fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP, " +
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
                // ejercicios principales para rankings
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Press Banca', 'Pecho', 'Medio')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Sentadilla', 'Pierna', 'Dificil')");
                db.execSQL("INSERT INTO Ejercicios (nombre, grupo_muscular, dificultad) VALUES ('Peso Muerto', 'Espalda', 'Dificil')");

                // Logros
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Primer Paso', 'Completa tu primer entrenamiento')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Constancia', 'Entrena 3 veces en una semana')");
                db.execSQL("INSERT INTO Logros (titulo, descripcion) VALUES ('Bestia', 'Levanta más de 100kg')");

                // Usuarios de prueba
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, fecha_registro) VALUES " +
                                "('Mounir', 'mounir@test.com', '1234', datetime('now'))");
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, fecha_registro) VALUES " +
                                "('Carlos', 'carlos@test.com', '1234', datetime('now'))");
                db.execSQL("INSERT INTO Usuarios (nombre, correo, contrasena, fecha_registro) VALUES " +
                                "('Ana', 'ana@test.com', '1234', datetime('now'))");

                // Obtener semana y año actual para rankings
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int semanaActual = cal.get(java.util.Calendar.WEEK_OF_YEAR);
                int anioActual = cal.get(java.util.Calendar.YEAR);

                // Rutinas de ejemplo para Mounir
                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion) VALUES " +
                                "(1, 'Entrenamiento 1', datetime('now', '-1 day'))");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(1, 1, 4, 10, 80.0)"); // Press Banca
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(1, 2, 3, 12, 100.0)"); // Sentadilla

                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion) VALUES " +
                                "(1, 'Entrenamiento 2', datetime('now', '-3 day'))");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(2, 3, 5, 5, 120.0)"); // Peso Muerto
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(2, 1, 3, 8, 85.0)"); // Press Banca

                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion) VALUES " +
                                "(1, 'Entrenamiento 3', datetime('now', '-6 day'))");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(3, 2, 4, 10, 110.0)"); // Sentadilla

                // Rutinas para Carlos (usuario_id = 2)
                db.execSQL("INSERT INTO Rutinas (usuario_id, nombre, fecha_creacion) VALUES " +
                                "(2, 'Entrenamiento Carlos', datetime('now', '-2 day'))");
                db.execSQL("INSERT INTO Rutina_Detalle (rutina_id, ejercicio_id, series, repeticiones, kilos) VALUES " +
                                "(4, 1, 4, 8, 90.0)"); // Press Banca

                // Rankings semanales actuales
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(1, 1, 85.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Press Banca
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(1, 2, 90.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Press Banca
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(1, 3, 70.0, " + semanaActual + ", " + anioActual + ")"); // Ana Press Banca

                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(2, 1, 110.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Sentadilla
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(2, 2, 95.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Sentadilla
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(2, 3, 85.0, " + semanaActual + ", " + anioActual + ")"); // Ana Sentadilla

                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(3, 1, 120.0, " + semanaActual + ", " + anioActual + ")"); // Mounir Peso Muerto
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(3, 2, 130.0, " + semanaActual + ", " + anioActual + ")"); // Carlos Peso Muerto
                db.execSQL("INSERT INTO Rankings_Mensuales (ejercicio_id, usuario_id, peso_maximo, semana, anio) VALUES "
                                +
                                "(3, 3, 100.0, " + semanaActual + ", " + anioActual + ")"); // Ana Peso Muerto

                // Comidas de ejemplo para Mounir
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES "
                                +
                                "('Pollo con arroz', 1, 450, 50.0, 35.0, 8.0)");
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES "
                                +
                                "('Ensalada de atún', 1, 320, 15.0, 28.0, 18.0)");
                db.execSQL("INSERT INTO Comidas (nombre, usuario_id, calorias, carbohidratos, proteinas, grasas) VALUES "
                                +
                                "('Batido proteico', 1, 180, 10.0, 25.0, 3.0)");

                // Logros para Mounir
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

        // Usuarios
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

        // PERFIL DE USUARIO
        public Cursor getUserProfile(String correo) {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT nombre as nombre, correo, 0 as age, 0.0 as peso, '' as sex FROM Usuarios WHERE correo = ?";
                return db.rawQuery(query, new String[] { correo });
        }

        public boolean updateUserProfile(String correo, String nombre, int age, double peso, String sex) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("nombre", nombre);
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

        // Logros
        public Cursor getLogros(int userId) {
                SQLiteDatabase db = this.getReadableDatabase();
                // Devolver columnas con nombres consistentes para el miAdaptador
                String query = "SELECT l." + COLUMN_LOGRO_ID + ", " +
                                "l." + COLUMN_LOGRO_TITULO + ", " +
                                "l." + COLUMN_LOGRO_DESCRIPCION + ", " +
                                "(SELECT COUNT(*) FROM " + TABLE_USUARIOS_LOGROS + " ul " +
                                "WHERE ul." + COLUMN_LOGRO_ID + " = l." + COLUMN_LOGRO_ID + " " +
                                "AND ul." + COLUMN_USUARIO_ID + " = ?) as obtenido " +
                                "FROM " + TABLE_LOGROS + " l";
                return db.rawQuery(query, new String[] { String.valueOf(userId) });
        }

        // Social / Rankings
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
                // SimpleCursorAdapter requires a column named '_id'
                String query = "SELECT " + COLUMN_EJERCICIO_ID + " as _id, " +
                                COLUMN_EJERCICIO_NOMBRE + " as nombre, " +
                                COLUMN_GRUPO_MUSCULAR + " " +
                                "FROM " + TABLE_EJERCICIOS;
                return this.getReadableDatabase().rawQuery(query, null);
        }

        // COMIDAS
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
                // Obtener usuario actual de GestorUsuarios
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

        // EJERCICIOS
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

        // RUTINAS / ENTRENAMIENTOS
        public long addTraining(String date, String correoUsuario) {
                int userId = getUserId(correoUsuario);
                if (userId == -1)
                        return -1;

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_RUTINA_USUARIO_ID, userId);
                values.put(COLUMN_RUTINA_NOMBRE, "Entrenamiento " + date); // Default nombre
                values.put(COLUMN_RUTINA_FECHA, date); // Note: Schema expects DATETIME, generally ISO.
                return db.insert(TABLE_RUTINAS, null, values);
        }

        public boolean addTrainingDetail(long routineId, String exerciseName, int series, int repeticiones,
                        double peso) {

                int exerciseId = getExerciseIdByName(exerciseName);
                if (exerciseId == -1)
                        return false; // Fail if not found

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
                                "SELECT " + COLUMN_RUTINA_ID + " as id, " + COLUMN_RUTINA_FECHA + " as date FROM "
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
}
