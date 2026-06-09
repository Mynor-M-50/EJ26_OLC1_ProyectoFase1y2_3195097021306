package com.golite.interpreter;

import com.golite.ast.NodoAST;
import com.golite.ast.sentencias.NodoPrograma;
import com.golite.errors.ErrorSemantic;

import java.util.ArrayList;
import java.util.List;

public class Interprete {

    private static Interprete instancia;
    private List<ErrorSemantic> errores;
    private List<String[]> tablaTokens;
    private StringBuilder consola;

    public static Interprete getInstancia() {
        if (instancia == null)
            instancia = new Interprete();
        return instancia;
    }

    public static void resetear() {
        instancia = new Interprete();
        Entorno.resetear();
    }

    private Interprete() {
        errores     = new ArrayList<>();
        tablaTokens = new ArrayList<>();
        consola     = new StringBuilder();
    }

    // Ejecutar el programa completo
    public String ejecutar(NodoPrograma programa) {
        consola = new StringBuilder();
        Entorno.resetear();

        try {
            programa.interpretar();
        } catch (RuntimeException e) {
            consola.append("Error: ").append(e.getMessage()).append("\n");
        }

        return consola.toString();
    }

    // Agregar línea a la consola
    public void agregarConsola(String texto) {
        consola.append(texto).append("\n");
    }

    // Manejo de errores
    public void agregarError(String descripcion, int linea, int columna, String tipo) {
        errores.add(new ErrorSemantic(descripcion, linea, columna, tipo));
    }

    public List<ErrorSemantic> getErrores() {
        return errores;
    }

    public void limpiarErrores() {
        errores = new ArrayList<>();
    }

    // Tokens
    public void agregarToken(String lexema, String tipo, int linea, int columna) {
        tablaTokens.add(new String[]{lexema, tipo, String.valueOf(linea), String.valueOf(columna)});
    }

    public List<String[]> getTablaTokens() {
        return tablaTokens;
    }

    public void limpiarTokens() {
        tablaTokens = new ArrayList<>();
    }
}