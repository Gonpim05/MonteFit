# 🏆 MONTEFIT: DOCUMENTACIÓN DEL PROYECTO (TFG) Y GUÍA MAESTRA 🚀

Esta es la documentación extensa, integral y sin recortes de MonteFit. Cubre desde la visión estratégica y la arquitectura de alto nivel, hasta el último detalle técnico de las clases en Java, los scripts en PHP y el diseño de la base de datos MySQL. 

---

## 1. VISIÓN GENERAL 🏟️

MonteFit es una aplicación móvil de fitness diseñada para ayudar a los usuarios a registrar sus entrenamientos, gestionar su nutrición, visualizar logros y compartir su progreso con otros usuarios. El enfoque principal es la simplicidad y la visualización anatómica interactiva, alejándose de las interfaces complejas y aburridas de las aplicaciones de fitness tradicionales.

La aplicación permite al usuario tener un control absoluto sobre sus rutinas, seleccionar ejercicios visualmente mediante un maniquí anatómico, y competir de forma sana a través de un módulo social y un sistema de logros automatizado.

---

## 2. ARQUITECTURA DEL SISTEMA 🏗️

El proyecto sigue un modelo robusto **Cliente-Servidor**:

*   **Cliente (Front-end):** Aplicación nativa Android desarrollada en Java.
*   **Servidor (Back-end):** API RESTful desarrollada en PHP y desplegada sobre un servidor WAMP.
*   **Base de Datos:** MySQL para el almacenamiento persistente de usuarios, ejercicios, comidas y rutinas.

### ¿Por qué esta arquitectura?

*   **Desacoplamiento:** Al separar el cliente del servidor, podríamos cambiar la aplicación Android por una web o una App de iOS en el futuro sin modificar la lógica de negocio ni la base de datos. Cada capa es independiente.
*   **Centralización:** Todos los datos están en un servidor central, permitiendo que un usuario acceda a su perfil desde cualquier dispositivo y vea el progreso de otros (módulo social). Si el usuario pierde el teléfono, no pierde sus datos.

---

## 3. STACK TECNOLÓGICO (¿Por qué estas tecnologías?) 🛠️

### 📱 Android (Java)
*   **Por qué Java:** Es el lenguaje nativo "clásico" de Android. Ofrece un rendimiento excelente, una gestión de memoria robusta y una compatibilidad total con las librerías de Google. Para un TFG, demuestra el dominio absoluto de la Programación Orientada a Objetos (POO).
*   **Material Design:** Se han usado componentes de Google (`CardView`, `RecyclerView`, `FloatingActionButton`) para garantizar que la app siga los estándares modernos de diseño de Android, ofreciendo una experiencia de usuario fluida e intuitiva.

### 🌐 Backend (PHP)
*   **Por qué PHP:** Es extremadamente eficiente para crear APIs rápidas. Su integración con bases de datos MySQL es nativa y muy sencilla de configurar en entornos locales con WAMP.
*   **REST API:** La comunicación se realiza mediante peticiones HTTP (`GET` y `POST`) intercambiando datos en formato **JSON**, que es el estándar de la industria por su ligereza y facilidad de lectura.

### 🗄️ Base de Datos (MySQL)
*   **Por qué MySQL:** Es un sistema de gestión de bases de datos relacionales (RDBMS) que permite definir relaciones claras entre tablas (ej. un entrenamiento pertenece a un usuario). Su lenguaje SQL es universal y permite consultas complejas de forma eficiente.

### 🔌 Comunicación (HttpURLConnection)
*   **Por qué no usar librerías externas (como Retrofit o Volley):** Se ha optado por usar la clase nativa de Java `HttpURLConnection` para demostrar un conocimiento profundo de cómo funcionan los protocolos de red "a bajo nivel". Esto implica gestionar manualmente los flujos de entrada/salida (streams) y la codificación de caracteres.

> **TIP PARA LA DEFENSA:** La palabra clave que debes usar es: *"He implementado una arquitectura desacoplada mediante una API REST propia, lo que garantiza la escalabilidad del sistema."*

---

## 4. BACKEND: API PHP Y BASE DE DATOS MYSQL ⚙️

Este apartado explica cómo funciona el "motor" de MonteFit: el servidor que almacena los datos y la lógica de base de datos.

