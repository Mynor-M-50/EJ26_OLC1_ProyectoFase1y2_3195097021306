package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoLen extends NodoAST {
    public NodoAST expresion;

    public NodoLen(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = expresion.interpretar();
        if (val instanceof List)
            return ((List<?>) val).size();
        if (val instanceof String)
            return ((String) val).length();
        String msg = "len: argumento invalido, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }
}