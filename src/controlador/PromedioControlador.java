package controlador;

import modelo.Grupo;
import modelo.ConfiguracionGrupo;
import persistencia.GuardadorJson;
import persistencia.GuardadorOds;
import registro.RegistroPromedios;

import java.io.File;
import java.io.IOException;
import java.util.List;

// Conecta la vista con el modelo, el registro y la persistencia.
public class PromedioControlador {

    private final RegistroPromedios registro = new RegistroPromedios();
    private final GuardadorJson guardador = new GuardadorJson();
    private final GuardadorOds guardadorOds = new GuardadorOds();
    private final File archivoGrupos = new File("datos", "grupos.json");

    public Grupo calcular(String nombre, double[][] notas) {
        Grupo grupo = new Grupo(nombre, notas);
        registro.agregar(grupo);
        return grupo;
    }

    public boolean hayResultados() {
        return !registro.estaVacio();
    }

    public void guardarConfiguracionGrupo(String nombre, int estudiantes, int notas) throws IOException {
        guardador.guardarEnLista(new ConfiguracionGrupo(nombre, estudiantes, notas), archivoGrupos);
    }

    public List<ConfiguracionGrupo> cargarConfiguracionesGrupo() throws IOException {
        return guardador.cargarLista(archivoGrupos);
    }

    public void guardarResultados(File archivo) throws IOException {
        guardadorOds.guardar(registro, archivo);
    }
}
