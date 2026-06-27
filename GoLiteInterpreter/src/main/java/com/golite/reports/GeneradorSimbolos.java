package com.golite.reports;

import com.golite.ast.sentencias.NodoFuncion;
import com.golite.ast.sentencias.NodoFuncionStruct;
import com.golite.ast.sentencias.NodoStruct;
import com.golite.interpreter.Entorno;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneradorSimbolos {

    public static String generar() {
        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append(String.format("%-5s| %-20s| %-12s| %-15s| %-15s| %-6s| %s%n",
                "No.", "ID", "Tipo Simbolo", "Tipo Dato", "Ambito", "Linea", "Columna"));
        sb.append(repeatChar('-', 5)).append("|")
                .append(repeatChar('-', 21)).append("|")
                .append(repeatChar('-', 13)).append("|")
                .append(repeatChar('-', 16)).append("|")
                .append(repeatChar('-', 16)).append("|")
                .append(repeatChar('-', 7)).append("|")
                .append(repeatChar('-', 8)).append("%n".formatted());

        int no = 1;

        // 1. Structs globales
        for (Map.Entry<String, NodoStruct> e : Entorno.getStructs().entrySet()) {
            NodoStruct s = e.getValue();
            sb.append(String.format("%-5d| %-20s| %-12s| %-15s| %-15s| %-6d| %d%n",
                    no++, e.getKey(), "Struct", e.getKey(), "Global", s.linea, s.columna));
        }

        // 2. Funciones globales
        for (Map.Entry<String, NodoFuncion> e : Entorno.getFunciones().entrySet()) {
            NodoFuncion f = e.getValue();
            String tipoDato = f.tipoRetorno != null ? f.tipoRetorno : "void";
            sb.append(String.format("%-5d| %-20s| %-12s| %-15s| %-15s| %-6d| %d%n",
                    no++, e.getKey(), "Funcion", tipoDato, "Global", f.linea, f.columna));
        }

        // 3. Variables del entorno actual (post-ejecucion)
        Map<String, Entorno.Variable> vars = new LinkedHashMap<>();
        Entorno.getInstancia().recolectarVariables(vars);
        for (Map.Entry<String, Entorno.Variable> e : vars.entrySet()) {
            Entorno.Variable v = e.getValue();
            String tipoDato = v.tipo != null ? v.tipo : "auto";
            sb.append(String.format("%-5d| %-20s| %-12s| %-15s| %-15s| %-6d| %d%n",
                    no++, e.getKey(), "Variable", tipoDato,
                    v.ambito != null ? v.ambito : "Global",
                    v.linea, v.columna));
        }

        if (no == 1)
            sb.append("\n   No hay simbolos registrados.\n");

        return sb.toString();
    }

    private static String repeatChar(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }
}