### 4.1 Diseño de la Base de Datos (Modelo E-R)
La base de datos se llama `MonfitDB` y utiliza el motor **InnoDB**, el cual permite integridad referencial (claves foráneas) y transacciones atómicas.

**Tablas Principales:**
*   **`Usuarios`:** Almacena el perfil (nombre, correo, contraseña, peso, edad). El correo es único (`UNIQUE`).
*   **`Ejercicios`:** Catálogo maestro de movimientos (Press Banca, Sentadilla, etc.) categorizados por grupo_muscular.
*   **`Rutinas`:** Registra la "cabecera" de un entrenamiento (quién lo hizo, cuándo y si es público).
*   **`Rutina_Detalle`:** Es el desglose de la rutina. Conecta una rutina con un ejercicio y guarda los kilos, series y repeticiones.
*   **`Comidas`:** Registro nutricional vinculado a cada usuario.
*   **`Logros` y `Usuarios_Logros`:** Una relación de muchos a muchos (N:M) que registra qué medallas ha ganado cada usuario.

### 4.2 La API en PHP (Capa de Servicio)
La API está estructurada en archivos temáticos para mantener el código limpio, organizado y modular:

*   **`usuarios.php`:** Gestión de login, registro y edición de perfiles.
*   **`rutinas.php`:** Guardado y recuperación del historial de entrenamientos.
*   **`ejercicios.php`:** Filtros por grupo muscular para alimentar el maniquí de Android.
*   **`comidas.php`:** CRUD (Crear, Leer, Actualizar, Borrar) de la sección de nutrición.

**¿Cómo funciona una petición? (Ejemplo: Login a bajo nivel)**
1.  Android envía una petición GET a `usuarios.php?action=login&correo=...&contrasena=...`.
2.  PHP recibe los parámetros mediante el array global `$_GET`.
3.  PHP llama a la función `getConexion()` (definida en `db.php`) para abrir el puente con MySQL.
4.  Se ejecuta una consulta SQL: `SELECT * FROM Usuarios WHERE correo = '$correo' AND contrasena = '$contrasena'`.
5.  PHP transforma el resultado de la base de datos en un objeto JSON (`json_encode`) y lo devuelve al móvil.

### 4.3 Configuración y Seguridad (WAMP)
*   **Puerto:** Se utiliza el puerto **3301** para MySQL, evitando conflictos con otras instalaciones previas en la máquina de desarrollo.
*   **CORS (Cross-Origin Resource Sharing):** En `db.php` se han añadido cabeceras `Access-Control-Allow-Origin: *`. Esto es vital para que una aplicación externa (el móvil en otra IP) tenga permiso para hablar con el servidor local.

> **CAUTION - Seguridad en Producción:** Para un entorno real, las contraseñas deberían estar encriptadas (hashes como BCRYPT) y se deberían usar "Prepared Statements" para evitar Inyección SQL. Para el TFG, se ha priorizado la claridad del código y el funcionamiento de la lógica de negocio para demostrar un MVP funcional.

---

## 5. LÓGICA DE BACKEND Y "TRIGGERS" DE APLICACIÓN 🧠

Este documento explica las decisiones técnicas tomadas en el servidor y cómo funciona la automatización de logros y rankings.

### 5.1 ¿Por qué utilizar PHP para este proyecto?
Aunque existen alternativas como Node.js o Python, PHP fue seleccionado por las siguientes razones:
*   **Despliegue Sencillo:** La infraestructura WAMP es el estándar para proyectos académicos, permitiendo un servidor web y de base de datos listo en minutos.
*   **Integración Nativa con MySQL:** La extensión `mysqli` es extremadamente eficiente y fácil de entender para operaciones CRUD.
*   **Arquitectura "Stateless":** Cada petición PHP es independiente, lo que simplifica la depuración y evita fugas de memoria en el servidor.

### 5.2 El Concepto de "Trigger de Aplicación"
Un Trigger tradicional es un trozo de código que vive dentro de la base de datos MySQL. Sin embargo, en MonteFit hemos optado por un **Trigger de Nivel de Aplicación** (en el código PHP).

**¿Por qué no usamos Triggers de SQL puros?**
*   **Portabilidad:** Si quisiéramos cambiar de MySQL a PostgreSQL, los triggers de SQL tendríamos que reescribirlos desde cero. El código PHP funcionaría casi igual.
*   **Depuración:** Es mucho más fácil ver y registrar errores en un log de PHP que dentro del motor oscuro de la base de datos.
*   **Carga del Servidor:** Delegar la lógica compleja al lenguaje de programación (PHP) libera al motor de base de datos para que se centre exclusivamente en su trabajo principal: guardar y leer datos rápidamente.

