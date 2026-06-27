package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoPrograma extends NodoAST {
    public List<NodoAST> funciones;
    public List<NodoAST> sentencias;

    public NodoPrograma(List<NodoAST> funciones, List<NodoAST> sentencias) {
        super(0, 0);
        this.funciones  = funciones;
        this.sentencias = sentencias;
    }

    @Override
    public Object interpretar() {
        // Primero registrar todas las funciones y structs
        for (NodoAST f : funciones) {
            if (f == null) continue;
            try {
                f.interpretar();
            } catch (RuntimeException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                boolean yaRegistrado = Interprete.getInstancia().getErrores().stream()
                        .anyMatch(err -> err.descripcion.equals(msg));
                if (!yaRegistrado)
                    Interprete.getInstancia().agregarError(msg, 0, 0, "semantico");
            }
        }

        // Luego ejecutar el main — un error semantico detiene la ejecucion
        for (NodoAST s : sentencias) {
            if (s == null) continue;
            try {
                s.interpretar();
            } catch (ReturnException e) {
                break;
            }
            // RuntimeException sube a Interprete.ejecutar() y detiene el programa
        }
        return null;
    }
}