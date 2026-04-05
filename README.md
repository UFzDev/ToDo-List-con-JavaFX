# ToDO List con JavaFX + Firebase

Aplicacion de escritorio para gestion de tareas, construida con JavaFX, Gradle y Firebase.

Este README documenta arquitectura, flujo funcional, estructura de carpetas, configuracion local, comandos y guia de mantenimiento del proyecto.

## 1. Objetivo del proyecto

La app permite que un usuario autenticado gestione su espacio personal de productividad con:

- CRUD completo de tareas.
- Catalogos de categorias y estados por usuario.
- Filtros y busquedas de tareas.
- Reportes (vista preparada para exportaciones).
- Estadisticas visuales con graficas (pie y barras) por semana, mes y anio.

## 2. Stack tecnico

- Java 23 (toolchain configurado en Gradle).
- JavaFX 21.0.6 (`javafx.controls`, `javafx.fxml`).
- Gradle 8.x (wrapper incluido).
- Firebase Admin SDK (Firestore/Auth).
- ControlsFX.
- SLF4J Simple (runtime logger).

Archivo clave de build:

- `build.gradle.kts`

## 3. Estructura principal del proyecto

```text
src/
  main/
    java/ufzdev/todo_list/
      Launcher.java
      Main.java
      config/
      controllers/
      dao/
      models/
      services/
      util/
    resources/
      firebase-key.json
      ufzdev/todo_list/
        view/
        css/
```

Capas:

- `controllers`: eventos UI y navegacion de vistas.
- `dao`: acceso a Firestore (interfaces + implementaciones).
- `models`: entidades (`UserModel`, `TaskModel`, `StatusModel`, `CategoryModel`).
- `services`: reglas de negocio por modulo.
- `util`: utilidades transversales (sesion, navegacion, hilos, alertas, estadisticas).
- `resources/view`: FXML.
- `resources/css`: estilos por pantalla + globales.

## 4. Modulos funcionales

### 4.1 Autenticacion

Vistas:

- `src/main/resources/ufzdev/todo_list/view/login.fxml`
- `src/main/resources/ufzdev/todo_list/view/register.fxml`

Controladores:

- `src/main/java/ufzdev/todo_list/controllers/LoginController.java`
- `src/main/java/ufzdev/todo_list/controllers/RegisterController.java`

Servicio:

- `src/main/java/ufzdev/todo_list/services/UserService.java`

Flujo:

1. Usuario ingresa credenciales (username/password).
2. `UserService.autenticate(...)` valida contra DAO.
3. Se cargan datos de sesion (categorias, estados, tareas) en `UserSessionUtil`.
4. Se navega a tareas y, si aplica, se abre settings inicial.

### 4.2 Tareas (CRUD)

Vista:

- `src/main/resources/ufzdev/todo_list/view/tasks.fxml`

Controlador:

- `src/main/java/ufzdev/todo_list/controllers/TasksController.java`

Servicio:

- `src/main/java/ufzdev/todo_list/services/TaskService.java`

DAO:

- `src/main/java/ufzdev/todo_list/dao/TaskDao.java`
- `src/main/java/ufzdev/todo_list/dao/TaskFirestoreDao.java`

Incluye:

- Crear tarea (modal).
- Editar tarea (reusa modal).
- Eliminar tarea.
- Filtros por texto, categoria y estado.
- Tabla con nombre, descripcion, categoria(s), estado, fecha creacion y vencimiento.

### 4.3 Modal de nueva tarea

Vista:

- `src/main/resources/ufzdev/todo_list/view/new-task.fxml`

Controlador:

- `src/main/java/ufzdev/todo_list/controllers/NewTaskController.java`

Notas:

- Fecha limite via selector de fecha.
- Estado via `ComboBox`.
- Categorias con seleccion multiple (checkbox por opcion).
- Carga de catalogos desde sesion del usuario.

### 4.4 Settings (catalogos por usuario)

Vista:

- `src/main/resources/ufzdev/todo_list/view/settings.fxml`

Controlador:

- `src/main/java/ufzdev/todo_list/controllers/SettingsController.java`

Servicios:

- `src/main/java/ufzdev/todo_list/services/CategoryService.java`
- `src/main/java/ufzdev/todo_list/services/StatusService.java`

DAO:

- `CategoryDao` + `CategoryFirestoreDao`
- `StatusDao` + `StatusFirestoreDao`

Incluye:

