package vista;

import controlador.PromedioControlador;
import modelo.Grupo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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
        JButton botonGuardar = new JButton("Guardar JSON");
        botonGuardar.addActionListener(e -> guardar());
        abajo.add(botonCalcular);
        abajo.add(botonGuardar);
        abajo.add(etiquetaPromedioGrupo);
        add(abajo, BorderLayout.SOUTH);

        setSize(700, 450);
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

    private void guardar() {
        if (!controlador.hayResultados()) {
            avisar("Primero calcula los promedios.");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("promedios.json"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.guardar(selector.getSelectedFile());
                avisar("Archivo guardado correctamente.");
            } catch (IOException ex) {
                avisar("No se pudo guardar: " + ex.getMessage());
            }
        }
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
