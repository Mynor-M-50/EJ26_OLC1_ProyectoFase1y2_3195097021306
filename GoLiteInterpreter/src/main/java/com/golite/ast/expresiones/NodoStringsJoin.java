package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoStringsJoin extends NodoAST {
    public NodoAST slice;
    public NodoAST separador;

    public NodoStringsJoin(NodoAST slice, NodoAST separador, int linea, int columna) {
        super(linea, columna);
        this.slice     = slice;
        this.separador = separador;
    }

    @Override
    public Object interpretar() {
        Object s = slice.interpretar();
        Object sep = separador.interpretar();

        if (!(s instanceof List)) {
            String msg = "strings.Join: primer argumento no es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> lista = (List<Object>) s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(sep.toString());
            sb.append(lista.get(i).toString());
        }
        return sb.toString();
    }
}