# Manual de EduCalc

## 1. ¿Qué es EduCalc?

EduCalc es un programa de escritorio que ayuda a calcular promedios de notas. Permite crear una tabla para un grupo de estudiantes, ingresar notas, calcular promedios individuales y del grupo, guardar la configuración del grupo y exportar resultados.

Este manual está dirigido a personas que recién comienzan a usar Java. No hace falta saber programar para utilizar el programa. Al final se explica cómo reconocer las partes principales del código.

## 2. Antes de empezar

Para usar EduCalc necesitas:

- Una computadora con entorno gráfico; el programa abre una ventana y no funciona como una página web.
- Java 8 o posterior instalado.
- El proyecto completo, incluida la carpeta `datos`.

Para **ejecutar código fuente** desde una terminal se necesita un JDK (Java Development Kit). El JDK incluye herramientas para compilar, como `javac`, además de Java para ejecutar el programa.

Puedes revisar si las herramientas están disponibles abriendo PowerShell en Windows y escribiendo:

```powershell
java -version
javac -version
```

Si la terminal indica que no reconoce uno de esos comandos, instala un JDK y vuelve a abrirla. Si vas a usar un IDE, como Visual Studio Code con soporte para Java, también puedes abrir la carpeta del proyecto y ejecutar la clase `Main`.

## 3. Estructura del proyecto

Las carpetas contienen distintas partes del programa:

```text
EduCalc/
├── datos/
│   └── grupos.json
├── src/
│   ├── app/
│   │   └── Main.java
│   ├── controlador/
│   ├── modelo/
│   ├── persistencia/
│   ├── registro/
│   └── vista/
├── ficha-tecnica.md
└── manual.md
```

- `src` contiene el código fuente: instrucciones escritas en Java que componen la aplicación.
- `datos/grupos.json` contiene configuraciones guardadas, como el nombre del grupo y cuántos estudiantes y notas tendrá.
- `app/Main.java` es el punto de entrada: allí comienza la aplicación.
- Las otras carpetas organizan la interfaz, las reglas de cálculo, el control y el almacenamiento.

## 4. Cómo iniciar el programa

### Opción A: desde Visual Studio Code u otro IDE

1. Abre en el IDE la carpeta principal del proyecto `EduCalc`, no solamente la carpeta `src`.
2. Espera a que el IDE detecte o configure un JDK.
3. Abre `src/app/Main.java`.
4. Usa la opción **Run** o **Ejecutar** que aparece sobre el método `main`.
5. Si el IDE permite configurar el directorio de trabajo, selecciona la carpeta principal `EduCalc`. Así el programa encontrará `datos/grupos.json`.

Los nombres de las opciones pueden variar según el IDE.

### Opción B: desde PowerShell en Windows

Abre PowerShell en la carpeta principal del proyecto. Puedes comprobar que estás en la carpeta correcta con:

```powershell
Get-Location
Get-ChildItem
```

Deberías ver las carpetas `src` y `datos`. Luego crea una carpeta para los archivos compilados:

```powershell
New-Item -ItemType Directory -Force out
```

Compila todos los archivos Java del proyecto:

```powershell
javac -encoding UTF-8 -d out src\app\Main.java src\controlador\PromedioControlador.java src\modelo\ConfiguracionGrupo.java src\modelo\Grupo.java src\persistencia\GuardadorJson.java src\persistencia\GuardadorOds.java src\registro\RegistroPromedios.java src\vista\VentanaPrincipal.java
```

Si la compilación termina sin mostrar errores, inicia el programa con:

```powershell
java -cp out app.Main
```

En estos comandos:

- `javac` traduce los archivos `.java` a archivos `.class` que Java puede ejecutar.
- `-d out` indica que los archivos compilados se guarden en la carpeta `out`.
- `java` inicia el programa.
- `-cp out` le indica a Java dónde buscar las clases compiladas.
- `app.Main` identifica la clase inicial por su paquete y su nombre.

Ejecuta estos comandos desde la carpeta principal del proyecto. EduCalc busca `datos/grupos.json` usando esa carpeta como referencia.

## 5. Cómo usar la ventana

### Crear una tabla

