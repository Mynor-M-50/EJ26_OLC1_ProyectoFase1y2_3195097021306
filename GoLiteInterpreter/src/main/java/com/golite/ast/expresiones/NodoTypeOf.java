package com.golite.ast.expresiones;

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
        if (val instanceof java.util.Map) {
            java.util.Map<?, ?> m = (java.util.Map<?, ?>) val;
            Object tipo = m.get("__tipo__");
            if (tipo != null) return tipo.toString();
            return "struct";
        }
        if (val instanceof java.util.List) {
            java.util.List<?> lista = (java.util.List<?>) val;
            if (!lista.isEmpty()) {
                Object primero = lista.get(0);
                if (primero instanceof Integer)   return "[]int";
                if (primero instanceof Double)    return "[]float64";
                if (primero instanceof String)    return "[]string";
                if (primero instanceof Boolean)   return "[]bool";
                if (primero instanceof Character) return "[]rune";
                if (primero instanceof java.util.List) return "[][]int";
            }
            return "[]";
        }
        if (val == null) return "nil";
        return val.getClass().getSimpleName();
    }
}