package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoNegacion extends NodoAST {
    public NodoAST expresion;

    public NodoNegacion(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = expresion.interpretar();

        if (val instanceof Integer) return -(Integer) val;
        if (val instanceof Double)  return -(Double) val;

        throw new RuntimeException("Negación invlida, linea " + linea);
    }
}