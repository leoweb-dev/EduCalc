package vista;

import controlador.PromedioControlador;
import modelo.ConfiguracionGrupo;
import modelo.Grupo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {

    private final PromedioControlador controlador;

    private final JTextField campoGrupo = new JTextField(12);
    private final JSpinner campoEstudiantes = new JSpinner(new SpinnerNumberModel(5, 1, 200, 1));
    private final JSpinner campoNotas = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
    private final JLabel etiquetaPromedioGrupo = new JLabel("Promedio del grupo: -");

    private DefaultTableModel modeloTabla;
    private final JTable tabla = new JTable();
    private int cantidadNotas = 0;

    public VentanaPrincipal(PromedioControlador controlador) {
        super("Promedio de notas");
        this.controlador = controlador;
        armarVentana();
    }

    private void armarVentana() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Parte superior: datos del grupo
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Grupo:"));
        arriba.add(campoGrupo);
        arriba.add(new JLabel("Estudiantes:"));
        arriba.add(campoEstudiantes);
        arriba.add(new JLabel("Notas:"));
        arriba.add(campoNotas);
        JButton botonCrear = new JButton("Crear tabla");
        botonCrear.addActionListener(e -> crearTabla());
        arriba.add(botonCrear);
        add(arriba, BorderLayout.NORTH);

        // Centro: tabla
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Parte inferior: botones y resultado
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonCalcular = new JButton("Calcular promedios");
        botonCalcular.addActionListener(e -> calcular());
        JButton botonGuardarGrupo = new JButton("Guardar grupo");
        botonGuardarGrupo.addActionListener(e -> guardarGrupo());
        JButton botonCargarGrupo = new JButton("Cargar grupo");
        botonCargarGrupo.addActionListener(e -> cargarGrupo());
        JButton botonGuardarNotas = new JButton("Exportar notas ODS");
        botonGuardarNotas.addActionListener(e -> guardarNotas());
        abajo.add(botonCalcular);
        abajo.add(botonGuardarGrupo);
        abajo.add(botonCargarGrupo);
        abajo.add(botonGuardarNotas);
        abajo.add(etiquetaPromedioGrupo);
        add(abajo, BorderLayout.SOUTH);

        setSize(950, 450);
        setLocationRelativeTo(null);
    }

    private void crearTabla() {
        int estudiantes = (Integer) campoEstudiantes.getValue();
        cantidadNotas = (Integer) campoNotas.getValue();

        // Columnas: Estudiante, Nota 1..N, Promedio
        String[] columnas = new String[cantidadNotas + 2];
        columnas[0] = "Estudiante";
        for (int i = 1; i <= cantidadNotas; i++) {
            columnas[i] = "Nota " + i;
        }
        columnas[cantidadNotas + 1] = "Promedio";

        final int ultimaColumna = cantidadNotas + 1;
        modeloTabla = new DefaultTableModel(columnas, estudiantes) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return columna != 0 && columna != ultimaColumna;
            }
        };
        for (int f = 0; f < estudiantes; f++) {
            modeloTabla.setValueAt("Estudiante " + (f + 1), f, 0);
        }
        tabla.setModel(modeloTabla);
        etiquetaPromedioGrupo.setText("Promedio del grupo: -");
    }

    private void calcular() {
        if (modeloTabla == null) {
            avisar("Primero crea la tabla.");
            return;
        }
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        int filas = modeloTabla.getRowCount();
        double[][] notas = new double[filas][cantidadNotas];

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < cantidadNotas; c++) {
                Object valor = modeloTabla.getValueAt(f, c + 1);
                try {
                    String texto = valor == null ? "" : valor.toString().trim().replace(',', '.');
                    notas[f][c] = Double.parseDouble(texto);
                } catch (NumberFormatException ex) {
                    avisar("Falta una nota o no es válida: estudiante " + (f + 1) + ", nota " + (c + 1));
                    return;
                }
            }
        }

        String nombre = campoGrupo.getText().trim();
        if (nombre.isEmpty()) {
            nombre = "Sin nombre";
        }

        Grupo grupo = controlador.calcular(nombre, notas);
        double[] promedios = grupo.getPromedios();
        for (int f = 0; f < filas; f++) {
            modeloTabla.setValueAt(String.format(Locale.US, "%.2f", promedios[f]), f, cantidadNotas + 1);
        }
        etiquetaPromedioGrupo.setText(
                String.format(Locale.US, "Promedio del grupo: %.2f", grupo.getPromedioGrupo()));
    }

    private void guardarGrupo() {
        String nombre = campoGrupo.getText().trim();
        if (nombre.isEmpty()) {
            avisar("Escribe el nombre del grupo antes de guardarlo.");
            return;
        }
        try {
            controlador.guardarConfiguracionGrupo(
                    nombre,
                    (Integer) campoEstudiantes.getValue(),
                    (Integer) campoNotas.getValue());
            avisar("Grupo guardado correctamente.");
        } catch (IOException | IllegalArgumentException ex) {
            avisar("No se pudo guardar el grupo: " + ex.getMessage());
        }
    }

    private void cargarGrupo() {
        try {
            List<ConfiguracionGrupo> grupos = controlador.cargarConfiguracionesGrupo();
            if (grupos.isEmpty()) {
                avisar("Todavía no hay grupos guardados.");
                return;
            }

            String[] nombres = new String[grupos.size()];
            for (int i = 0; i < grupos.size(); i++) {
                nombres[i] = grupos.get(i).getNombre();
            }
            String seleccionado = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona el grupo que deseas cargar:",
                    "Cargar grupo",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    nombres,
                    nombres[0]);
            if (seleccionado == null) {
                return;
            }

            for (ConfiguracionGrupo grupo : grupos) {
                if (grupo.getNombre().equals(seleccionado)) {
                    campoGrupo.setText(grupo.getNombre());
                    campoEstudiantes.setValue(grupo.getCantidadEstudiantes());
                    campoNotas.setValue(grupo.getCantidadNotas());
                    crearTabla();
                    avisar("Grupo cargado correctamente. Puedes ingresar sus notas.");
                    return;
                }
            }
        } catch (IOException ex) {
            avisar("No se pudo cargar los grupos: " + ex.getMessage());
        }
    }

    private void guardarNotas() {
        if (!controlador.hayResultados()) {
            avisar("Primero calcula los promedios.");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Hoja de cálculo ODS (*.ods)", "ods"));
        selector.setSelectedFile(new File("promedios.ods"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.guardarResultados(conExtension(selector.getSelectedFile(), ".ods"));
                avisar("Notas y promedios guardados correctamente.");
            } catch (IOException ex) {
                avisar("No se pudo guardar: " + ex.getMessage());
            }
        }
    }

    private File conExtension(File archivo, String extension) {
        if (archivo.getName().toLowerCase(Locale.ROOT).endsWith(extension)) {
            return archivo;
        }
        return new File(archivo.getParentFile(), archivo.getName() + extension);
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
