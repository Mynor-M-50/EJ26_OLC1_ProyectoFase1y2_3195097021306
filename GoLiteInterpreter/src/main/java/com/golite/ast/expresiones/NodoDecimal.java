package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoDecimal extends NodoAST {
    public double valor;

    public NodoDecimal(double valor, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        return valor;
    }
}