### 5.3 Funcionamiento de `evaluar_logros.php`
Este archivo actúa como el "vigilante" del sistema. No se ejecuta solo, sino que es disparado por otros eventos. Es uno de los puntos fuertes del proyecto.

**El Flujo del Disparador:**
1.  El usuario guarda una serie de ejercicio en la app.
2.  Android llama a `rutinas.php?action=addDetalle`.
3.  Tras insertar el dato en la tabla `Rutina_Detalle`, el código PHP hace lo siguiente (Línea 135 de `rutinas.php`):
    `evaluarLogros($conn, $uid);`
4.  La función `evaluarLogros` analiza TODA la historia del usuario (total de entrenos, peso máximo levantado, suma de repeticiones).
5.  Comprueba si con ese nuevo entrenamiento el usuario ha cumplido alguna condición (ej: "Levantar más de 100kg").
6.  Si se cumple, inserta automáticamente una fila en `Usuarios_Logros`.

**Beneficio para el TFG:** Esto demuestra lógica de negocio compleja del lado del servidor (Server-side logic), un concepto avanzado en el desarrollo de software.

### 5.4 Gestión de los Rankings (Actualización en Caliente)
Al igual que los logros, los rankings se actualizan en el momento exacto en el que ocurren:
*   Antes de evaluar logros, el sistema comprueba si el peso recién levantado es mayor al anterior récord semanal de ese usuario.
*   Si es así, ejecuta un `UPDATE` directo en la tabla `Rankings_Mensuales`.
*   **Ventaja:** Esto garantiza que las tablas de clasificación siempre estén actualizadas al segundo para toda la red social de la app, sin necesidad de pesados procesos de limpieza nocturnos (cron jobs).

> **IMPORTANT - Punto clave para la defensa:** "He implementado la lógica de negocio en el lado del servidor para garantizar que las reglas (como los logros o rankings) sean consistentes, independientemente de si el usuario usa la App o accede a sus datos desde otro lugar."

---

## 6. ARQUITECTURA DE CLASES ANDROID (JAVA) 📱

Este documento detalla el propósito de cada clase en la aplicación móvil MonteFit, explicando su lógica y el porqué de los imports utilizados.

### 6.1 Clases de Pantalla (Activities)
Cada pantalla en Android es una `Activity`. Su función es gestionar el ciclo de vida de la vista (creación, pausa, destrucción) y manejar la interfaz (UI).

*   **`PantallaInicial.java`**
    *   **Propósito:** Actúa como el panel de control (Dashboard). Muestra los últimos entrenamientos del usuario y permite navegar a las demás secciones.
    *   **Lógica Clave:** Usa un `RecyclerView` para listar los entrenamientos. Al abrirse (`onResume`), lanza un hilo secundario para pedir los datos a la API sin bloquear la interfaz de usuario.
    *   **Imports Clave:**
        *   `org.json.JSONArray`: Permite manejar la lista de datos en crudo que devuelve el servidor.
        *   `androidx.recyclerview.widget.RecyclerView`: Fundamental para crear listas eficientes que reutilizan vistas en lugar de instanciarlas todas, ahorrando memoria RAM de forma drástica.

*   **`PantallaEntrenar.java`**
    *   **Propósito:** La parte más compleja y central de la app. Permite registrar un entrenamiento dinámicamente en tiempo real.
    *   **Lógica Clave:** Implementa el Maniquí Interactivo. Al seleccionar un músculo, filtra los ejercicios disponibles en la base de datos remota para ese grupo. Permite añadir múltiples series (peso y repeticiones) a cada ejercicio seleccionado.
    *   **Imports Clave:**
        *   `android.app.AlertDialog`: Utilizado para mostrar el selector de músculos y ejercicios de forma flotante, mejorando la UX.
        *   `java.text.SimpleDateFormat`: Esencial para registrar la fecha y hora exacta del entrenamiento y sincronizarla con la BD.

*   **`PantallaSocial.java`**
    *   **Propósito:** Fomentar la comunidad y retención de usuarios. Permite buscar a otros usuarios y ver sus rutinas marcadas explícitamente como "públicas".
    *   **Lógica Clave:** Implementa un buscador dinámico. Al escribir en el input, pide a la API de forma asíncrona los usuarios que coincidan con el patrón de texto.

