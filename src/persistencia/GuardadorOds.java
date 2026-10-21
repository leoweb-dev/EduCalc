package persistencia;

import modelo.Grupo;
import registro.RegistroPromedios;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// Genera un libro ODS sencillo sin dependencias externas.
public class GuardadorOds {

    private static final String MIME_TYPE = "application/vnd.oasis.opendocument.spreadsheet";

    public void guardar(RegistroPromedios registro, File archivo) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivo))) {
            escribirMimetype(zip);
            escribirEntrada(zip, "content.xml", crearContenido(registro));
            escribirEntrada(zip, "META-INF/manifest.xml", crearManifiesto());
        }
    }

    private void escribirMimetype(ZipOutputStream zip) throws IOException {
        byte[] contenido = MIME_TYPE.getBytes(StandardCharsets.UTF_8);
        CRC32 crc = new CRC32();
        crc.update(contenido);
        ZipEntry entrada = new ZipEntry("mimetype");
        entrada.setMethod(ZipEntry.STORED);
        entrada.setSize(contenido.length);
        entrada.setCompressedSize(contenido.length);
        entrada.setCrc(crc.getValue());
        zip.putNextEntry(entrada);
        zip.write(contenido);
        zip.closeEntry();
    }

    private void escribirEntrada(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String crearContenido(RegistroPromedios registro) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<office:document-content ")
                .append("xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" ")
                .append("xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\" ")
                .append("xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" ")
                .append("office:version=\"1.2\"><office:body><office:spreadsheet>");

        for (int i = 0; i < registro.getGrupos().size(); i++) {
            agregarHoja(xml, registro.getGrupos().get(i), i + 1);
        }
        return xml.append("</office:spreadsheet></office:body></office:document-content>").toString();
    }

    private void agregarHoja(StringBuilder xml, Grupo grupo, int numeroHoja) {
        xml.append("<table:table table:name=\"Grupo_").append(numeroHoja).append("\">");

        xml.append("<table:table-row>");
        agregarCeldaTexto(xml, "Grupo");
        agregarCeldaTexto(xml, grupo.getNombre());
        xml.append("</table:table-row>");

        xml.append("<table:table-row>");
        agregarCeldaTexto(xml, "Estudiante");
        for (int nota = 1; nota <= grupo.getCantidadNotas(); nota++) {
            agregarCeldaTexto(xml, "Nota " + nota);
        }
        agregarCeldaTexto(xml, "Promedio");
        xml.append("</table:table-row>");

        double[] promedios = grupo.getPromedios();
        for (int estudiante = 0; estudiante < grupo.getCantidadEstudiantes(); estudiante++) {
            xml.append("<table:table-row>");
            agregarCeldaTexto(xml, "Estudiante " + (estudiante + 1));
            for (int nota = 0; nota < grupo.getCantidadNotas(); nota++) {
                agregarCeldaNumero(xml, grupo.getNota(estudiante, nota));
            }
            agregarCeldaNumero(xml, promedios[estudiante]);
            xml.append("</table:table-row>");
        }

        xml.append("<table:table-row>");
        agregarCeldaTexto(xml, "Promedio del grupo");
        for (int nota = 0; nota < grupo.getCantidadNotas(); nota++) {
            agregarCeldaVacia(xml);
        }
        agregarCeldaNumero(xml, grupo.getPromedioGrupo());
        xml.append("</table:table-row></table:table>");
    }

    private void agregarCeldaTexto(StringBuilder xml, String valor) {
        xml.append("<table:table-cell office:value-type=\"string\"><text:p>")
                .append(escaparXml(valor))
                .append("</text:p></table:table-cell>");
    }

    private void agregarCeldaNumero(StringBuilder xml, double valor) {
        String numero = String.format(Locale.US, "%.2f", valor);
        xml.append("<table:table-cell office:value-type=\"float\" office:value=\"")
                .append(numero)
                .append("\"><text:p>")
                .append(numero)
                .append("</text:p></table:table-cell>");
    }

    private void agregarCeldaVacia(StringBuilder xml) {
        xml.append("<table:table-cell/>");
    }

    private String escaparXml(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length();) {
            int codigo = texto.codePointAt(i);
            i += Character.charCount(codigo);
            if (codigo == '&') {
                resultado.append("&amp;");
            } else if (codigo == '<') {
                resultado.append("&lt;");
            } else if (codigo == '>') {
                resultado.append("&gt;");
            } else if (codigo == '"') {
                resultado.append("&quot;");
            } else if (codigo == '\'') {
                resultado.append("&apos;");
            } else if (codigo == 0x9 || codigo == 0xA || codigo == 0xD
                    || (codigo >= 0x20 && codigo <= 0xD7FF)
                    || (codigo >= 0xE000 && codigo <= 0xFFFD)
                    || (codigo >= 0x10000 && codigo <= 0x10FFFF)) {
                resultado.appendCodePoint(codigo);
            } else {
                resultado.append('\uFFFD');
            }
        }
        return resultado.toString();
    }

    private String crearManifiesto() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<manifest:manifest "
                + "xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\" "
                + "manifest:version=\"1.2\">"
                + "<manifest:file-entry manifest:full-path=\"/\" manifest:media-type=\"" + MIME_TYPE + "\"/>"
                + "<manifest:file-entry manifest:full-path=\"content.xml\" manifest:media-type=\"text/xml\"/>"
                + "</manifest:manifest>";
    }
}
