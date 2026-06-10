package com.golite.gui;

import com.golite.interpreter.Interprete;
import com.golite.lexer.Lexer;
import com.golite.parser.Parser;
import com.golite.ast.sentencias.NodoPrograma;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class VentanaPrincipal extends JFrame {

    // Componentes principales
    private JTextArea editorArea;
    private JTextArea consolaArea;
    private JLabel statusBar;
    private File archivoActual;

    public VentanaPrincipal() {
        inicializarVentana();
        crearComponentes();
        crearMenu();
    }

    private void inicializarVentana() {
        setTitle("GoLite IDE");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void crearComponentes() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // ── EDITOR ──
        editorArea = new JTextArea();
        editorArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editorArea.setBackground(new Color(30, 30, 30));
        editorArea.setForeground(Color.WHITE);
        editorArea.setCaretColor(Color.WHITE);
        editorArea.setTabSize(4);

        // Números de línea
        JTextArea lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(45, 45, 45));
        lineNumbers.setForeground(new Color(150, 150, 150));
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumbers.setEditable(false);
        lineNumbers.setMargin(new Insets(0, 5, 0, 5));

        // Actualizar números de línea al escribir
        editorArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarNumeroLineas(lineNumbers);
                actualizarStatusBar();
            }
        });

        JScrollPane editorScroll = new JScrollPane(editorArea);
        editorScroll.setRowHeaderView(lineNumbers);
        editorScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Editor",
                0, 0, null, Color.GRAY
        ));

        // ── CONSOLA ──
        consolaArea = new JTextArea();
        consolaArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consolaArea.setBackground(new Color(20, 20, 20));
        consolaArea.setForeground(new Color(0, 255, 0));
        consolaArea.setEditable(false);
        consolaArea.setText("CONSOLA - GoLite IDE\n");

        JScrollPane consolaScroll = new JScrollPane(consolaArea);
        consolaScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Consola",
                0, 0, null, Color.GRAY
        ));
        consolaScroll.setPreferredSize(new Dimension(0, 200));

        // ── SPLIT PANE ──
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, editorScroll, consolaScroll
        );
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);

        // ── STATUS BAR ──
        statusBar = new JLabel("  Listo  |  Línea: 1  Col: 1");
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(statusBar, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // ── ARCHIVO ──
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        itemNuevo.addActionListener(e -> nuevoArchivo());

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemGuardarComo = new JMenuItem("Guardar como...");
        itemGuardarComo.addActionListener(e -> guardarComo());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);

        // ── EJECUTAR ──
        JMenu menuEjecutar = new JMenu("Ejecutar");

        JMenuItem itemEjecutar = new JMenuItem("Ejecutar");
        itemEjecutar.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemEjecutar.addActionListener(e -> ejecutarCodigo());

        JMenuItem itemLimpiar = new JMenuItem("Limpiar consola");
        itemLimpiar.addActionListener(e -> consolaArea.setText("CONSOLA - GoLite IDE\n"));

        menuEjecutar.add(itemEjecutar);
        menuEjecutar.add(itemLimpiar);

        // ── REPORTES ──
        JMenu menuReportes = new JMenu("Reportes");

        JMenuItem itemTokens = new JMenuItem("Tabla de Tokens");
        itemTokens.addActionListener(e -> mostrarTokens());

        JMenuItem itemErrores = new JMenuItem("Reporte de Errores");
        itemErrores.addActionListener(e -> mostrarErrores());

        menuReportes.add(itemTokens);
        menuReportes.add(itemErrores);

        menuBar.add(menuArchivo);
        menuBar.add(menuEjecutar);
        menuBar.add(menuReportes);

        setJMenuBar(menuBar);
    }

    // ── ACCIONES ──

    private void nuevoArchivo() {
        editorArea.setText("");
        archivoActual = null;
        setTitle("GoLite IDE - Nuevo archivo");
    }

    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("GoLite (*.glt)", "glt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoActual = fc.getSelectedFile();
            try {
                String contenido = new String(Files.readAllBytes(archivoActual.toPath()));
                editorArea.setText(contenido);
                setTitle("GoLite IDE - " + archivoActual.getName());
            } catch (IOException ex) {
                mostrarError("Error al abrir: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivo() {
        if (archivoActual == null) {
            guardarComo();
        } else {
            try {
                Files.write(archivoActual.toPath(), editorArea.getText().getBytes());
            } catch (IOException ex) {
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void guardarComo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("GoLite (*.glt)", "glt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoActual = fc.getSelectedFile();
            if (!archivoActual.getName().endsWith(".glt"))
                archivoActual = new File(archivoActual.getAbsolutePath() + ".glt");
            guardarArchivo();
            setTitle("GoLite IDE - " + archivoActual.getName());
        }
    }

    private void ejecutarCodigo() {
        String codigo = editorArea.getText().trim();
        if (codigo.isEmpty()) {
            consolaArea.append("\n⚠ No hay código para ejecutar.\n");
            return;
        }

        consolaArea.append("\n▶ Ejecutando...\n");
        Interprete.resetear();

        try {
            Lexer lexer = new Lexer(new java.io.StringReader(codigo));
            Parser parser = new Parser(lexer);
            NodoPrograma programa = (NodoPrograma) parser.parse().value;

            if (programa != null) {
                String resultado = Interprete.getInstancia().ejecutar(programa);
                consolaArea.append(resultado);
                consolaArea.append("\n✓ Ejecución completada.\n");
            }
        } catch (Exception ex) {
            consolaArea.append("✗ Error: " + ex.getMessage() + "\n");
        }
    }

    private void mostrarTokens() {
        StringBuilder sb = new StringBuilder();
        sb.append("No.  | Lexema          | Tipo            | Línea | Columna\n");
        sb.append("-----|-----------------|-----------------|-------|--------\n");
        int i = 1;
        for (String[] token : Interprete.getInstancia().getTablaTokens()) {
            sb.append(String.format("%-5d| %-16s| %-16s| %-6s| %s%n",
                    i++, token[0], token[1], token[2], token[3]));
        }
        mostrarReporte("Tabla de Tokens", sb.toString());
    }

    private void mostrarErrores() {
        StringBuilder sb = new StringBuilder();
        sb.append("No.  | Descripción                    | Línea | Col | Tipo\n");
        sb.append("-----|--------------------------------|-------|-----|----------\n");
        int i = 1;
        for (var error : Interprete.getInstancia().getErrores()) {
            sb.append(String.format("%-5d| %-31s| %-6d| %-4d| %s%n",
                    i++, error.descripcion, error.linea, error.columna, error.tipo));
        }
        mostrarReporte("Reporte de Errores", sb.toString());
    }

    private void mostrarReporte(String titulo, String contenido) {
        JFrame frame = new JFrame(titulo);
        JTextArea area = new JTextArea(contenido);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        frame.add(new JScrollPane(area));
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void actualizarNumeroLineas(JTextArea lineNumbers) {
        int lines = editorArea.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++)
            sb.append(i).append("\n");
        lineNumbers.setText(sb.toString());
    }

    private void actualizarStatusBar() {
        try {
            int pos = editorArea.getCaretPosition();
            int line = editorArea.getLineOfOffset(pos) + 1;
            int col  = pos - editorArea.getLineStartOffset(line - 1) + 1;
            statusBar.setText("  Listo  |  Línea: " + line + "  Col: " + col);
        } catch (Exception ignored) {}
    }
}