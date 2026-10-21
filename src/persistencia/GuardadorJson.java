package persistencia;

import modelo.Grupo;
import registro.RegistroPromedios;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

// Escribe el registro en un archivo JSON (sin librerías externas).
public class GuardadorJson {

    public void guardar(RegistroPromedios registro, File archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"grupos\": [\n");

        for (int g = 0; g < registro.getGrupos().size(); g++) {
            Grupo grupo = registro.getGrupos().get(g);
            double[] promedios = grupo.getPromedios();

            sb.append("    {\n");
            sb.append("      \"grupo\": \"").append(escapar(grupo.getNombre())).append("\",\n");
            sb.append("      \"cantidadEstudiantes\": ").append(grupo.getCantidadEstudiantes()).append(",\n");
            sb.append("      \"cantidadNotas\": ").append(grupo.getCantidadNotas()).append(",\n");
            sb.append("      \"promediosEstudiantes\": [");
            for (int i = 0; i < promedios.length; i++) {
                sb.append(numero(promedios[i]));
                if (i < promedios.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("],\n");
            sb.append("      \"promedioGrupo\": ").append(numero(grupo.getPromedioGrupo())).append("\n");
            sb.append("    }");
            if (g < registro.getGrupos().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n}\n");
        Files.write(archivo.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String numero(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
