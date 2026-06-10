package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoAtoi extends NodoAST {
    public NodoAST expresion;

    public NodoAtoi(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = expresion.interpretar();
        if (!(val instanceof String))
            throw new RuntimeException("strconv.Atoi requiere un string, linea " + linea);
        try {
            return Integer.parseInt((String) val);
        } catch (NumberFormatException e) {
            throw new RuntimeException("strconv.Atoi: no se puede convertir '" + val + "' a int, linea " + linea);
        }
    }
}