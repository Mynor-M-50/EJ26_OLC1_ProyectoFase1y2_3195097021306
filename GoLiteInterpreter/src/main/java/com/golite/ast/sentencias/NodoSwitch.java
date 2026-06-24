package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;

public class NodoSwitch extends NodoAST {
    public NodoAST expresion;
    public List<NodoAST> casos;
    public List<NodoAST> porDefecto;

    public NodoSwitch(NodoAST expresion, List<NodoAST> casos, List<NodoAST> porDefecto, int linea, int columna) {
        super(linea, columna);
        this.expresion   = expresion;
        this.casos       = casos;
        this.porDefecto  = porDefecto;
    }

    @Override
    public Object interpretar() {
        Object valSwitch = expresion.interpretar();

        for (NodoAST nodo : casos) {
            NodoCaso caso = (NodoCaso) nodo;
            Object valCaso = caso.valor.interpretar();

            if (sonIguales(valSwitch, valCaso)) {
                try {
                    caso.interpretar();
                } catch (BreakException e) {
                    // break explicito, salir del switch
                }
                return null;
            }
        }

        // ninguno coincidio, ejecutar default si existe
        if (porDefecto != null) {
            try {
                for (NodoAST s : porDefecto)
                    s.interpretar();
            } catch (BreakException e) {
                // break en default, ignorar
            }
        }

        return null;
    }

    private boolean sonIguales(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}