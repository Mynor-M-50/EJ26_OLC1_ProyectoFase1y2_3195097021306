package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoDivision extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha;

    public NodoDivision(NodoAST izquierda, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public Object interpretar() {
        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        if (izq instanceof Integer && der instanceof Integer) {
            if ((Integer) der == 0)
                throw new RuntimeException("División por cero, línea " + linea);
            return (Integer) izq / (Integer) der;
        }

        if (izq instanceof Double || der instanceof Double) {
            double a = izq instanceof Integer ? ((Integer) izq).doubleValue() : (Double) izq;
            double b = der instanceof Integer ? ((Integer) der).doubleValue() : (Double) der;
            if (b == 0) throw new RuntimeException("División por cero, línea " + linea);
            return a / b;
        }

        throw new RuntimeException("Operación inválida en división, línea " + linea);
    }
}