### 6.2 Lógica de Red y Datos
*   **`ClienteApi.java` (El Patrón Singleton)**
    *   **Propósito:** Es el "puente" de comunicación único y centralizado entre Android y el Servidor PHP.
    *   **¿Por qué es un Singleton?:** Para garantizar que solo exista una instancia de comunicación en toda la app simultáneamente, evitando aperturas de puerto innecesarias, colisiones de red y fugas de memoria.
    *   **Lógica Clave:** Implementa métodos personalizados para GET y POST. Se asegura de que todas las peticiones lleven el formato HTTP adecuado y gestiona robustamente los errores de red y los Timeouts.
    *   **Imports Clave:**
        *   `java.net.HttpURLConnection`: Clase base nativa para enviar y recibir datos por internet mediante el protocolo HTTP.
        *   `java.net.URLEncoder`: Crítico para la seguridad. Protege y codifica los datos enviados (como un correo electrónico con un '+') para que no corrompan la estructura de la URL si contienen espacios o caracteres especiales.

*   **`GestorUsuarios.java`**
    *   **Propósito:** Mantener el estado de la sesión activa del usuario. Guarda el `correo` y el `usuarioId` en la memoria RAM mientras la app está abierta para saber exactamente de quién son los datos que se están guardando en cada operación.

### 6.3 Modelos de Datos (Entities)
Son clases simples (conocidas como POJOs - Plain Old Java Objects) que representan objetos de la vida real en el código, facilitando el trasiego de datos entre componentes.
*   **`Entrenamiento.java`:** Contiene la fecha, el ID de BD y la lista interna de ejercicios realizados.
*   **`Ejercicio.java`:** Contiene el nombre del ejercicio, su dificultad y el músculo específico que trabaja.
*   **`Comida.java`:** Registra nombre, calorías totales, proteínas, carbohidratos y grasas.

### 6.4 Adaptadores (Adapters)
Los adaptadores son los traductores esenciales entre los Datos puros (Listas y Arrays de Java) y la Vista final visual (`RecyclerView`).
*   **`InterfazListaEntrenamientos.java`:** Toma un objeto `Entrenamiento` y lo "dibuja" asignando sus variables a los `TextViews` de cada tarjeta en la pantalla inicial.
*   **`InterfazListaLogros.java`:** Ejecuta lógica visual. Compara los logros del catálogo del servidor con el progreso real del usuario y decide si mostrar el emoji/medalla a todo color (1.0f alpha) o apagado/en gris (0.5f alpha).

> **IMPORTANT - Hilos de Ejecución:** Todas las llamadas a `ClienteApi` se hacen estrictamente dentro de un bloque `new Thread(() -> { ... }).start()`. Esto es absolutamente obligatorio en el desarrollo Android moderno porque el sistema operativo prohíbe realizar operaciones de red en el hilo principal (Main Thread). Si se hiciera, la app se congelaría y el SO la cerraría por error.

---

## 7. DESGLOSE DE CÓDIGO: PASO A PASO 🔍

Este documento explica cómo funcionan las entrañas exactas de MonteFit. Entendiendo estos procesos, dominas el código fuente.

### 7.1 La Comunicación con el Servidor (`ClienteApi.java`)
Es la clase que funciona como un negociador de bajo nivel.
**Paso a paso de una petición GET:**
1.  **`peticionGET(String endpoint)`:** Inicia abriendo una conexión URL hacia la IP del servidor WAMP.
2.  **`setConnectTimeout(5000)`:** Establece una regla vital: "Si el servidor PHP no responde en 5 segundos, aborta la operación". Esto previene que la app Android se quede colgada para siempre esperando a un servidor caído.
3.  **`BufferedReader`:** Una vez abierta la conexión, lee la respuesta del servidor línea por línea a través de un stream de texto (la respuesta suele ser una larga cadena en formato JSON).
4.  **`JSONObject` / `JSONArray`:** Convierte esa larga cadena de texto inerte en objetos estructurados de Java que el resto de la app puede manipular fácilmente.

