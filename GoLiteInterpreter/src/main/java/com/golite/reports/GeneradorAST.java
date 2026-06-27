package com.golite.reports;

import com.golite.ast.NodoAST;
import com.golite.ast.expresiones.*;
import com.golite.ast.sentencias.*;
import java.util.List;

public class GeneradorAST {

    private final Graphviz gv = new Graphviz();

    public String generar(NodoPrograma programa) {
        String raiz = gv.addNode("PROGRAMA");
        visitarPrograma(programa, raiz);
        return gv.getDot();
    }

    // ── PROGRAMA ──────────────────────────────────────────────

    private void visitarPrograma(NodoPrograma n, String padre) {
        if (!n.funciones.isEmpty()) {
            String nFuncs = gv.addNode("DECLARACIONES");
            gv.addEdge(padre, nFuncs);
            for (NodoAST f : n.funciones)
                visitarNodo(f, nFuncs);
        }
        if (!n.sentencias.isEmpty()) {
            String nMain = gv.addNode("func main");
            gv.addEdge(padre, nMain);
            String nStmts = gv.addNode("INSTRUCCIONES");
            gv.addEdge(nMain, nStmts);
            for (NodoAST s : n.sentencias)
                visitarNodo(s, nStmts);
        }
    }

    // ── DISPATCHER ────────────────────────────────────────────

