package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoFuncion extends NodoAST {
    public String nombre;
    public List<Object[]> parametros;
    public String tipoRetorno;
    public List<NodoAST> cuerpo;

    public NodoFuncion(String nombre, List<Object[]> parametros, String tipoRetorno, List<NodoAST> cuerpo, int linea, int columna) {
        super(linea, columna);
        this.nombre      = nombre;
        this.parametros  = parametros;
        this.tipoRetorno = tipoRetorno;
        this.cuerpo      = cuerpo;
    }

    @Override
    public Object interpretar() {
        Entorno.registrarFuncion(nombre, this);
        return null;
    }

    public Object ejecutar(List<Object> argumentos) {
        Entorno.pushBloque();
        try {
            for (int i = 0; i < parametros.size(); i++) {
                Object[] param = parametros.get(i);
                String nombreParam = (String) param[0];
                String tipoParam   = (String) param[1];
                Object val = (i < argumentos.size()) ? argumentos.get(i) : null;
                Entorno.getInstancia().declarar(nombreParam, tipoParam, val, linea, columna);
            }

            for (NodoAST s : cuerpo)
                s.interpretar();

        } catch (ReturnException e) {
            return e.valor;
        } finally {
            Entorno.popBloque();
        }
        return null;
    }
}