1. En **Grupo**, escribe un nombre, por ejemplo `9no 3`.
2. En **Estudiantes**, selecciona cuántos estudiantes quieres incluir.
3. En **Notas**, selecciona cuántas notas tendrá cada estudiante.
4. Haz clic en **Crear tabla**.

Se puede configurar entre **1 y 200 estudiantes** y entre **1 y 20 notas por estudiante**. La tabla crea una fila por estudiante. Las columnas de notas son editables; los nombres de estudiantes y los promedios no lo son.

### Ingresar notas y calcular

1. Escribe una nota en cada celda de las columnas **Nota 1**, **Nota 2**, etc.
2. Asegúrate de completar todas las notas.
3. Haz clic en **Calcular promedios**.

El programa muestra el promedio de cada estudiante en la columna **Promedio** y el promedio general junto a los botones.

El cálculo es un promedio aritmético simple. Por ejemplo, si un estudiante obtiene 6, 8 y 10:

```text
(6 + 8 + 10) / 3 = 8
```

El promedio del grupo se calcula promediando los promedios de sus estudiantes. No se aplican ponderaciones ni una escala mínima o máxima para las notas.

Se aceptan notas con punto o coma decimal, por ejemplo `7.5` o `7,5`. Si falta una nota o un valor no es numérico, el programa muestra qué estudiante y qué nota debe revisarse.

> **Importante:** si modificas una nota después de calcular, haz clic otra vez en **Calcular promedios** para actualizar los resultados. Cada cálculo correcto se incorpora al registro que puede exportarse.

### Guardar la configuración de un grupo

1. Escribe el nombre del grupo.
2. Selecciona la cantidad de estudiantes y de notas.
3. Haz clic en **Guardar grupo**.

Esta acción guarda la configuración, **no las notas ni los promedios**. La información se almacena en `datos/grupos.json`. Si guardas otra configuración con el mismo nombre, se actualizan las cantidades almacenadas.

### Cargar una configuración

1. Haz clic en **Cargar grupo**.
2. Selecciona un grupo de la lista.
3. Ingresa las notas en la tabla que aparece y calcula los promedios.

Si no hay configuraciones guardadas, el programa lo indicará. Al cargar un grupo se crea una tabla nueva para ingresar sus notas.

### Exportar notas y promedios

1. Calcula los promedios de uno o más grupos.
2. Haz clic en **Exportar notas ODS**.
3. Elige dónde guardar el archivo y confirma.

El archivo `.ods` se puede abrir con LibreOffice Calc y otras aplicaciones compatibles con OpenDocument. Incluye una hoja por cada grupo calculado en la sesión, con sus notas, promedios individuales y promedio general.

> **Importante:** los resultados calculados se conservan en memoria mientras el programa está abierto. Al cerrar EduCalc se pierde ese registro; exporta los resultados antes de cerrar si quieres conservarlos. Guardar o cargar una configuración no recupera resultados anteriores.

## 6. Conceptos básicos de Java que aparecen en el proyecto

Java es un lenguaje de programación. El código se escribe en archivos terminados en `.java`. EduCalc está compuesto por clases: cada clase agrupa datos y acciones relacionados.

### Paquetes y carpetas

Al principio de `Main.java` aparece:

```java
package app;
```

Esto indica que la clase pertenece al paquete `app`. En este proyecto, los paquetes corresponden a carpetas dentro de `src`. Por ejemplo, `package modelo;` corresponde a `src/modelo`.

### Clases y objetos

Una clase describe una pieza del programa. Por ejemplo, `Grupo` representa un grupo con notas y sabe calcular sus promedios.

Un objeto es una instancia creada a partir de una clase. Cuando el programa ejecuta:

```java
new PromedioControlador()
```

crea un objeto controlador. La palabra `new` significa “crear un nuevo objeto”.

### El método `main`

Este es el método desde el que Java comienza a ejecutar la aplicación:

```java
public static void main(String[] args) {
    // instrucciones iniciales
}
```

No es necesario memorizar cada palabra para empezar. Por ahora, puedes reconocer `main` como el punto de inicio. En EduCalc, `Main` prepara la ventana y el controlador.

### Variables y tipos

