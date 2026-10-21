package modelo;

public class ConfiguracionGrupo {

    private final String nombre;
    private final int cantidadEstudiantes;
    private final int cantidadNotas;

    public ConfiguracionGrupo(String nombre, int cantidadEstudiantes, int cantidadNotas) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del grupo no puede estar vacío.");
        }
        if (cantidadEstudiantes < 1 || cantidadEstudiantes > 200) {
            throw new IllegalArgumentException("La cantidad de estudiantes debe estar entre 1 y 200.");
        }
        if (cantidadNotas < 1 || cantidadNotas > 20) {
            throw new IllegalArgumentException("La cantidad de notas debe estar entre 1 y 20.");
        }
        this.nombre = nombre;
        this.cantidadEstudiantes = cantidadEstudiantes;
        this.cantidadNotas = cantidadNotas;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadEstudiantes() {
        return cantidadEstudiantes;
    }

    public int getCantidadNotas() {
        return cantidadNotas;
    }
}
