package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;
import java.util.Map;

public class NodoFuncionStruct extends NodoAST {
    public String refNombre;
    public String tipoStruct;
    public String nombre;
    public List<Object[]> parametros;
    public String tipoRetorno;
    public List<NodoAST> cuerpo;

    public NodoFuncionStruct(String refNombre, String tipoStruct, String nombre,
                             List<Object[]> parametros, String tipoRetorno,
                             List<NodoAST> cuerpo, int linea, int columna) {
        super(linea, columna);
        this.refNombre  = refNombre;
        this.tipoStruct = tipoStruct;
        this.nombre     = nombre;
        this.parametros = parametros;
        this.tipoRetorno = tipoRetorno;
        this.cuerpo     = cuerpo;
    }

    @Override
    public Object interpretar() {
        Entorno.registrarMetodo(tipoStruct, nombre, this);
        return null;
    }

    public Object ejecutar(List<Object> argumentos) {
        Entorno.pushBloque();
        try {
            // Primer argumento es la instancia del struct
            Map<String, Object> instancia = (Map<String, Object>) argumentos.get(0);
            Entorno.getInstancia().declararStruct(refNombre, instancia, linea, columna);

            // Resto de parametros
            for (int i = 0; i < parametros.size(); i++) {
                Object[] param = parametros.get(i);
                String nombreParam = (String) param[0];
                String tipoParam   = (String) param[1];
                Object val = (i + 1 < argumentos.size()) ? argumentos.get(i + 1) : null;
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