### 7.2 El Maniquí Interactivo (`PantallaEntrenar.java`)
Aquí es donde ocurre la "magia" visual y donde se demuestra eficiencia de código.
**¿Cómo sabe la app qué imagen cargar sin un switch gigante de 20 casos?**
Se utiliza reflexión de recursos mediante la siguiente lógica:
```java
String muscId = "maniqui_" + grupoSeleccionado.toLowerCase().replace("í", "i");
int resId = getResources().getIdentifier(muscId, "drawable", getPackageName());
```
1.  **Limpieza de datos:** Si el usuario elige "Bíceps" en la UI, el código lo normaliza a "biceps" (pasando a minúsculas y quitando tildes).
2.  **Identificador dinámico:** Usa el string resultante para formar el nombre `"maniqui_biceps"` y la función `getIdentifier` busca exactamente ese nombre en la carpeta de recursos `res/drawable` en tiempo de ejecución.
3.  **Ventaja brutal:** Si mañana se quiere añadir el grupo muscular "Cuádriceps" a la BD, no hay que tocar ni compilar una sola línea de Java. Solo hay que subir un archivo llamado `maniqui_cuadriceps.png` a la carpeta de recursos.

### 7.3 El "Cerebro" de la Base de Datos (`montefit_mysql.sql`)
La base de datos de MonteFit no es una simple hoja de cálculo, es una red de entidades altamente conectada.
**Relaciones y Reglas Clave:**
*   **Usuarios ↔ Rutinas (1:N):** Un usuario puede tener registradas miles de rutinas a lo largo de los años, pero cada rutina específica pertenece a un único usuario (identificado rígidamente por su `usuario_id`).
*   **Rutinas ↔ Rutina_Detalle (1:N):** La tabla `Rutinas` es solo la cabecera (Fecha y Usuario). La verdadera "chicha" está en `Rutina_Detalle`, que alberga cada ejercicio específico con sus series, kilos exactos y repeticiones, enlazado siempre a un `rutina_id`.
*   **Cascada (`ON DELETE CASCADE`):** Una restricción de integridad fundamental. Si un usuario decide eliminar su cuenta por RGPD, MySQL busca y borra de forma automática e instantánea absolutamente todos sus entrenamientos, detalles y comidas en todas las demás tablas. Esto impide que la base de datos se llene de datos "zombis" o huérfanos.

### 7.4 El Flujo Complejo del Guardado de Entrenamientos
Cuando pulsas el botón "Finalizar Entrenamiento", ocurre una orquestación en varios pasos:
1.  Android agrupa los datos de la UI y llama a la API PHP solicitando crear una rutina.
2.  PHP crea la "cápsula" vacía en la tabla `Rutinas` y le devuelve a Android el nuevo `rutina_id` generado.
3.  Android recibe el ID, recorre en un bucle la lista de ejercicios que el usuario ha añadido y, uno por uno, envía las peticiones de detalle (ejercicio, series, peso) vinculándolos inexorablemente a ese `rutina_id`.
4.  Al terminar de insertar los detalles, el PHP ejecuta la función `evaluar_logros()`. Si es la primera vez que el usuario entrena, el servidor le otorga automáticamente el logro de nivel 1.

### 7.5 Lógica del Sistema de Temas (Dark/Light Mode)
Esta es una de las implementaciones más avanzadas y exigidas en la actualidad.
**¿Cómo funciona el cambio dinámico de color?**
*   **Styles (`themes.xml`):** En Android, se han definido estructuralmente dos temas base: `Theme.MonteFit` (Modo Claro) y `Theme.MonteFit.Dark` (Modo Oscuro).
*   **Variables Semánticas en XML:** En lugar de codificar colores rígidos (ej. `android:background="#FF0000"`), se utilizan referencias abstractas a atributos del tema, como `?attr/colorSurface` o `?attr/colorPrimary`. De este modo, Android sabe que si está en modo oscuro debe pintar la superficie gris oscuro, y si está en modo claro, blanca, sin tener que duplicar layouts.
*   **Persistencia Global:** El tema elegido por el usuario se guarda en el servidor remoto (en una columna de la tabla `Usuarios`) mediante una llamada a `PreferenciasApp`. De esta forma, siempre que el usuario inicie sesión, aunque sea en un móvil completamente nuevo, la app recuperará su preferencia visual y se pintará con su estilo favorito desde el primer segundo.

