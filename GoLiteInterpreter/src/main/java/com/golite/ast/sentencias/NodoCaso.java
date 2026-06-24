package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;

public class NodoCaso extends NodoAST {
    public NodoAST valor;
    public List<NodoAST> cuerpo;

    public NodoCaso(NodoAST valor, List<NodoAST> cuerpo, int linea, int columna) {
        super(linea, columna);
        this.valor  = valor;
        this.cuerpo = cuerpo;
    }

    @Override
    public Object interpretar() {
        for (NodoAST s : cuerpo)
            s.interpretar();
        return null;
    }
}