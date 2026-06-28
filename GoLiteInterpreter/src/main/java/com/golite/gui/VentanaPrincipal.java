package com.golite.gui;

import com.golite.interpreter.Interprete;
import com.golite.lexer.Lexer;
import com.golite.parser.Parser;
import com.golite.ast.sentencias.NodoPrograma;
import com.golite.errors.ErrorSemantic;
import com.golite.reports.GeneradorAST;
import com.golite.reports.GeneradorSimbolos;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class VentanaPrincipal extends JFrame {

    private static class Tab {
        JTextArea editorArea;
        JTextArea lineNumbers;
        File archivo;

        Tab() {
            editorArea = new JTextArea();
            editorArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            editorArea.setBackground(new Color(30, 30, 30));
            editorArea.setForeground(Color.WHITE);
            editorArea.setCaretColor(Color.WHITE);
            editorArea.setTabSize(4);

            lineNumbers = new JTextArea("1");
            lineNumbers.setBackground(new Color(45, 45, 45));
            lineNumbers.setForeground(new Color(150, 150, 150));
            lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
            lineNumbers.setEditable(false);
            lineNumbers.setMargin(new Insets(0, 5, 0, 5));

            archivo = null;
        }

        String getNombre() {
            return archivo != null ? archivo.getName() : "Nuevo archivo";
        }

        JScrollPane crearScroll() {
            JScrollPane scroll = new JScrollPane(editorArea);
            scroll.setRowHeaderView(lineNumbers);
            return scroll;
        }
    }

    private JTabbedPane tabbedPane;
    private JTextArea consolaArea;
    private JLabel statusBar;

    public VentanaPrincipal() {
        inicializarVentana();
        crearComponentes();
        crearMenu();
        nuevaTab();
    }

    private void inicializarVentana() {
        setTitle("GoLite IDE");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void crearComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(45, 45, 45));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.addChangeListener(e -> actualizarStatusBar());

        consolaArea = new JTextArea();
        consolaArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consolaArea.setBackground(new Color(20, 20, 20));
        consolaArea.setForeground(new Color(0, 255, 0));
        consolaArea.setEditable(false);
        consolaArea.setText("CONSOLA - GoLite IDE\n");

        JScrollPane consolaScroll = new JScrollPane(consolaArea);
        consolaScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Consola", 0, 0, null, Color.GRAY));
        consolaScroll.setPreferredSize(new Dimension(0, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, consolaScroll);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);

        statusBar = new JLabel("  Listo  |  Linea: 1  Col: 1");
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(statusBar, BorderLayout.SOUTH);
        setContentPane(panelPrincipal);
    }

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        itemNuevo.addActionListener(e -> nuevaTab());

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemGuardarComo = new JMenuItem("Guardar como...");
        itemGuardarComo.addActionListener(e -> guardarComo());

        JMenuItem itemCerrarTab = new JMenuItem("Cerrar pestania");
        itemCerrarTab.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
        itemCerrarTab.addActionListener(e -> cerrarTabActual());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrarTab);

        JMenu menuEjecutar = new JMenu("Ejecutar");

        JMenuItem itemEjecutar = new JMenuItem("Ejecutar");
        itemEjecutar.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemEjecutar.addActionListener(e -> ejecutarCodigo());

        JMenuItem itemLimpiar = new JMenuItem("Limpiar consola");
        itemLimpiar.addActionListener(e -> consolaArea.setText("CONSOLA - GoLite IDE\n"));

        menuEjecutar.add(itemEjecutar);
        menuEjecutar.add(itemLimpiar);

        JMenu menuReportes = new JMenu("Reportes");

        JMenuItem itemTokens = new JMenuItem("Tabla de Tokens");
        itemTokens.addActionListener(e -> mostrarTokens());

        JMenuItem itemErrores = new JMenuItem("Reporte de Errores");
        itemErrores.addActionListener(e -> mostrarErrores());

        JMenuItem itemSimbolos = new JMenuItem("Tabla de Simbolos");
        itemSimbolos.addActionListener(e -> mostrarSimbolos());

        JMenuItem itemAST = new JMenuItem("Reporte AST");
        itemAST.addActionListener(e -> mostrarAST());

        menuReportes.add(itemTokens);
        menuReportes.add(itemErrores);
        menuReportes.add(itemSimbolos);
        menuReportes.add(itemAST);

        menuBar.add(menuArchivo);
        menuBar.add(menuEjecutar);
        menuBar.add(menuReportes);
        setJMenuBar(menuBar);
    }

    private void nuevaTab() {
        Tab tab = new Tab();
        agregarTab(tab);
    }

    private void agregarTab(Tab tab) {
        JScrollPane scroll = tab.crearScroll();

        tab.editorArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarNumeroLineas(tab);
                actualizarStatusBar();
            }
        });

        tabbedPane.addTab(tab.getNombre(), scroll);
        scroll.putClientProperty("tab", tab);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        setTitle("GoLite IDE - " + tab.getNombre());
    }

    private Tab getTabActual() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == -1) return null;
        JScrollPane scroll = (JScrollPane) tabbedPane.getComponentAt(idx);
        return (Tab) scroll.getClientProperty("tab");
    }

    private void cerrarTabActual() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == -1) return;
        if (tabbedPane.getTabCount() == 1) {
            Tab tab = getTabActual();
            if (tab != null) {
                tab.editorArea.setText("");
                tab.archivo = null;
                tabbedPane.setTitleAt(0, "Nuevo archivo");
                setTitle("GoLite IDE - Nuevo archivo");
            }
            return;
        }
        tabbedPane.removeTabAt(idx);
        setTitle("GoLite IDE - " + (getTabActual() != null ? getTabActual().getNombre() : ""));
    }

    private void nuevoArchivo() {
        nuevaTab();
    }

    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("GoLite (*.glt)", "glt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fc.getSelectedFile();
            try {
                String contenido = new String(Files.readAllBytes(archivo.toPath()));
                Tab tab = new Tab();
                tab.archivo = archivo;
                tab.editorArea.setText(contenido);
                agregarTab(tab);
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), tab.getNombre());
                setTitle("GoLite IDE - " + tab.getNombre());
            } catch (IOException ex) {
                mostrarError("Error al abrir: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivo() {
        Tab tab = getTabActual();
        if (tab == null) return;
        if (tab.archivo == null) {
            guardarComo();
        } else {
            try {
                Files.write(tab.archivo.toPath(), tab.editorArea.getText().getBytes());
            } catch (IOException ex) {
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void guardarComo() {
        Tab tab = getTabActual();
        if (tab == null) return;
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("GoLite (*.glt)", "glt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            tab.archivo = fc.getSelectedFile();
            if (!tab.archivo.getName().endsWith(".glt"))
                tab.archivo = new File(tab.archivo.getAbsolutePath() + ".glt");
            guardarArchivo();
            int idx = tabbedPane.getSelectedIndex();
            tabbedPane.setTitleAt(idx, tab.getNombre());
            setTitle("GoLite IDE - " + tab.getNombre());
        }
    }

    private void ejecutarCodigo() {
        Tab tab = getTabActual();
        if (tab == null) return;

        String codigo = tab.editorArea.getText().trim();
        if (codigo.isEmpty()) {
            consolaArea.append("\nNo hay codigo para ejecutar.\n");
            return;
        }

        consolaArea.append("\n ▶ Ejecutando...\n");
        Interprete.resetear();

        try {
            Lexer lexer = new Lexer(new java.io.StringReader(codigo));
            Parser parser = new Parser(lexer);
            java_cup.runtime.Symbol sym = parser.parse();
            Object parseResult = (sym != null) ? sym.value : null;
            NodoPrograma programa = null;
            if (parseResult instanceof NodoPrograma) {
                programa = (NodoPrograma) parseResult;
            }

            if (programa != null) {
                String resultado = Interprete.getInstancia().ejecutar(programa);
                consolaArea.append(resultado);
            } else {
                consolaArea.append("\n No se pudo ejecutar debido a errores en el codigo.\n");
            }

            List<ErrorSemantic> errores = Interprete.getInstancia().getErrores();
            Set<Integer> lineasLexicas = errores.stream()
                    .filter(e -> e.tipo.toLowerCase().contains("lex"))
                    .map(e -> e.linea)
                    .collect(java.util.stream.Collectors.toSet());
            errores.removeIf(e -> e.tipo.toLowerCase().contains("sint")
                    && lineasLexicas.contains(e.linea));

            if (errores.isEmpty()) {
                consolaArea.append("\n Ejecucion completada.\n");
            } else {
                consolaArea.append("\n Ejecucion con " + errores.size() + " error(es).\n");
            }

        } catch (RuntimeException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
            consolaArea.append("Error: " + msg + "\n");
            Interprete.getInstancia().agregarError(msg, 0, 0, "semantico");
        } catch (Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
            consolaArea.append("Error: " + msg + "\n");
            Interprete.getInstancia().agregarError(msg, 0, 0, "sintactico");
        }
    }

    // REPORTES

    private void mostrarTokens() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s| %-20s| %-16s| %-6s| %s%n",
                "No.", "Lexema", "Tipo", "Linea", "Columna"));
        sb.append(String.format("%-5s|%-21s|%-17s|%-7s|%s%n",
                "-----", "--------------------", "-----------------", "-------", "--------"));
        int i = 1;
        for (String[] token : Interprete.getInstancia().getTablaTokens()) {
            sb.append(String.format("%-5d| %-20s| %-16s| %-6s| %s%n",
                    i++, token[0], token[1], token[2], token[3]));
        }
        mostrarReporte("Tabla de Tokens", sb.toString());
    }

    private void mostrarErrores() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s| %-35s| %-6s| %-5s| %s%n",
                "No.", "Descripcion", "Linea", "Col", "Tipo"));
        sb.append(String.format("%-5s|%-36s|%-7s|%-6s|%s%n",
                "-----", "-----------------------------------", "-------", "------", "----------"));

        if (Interprete.getInstancia().getErrores().isEmpty()) {
            sb.append("\n   No se encontraron errores.\n");
        } else {
            int i = 1;
            for (var error : Interprete.getInstancia().getErrores()) {
                sb.append(String.format("%-5d| %-35s| %-6d| %-5d| %s%n",
                        i++, error.descripcion, error.linea, error.columna, error.tipo));
            }
        }
        mostrarReporte("Reporte de Errores", sb.toString());
    }

    private void mostrarSimbolos() {
        String contenido = GeneradorSimbolos.generar();
        mostrarReporte("Tabla de Simbolos", contenido);
    }

    private void mostrarAST() {
        NodoPrograma prog = Interprete.getInstancia().getUltimoPrograma();
        if (prog == null) {
            mostrarReporte("Reporte AST", "Ejecuta el codigo primero para generar el AST.");
            return;
        }
        String dot = new GeneradorAST().generar(prog);
        mostrarReporteDOT("Reporte AST", dot);
    }

    private void mostrarReporteDOT(String titulo, String dot) {
        java.io.File carpeta = new java.io.File("reportes");
        carpeta.mkdirs();

        java.io.File archivoDot = new java.io.File(carpeta, "ast.dot");
        java.io.File archivoPng = new java.io.File(carpeta, "ast.png");
        java.io.File archivoSvg = new java.io.File(carpeta, "ast.svg");

        // Escribir el .dot
        try {
            java.nio.file.Files.writeString(archivoDot.toPath(), dot);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al escribir .dot: " + ex.getMessage());
            return;
        }

        // Generar PNG
        boolean pngGenerado = false;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "dot", "-Tpng",
                    archivoDot.getAbsolutePath(),
                    "-o", archivoPng.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            proc.waitFor();
            pngGenerado = archivoPng.exists() && archivoPng.length() > 0;
        } catch (Exception ex) {
            // dot no disponible
        }

        // Generar SVG
        boolean svgGenerado = false;
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "dot", "-Tsvg",
                    archivoDot.getAbsolutePath(),
                    "-o", archivoSvg.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            proc.waitFor();
            svgGenerado = archivoSvg.exists() && archivoSvg.length() > 0;
        } catch (Exception ex) {
            // dot no disponible
        }

        // Construir ventana
        JFrame frame = new JFrame(titulo);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(this);

        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: imagen PNG
        if (pngGenerado) {
            ImageIcon icono = new ImageIcon(archivoPng.getAbsolutePath());
            JLabel lblImg = new JLabel(icono);
            JScrollPane scrollImg = new JScrollPane(lblImg);
            tabs.addTab("Imagen AST (PNG)", scrollImg);
        }

        // Tab 2: SVG en navegador
        if (svgGenerado) {
            JPanel panelSvg = new JPanel(new BorderLayout());
            JLabel lblSvg = new JLabel("SVG generado. Abrelo en tu navegador para mejor visualizacion.", SwingConstants.CENTER);
            lblSvg.setFont(new Font("Monospaced", Font.PLAIN, 13));
            JButton btnAbrirSvg = new JButton("Abrir AST en navegador (SVG)");
            btnAbrirSvg.addActionListener(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(archivoSvg.toURI());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "No se pudo abrir: " + ex.getMessage());
                }
            });
            JPanel centro = new JPanel();
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
            lblSvg.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAbrirSvg.setAlignmentX(Component.CENTER_ALIGNMENT);
            centro.add(Box.createVerticalGlue());
            centro.add(lblSvg);
            centro.add(Box.createRigidArea(new Dimension(0, 15)));
            centro.add(btnAbrirSvg);
            centro.add(Box.createVerticalGlue());
            panelSvg.add(centro, BorderLayout.CENTER);
            tabs.addTab("Imagen AST (SVG)", panelSvg);
        }

        // Tab 3: codigo DOT
        JTextArea area = new JTextArea(dot);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        tabs.addTab("Codigo DOT", new JScrollPane(area));

        // Panel inferior
        JPanel panelBtn = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        if (!pngGenerado && !svgGenerado) {
            panelBtn.add(new JLabel("Pega el DOT en: https://dreampuf.github.io/GraphvizOnline"));
        }
        JButton btnCopiar = new JButton("Copiar DOT");
        btnCopiar.addActionListener(e -> {
            java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(dot);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
            JOptionPane.showMessageDialog(frame, "Contenido DOT copiado al portapapeles.");
        });
        JButton btnAbrir = new JButton("Abrir carpeta reportes");
        btnAbrir.addActionListener(e -> {
            try {
                java.awt.Desktop.getDesktop().open(carpeta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "No se pudo abrir: " + ex.getMessage());
            }
        });
        panelBtn.add(btnCopiar);
        panelBtn.add(btnAbrir);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        panel.add(panelBtn, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setVisible(true);
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

    private void actualizarNumeroLineas(Tab tab) {
        int lines = tab.editorArea.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++)
            sb.append(i).append("\n");
        tab.lineNumbers.setText(sb.toString());
    }

    private void actualizarStatusBar() {
        Tab tab = getTabActual();
        if (tab == null) return;
        try {
            int pos = tab.editorArea.getCaretPosition();
            int line = tab.editorArea.getLineOfOffset(pos) + 1;
            int col = pos - tab.editorArea.getLineStartOffset(line - 1) + 1;
            statusBar.setText("  Listo  |  Linea: " + line + "  Col: " + col);
        } catch (Exception ignored) {}
    }
}