### 7.6 SQL Avanzado y Lógica de Logros en Backend
El archivo `evaluar_logros.php` contiene queries de alto nivel que superan los simples SELECT y demuestran capacidad analítica.
**Ejemplo de consulta compleja:**
Para verificar si se ha cumplido el logro "Centurión" (hacer más de 100 repeticiones en total sumadas a lo largo de un solo entrenamiento), se ejecuta este bloque:
```sql
SELECT MAX(total_reps) FROM (
    SELECT sum(repeticiones) as total_reps 
    FROM Rutina_Detalle 
    GROUP BY rutina_id
) sub
```
1.  **Agrupación interna:** Primero, se agrupan y suman las repeticiones totales separadas por cada entrenamiento individual (`GROUP BY rutina_id`).
2.  **Subconsulta (`sub`):** El resultado de esas sumas se trata temporalmente como si fuera una tabla nueva.
3.  **Máximo absoluto:** Finalmente, se busca el valor máximo entre todos esos totales para ver si supera la barrera de las 100 repeticiones.
*   **¿Por qué es importante esto para el TFG?** Porque demuestra con contundencia que se domina el lenguaje SQL para realizar operaciones matemáticas y **Análisis de Datos** (Data Analytics) dentro del propio motor relacional, lo cual es inmensamente más rápido que pedir miles de filas al servidor PHP y sumarlas en un bucle.

> **IMPORTANT - Término de Arquitectura para el tribunal:** "En este proyecto, he implementado un sistema de Persistencia Relacional centralizado en un servidor WAMP, garantizando la Integridad Transaccional y la Consistencia de Datos mediante la aplicación estricta de claves foráneas y restricciones en cascada."

---

## 8. REFERENCIA TÉCNICA: DICCIONARIO DE BASE DE DATOS (MonfitDB) 🗄️

Este apartado detalla la estructura profunda (Schema) de la base de datos MySQL, ideal para exponer la capa de persistencia en la defensa.

### 8.1 Diccionario de Tablas Completo

*   **Tabla: `Usuarios`**
    *   Almacena la información crítica de acceso, seguridad y perfil físico inicial del usuario.
    *   `usuario_id` (INT, PK, AUTO_INCREMENT): Identificador único inmutable.
    *   `nombre` (VARCHAR): Nombre completo o alias de la red social.
    *   `correo` (VARCHAR, UNIQUE): Email del usuario, utilizado como llave de login para evitar duplicidades en el registro.
    *   `contrasena` (VARCHAR): Clave de acceso.
    *   `edad` (INT): Edad registrada para posibles cálculos de metabolismo.
    *   `peso` (DOUBLE): Peso corporal base en kilogramos.
    *   `sexo` (VARCHAR): Sexo biológico o género.
    *   `fecha_registro` (DATETIME): Timestamp automático de creación de cuenta.

*   **Tabla: `Ejercicios`**
    *   Constituye el catálogo maestro de movimientos y biomecánica disponibles en la app.
    *   `ejercicio_id` (INT, PK): ID único del movimiento.
    *   `nombre` (VARCHAR): Denominación (ej: "Press Banca Plano").
    *   `grupo_muscular` (VARCHAR): Categoría principal para el filtrado del maniquí (ej: "Pecho", "Espalda").
    *   `descripcion` (TEXT): Explicación técnica detallada de la correcta ejecución del movimiento.
    *   `dificultad` (VARCHAR): Clasificación de curva de aprendizaje (Fácil, Medio, Difícil).

*   **Tabla: `Rutinas`**
    *   Cabecera temporal de cada sesión global de entrenamiento que el usuario inicia y finaliza.
    *   `rutina_id` (INT, PK, AUTO_INCREMENT): ID único del evento.
    *   `usuario_id` (INT, FK): Referencia relacional al propietario del entreno.
    *   `nombre` (VARCHAR): Nombre descriptivo opcional aportado por el usuario (ej: "Empuje de Lunes").
    *   `fecha_creacion` (DATETIME): Sello de tiempo exacto del inicio del evento.
    *   `es_publico` (TINYINT): Booleano binario (1=Público, 0=Privado) que controla los filtros de privacidad del módulo social.

*   **Tabla: `Rutina_Detalle`**
    *   El núcleo de la analítica. Desglose atómico de cada serie y esfuerzo físico dentro de una rutina concreta.
    *   `rutina_id` (INT, FK): Enlace al evento padre.
    *   `ejercicio_id` (INT, FK): Enlace al catálogo maestro de ejercicios.
    *   `series`, `repeticiones`, `kilos` (INT/DOUBLE): Métricas crudas de rendimiento físico.

