package com.golite.ast;

public abstract class NodoAST {
    public int linea;
    public int columna;

    public NodoAST(int linea, int columna) {
        this.linea = linea;
        this.columna = columna;
    }

    public NodoAST() {
        this.linea = 0;
        this.columna = 0;
    }

    // Cada nodo va a saber ejecutarse a si mismo
    public abstract Object interpretar();
}
