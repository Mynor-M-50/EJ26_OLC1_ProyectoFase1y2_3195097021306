package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;

public class NodoPrograma extends NodoAST {
    public List<NodoAST> sentencias;

    public NodoPrograma(List<NodoAST> sentencias) {
        super(0, 0);
        this.sentencias = sentencias;
    }

    @Override
    public Object interpretar() {
        for (NodoAST s : sentencias) {
            s.interpretar();
        }
        return null;
    }
}