    private String visitarNodo(NodoAST nodo, String padre) {
        if (nodo == null) return gv.addNode("null");

        String id;

        // Sentencias
        if (nodo instanceof NodoFuncion)          id = visitarFuncion((NodoFuncion) nodo);
        else if (nodo instanceof NodoFuncionStruct) id = visitarFuncionStruct((NodoFuncionStruct) nodo);
        else if (nodo instanceof NodoStruct)       id = visitarStruct((NodoStruct) nodo);
        else if (nodo instanceof NodoDeclaracion)  id = visitarDeclaracion((NodoDeclaracion) nodo);
        else if (nodo instanceof NodoAsignacion)   id = visitarAsignacion((NodoAsignacion) nodo);
        else if (nodo instanceof NodoAsignacionAtributo) id = visitarAsignacionAtributo((NodoAsignacionAtributo) nodo);
        else if (nodo instanceof NodoAsignacionSlice2D)  id = visitarAsignacionSlice2D((NodoAsignacionSlice2D) nodo);
        else if (nodo instanceof NodoAsignacionSlice)    id = visitarAsignacionSlice((NodoAsignacionSlice) nodo);
        else if (nodo instanceof NodoIf)           id = visitarIf((NodoIf) nodo);
        else if (nodo instanceof NodoFor)          id = visitarFor((NodoFor) nodo);
        else if (nodo instanceof NodoForRange)     id = visitarForRange((NodoForRange) nodo);
        else if (nodo instanceof NodoSwitch)       id = visitarSwitch((NodoSwitch) nodo);
        else if (nodo instanceof NodoCaso)         id = visitarCaso((NodoCaso) nodo);
        else if (nodo instanceof NodoPrintln)      id = visitarPrintln((NodoPrintln) nodo);
        else if (nodo instanceof NodoReturn)       id = visitarReturn((NodoReturn) nodo);
        else if (nodo instanceof NodoBreak)        { id = gv.addNode("break"); }
        else if (nodo instanceof NodoContinue)     { id = gv.addNode("continue"); }
        else if (nodo instanceof NodoBloque)       id = visitarBloque((NodoBloque) nodo);
        else if (nodo instanceof NodoInstanciaStruct) id = visitarInstanciaStruct((NodoInstanciaStruct) nodo);
            // Expresiones
        else if (nodo instanceof NodoSuma)         id = visitarBinario("+", ((NodoSuma) nodo).izquierda, ((NodoSuma) nodo).derecha);
        else if (nodo instanceof NodoResta)        id = visitarBinario("-", ((NodoResta) nodo).izquierda, ((NodoResta) nodo).derecha);
        else if (nodo instanceof NodoMultiplicacion) id = visitarBinario("*", ((NodoMultiplicacion) nodo).izquierda, ((NodoMultiplicacion) nodo).derecha);
        else if (nodo instanceof NodoDivision)     id = visitarBinario("/", ((NodoDivision) nodo).izquierda, ((NodoDivision) nodo).derecha);
        else if (nodo instanceof NodoModulo)       id = visitarBinario("%", ((NodoModulo) nodo).izquierda, ((NodoModulo) nodo).derecha);
        else if (nodo instanceof NodoComparacion)  id = visitarComparacion((NodoComparacion) nodo);
        else if (nodo instanceof NodoLogico)       id = visitarLogico((NodoLogico) nodo);
        else if (nodo instanceof NodoNegacion)     id = visitarNegacion((NodoNegacion) nodo);
        else if (nodo instanceof NodoEntero)       { id = gv.addNode("<int: " + ((NodoEntero) nodo).valor + ">"); }
        else if (nodo instanceof NodoDecimal)      { id = gv.addNode("<float64: " + ((NodoDecimal) nodo).valor + ">"); }
        else if (nodo instanceof NodoString)       { id = gv.addNode("<string: " + ((NodoString) nodo).valor + ">"); }
        else if (nodo instanceof NodoBooleano)     { id = gv.addNode("<bool: " + ((NodoBooleano) nodo).valor + ">"); }
        else if (nodo instanceof NodoRune)         { id = gv.addNode("<rune: " + ((NodoRune) nodo).valor + ">"); }
        else if (nodo instanceof NodoNil)          { id = gv.addNode("<nil>"); }
        else if (nodo instanceof NodoIdentificador) { id = gv.addNode("<id: " + ((NodoIdentificador) nodo).nombre + ">"); }
        else if (nodo instanceof NodoLlamada)      id = visitarLlamada((NodoLlamada) nodo);
        else if (nodo instanceof NodoLlamadaMetodo) id = visitarLlamadaMetodo((NodoLlamadaMetodo) nodo);
        else if (nodo instanceof NodoAccesoAtributo) id = visitarAccesoAtributo((NodoAccesoAtributo) nodo);
        else if (nodo instanceof NodoAccesoSlice)  id = visitarAccesoSlice((NodoAccesoSlice) nodo);
        else if (nodo instanceof NodoSlice)        id = visitarSlice((NodoSlice) nodo);
        else if (nodo instanceof NodoAppend)       id = visitarAppend((NodoAppend) nodo);
        else if (nodo instanceof NodoLen)          id = visitarLen((NodoLen) nodo);
        else if (nodo instanceof NodoSlicesIndex)  id = visitarSlicesIndex((NodoSlicesIndex) nodo);
        else if (nodo instanceof NodoStringsJoin)  id = visitarStringsJoin((NodoStringsJoin) nodo);
        else if (nodo instanceof NodoTypeOf)       id = visitarTypeOf((NodoTypeOf) nodo);
        else if (nodo instanceof NodoAtoi)         id = visitarAtoi((NodoAtoi) nodo);
        else if (nodo instanceof NodoParseFloat)   id = visitarParseFloat((NodoParseFloat) nodo);
        else { id = gv.addNode(nodo.getClass().getSimpleName()); }

        if (padre != null) gv.addEdge(padre, id);
        return id;
    }

    // ── SENTENCIAS ────────────────────────────────────────────

    private String visitarFuncion(NodoFuncion n) {
        String id = gv.addNode("func " + n.nombre);
        if (!n.parametros.isEmpty()) {
            String nParams = gv.addNode("PARAMS");
            gv.addEdge(id, nParams);
            for (Object[] p : n.parametros) {
                String nP = gv.addNode("<id: " + p[0] + " " + p[1] + ">");
                gv.addEdge(nParams, nP);
            }
        }
        if (n.tipoRetorno != null) {
            String nRet = gv.addNode("return: " + n.tipoRetorno);
            gv.addEdge(id, nRet);
        }
        String nBody = gv.addNode("INSTRUCCIONES");
        gv.addEdge(id, nBody);
        for (NodoAST s : n.cuerpo)
            visitarNodo(s, nBody);
        return id;
    }

    private String visitarFuncionStruct(NodoFuncionStruct n) {
        String id = gv.addNode("func (" + n.refNombre + " " + n.tipoStruct + ") " + n.nombre);
        String nBody = gv.addNode("INSTRUCCIONES");
        gv.addEdge(id, nBody);
        for (NodoAST s : n.cuerpo)
            visitarNodo(s, nBody);
        return id;
    }

