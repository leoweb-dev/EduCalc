# Ficha técnica de EduCalc

## Identificación

| Característica | Detalle |
|---|---|
| Nombre | EduCalc |
| Tipo de software | Aplicación de escritorio para gestión y cálculo de promedios |
| Lenguaje | Java |
| Interfaz gráfica | Java Swing |
| Punto de entrada | `app.Main` |
| Persistencia | Archivo JSON local para configuraciones; resultados en memoria durante la sesión |
| Exportación | Hoja de cálculo ODS (OpenDocument Spreadsheet 1.2) |
| Dependencias externas | No se observan dependencias de terceros en el código fuente |

## Descripción

EduCalc permite definir grupos, ingresar las notas de sus estudiantes y calcular el promedio de cada estudiante y el promedio general del grupo. También permite guardar y volver a cargar la configuración de un grupo, y exportar los grupos calculados durante la sesión a un archivo ODS.

## Funcionalidades

- Crear una tabla para un grupo de entre **1 y 200 estudiantes** y **1 y 20 notas por estudiante**.
- Ingresar notas numéricas. Se aceptan tanto el punto como la coma como separador decimal.
- Calcular el promedio aritmético simple de cada estudiante y el promedio del grupo, que corresponde al promedio de los promedios individuales.
- Guardar configuraciones de grupo y cargarlas posteriormente desde `datos/grupos.json`. Si se guarda otra configuración con el mismo nombre, se actualizan sus cantidades de estudiantes y notas.
- Exportar a ODS todos los grupos calculados durante la sesión. El libro incluye una hoja por grupo, las notas, los promedios individuales y el promedio general.
- Mostrar avisos ante datos incompletos o inválidos y ante errores de lectura o escritura.

## Arquitectura y componentes

La aplicación separa la interfaz, el control de operaciones, el modelo y la persistencia:

| Componente | Responsabilidad |
|---|---|
| `app.Main` | Inicia la interfaz en el hilo de eventos de Swing. |
| `vista.VentanaPrincipal` | Presenta los campos, la tabla, los botones y los mensajes al usuario. |
| `controlador.PromedioControlador` | Coordina los cálculos, el registro de resultados y las operaciones de guardar y cargar. |
| `modelo.Grupo` | Representa las notas de un grupo y calcula promedios. |
| `modelo.ConfiguracionGrupo` | Representa y valida el nombre del grupo y las cantidades configuradas. |
| `registro.RegistroPromedios` | Conserva los grupos calculados mientras la aplicación está abierta. |
| `persistencia.GuardadorJson` | Lee y escribe configuraciones de grupo en JSON. |
| `persistencia.GuardadorOds` | Genera el archivo ODS y su contenido XML sin una biblioteca externa. |

## Datos y persistencia

- **Configuraciones:** se guardan en `datos/grupos.json`, relativo al directorio desde el que se inicia la aplicación. Cada configuración contiene el nombre del grupo (`grupo`), la cantidad de estudiantes (`cantidadEstudiantes`) y la cantidad de notas (`cantidadNotas`).
- **Resultados:** los grupos calculados se conservan en memoria durante la sesión. Para conservarlos fuera de ella, deben exportarse a un archivo ODS.
- **Almacenamiento:** no se observa uso de bases de datos, servicios remotos ni comunicación de red.

## Requisitos y ejecución

- Se requiere un entorno Java compatible con Java 8 o posterior. El código utiliza características de Java 8 y no declara una versión objetivo en un archivo de construcción.
- Se necesita un entorno de escritorio compatible con Swing/AWT.
- El proceso debe tener permiso para leer y escribir los archivos locales correspondientes.
- La clase de inicio es `app.Main`. El repositorio no incluye una configuración de Maven o Gradle; la compilación debe realizarse desde un IDE Java o con las herramientas del JDK.

## Alcance y consideraciones

- No se aplica una escala o rango académico a las notas; se validan como valores numéricos, pero no se limita su valor según una escala.
- Los promedios son simples, sin ponderaciones.
- Las configuraciones se guardan localmente; los resultados no se guardan automáticamente.
- La aplicación no incluye autenticación ni gestión de usuarios.
