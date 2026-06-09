package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;

public class NodoContinue extends NodoAST {
    public NodoContinue(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object interpretar() {
        throw new ContinueException();
    }
}