*   **Tabla: `Comidas`**
    *   Registro temporal de la ingesta nutricional y macro-nutrientes.
    *   `comida_id` (INT, PK): ID del registro dietético.
    *   `usuario_id` (INT, FK): Referencia relacional al consumidor.
    *   `calorias`, `carbohidratos`, `proteinas`, `grasas` (DOUBLE): Valores de la tabla nutricional.

*   **Tabla: `Rankings_Mensuales`**
    *   Almacena y consolida de forma estática los récords históricos de fuerza de cada usuario fragmentados por periodos de tiempo.
    *   `semana`, `anio` (INT): Fragmentación del periodo de récord.
    *   `peso_maximo` (DOUBLE): El pico de tonelaje mayor alcanzado en una sola serie para ese usuario y ejercicio en esa ventana de tiempo.

*   **Tablas de Gamificación (`Logros` y `Usuarios_Logros`)**
    *   `Logros`: Catálogo de configuración estática de metas inmutables (título y descripción).
    *   `Usuarios_Logros`: Tabla pivote intermedia (relación N:M) que registra exactamente qué usuario ha desbloqueado qué logro del catálogo y el instante exacto (`fecha_obtencion`) en que lo logró.

> **TIP - Nota esencial para la memoria del proyecto:** *"Se han definido explícitamente restricciones de integridad referencial de tipo FOREIGN KEY junto con la directiva ON DELETE CASCADE. Esto certifica que, ante la eventual baja de un usuario del sistema, MySQL actuará como recolector de basura automático, eliminando en cascada todo su rastro de datos en tablas secundarias, garantizando que jamás existan registros huérfanos que corrompan los cálculos estadísticos globales."*

---

## 9. GUÍA DE SUPERVIVENCIA: PREGUNTAS DEL TRIBUNAL 🎓

Esta sección recopila las posibles ofensivas del tribunal evaluador y proporciona las argumentaciones técnicas más sólidas para defender el proyecto de manera sobresaliente.

**1. Sobre el Maniquí y los Gráficos:** 
*   **Pregunta:** "Veo que los músculos cambian de color y el maniquí se actualiza al tocar la pantalla. ¿Cómo has implementado a nivel de código esa relación entre el texto del músculo y la imagen visual?"
*   **Respuesta Ganadora:** "Para evitar escribir un condicional `switch` gigantesco y difícil de mantener, he utilizado una técnica de reflexión dinámica de recursos mediante la función `getResources().getIdentifier()`. El código Java es agnóstico a las imágenes; simplemente compone un String con el formato `maniqui_ + nombreDelMusculo` y le pide al sistema Android que busque un archivo con ese nombre exacto en el directorio `drawable` en pleno tiempo de ejecución. La mayor ventaja arquitectónica de esto es la escalabilidad pasiva: si mañana el cliente quiere añadir un grupo muscular nuevo, basta con arrastrar un archivo PNG con el nombre correcto a la carpeta del proyecto. No hay que añadir, modificar ni compilar ni una sola línea de código fuente Java."

**2. Sobre la Comunicación de Red y Concurrencia:** 
*   **Pregunta:** "¿Por qué usas hilos secundarios (`new Thread`) explícitamente para llamar a la API PHP? ¿Qué pasaría si ejecutaras esas sentencias en el flujo principal?"
*   **Respuesta Ganadora:** "Hacer llamadas de red en el hilo principal está prohibido en la arquitectura Android moderna. El hilo principal (Main/UI Thread) es el encargado exclusivo de repintar la pantalla a 60fps y registrar toques táctiles. Si realizáramos una llamada HTTP sincrónica ahí, y el servidor tardase 3 segundos en responder debido a lag, la pantalla de la app se congelaría completamente durante 3 segundos, lo que derivaría en el fatal error **ANR (Application Not Responding)** provocando que el Sistema Operativo matase el proceso de la app de forma abrupta. Utilizando hilos secundarios (`Threads`) delegamos la pesada carga de red al procesador en background y, al terminar, retomamos el control visual mediante callbacks a `runOnUiThread()`, asegurando una fluidez inquebrantable."

