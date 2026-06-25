package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import java.util.ArrayList;
import java.util.List;

public class NodoSlice extends NodoAST {
    public String tipo;
    public List<NodoAST> elementos;

    public NodoSlice(String tipo, List<NodoAST> elementos, int linea, int columna) {
        super(linea, columna);
        this.tipo      = tipo;
        this.elementos = elementos;
    }

    @Override
    public Object interpretar() {
        List<Object> lista = new ArrayList<>();
        for (NodoAST e : elementos)
            lista.add(e.interpretar());
        return lista;
    }
}