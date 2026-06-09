package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;

public class NodoBreak extends NodoAST {
    public NodoBreak(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object interpretar() {
        throw new BreakException();
    }
}