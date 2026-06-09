package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;

public class NodoTypeOf extends NodoAST {
    public NodoAST expresion;

    public NodoTypeOf(NodoAST expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object interpretar() {
        Object val = expresion.interpretar();
        if (val instanceof Integer)   return "int";
        if (val instanceof Double)    return "float64";
        if (val instanceof String)    return "string";
        if (val instanceof Boolean)   return "bool";
        if (val instanceof Character) return "rune";
        if (val == null)              return "nil";
        return val.getClass().getSimpleName();
    }
}