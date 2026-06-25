package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoSlicesIndex extends NodoAST {
    public NodoAST slice;
    public NodoAST valor;

    public NodoSlicesIndex(NodoAST slice, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.slice = slice;
        this.valor = valor;
    }

    @Override
    public Object interpretar() {
        Object s = slice.interpretar();
        Object v = valor.interpretar();

        if (!(s instanceof List)) {
            String msg = "slices.Index: primer argumento no es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> lista = (List<Object>) s;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) != null && lista.get(i).equals(v))
                return i;
        }
        return -1;
    }
}