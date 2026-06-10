package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;

public class NodoPrintln extends NodoAST {
    public List<NodoAST> argumentos;

    public NodoPrintln(List<NodoAST> argumentos, int linea, int columna) {
        super(linea, columna);
        this.argumentos = argumentos;
    }

    @Override
    public Object interpretar() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < argumentos.size(); i++) {
            Object val = argumentos.get(i).interpretar();
            sb.append(formatear(val));
            if (i < argumentos.size() - 1)
                sb.append(" ");
        }

        String resultado = sb.toString();
        com.golite.interpreter.Interprete.getInstancia().agregarConsola(resultado);
        return resultado;
    }

    private String formatear(Object val) {
        if (val == null)          return "nil";
        if (val instanceof Character) return String.valueOf((Character) val);
        return val.toString();
    }
}