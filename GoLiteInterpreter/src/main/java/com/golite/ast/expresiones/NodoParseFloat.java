package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoParseFloat extends NodoAST {
    public NodoAST expresion;

    public NodoParseFloat(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = expresion.interpretar();
        if (!(val instanceof String))
            throw new RuntimeException("strconv.ParseFloat requiere un string, linea " + linea);
        try {
            return Double.parseDouble((String) val);
        } catch (NumberFormatException e) {
            String msg = "strconv.ParseFloat: no se puede convertir '" + val + "', línea " + linea;
            com.golite.interpreter.Interprete.getInstancia().agregarError(msg, linea, columna, "semántico");
            throw new RuntimeException(msg);
        }
    }
}