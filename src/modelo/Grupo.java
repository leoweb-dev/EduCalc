package modelo;

public class Grupo {

    private final String nombre;
    private final double[][] notas; // [estudiante][nota]

    public Grupo(String nombre, double[][] notas) {
        this.nombre = nombre;
        this.notas = notas;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadEstudiantes() {
        return notas.length;
    }

    public int getCantidadNotas() {
        return notas.length == 0 ? 0 : notas[0].length;
    }

    // Promedio de cada estudiante
    public double[] getPromedios() {
        double[] promedios = new double[notas.length];
        for (int i = 0; i < notas.length; i++) {
            double suma = 0;
            for (double nota : notas[i]) {
                suma += nota;
            }
            promedios[i] = notas[i].length == 0 ? 0 : suma / notas[i].length;
        }
        return promedios;
    }

    // Promedio general del grupo
    public double getPromedioGrupo() {
        double[] promedios = getPromedios();
        if (promedios.length == 0) {
            return 0;
        }
        double suma = 0;
        for (double p : promedios) {
            suma += p;
        }
        return suma / promedios.length;
    }
}