- Crear/eliminar categorias.
- Crear/eliminar estados.
- Sincronizacion inmediata de sesion en memoria.

### 4.5 Reportes (UI preparada)

Vista:

- `src/main/resources/ufzdev/todo_list/view/reports.fxml`

Controlador:

- `src/main/java/ufzdev/todo_list/controllers/ReportsController.java`

Estado actual:

- Seleccion de filtros con checkboxes (categorias y estados).
- Botones para generar Excel/PDF en la UI.
- Navegacion integrada con resto de modulos.

### 4.6 Estadisticas

Vista:

- `src/main/resources/ufzdev/todo_list/view/stats.fxml`

Controlador:

- `src/main/java/ufzdev/todo_list/controllers/StatsController.java`

Servicio:

- `src/main/java/ufzdev/todo_list/services/StatisticsService.java`

Utilidades:

- `src/main/java/ufzdev/todo_list/util/StatisticsChartUtil.java`
- `src/main/java/ufzdev/todo_list/util/StatisticsPeriod.java`

Incluye:

- Filtro temporal: Semana / Mes / Anio.
- Pie chart: `No completada`, `En progreso`, `Completada`.
- Bar chart: total de tareas por categoria.
- Resumen textual dinamico segun periodo.

## 5. Patrones y arquitectura aplicada

- DAO Pattern: interfaces + implementaciones Firestore.
- Service Layer: reglas de negocio desacopladas de UI.
- Session Cache (`UserSessionUtil`): carga una vez por login y reuso en toda la app.
- Navegacion centralizada (`NavigationUtil`).
- Ejecucion asincrona para acciones largas (`TaskExecutorUtil`).

## 6. Gestion de sesion

Archivo:

- `src/main/java/ufzdev/todo_list/util/UserSessionUtil.java`

Mantiene en memoria:

- Usuario actual.
- Categorias del usuario.
- Estados del usuario.
- Tareas del usuario.
- Tarea en edicion (para modal reutilizable).

Ventaja:

- Evita consultar Firestore en cada apertura de ventana.
- Mejora UX y reduce latencia.

## 7. Configuracion de Firebase local

Coloca la credencial de servicio en:

- `src/main/resources/firebase-key.json`

Tambien valida la ruta equivalente en:

- `build/resources/main/firebase-key.json` (generada al build)

Recomendaciones:

- No subir claves reales al repositorio.
- Usar credenciales por entorno (dev/test/prod).
- Restringir permisos del service account.

## 8. Comandos utiles (Windows / PowerShell)

Ejecuta desde la raiz del proyecto (`ToDO_List`):

```powershell
.\gradlew.bat clean
.\gradlew.bat build
.\gradlew.bat run
```

Pruebas unitarias:

```powershell
.\gradlew.bat test
```

## 9. Flujo de datos (resumen)

1. UI dispara accion en controlador.
2. Controlador delega a servicio.
3. Servicio usa DAO para persistencia/consulta.
4. Servicio actualiza `UserSessionUtil`.
5. Controlador refresca tabla/grafica/labels.

## 10. Estado inicial de estados base

En `UserService` se asegura que existan estados base por usuario:

- `No completada`
- `En progreso`
- `Completada`

Esto garantiza consistencia para filtros y estadisticas.

## 11. Navegacion

Archivo:

- `src/main/java/ufzdev/todo_list/util/NavigationUtil.java`

Rutas principales:

- `goToLogin(...)`
- `goToTasks(...)`
- `goToReports(...)`
- `goToStats(...)`
- Modales: `goToRegister()`, `goToNewTask()`, `goToSettings()`

## 12. Disenio y UI

Referencia de lineamientos:

- `src/DESIGN.md`

CSS clave:

- `src/main/resources/ufzdev/todo_list/css/global.css`
- `src/main/resources/ufzdev/todo_list/css/tasks.css`
- `src/main/resources/ufzdev/todo_list/css/new-task.css`
- `src/main/resources/ufzdev/todo_list/css/settings.css`
- `src/main/resources/ufzdev/todo_list/css/reports.css`
- `src/main/resources/ufzdev/todo_list/css/stats.css`
- `src/main/resources/ufzdev/todo_list/css/login.css`
- `src/main/resources/ufzdev/todo_list/css/register.css`

## 13. Creditos

- Repositorio: `https://github.com/UFzDev/ToDo-List-con-JavaFX`
- Proyecto academico/practico de gestion de tareas con JavaFX.
