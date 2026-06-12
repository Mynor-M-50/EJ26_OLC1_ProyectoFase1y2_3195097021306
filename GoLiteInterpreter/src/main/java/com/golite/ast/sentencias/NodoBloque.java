package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import java.util.List;

public class NodoBloque extends NodoAST {
    public List<NodoAST> sentencias;

    public NodoBloque(List<NodoAST> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias;
    }

    @Override
    public Object interpretar() {
        Entorno.pushBloque();
        try {
            for (NodoAST s : sentencias)
                s.interpretar();
        } finally {
            Entorno.popBloque();
        }
        return null;
    }
}