Una variable guarda un dato. El tipo indica qué clase de dato puede guardar:

```java
String nombre = "9no 3";
int estudiantes = 5;
double nota = 8.5;
```

- `String` guarda texto.
- `int` guarda números enteros.
- `double` guarda números que pueden tener decimales.

### Métodos

Un método es una acción que una clase puede realizar. Por ejemplo, `getPromedios()` calcula los promedios individuales de un grupo. Los paréntesis indican una llamada a un método:

```java
double[] promedios = grupo.getPromedios();
```

### Arreglos

Un arreglo almacena varios valores del mismo tipo. En EduCalc, las notas se representan con `double[][]`: es un arreglo de dos dimensiones, parecido a una tabla. La primera dimensión corresponde a los estudiantes y la segunda a sus notas.

```text
notas[estudiante][numeroDeNota]
```

En Java, las posiciones de los arreglos comienzan en cero. Por eso, la primera nota de la primera persona se ubicaría en `notas[0][0]`.

### Condiciones y ciclos

Una condición permite decidir si se ejecuta una acción. El proyecto, por ejemplo, comprueba si el nombre está vacío antes de guardar una configuración.

Un ciclo repite una acción. Para calcular un promedio, el programa recorre las notas, las suma y divide el total por la cantidad de notas.

### Importaciones y Swing

Una línea que empieza con `import` permite usar una clase de Java desde otro paquete. EduCalc importa clases de Swing, la biblioteca incluida en Java para crear ventanas, botones, tablas y mensajes.

Cuando haces clic en un botón, Swing ejecuta la acción asociada a ese botón. Por ejemplo, **Crear tabla** llama al método que construye la tabla.

### Manejo de errores

Algunas operaciones, como leer o escribir un archivo, pueden fallar. Java representa ciertos errores con excepciones. EduCalc captura algunos de ellos y presenta un mensaje en una ventana para indicar qué ocurrió.

## 7. ¿Dónde buscar cada cosa en el código?

| Si quieres entender o cambiar... | Archivo o carpeta |
|---|---|
| Cómo arranca la aplicación | `src/app/Main.java` |
| La ventana, los botones y los mensajes | `src/vista/VentanaPrincipal.java` |
| Cómo se coordinan las acciones | `src/controlador/PromedioControlador.java` |
| Cómo se representan las notas y se calculan los promedios | `src/modelo/Grupo.java` |
| Los límites y validaciones de la configuración | `src/modelo/ConfiguracionGrupo.java` |
| Cómo se guardan y cargan configuraciones | `src/persistencia/GuardadorJson.java` |
| Cómo se crea el archivo de hoja de cálculo | `src/persistencia/GuardadorOds.java` |
| El registro temporal de los grupos calculados | `src/registro/RegistroPromedios.java` |

Si estás aprendiendo, empieza leyendo `Main.java` y luego sigue el flujo hacia la ventana, el controlador y el modelo. Puedes modificar el código en un IDE, pero guarda una copia antes de hacer cambios y vuelve a compilar el proyecto para comprobarlos.

## 8. Problemas comunes

| Problema | Qué revisar |
|---|---|
| La terminal no reconoce `java` o `javac` | Instala un JDK y abre una terminal nueva. |
| Aparecen errores al compilar | Comprueba que ejecutas el comando desde la carpeta principal y que todos los archivos de `src` están presentes. Lee el primer mensaje de error: a menudo señala el archivo y la línea a corregir. |
| El programa indica que falta una nota | Revisa que cada celda de notas tenga un número y vuelve a calcular. |
| El programa no encuentra grupos guardados | Verifica que `datos/grupos.json` exista y que iniciaste EduCalc desde la carpeta principal del proyecto. |
| No se puede guardar una configuración o exportación | Revisa que la carpeta de destino exista y que tengas permiso para escribir en ella. |
| No aparece una ventana | Asegúrate de ejecutar la aplicación en una computadora con entorno gráfico. |
| Quieres conservar los resultados al cerrar | Usa **Exportar notas ODS** antes de cerrar el programa. |

## Documentación relacionada

- [Ficha técnica de EduCalc](ficha-tecnica.md)
