package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;

public class NodoIf extends NodoAST {
    public NodoAST condicion;
    public List<NodoAST> cuerpoIf;
    public List<NodoAST> cuerpoElse; // null si no hay else
    public NodoIf nodoElseIf;        // null si no hay else if

    public NodoIf(NodoAST condicion, List<NodoAST> cuerpoIf, List<NodoAST> cuerpoElse, NodoIf nodoElseIf, int linea, int columna) {
        super(linea, columna);
        this.condicion   = condicion;
        this.cuerpoIf    = cuerpoIf;
        this.cuerpoElse  = cuerpoElse;
        this.nodoElseIf  = nodoElseIf;
    }

    @Override
    public Object interpretar() {
        Object cond = condicion.interpretar();

        if (!(cond instanceof Boolean))
            throw new RuntimeException("La condicion del if debe ser booleana, linea " + linea);

        if ((Boolean) cond) {
            for (NodoAST s : cuerpoIf)
                s.interpretar();
        } else if (nodoElseIf != null) {
            nodoElseIf.interpretar();
        } else if (cuerpoElse != null) {
            for (NodoAST s : cuerpoElse)
                s.interpretar();
        }

        return null;
    }
}