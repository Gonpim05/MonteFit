package com.example.montefit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;

/**
 * ClienteApi - Conecta la app Android con la API PHP.
 */
public class ClienteApi {

    private static final String URL_BASE = "http://192.168.254.171/montefit_api/";
    private static final String TAG = "ClienteApi";

    private static ClienteApi instancia;
    private String ultimoError = "";

    private ClienteApi() {}

    public static synchronized ClienteApi obtenerInstancia() {
        if (instancia == null) instancia = new ClienteApi();
        return instancia;
    }

    public String getUltimoError() { return ultimoError; }

    // =============================================
    // MÉTODOS HTTP
    // =============================================

    /** Petición GET con parámetros en la URL */
    private String peticionGET(String endpoint) {
        try {
            URL url = new URL(URL_BASE + endpoint);
            android.util.Log.d(TAG, "GET: " + url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int codigo = con.getResponseCode();
            java.io.InputStream stream = (codigo >= 200 && codigo < 300)
                    ? con.getInputStream() : con.getErrorStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(stream));
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) respuesta.append(linea);
            lector.close();
            con.disconnect();
            String resp = respuesta.toString();
            android.util.Log.d(TAG, "Respuesta GET (" + codigo + "): " + resp);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            ultimoError = "Error conexión GET: " + e.getMessage();
            android.util.Log.e(TAG, ultimoError);
            return "{}";
        }
    }

    /** Petición POST con cuerpo JSON */
    private String peticionPOST(String endpoint, JSONObject cuerpo) {
        try {
            URL url = new URL(URL_BASE + endpoint);
            android.util.Log.d(TAG, "POST: " + url.toString() + " Body: " + cuerpo.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            OutputStream salida = con.getOutputStream();
            salida.write(cuerpo.toString().getBytes("UTF-8"));
            salida.flush();
            salida.close();

            int codigo = con.getResponseCode();
            java.io.InputStream stream = (codigo >= 200 && codigo < 300)
                    ? con.getInputStream() : con.getErrorStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(stream));
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) respuesta.append(linea);
            lector.close();
            con.disconnect();
            String resp = respuesta.toString();
            android.util.Log.d(TAG, "Respuesta POST (" + codigo + "): " + resp);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            ultimoError = "Error conexión POST: " + e.getMessage();
            android.util.Log.e(TAG, ultimoError);
            return "{}";
        }
    }

    // =============================================
    // USUARIOS (usuarios.php)
    // =============================================

    public JSONObject iniciarSesion(String correo, String contrasena) {
        try {
            String url = "usuarios.php?action=login"
                    + "&correo=" + URLEncoder.encode(correo, "UTF-8")
                    + "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8");
            String respStr = peticionGET(url);
            JSONObject json = new JSONObject(respStr);
            if (!json.has("ok")) {
                json.put("ok", false);
                json.put("error", "Respuesta del servidor inválida");
            }
            return json;
        } catch (Exception e) {
            JSONObject err = new JSONObject();
            try { err.put("ok", false); err.put("error", "Error: " + e.getMessage()); } catch (Exception ignored) {}
            return err;
        }
    }

    public boolean existeCorreo(String correo) {
        try {
            String url = "usuarios.php?action=check&correo=" + URLEncoder.encode(correo, "UTF-8");
            JSONObject resp = new JSONObject(peticionGET(url));
            return resp.optBoolean("existe", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Obtiene el usuario_id a partir del correo */
    public int obtenerUsuarioId(String correo) {
        try {
            String url = "usuarios.php?action=getId&correo=" + URLEncoder.encode(correo, "UTF-8");
            JSONObject resp = new JSONObject(peticionGET(url));
            return resp.optInt("usuario_id", -1);
        } catch (Exception e) {
            return -1;
        }
    }

    /** Obtiene el nombre del usuario a partir del correo */
    public String obtenerNombre(String correo) {
        try {
            String url = "usuarios.php?action=getName&correo=" + URLEncoder.encode(correo, "UTF-8");
            JSONObject resp = new JSONObject(peticionGET(url));
            return resp.optString("nombre", "");
        } catch (Exception e) {
            return "";
        }
    }

    /** Obtiene el perfil completo: nombre, correo, age, peso, sex */
    public JSONObject obtenerPerfil(String correo) {
        try {
            String url = "usuarios.php?action=getProfile&correo=" + URLEncoder.encode(correo, "UTF-8");
            return new JSONObject(peticionGET(url));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /** Busca usuarios por nombre (para social) */
    public JSONArray buscarUsuario(String nombre) {
        try {
            String url = "usuarios.php?action=buscar&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            return new JSONArray(peticionGET(url));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Registra un usuario nuevo. Devuelve JSONObject con {ok, error} */
    public JSONObject registrarUsuario(String nombre, String correo, String contrasena) {
        try {
            JSONObject body = new JSONObject();
            body.put("nombre", nombre);
            body.put("correo", correo);
            body.put("contrasena", contrasena);
            String respStr = peticionPOST("usuarios.php?action=register", body);
            JSONObject json = new JSONObject(respStr);
            if (!json.has("ok")) {
                json.put("ok", false);
                json.put("error", "El servidor no respondió correctamente");
            }
            return json;
        } catch (Exception e) {
            JSONObject err = new JSONObject();
            try { err.put("ok", false); err.put("error", "Error de red: " + e.getMessage()); } catch (Exception ignored) {}
            return err;
        }
    }

    /** Actualiza perfil (nombre, edad, peso, sexo) */
    public boolean actualizarPerfil(String correo, String nombre, int edad, double peso, String sexo) {
        try {
            JSONObject body = new JSONObject();
            body.put("correo", correo);
            body.put("nombre", nombre);
            body.put("edad", edad);
            body.put("peso", peso);
            body.put("sexo", sexo);
            JSONObject resp = new JSONObject(peticionPOST("usuarios.php?action=update", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Cambia la contraseña */
    public boolean cambiarContrasena(String correo, String nuevaContrasena) {
        try {
            JSONObject body = new JSONObject();
            body.put("correo", correo);
            body.put("contrasena", nuevaContrasena);
            JSONObject resp = new JSONObject(peticionPOST("usuarios.php?action=updatePass", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    // =============================================
    // EJERCICIOS (ejercicios.php)
    // =============================================

    /** Obtiene todos los ejercicios */
    public JSONArray obtenerTodosEjercicios() {
        try {
            return new JSONArray(peticionGET("ejercicios.php?action=getAll"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Obtiene ejercicios filtrados por grupo muscular */
    public JSONArray obtenerEjerciciosPorGrupo(String grupo) {
        try {
            String url = "ejercicios.php?action=getByGrupo&grupo=" + URLEncoder.encode(grupo, "UTF-8");
            return new JSONArray(peticionGET(url));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Crea un ejercicio nuevo */
    public boolean crearEjercicio(String nombre, String grupoMuscular) {
        try {
            JSONObject body = new JSONObject();
            body.put("nombre", nombre);
            body.put("grupo_muscular", grupoMuscular);
            JSONObject resp = new JSONObject(peticionPOST("ejercicios.php?action=add", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Edita un ejercicio */
    public boolean editarEjercicio(int id, String nombre, String grupoMuscular) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", id);
            body.put("nombre", nombre);
            body.put("grupo_muscular", grupoMuscular);
            JSONObject resp = new JSONObject(peticionPOST("ejercicios.php?action=update", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Elimina un ejercicio */
    public boolean eliminarEjercicio(int id) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", id);
            JSONObject resp = new JSONObject(peticionPOST("ejercicios.php?action=delete", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    // =============================================
    // COMIDAS (comidas.php)
    // =============================================

    /** Obtiene todas las comidas */
    public JSONArray obtenerComidas() {
        try {
            return new JSONArray(peticionGET("comidas.php?action=getAll"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Guarda una comida nueva (envía correo para que PHP busque el usuario_id) */
    public boolean guardarComida(String nombre, String correo, int calorias, double proteinas, double carbohidratos, double grasas) {
        try {
            JSONObject body = new JSONObject();
            body.put("nombre", nombre);
            body.put("correo", correo);
            body.put("calorias", calorias);
            body.put("proteinas", proteinas);
            body.put("carbohidratos", carbohidratos);
            body.put("grasas", grasas);
            JSONObject resp = new JSONObject(peticionPOST("comidas.php?action=add", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Edita una comida */
    public boolean editarComida(int id, String nombre, int calorias, double proteinas, double carbohidratos, double grasas) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", id);
            body.put("nombre", nombre);
            body.put("calorias", calorias);
            body.put("proteinas", proteinas);
            body.put("carbohidratos", carbohidratos);
            body.put("grasas", grasas);
            JSONObject resp = new JSONObject(peticionPOST("comidas.php?action=update", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Elimina una comida */
    public boolean eliminarComida(int id) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", id);
            JSONObject resp = new JSONObject(peticionPOST("comidas.php?action=delete", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    // =============================================
    // RUTINAS / ENTRENAMIENTOS (rutinas.php)
    // =============================================

    /** Obtiene los entrenamientos del usuario (por correo) */
    public JSONArray obtenerMisRutinas(String correo) {
        try {
            String url = "rutinas.php?action=getMias&correo=" + URLEncoder.encode(correo, "UTF-8");
            return new JSONArray(peticionGET(url));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Obtiene entrenamientos públicos de otro usuario */
    public JSONArray obtenerRutinasPublicas(int usuarioId) {
        try {
            return new JSONArray(peticionGET("rutinas.php?action=getPublicas&usuario_id=" + usuarioId));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Obtiene los detalles (ejercicios) de una rutina */
    public JSONArray obtenerDetallesRutina(long rutinaId) {
        try {
            return new JSONArray(peticionGET("rutinas.php?action=getDetalles&rutina_id=" + rutinaId));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /** Comprueba si una rutina es pública */
    public boolean esPublica(long rutinaId) {
        try {
            JSONObject resp = new JSONObject(peticionGET("rutinas.php?action=isPublico&rutina_id=" + rutinaId));
            return resp.optBoolean("es_publico", true);
        } catch (Exception e) {
            return true;
        }
    }

    /** Crea un entrenamiento nuevo. Devuelve el rutina_id creado (-1 si error) */
    public long crearRutina(String fecha, String correo, boolean esPublico) {
        try {
            JSONObject body = new JSONObject();
            body.put("fecha", fecha);
            body.put("correo", correo);
            body.put("es_publico", esPublico ? 1 : 0);
            JSONObject resp = new JSONObject(peticionPOST("rutinas.php?action=add", body));
            return resp.optLong("rutina_id", -1);
        } catch (Exception e) {
            return -1;
        }
    }

    /** Añade un ejercicio (detalle) a una rutina existente */
    public boolean agregarDetalleRutina(long rutinaId, String ejercicioNombre, int series, int repeticiones, double peso) {
        try {
            JSONObject body = new JSONObject();
            body.put("rutina_id", rutinaId);
            body.put("ejercicio_nombre", ejercicioNombre);
            body.put("series", series);
            body.put("repeticiones", repeticiones);
            body.put("peso", peso);
            JSONObject resp = new JSONObject(peticionPOST("rutinas.php?action=addDetalle", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Elimina una rutina */
    public boolean eliminarRutina(long id) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", id);
            JSONObject resp = new JSONObject(peticionPOST("rutinas.php?action=delete", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    /** Alterna público/privado de una rutina */
    public boolean togglePublico(long rutinaId) {
        try {
            JSONObject body = new JSONObject();
            body.put("rutina_id", rutinaId);
            JSONObject resp = new JSONObject(peticionPOST("rutinas.php?action=toggle", body));
            return resp.optBoolean("ok", false);
        } catch (Exception e) {
            return false;
        }
    }

    // =============================================
    // RANKINGS (rankings.php)
    // =============================================

    /** Obtiene el ranking semanal de un ejercicio */
    public JSONArray obtenerRankings(int ejercicioId) {
        try {
            return new JSONArray(peticionGET("rankings.php?action=getRanking&ejercicio_id=" + ejercicioId));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    // =============================================
    // LOGROS (logros.php)
    // =============================================

    /** Obtiene todos los logros y cuáles ha conseguido el usuario */
    public JSONArray obtenerLogros(int usuarioId) {
        try {
            return new JSONArray(peticionGET("logros.php?action=getLogros&usuario_id=" + usuarioId));
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
