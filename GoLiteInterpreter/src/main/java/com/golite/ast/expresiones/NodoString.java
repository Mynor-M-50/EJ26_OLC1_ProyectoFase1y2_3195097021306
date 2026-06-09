package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoString extends NodoAST {
    public String valor;

    public NodoString(String valor, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        return valor;
    }
}