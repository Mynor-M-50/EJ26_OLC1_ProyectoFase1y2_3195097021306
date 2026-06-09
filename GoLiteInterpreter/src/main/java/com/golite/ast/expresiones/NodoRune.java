package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoRune extends NodoAST {
    public char valor;

    public NodoRune(char valor, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        return valor;
    }
}