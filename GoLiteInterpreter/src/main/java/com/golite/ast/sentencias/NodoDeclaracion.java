package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;

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

        if (valor != null) {
            val = valor.interpretar();
        } else {
            switch (tipo) {
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
                    val = null;
                    break;
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

        Entorno.getInstancia().declarar(nombre, tipoFinal, val, linea, columna);
        return null;
    }
}