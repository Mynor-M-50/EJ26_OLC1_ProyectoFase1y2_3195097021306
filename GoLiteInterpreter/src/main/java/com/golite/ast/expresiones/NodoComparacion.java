package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;

public class NodoComparacion extends NodoAST {
    public NodoAST izquierda;
    public NodoAST derecha;
    public String operador; // "==", "!=", "<", ">", "<=", ">="

    public NodoComparacion(NodoAST izquierda, String operador, NodoAST derecha, int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.operador  = operador;
        this.derecha   = derecha;
    }

    @Override
    public Object interpretar() {
        Object izq = izquierda.interpretar();
        Object der = derecha.interpretar();

        switch (operador) {
            case "==": return izq.equals(der);
            case "!=": return !izq.equals(der);
            case "<":  return toDouble(izq) <  toDouble(der);
            case ">":  return toDouble(izq) >  toDouble(der);
            case "<=": return toDouble(izq) <= toDouble(der);
            case ">=": return toDouble(izq) >= toDouble(der);
            default:   throw new RuntimeException("Operador desconocido: " + operador);
        }
    }

    private double toDouble(Object val) {
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof Double)  return (Double) val;
        throw new RuntimeException("No se puede comparar tipo: " + val.getClass());
    }
}