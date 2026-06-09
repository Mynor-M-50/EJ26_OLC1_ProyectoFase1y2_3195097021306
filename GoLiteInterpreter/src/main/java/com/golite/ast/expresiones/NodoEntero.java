package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoEntero extends NodoAST {
    public int valor;

    public NodoEntero(int valor, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        return valor;
    }
}