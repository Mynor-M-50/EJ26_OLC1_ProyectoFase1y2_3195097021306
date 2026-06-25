package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoForRange extends NodoAST {
    public String varIndice;
    public String varValor;
    public NodoAST slice;
    public List<NodoAST> cuerpo;

    public NodoForRange(String varIndice, String varValor, NodoAST slice, List<NodoAST> cuerpo, int linea, int columna) {
        super(linea, columna);
        this.varIndice = varIndice;
        this.varValor  = varValor;
        this.slice     = slice;
        this.cuerpo    = cuerpo;
    }

    @Override
    public Object interpretar() {
        Object s = slice.interpretar();

        if (!(s instanceof List)) {
            String msg = "range: se esperaba un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> lista = (List<Object>) s;

        Interprete.getInstancia().entrarCiclo();
        try {
            for (int i = 0; i < lista.size(); i++) {
                Entorno.pushBloque();
                try {
                    if (varIndice != null)
                        Entorno.getInstancia().declarar(varIndice, "int", i, linea, columna);
                    Entorno.getInstancia().declarar(varValor, null, lista.get(i), linea, columna);

                    for (NodoAST stmt : cuerpo)
                        stmt.interpretar();

                } catch (BreakException e) {
                    Entorno.popBloque();
                    return null;
                } catch (ContinueException e) {
                    // continua al siguiente
                } finally {
                    Entorno.popBloque();
                }
            }
        } finally {
            Interprete.getInstancia().salirCiclo();
        }

        return null;
    }
}