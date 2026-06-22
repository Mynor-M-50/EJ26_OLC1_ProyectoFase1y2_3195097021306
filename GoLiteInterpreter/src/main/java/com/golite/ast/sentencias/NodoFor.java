package com.golite.ast.sentencias;

import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import com.golite.ast.NodoAST;
import java.util.List;

public class NodoFor extends NodoAST {
    public NodoAST condicion;
    public NodoAST inicializacion;
    public NodoAST actualizacion;
    public List<NodoAST> cuerpo;

    public NodoFor(NodoAST condicion, NodoAST inicializacion, NodoAST actualizacion, List<NodoAST> cuerpo, int linea, int columna) {
        super(linea, columna);
        this.condicion = condicion;
        this.inicializacion = inicializacion;
        this.actualizacion = actualizacion;
        this.cuerpo = cuerpo;
    }

    @Override
    public Object interpretar() {
        boolean tieneInit = (inicializacion != null);
        if (tieneInit) Entorno.pushBloque();

        Interprete.getInstancia().entrarCiclo();
        try {
            if (inicializacion != null)
                inicializacion.interpretar();

            while (true) {
                Object cond = condicion.interpretar();
                if (!(cond instanceof Boolean))
                    throw new RuntimeException("Condicion del for debe ser booleana, linea " + linea);
                if (!(Boolean) cond) break;

                Entorno.pushBloque();
                try {
                    for (NodoAST s : cuerpo)
                        s.interpretar();
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {
                    // continua
                } finally {
                    Entorno.popBloque();
                }

                if (actualizacion != null)
                    actualizacion.interpretar();
            }
        } finally {
            Interprete.getInstancia().salirCiclo();
            if (tieneInit) Entorno.popBloque();
        }

        return null;
    }
}