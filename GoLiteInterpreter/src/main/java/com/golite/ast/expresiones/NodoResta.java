package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoResta extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha;

    public NodoResta(NodoAST izquierda, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public Object interpretar() {
        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        if (izq instanceof Integer && der instanceof Integer)
            return (Integer) izq - (Integer) der;

        if (izq instanceof Double || der instanceof Double) {
            double a = izq instanceof Integer ? ((Integer) izq).doubleValue() : (Double) izq;
            double b = der instanceof Integer ? ((Integer) der).doubleValue() : (Double) der;
            return a - b;
        }

        throw new RuntimeException("Operacion invalida en resta, linea " + linea);
    }
}