package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;

public class NodoBreak extends NodoAST {
    public NodoBreak(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object interpretar() {
        if (!com.golite.interpreter.Interprete.getInstancia().enCiclo()) {
            String msg = "Sentencia break fuera de un ciclo, linea " + linea;
            com.golite.interpreter.Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        throw new BreakException();
    }
}