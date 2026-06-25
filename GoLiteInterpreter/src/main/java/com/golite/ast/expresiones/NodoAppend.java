package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.ArrayList;
import java.util.List;

public class NodoAppend extends NodoAST {
    public NodoAST slice;
    public NodoAST valor;

    public NodoAppend(NodoAST slice, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.slice = slice;
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        Object s = slice.interpretar();
        Object v = valor.interpretar();

        if (!(s instanceof List)) {
            String msg = "append: primer argumento no es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> nueva = new ArrayList<>((List<Object>) s);
        nueva.add(v);
        return nueva;
    }
}