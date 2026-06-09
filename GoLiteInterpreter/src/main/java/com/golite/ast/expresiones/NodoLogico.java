package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoLogico extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha; // null si es NOT
    public String operador; // "&&", "||", "!"

    public NodoLogico(NodoAST izquierda, String operador, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.operador  = operador;
        this.derecha   = derecha;
    }

    @Override
    public Object interpretar() {
        if (operador.equals("!")) {
            Object val = izquierda.interpretar();
            if (val instanceof Boolean) return !(Boolean) val;
            throw new RuntimeException("NOT requiere booleano, línea " + linea);
        }

        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        if (!(izq instanceof Boolean) || !(der instanceof Boolean))
            throw new RuntimeException("Operación logica requiere booleanos, linea " + linea);

        if (operador.equals("&&")) return (Boolean) izq && (Boolean) der;
        if (operador.equals("||")) return (Boolean) izq || (Boolean) der;

        throw new RuntimeException("Operador logico desconocido: " + operador);
    }
}