    private String visitarStruct(NodoStruct n) {
        String id = gv.addNode("struct " + n.nombre);
        for (Object[] a : n.atributos) {
            String nA = gv.addNode("<" + a[0] + " " + a[1] + ">");
            gv.addEdge(id, nA);
        }
        return id;
    }

    private String visitarDeclaracion(NodoDeclaracion n) {
        String label = n.tipo != null ? "var " + n.nombre + " " + n.tipo : n.nombre + " :=";
        String id = gv.addNode(label);
        if (n.valor != null) visitarNodo(n.valor, id);
        return id;
    }

    private String visitarAsignacion(NodoAsignacion n) {
        String id = gv.addNode(n.nombre + " " + n.operador);
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarAsignacionAtributo(NodoAsignacionAtributo n) {
        String id = gv.addNode(n.nombreVar + "." + n.atributo + " =");
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarAsignacionSlice(NodoAsignacionSlice n) {
        String id = gv.addNode(n.nombre + "[i] =");
        visitarNodo(n.indice, id);
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarAsignacionSlice2D(NodoAsignacionSlice2D n) {
        String id = gv.addNode(n.nombre + "[i][j] =");
        visitarNodo(n.indiceFila, id);
        visitarNodo(n.indiceCol, id);
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarIf(NodoIf n) {
        String id = gv.addNode("if");
        String nCond = gv.addNode("CONDICION");
        gv.addEdge(id, nCond);
        visitarNodo(n.condicion, nCond);
        String nThen = gv.addNode("THEN");
        gv.addEdge(id, nThen);
        for (NodoAST s : n.cuerpoIf) visitarNodo(s, nThen);
        if (n.cuerpoElse != null && !n.cuerpoElse.isEmpty()) {
            String nElse = gv.addNode("ELSE");
            gv.addEdge(id, nElse);
            for (NodoAST s : n.cuerpoElse) visitarNodo(s, nElse);
        }
        if (n.nodoElseIf != null) visitarNodo(n.nodoElseIf, id);
        return id;
    }

    private String visitarFor(NodoFor n) {
        String id = gv.addNode("for");
        if (n.inicializacion != null) visitarNodo(n.inicializacion, id);
        String nCond = gv.addNode("CONDICION");
        gv.addEdge(id, nCond);
        visitarNodo(n.condicion, nCond);
        if (n.actualizacion != null) visitarNodo(n.actualizacion, id);
        String nBody = gv.addNode("INSTRUCCIONES");
        gv.addEdge(id, nBody);
        for (NodoAST s : n.cuerpo) visitarNodo(s, nBody);
        return id;
    }

    private String visitarForRange(NodoForRange n) {
        String label = n.varIndice != null
                ? "for " + n.varIndice + ", " + n.varValor + " := range"
                : "for _, " + n.varValor + " := range";
        String id = gv.addNode(label);
        visitarNodo(n.slice, id);
        String nBody = gv.addNode("INSTRUCCIONES");
        gv.addEdge(id, nBody);
        for (NodoAST s : n.cuerpo) visitarNodo(s, nBody);
        return id;
    }

    private String visitarSwitch(NodoSwitch n) {
        String id = gv.addNode("switch");
        visitarNodo(n.expresion, id);
        for (NodoAST c : n.casos) visitarNodo(c, id);
        if (n.porDefecto != null && !n.porDefecto.isEmpty()) {
            String nDef = gv.addNode("default");
            gv.addEdge(id, nDef);
            for (NodoAST s : n.porDefecto) visitarNodo(s, nDef);
        }
        return id;
    }

    private String visitarCaso(NodoCaso n) {
        String id = gv.addNode("case");
        visitarNodo(n.valor, id);
        String nBody = gv.addNode("INSTRUCCIONES");
        gv.addEdge(id, nBody);
        for (NodoAST s : n.cuerpo) visitarNodo(s, nBody);
        return id;
    }

    private String visitarPrintln(NodoPrintln n) {
        String id = gv.addNode("fmt.Println");
        String nArgs = gv.addNode("ARGS");
        gv.addEdge(id, nArgs);
        for (NodoAST a : n.argumentos) visitarNodo(a, nArgs);
        return id;
    }

    private String visitarReturn(NodoReturn n) {
        String id = gv.addNode("return");
        if (n.expresion != null) visitarNodo(n.expresion, id);
        return id;
    }

    private String visitarBloque(NodoBloque n) {
        String id = gv.addNode("BLOQUE");
        for (NodoAST s : n.sentencias) visitarNodo(s, id);
        return id;
    }

    private String visitarInstanciaStruct(NodoInstanciaStruct n) {
        String id = gv.addNode(n.tipoStruct + "{...}");
        for (Object[] v : n.valores) {
            String nV = gv.addNode("<" + v[0] + ">");
            gv.addEdge(id, nV);
            visitarNodo((NodoAST) v[1], nV);
        }
        return id;
    }

    // ── EXPRESIONES ───────────────────────────────────────────

    private String visitarBinario(String op, NodoAST izq, NodoAST der) {
        String id = gv.addNode(op);
        visitarNodo(izq, id);
        visitarNodo(der, id);
        return id;
    }

    private String visitarComparacion(NodoComparacion n) {
        String id = gv.addNode(n.operador);
        visitarNodo(n.izquierda, id);
        visitarNodo(n.derecha, id);
        return id;
    }

    private String visitarLogico(NodoLogico n) {
        String id = gv.addNode(n.operador);
        visitarNodo(n.izquierda, id);
        if (n.derecha != null) visitarNodo(n.derecha, id);
        return id;
    }

    private String visitarNegacion(NodoNegacion n) {
        String id = gv.addNode("-");
        visitarNodo(n.expresion, id);
        return id;
    }

    private String visitarLlamada(NodoLlamada n) {
        String id = gv.addNode(n.nombre + "()");
        if (!n.argumentos.isEmpty()) {
            String nArgs = gv.addNode("ARGS");
            gv.addEdge(id, nArgs);
            for (NodoAST a : n.argumentos) visitarNodo(a, nArgs);
        }
        return id;
    }

    private String visitarLlamadaMetodo(NodoLlamadaMetodo n) {
        String id = gv.addNode("." + n.metodo + "()");
        visitarNodo(n.objeto, id);
        if (!n.argumentos.isEmpty()) {
            String nArgs = gv.addNode("ARGS");
            gv.addEdge(id, nArgs);
            for (NodoAST a : n.argumentos) visitarNodo(a, nArgs);
        }
        return id;
    }

    private String visitarAccesoAtributo(NodoAccesoAtributo n) {
        String id = gv.addNode("." + n.atributo);
        visitarNodo(n.objeto, id);
        return id;
    }

    private String visitarAccesoSlice(NodoAccesoSlice n) {
        String id = gv.addNode("[idx]");
        visitarNodo(n.slice, id);
        visitarNodo(n.indice, id);
        return id;
    }

    private String visitarSlice(NodoSlice n) {
        String label = n.tipo != null ? "[]" + n.tipo + "{}" : "{...}";
        String id = gv.addNode(label);
        for (NodoAST e : n.elementos) visitarNodo(e, id);
        return id;
    }

    private String visitarAppend(NodoAppend n) {
        String id = gv.addNode("append");
        visitarNodo(n.slice, id);
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarLen(NodoLen n) {
        String id = gv.addNode("len");
        visitarNodo(n.expresion, id);
        return id;
    }

    private String visitarSlicesIndex(NodoSlicesIndex n) {
        String id = gv.addNode("slices.Index");
        visitarNodo(n.slice, id);
        visitarNodo(n.valor, id);
        return id;
    }

    private String visitarStringsJoin(NodoStringsJoin n) {
        String id = gv.addNode("strings.Join");
        visitarNodo(n.slice, id);
        visitarNodo(n.separador, id);
        return id;
    }

    private String visitarTypeOf(NodoTypeOf n) {
        String id = gv.addNode("reflect.TypeOf");
        visitarNodo(n.expresion, id);
        return id;
    }

    private String visitarAtoi(NodoAtoi n) {
        String id = gv.addNode("strconv.Atoi");
        visitarNodo(n.expresion, id);
        return id;
    }

    private String visitarParseFloat(NodoParseFloat n) {
        String id = gv.addNode("strconv.ParseFloat");
        visitarNodo(n.expresion, id);
        return id;
    }
}