package com.golite.ast.sentencias;

import com.golite.interpreter.Entorno;
import com.golite.ast.NodoAST;
import java.util.List;

public class NodoFor extends NodoAST {
    public NodoAST condicion;        // for condicion { }
    public NodoAST inicializacion;   // for init; cond; update { }
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
        // El for clasico necesita su propio scope para la variable de init
        boolean tieneInit = (inicializacion != null);
        if (tieneInit) Entorno.pushBloque();

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
                    // continua pero limpia el scope del cuerpo
                } finally {
                    Entorno.popBloque();
                }

                if (actualizacion != null)
                    actualizacion.interpretar();
            }
        } finally {
            if (tieneInit) Entorno.popBloque();
        }

        return null;
    }
}