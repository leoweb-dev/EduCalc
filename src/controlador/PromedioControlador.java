package controlador;

import modelo.Grupo;
import persistencia.GuardadorJson;
import registro.RegistroPromedios;

import java.io.File;
import java.io.IOException;

// Conecta la vista con el modelo, el registro y la persistencia.
public class PromedioControlador {

    private final RegistroPromedios registro = new RegistroPromedios();
    private final GuardadorJson guardador = new GuardadorJson();

    public Grupo calcular(String nombre, double[][] notas) {
        Grupo grupo = new Grupo(nombre, notas);
        registro.agregar(grupo);
        return grupo;
    }

    public boolean hayResultados() {
        return !registro.estaVacio();
    }

    public void guardar(File archivo) throws IOException {
        guardador.guardar(registro, archivo);
    }
}