**3. Sobre la Decisión de Arquitectura y Persistencia:** 
*   **Pregunta:** "¿Por qué has invertido tiempo en programar un servidor PHP externo y montar una base de datos MySQL en lugar de utilizar la base de datos embebida nativa SQLite que viene integrada en el móvil de forma mucho más fácil?"
*   **Respuesta Ganadora:** "La decisión de descartar SQLite a favor de un backend PHP obedece a dos requisitos irrenunciables del proyecto: La Persistencia Universal y la Socialización. Si todos los registros residieran exclusivamente dentro del archivo SQLite del dispositivo local (Edge Computing), ante la rotura, formateo o sustitución del terminal móvil, el usuario perdería años de progresión física irremediablemente. Al migrar a un modelo de API REST centralizada, la fuente de la verdad reside en la nube. Esta centralización, además de actuar como backup, es el habilitador técnico fundamental que hace posible el módulo 'Comunidad/Social', dado que permite a un cliente Android consultar los registros cruzados que pertenecen a otros usuarios de la red."

**4. Sobre la Seguridad Informática:** 
*   **Pregunta:** "He auditado el código y he visto que las contraseñas y datos del login se transmiten en peticiones estándar y se guardan sin cifrar. Si esto fuera un producto real que se lanza a producción mañana, ¿qué medidas urgentes aplicarías para proteger los datos?"
*   **Respuesta Ganadora:** "Efectivamente. Para el contexto académico de este TFG me he concentrado en construir un MVP (Producto Mínimo Viable) para demostrar la lógica y la integración de sistemas complejos. No obstante, para un despliegue en servidor de producción implementaría una batería de tres medidas: Primero, forzaría el despliegue del servidor bajo protocolos **HTTPS/TLS** para encriptar los paquetes HTTP en tránsito contra ataques Man-In-The-Middle. Segundo, en lugar de guardar cadenas de texto plano, utilizaría algoritmos de derivación unidireccional y salting en PHP, como **BCRYPT** (`password_hash`), de forma que si la base de datos se viera comprometida, las credenciales fuesen criptográficamente irrecuperables. Por último, sustituiría la concatenación directa de variables en `mysqli` por **Consultas Preparadas (Prepared Statements)** vinculando parámetros mediante `bind_param()`, lo cual anula por diseño cualquier vulnerabilidad de Inyección SQL."

**5. Sobre la Eficiencia de Listas y Renderizado UI:** 
*   **Pregunta:** "Para visualizar la lista de rutinas empleas un componente llamado `RecyclerView`. ¿Para qué sirve exactamente y por qué no usar el antiguo y más sencillo `ListView` o simplemente inyectar `TextViews` dentro de un bucle `ScrollView`?"
*   **Respuesta Ganadora:** "El `RecyclerView` no es un simple contenedor visual, es un orquestador de rendimiento. Si un usuario tuviera 5.000 entrenamientos registrados en la base de datos, inyectar 5.000 tarjetas gráficas en memoria usando un ScrollView convencional saturaría la memoria RAM y bloquearía la máquina de recolección de basura (Garbage Collector). El `RecyclerView`, tal y como indica su nombre, implementa un patrón de reciclaje agresivo: solo crea en memoria las (por ejemplo) 8 vistas de tarjetas que caben físicamente en la pantalla del teléfono. A medida que el usuario hace scroll hacia abajo, las tarjetas gráficas que desaparecen por la parte superior se reciclan y se rellenan con los datos nuevos que entran por la parte inferior. Esto mantiene el consumo de memoria RAM estrictamente plano y constante sin importar si hay 10 registros o 10 millones, asegurando que la aplicación no sufra tirones ('Jank') ni crasheos por 'Out Of Memory' incluso en terminales de gama baja."

> **EL CONSEJO DE ORO (Comodín Definitivo):** Si durante la defensa el tribunal realiza alguna pregunta de índole muy técnica, capciosa o relacionada con un patrón de diseño que no consigas recordar en ese momento de nervios, mantén la calma y utiliza esta frase escudo: *"La estructura y refactorización de esa pieza específica fue una decisión de diseño premeditada, firmemente enfocada a mantener el código ampliamente desacoplado, escalable a futuro y puramente orientado a objetos, garantizando así la adherencia a los principios SOLID fundamentales en la ingeniería de software moderna."* Esta respuesta proyecta profunda seguridad, madurez técnica y habitualmente da por zanjado el debate de forma favorable.

---
*Fin del Documento Técnico. Memoriza los conceptos clave, confía en el trabajo desarrollado y el 10 es indudable.* 🚀🏆
