package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;

public class NodoReturn extends NodoAST {
    public NodoAST expresion;

    public NodoReturn(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = (expresion != null) ? expresion.interpretar() : null;
        throw new ReturnException(val);
    }
}