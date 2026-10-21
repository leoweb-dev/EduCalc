package persistencia;

import modelo.ConfiguracionGrupo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Guarda y carga configuraciones de grupos en JSON.
public class GuardadorJson {

    public void guardar(ConfiguracionGrupo grupo, File archivo) throws IOException {
        Files.write(archivo.toPath(), serializarGrupo(grupo).getBytes(StandardCharsets.UTF_8));
    }

    public void guardarEnLista(ConfiguracionGrupo grupo, File archivo) throws IOException {
        List<ConfiguracionGrupo> grupos = new ArrayList<>(cargarLista(archivo));
        boolean actualizado = false;
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getNombre().equals(grupo.getNombre())) {
                grupos.set(i, grupo);
                actualizado = true;
                break;
            }
        }
        if (!actualizado) {
            grupos.add(grupo);
        }

        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < grupos.size(); i++) {
            json.append("  ").append(serializarGrupo(grupos.get(i)).replace("\n", "\n  "));
            if (i < grupos.size() - 1) {
                json.append(',');
            }
            json.append('\n');
        }
        json.append("]\n");
        Files.write(archivo.toPath(), json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public List<ConfiguracionGrupo> cargarLista(File archivo) throws IOException {
        String json = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
        List<ConfiguracionGrupo> grupos = new ArrayList<>();
        int posicion = saltarEspacios(json, 0);
        if (posicion >= json.length() || json.charAt(posicion++) != '[') {
            throw new IOException("El archivo de grupos debe contener una lista JSON.");
        }

        boolean listaCerrada = false;
        posicion = saltarEspacios(json, posicion);
        if (posicion < json.length() && json.charAt(posicion) == ']') {
            posicion++;
            listaCerrada = true;
        } else {
            while (posicion < json.length()) {
                posicion = saltarEspacios(json, posicion);
                if (posicion >= json.length() || json.charAt(posicion) != '{') {
                    throw new IOException("La lista JSON contiene un grupo inválido.");
                }
                int inicio = posicion;
                int profundidad = 0;
                boolean dentroCadena = false;
                boolean escape = false;
                do {
                    char caracter = json.charAt(posicion++);
                    if (dentroCadena) {
                        if (escape) {
                            escape = false;
                        } else if (caracter == '\\') {
                            escape = true;
                        } else if (caracter == '"') {
                            dentroCadena = false;
                        }
                    } else if (caracter == '"') {
                        dentroCadena = true;
                    } else if (caracter == '{') {
                        profundidad++;
                    } else if (caracter == '}') {
                        profundidad--;
                    }
                    if (posicion == json.length() && profundidad != 0) {
                        throw new IOException("La lista JSON contiene un grupo incompleto.");
                    }
                } while (profundidad > 0);

                grupos.add(cargarDesdeTexto(json.substring(inicio, posicion)));
                posicion = saltarEspacios(json, posicion);
                if (posicion >= json.length()) {
                    throw new IOException("La lista JSON no está cerrada.");
                }
                char separador = json.charAt(posicion++);
                if (separador == ']') {
                    listaCerrada = true;
                    break;
                }
                if (separador != ',') {
                    throw new IOException("La lista JSON contiene un separador inválido.");
                }
            }
        }

        if (!listaCerrada) {
            throw new IOException("La lista JSON no está cerrada.");
        }
        if (saltarEspacios(json, posicion) != json.length()) {
            throw new IOException("Hay contenido inesperado después de la lista JSON.");
        }
        return grupos;
    }

    public ConfiguracionGrupo cargar(File archivo) throws IOException {
        String json = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
        return cargarDesdeTexto(json);
    }

    private ConfiguracionGrupo cargarDesdeTexto(String json) throws IOException {
        String nombre = leerCadena(json, "grupo");
        int estudiantes = leerEntero(json, "cantidadEstudiantes");
        int notas = leerEntero(json, "cantidadNotas");
        try {
            return new ConfiguracionGrupo(nombre, estudiantes, notas);
        } catch (IllegalArgumentException ex) {
            throw new IOException("La configuración del grupo contiene valores inválidos.", ex);
        }
    }

    private String serializarGrupo(ConfiguracionGrupo grupo) {
        return "{\n"
                + "  \"grupo\": \"" + escapar(grupo.getNombre()) + "\",\n"
                + "  \"cantidadEstudiantes\": " + grupo.getCantidadEstudiantes() + ",\n"
                + "  \"cantidadNotas\": " + grupo.getCantidadNotas() + "\n"
                + "}";
    }

    private int saltarEspacios(String texto, int posicion) {
        while (posicion < texto.length() && Character.isWhitespace(texto.charAt(posicion))) {
            posicion++;
        }
        return posicion;
    }

    private String escapar(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            switch (caracter) {
                case '"':
                    resultado.append("\\\"");
                    break;
                case '\\':
                    resultado.append("\\\\");
                    break;
                case '\b':
                    resultado.append("\\b");
                    break;
                case '\f':
                    resultado.append("\\f");
                    break;
                case '\n':
                    resultado.append("\\n");
                    break;
                case '\r':
                    resultado.append("\\r");
                    break;
                case '\t':
                    resultado.append("\\t");
                    break;
                default:
                    if (caracter < 0x20) {
                        resultado.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        resultado.append(caracter);
                    }
            }
        }
        return resultado.toString();
    }

    private String leerCadena(String json, String clave) throws IOException {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(clave)
                + "\"\\s*:\\s*(\"(?:\\\\.|[^\"\\\\])*\")").matcher(json);
        if (!matcher.find()) {
            throw new IOException("Falta el campo \"" + clave + "\" en el archivo JSON.");
        }
        String valor = matcher.group(1);
        StringBuilder resultado = new StringBuilder();
        for (int i = 1; i < valor.length() - 1; i++) {
            char caracter = valor.charAt(i);
            if (caracter != '\\') {
                resultado.append(caracter);
                continue;
            }
            if (++i >= valor.length() - 1) {
                throw new IOException("El campo \"" + clave + "\" contiene un escape inválido.");
            }
            char escape = valor.charAt(i);
            switch (escape) {
                case '"':
                case '\\':
                case '/':
                    resultado.append(escape);
                    break;
                case 'b':
                    resultado.append('\b');
                    break;
                case 'f':
                    resultado.append('\f');
                    break;
                case 'n':
                    resultado.append('\n');
                    break;
                case 'r':
                    resultado.append('\r');
                    break;
                case 't':
                    resultado.append('\t');
                    break;
                case 'u':
                    if (i + 4 >= valor.length() - 1) {
                        throw new IOException("El campo \"" + clave + "\" contiene un escape Unicode inválido.");
                    }
                    try {
                        resultado.append((char) Integer.parseInt(valor.substring(i + 1, i + 5), 16));
                    } catch (NumberFormatException ex) {
                        throw new IOException("El campo \"" + clave + "\" contiene un escape Unicode inválido.", ex);
                    }
                    i += 4;
                    break;
                default:
                    throw new IOException("El campo \"" + clave + "\" contiene un escape inválido.");
            }
        }
        return resultado.toString();
    }

    private int leerEntero(String json, String clave) throws IOException {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(clave)
                + "\"\\s*:\\s*(\\d+)").matcher(json);
        if (!matcher.find()) {
            throw new IOException("Falta el campo numérico \"" + clave + "\" en el archivo JSON.");
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            throw new IOException("El campo \"" + clave + "\" está fuera de rango.", ex);
        }
    }
}
