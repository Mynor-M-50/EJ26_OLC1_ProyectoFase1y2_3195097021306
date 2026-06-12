package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoModulo extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha;

    public NodoModulo(NodoAST izquierda, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public Object interpretar() {
        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        if (izq instanceof Integer && der instanceof Integer) {
            if ((Integer) der == 0) {
                String msg = "Modulo por cero, linea " + linea;
                com.golite.interpreter.Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
                throw new RuntimeException(msg);
            }
            return (Integer) izq % (Integer) der;
        }

        throw new RuntimeException("Modulo solo aplica a enteros, linea " + linea);
    }
}