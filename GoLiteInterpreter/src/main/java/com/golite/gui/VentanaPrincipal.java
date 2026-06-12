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

    // Cada tab tiene su propio editor y archivo
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
        // Abrir con una tab vacía por defecto
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

        // ── TABS ──
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(45, 45, 45));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.addChangeListener(e -> actualizarStatusBar());

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
                "Consola", 0, 0, null, Color.GRAY));
        consolaScroll.setPreferredSize(new Dimension(0, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, consolaScroll);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);

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
        itemNuevo.addActionListener(e -> nuevaTab());

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemGuardarComo = new JMenuItem("Guardar como...");
        itemGuardarComo.addActionListener(e -> guardarComo());

        JMenuItem itemCerrarTab = new JMenuItem("Cerrar pestaña");
        itemCerrarTab.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
        itemCerrarTab.addActionListener(e -> cerrarTabActual());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrarTab);

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

    // ── GESTIÓN DE TABS ──

    private void nuevaTab() {
        Tab tab = new Tab();
        agregarTab(tab);
    }

    private void agregarTab(Tab tab) {
        JScrollPane scroll = tab.crearScroll();

        // Actualizar números de línea y statusbar al escribir
        tab.editorArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarNumeroLineas(tab);
                actualizarStatusBar();
            }
        });

        tabbedPane.addTab(tab.getNombre(), scroll);
        // Guardar la tab en el componente para recuperarla después
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
            // Si es la última tab, limpiarla en lugar de cerrarla
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

    // ── ACCIONES ──

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
                if (Interprete.getInstancia().getErrores().isEmpty()) {
                    consolaArea.append("\n✓ Ejecución completada.\n");
                } else {
                    consolaArea.append("\n⚠ Ejecución con " + Interprete.getInstancia().getErrores().size() + " error(es).\n");
                }
            }
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
            consolaArea.append("✗ Error: " + msg + "\n");
            Interprete.getInstancia().agregarError(msg, 0, 0, "semántico");
        } catch (Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
            consolaArea.append("✗ Error: " + msg + "\n");
            Interprete.getInstancia().agregarError(msg, 0, 0, "sintáctico");
        }
    }

    // ── REPORTES ──

    private void mostrarTokens() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s| %-20s| %-16s| %-6s| %s%n",
                "No.", "Lexema", "Tipo", "Línea", "Columna"));
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
                "No.", "Descripción", "Línea", "Col", "Tipo"));
        sb.append(String.format("%-5s|%-36s|%-7s|%-6s|%s%n",
                "-----", "-----------------------------------", "-------", "------", "----------"));

        if (Interprete.getInstancia().getErrores().isEmpty()) {
            sb.append("\n  ✓ No se encontraron errores.\n");
        } else {
            int i = 1;
            for (var error : Interprete.getInstancia().getErrores()) {
                sb.append(String.format("%-5d| %-35s| %-6d| %-5d| %s%n",
                        i++, error.descripcion, error.linea, error.columna, error.tipo));
            }
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
            statusBar.setText("  Listo  |  Línea: " + line + "  Col: " + col);
        } catch (Exception ignored) {}
    }
}