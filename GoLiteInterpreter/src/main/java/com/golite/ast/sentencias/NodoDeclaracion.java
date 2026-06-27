package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import java.util.ArrayList;

public class NodoDeclaracion extends NodoAST {
    public String nombre;
    public String tipo;       // "int", "float64", "string", "bool", "rune" — null si es :=
    public NodoAST valor;     // null si no tiene valor inicial

    public NodoDeclaracion(String nombre, String tipo, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        Object val = null;
        boolean errorEnValor = false;

        if (valor != null) {
            try {
                val = valor.interpretar();
                // Copia independiente de slices
                if (val instanceof java.util.ArrayList) {
                    val = new java.util.ArrayList<>((java.util.ArrayList<?>) val);
                }
            } catch (RuntimeException e) {
                errorEnValor = true;
                String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                boolean yaRegistrado = com.golite.interpreter.Interprete.getInstancia()
                        .getErrores().stream().anyMatch(err -> err.descripcion.equals(msg));
                if (!yaRegistrado)
                    com.golite.interpreter.Interprete.getInstancia()
                            .agregarError(msg, linea, columna, "semantico");
            }
        }

        // Inferir tipo si viene de :=
        String tipoFinal = tipo;
        if (tipoFinal == null && val != null) {
            if (val instanceof Integer) tipoFinal = "int";
            else if (val instanceof Double) tipoFinal = "float64";
            else if (val instanceof String) tipoFinal = "string";
            else if (val instanceof Boolean) tipoFinal = "bool";
            else if (val instanceof Character) tipoFinal = "rune";
        }

        // Si hubo error en el valor, usar el valor por defecto del tipo
        if (errorEnValor || val == null) {
            if (tipoFinal == null) tipoFinal = "int";
            switch (tipoFinal) {
                case "int":
                    val = 0;
                    break;
                case "float64":
                    val = 0.0;
                    break;
                case "string":
                    val = "";
                    break;
                case "bool":
                    val = false;
                    break;
                case "rune":
                    val = '\0';
                    break;
                default:
                    // Slices y structs — valor por defecto es nil
                    val = null;
                    break;
            }
        }

        // Siempre declarar la variable para evitar errores en cascada
        try {
            Entorno.getInstancia().declarar(nombre, tipoFinal, val, linea, columna);
        } catch (RuntimeException e) {
            // Ya fue reportado por Entorno.declarar (variable duplicada)
        }
        return null;
    }
}