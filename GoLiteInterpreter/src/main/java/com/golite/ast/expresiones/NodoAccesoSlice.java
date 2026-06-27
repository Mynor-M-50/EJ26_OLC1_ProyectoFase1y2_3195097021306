package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoAccesoSlice extends NodoAST {
    public NodoAST slice;
    public NodoAST indice;

    public NodoAccesoSlice(NodoAST slice, NodoAST indice, int linea, int columna) {
        super(linea, columna);
        this.slice  = slice;
        this.indice = indice;
    }

    @Override
    public Object interpretar() {
        Object s = slice.interpretar();
        Object i = indice.interpretar();

        if (s == null) {
            String msg = "Operacion invalida sobre nil, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        if (!(s instanceof List)) {
            String msg = "No es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        if (!(i instanceof Integer)) {
            String msg = "Indice debe ser entero, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> lista = (List<Object>) s;
        int idx = (Integer) i;

        if (idx < 0 || idx >= lista.size()) {
            String msg = "Indice fuera de rango: " + idx + ", linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        return lista.get(idx);
    }
}