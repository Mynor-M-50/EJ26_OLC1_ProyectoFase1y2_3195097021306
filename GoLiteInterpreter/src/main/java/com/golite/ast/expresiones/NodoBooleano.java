package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoBooleano extends NodoAST {
    public boolean valor;

    public NodoBooleano(boolean valor, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        return valor;
    }
}