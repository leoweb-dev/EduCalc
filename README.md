# EduCalc

## Guardar grupos y promedios

La ventana guarda la configuración de cada grupo (nombre, cantidad de estudiantes y cantidad de notas) en `datos/grupos.json` mediante **Guardar grupo**; no es necesario elegir una ubicación. El archivo ya está incluido en el proyecto y contiene una lista JSON. Si se vuelve a guardar un grupo con el mismo nombre, se actualizan sus cantidades. Usa **Cargar grupo** para seleccionar una configuración guardada; la tabla se crea con la cantidad de estudiantes y notas correspondientes.

Después de ingresar las notas y calcular los promedios, **Exportar notas ODS** genera una hoja de cálculo compatible con LibreOffice Calc y otras aplicaciones que admiten OpenDocument. Cada grupo calculado se incluye en una hoja, con las notas de cada estudiante, su promedio y el promedio general del grupo.