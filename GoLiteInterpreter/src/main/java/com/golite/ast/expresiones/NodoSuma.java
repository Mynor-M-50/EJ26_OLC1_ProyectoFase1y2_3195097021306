package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoSuma extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha;

    public NodoSuma(NodoAST izquierda, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    public Object interpretar() {
        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        if (izq == null || der == null) {
            String msg = "Operacion invalida sobre nil, linea " + linea;
            com.golite.interpreter.Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        // int + int
        if (izq instanceof Integer && der instanceof Integer)
            return (Integer) izq + (Integer) der;

        // float + float o combinaciones numericas
        if ((izq instanceof Double || izq instanceof Integer) &&
                (der instanceof Double || der instanceof Integer)) {
            double a = izq instanceof Integer ? ((Integer) izq).doubleValue() : (Double) izq;
            double b = der instanceof Integer ? ((Integer) der).doubleValue() : (Double) der;
            return a + b;
        }

        // string + cualquier cosa
        if (izq instanceof String || der instanceof String)
            return String.valueOf(izq) + String.valueOf(der);

        throw new RuntimeException("Operacion invalida en suma, linea " + linea);
    }
}