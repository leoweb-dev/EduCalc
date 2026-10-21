package registro;

import modelo.Grupo;

import java.util.ArrayList;
import java.util.List;

// Guarda los grupos calculados durante la sesión.
public class RegistroPromedios {

    private final List<Grupo> grupos = new ArrayList<>();

    public void agregar(Grupo grupo) {
        grupos.add(grupo);
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public boolean estaVacio() {
        return grupos.isEmpty();
    }
}
