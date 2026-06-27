package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoNil extends NodoAST {

    public NodoNil(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object interpretar() {
        return null;
    }
}