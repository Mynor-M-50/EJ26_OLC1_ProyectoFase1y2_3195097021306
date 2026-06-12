package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
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
            if (s == null) continue;
            try {
                s.interpretar();
            } catch (RuntimeException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                boolean yaRegistrado = Interprete.getInstancia().getErrores().stream()
                        .anyMatch(err -> err.descripcion.equals(msg));
                if (!yaRegistrado)
                    Interprete.getInstancia().agregarError(msg, 0, 0, "semántico");
            }
        }
        return null;
    }
}