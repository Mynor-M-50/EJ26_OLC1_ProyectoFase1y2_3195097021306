package com.golite.lexer;

import com.golite.parser.*;
import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%cupsym com.golite.parser.Sym
%cup
%line
%column
%public

%{
    public int getLine() { return yyline + 1; }
    public int getColumn() { return yycolumn + 1; }

    private Symbol token(int tipo, String nombre, Object valor) {
        com.golite.interpreter.Interprete.getInstancia()
            .agregarToken(yytext(), nombre, yyline+1, yycolumn+1);
        return new Symbol(tipo, yyline+1, yycolumn+1, valor);
    }

    private Symbol token(int tipo, String nombre) {
        com.golite.interpreter.Interprete.getInstancia()
            .agregarToken(yytext(), nombre, yyline+1, yycolumn+1);
        return new Symbol(tipo, yyline+1, yycolumn+1);
    }
%}

/* ── Definiciones de patrones ── */
LineTerminator  = \r|\n|\r\n
WhiteSpace      = {LineTerminator} | [ \t\f]
Digit           = [0-9]
Letter          = [a-zA-Z_]
Identifier      = {Letter}({Letter}|{Digit})*
IntLiteral      = {Digit}+
FloatLiteral    = {Digit}+"."{Digit}+
StringLiteral   = \"([^\"\\\n]|\\.)*\"
RuneLiteral     = \'([^\'\\\n]|\\.)\'

%%

/* ── Palabras reservadas ── */
"var"                { return token(Sym.VAR,          "VAR"); }
"func"               { return token(Sym.FUNC,         "FUNC"); }
"main"               { return token(Sym.MAIN,         "MAIN"); }
"if"                 { return token(Sym.IF,           "IF"); }
"else"               { return token(Sym.ELSE,         "ELSE"); }
"for"                { return token(Sym.FOR,          "FOR"); }
"return"[ \t]*(\r\n|\r|\n)  {
    com.golite.interpreter.Interprete.getInstancia()
        .agregarToken("return", "RETURN", yyline+1, yycolumn+1);
    return new Symbol(Sym.RETURN, yyline+1, yycolumn+1);
}
"break"[ \t]*(\r\n|\r|\n)  {
    com.golite.interpreter.Interprete.getInstancia()
        .agregarToken("break", "BREAK", yyline+1, yycolumn+1);
    return new Symbol(Sym.BREAK, yyline+1, yycolumn+1);
}
"continue"[ \t]*(\r\n|\r|\n)  {
    com.golite.interpreter.Interprete.getInstancia()
        .agregarToken("continue", "CONTINUE", yyline+1, yycolumn+1);
    return new Symbol(Sym.CONTINUE, yyline+1, yycolumn+1);
}
"break"              { return token(Sym.BREAK,        "BREAK"); }
"continue"           { return token(Sym.CONTINUE,     "CONTINUE"); }
"return"             { return token(Sym.RETURN,       "RETURN"); }
"break"              { return token(Sym.BREAK,        "BREAK"); }
"continue"           { return token(Sym.CONTINUE,     "CONTINUE"); }
"return"             { return token(Sym.RETURN,       "RETURN"); }
"true"               { return token(Sym.TRUE,         "TRUE",  true); }
"false"              { return token(Sym.FALSE,        "FALSE", false); }
"nil"                { return token(Sym.NIL,          "NIL"); }
"switch"    { return new Symbol(Sym.SWITCH,   yyline+1, yycolumn+1); }
"case"      { return new Symbol(Sym.CASE,     yyline+1, yycolumn+1); }
"default"   { return new Symbol(Sym.DEFAULT,  yyline+1, yycolumn+1); }
"range"              { return token(Sym.RANGE, "RANGE"); }
"struct"             { return token(Sym.STRUCT, "STRUCT"); }

/* ── Tipos de datos ── */
"int"                { return token(Sym.TINT,         "TINT"); }
"float64"            { return token(Sym.TFLOAT64,     "TFLOAT64"); }
"string"             { return token(Sym.TSTRING,      "TSTRING"); }
"bool"               { return token(Sym.TBOOL,        "TBOOL"); }
"rune"               { return token(Sym.TRUNE,        "TRUNE"); }

/* ── Funciones embebidas ── */
"fmt.Println"        { return token(Sym.PRINTLN,      "PRINTLN"); }
"strconv.Atoi"       { return token(Sym.ATOI,         "ATOI"); }
"strconv.ParseFloat" { return token(Sym.PARSEFLOAT,   "PARSEFLOAT"); }
"reflect.TypeOf"     { return token(Sym.TYPEOF,       "TYPEOF"); }
"append"             { return token(Sym.APPEND,       "APPEND"); }
"len"                { return token(Sym.LEN,           "LEN"); }
"slices.Index"       { return token(Sym.SLICES_INDEX,  "SLICES_INDEX"); }
"strings.Join"       { return token(Sym.STRINGS_JOIN,  "STRINGS_JOIN"); }

/* ── Operadores de comparacin ── */
"=="  { return token(Sym.EQ,           "EQ"); }
"!="  { return token(Sym.NEQ,          "NEQ"); }
"<="  { return token(Sym.LEQ,          "LEQ"); }
">="  { return token(Sym.GEQ,          "GEQ"); }
"<"   { return token(Sym.LT,           "LT"); }
">"   { return token(Sym.GT,           "GT"); }

/* ── Operadores logicos ── */
"&&"  { return token(Sym.AND,          "AND"); }
"||"  { return token(Sym.OR,           "OR"); }
"!"   { return token(Sym.NOT,          "NOT"); }

/* ── Operadores de asignacion ── */
":="  { return token(Sym.DECL_ASSIGN,  "DECL_ASSIGN"); }
"+="  { return token(Sym.PLUS_ASSIGN,  "PLUS_ASSIGN"); }
"-="  { return token(Sym.MINUS_ASSIGN, "MINUS_ASSIGN"); }
"="   { return token(Sym.ASSIGN,       "ASSIGN"); }

/* ── Operadores aritmeticos ── */
"++"  { return token(Sym.INC,          "INC"); }
"--"  { return token(Sym.DEC,          "DEC"); }
"+"   { return token(Sym.PLUS,         "PLUS"); }
"-"   { return token(Sym.MINUS,        "MINUS"); }
"*"   { return token(Sym.TIMES,        "TIMES"); }
"/"   { return token(Sym.DIV,          "DIV"); }
"%"   { return token(Sym.MOD,          "MOD"); }

/* ── Delimitadores ── */
"("   { return token(Sym.LPAREN,    "LPAREN"); }
")"   { return token(Sym.RPAREN,    "RPAREN"); }
"{"   { return token(Sym.LBRACE,    "LBRACE"); }
"}"   { return token(Sym.RBRACE,    "RBRACE"); }
","   { return token(Sym.COMMA,     "COMMA"); }
";"   { return token(Sym.SEMICOLON, "SEMICOLON"); }
"."   { return token(Sym.DOT,       "DOT"); }
":"   { return token(Sym.COLON,    "COLON"); }
"["   { return token(Sym.LBRACKET, "LBRACKET"); }
"]"   { return token(Sym.RBRACKET, "RBRACKET"); }

/* ── Literales ── */
{IntLiteral}    { return token(Sym.INT_LIT,    "INT_LIT",    Integer.parseInt(yytext())); }
{FloatLiteral}  { return token(Sym.FLOAT_LIT,  "FLOAT_LIT",  Double.parseDouble(yytext())); }
{StringLiteral} { return token(Sym.STRING_LIT, "STRING_LIT", yytext().substring(1, yytext().length()-1)); }
{RuneLiteral}   { return token(Sym.RUNE_LIT,   "RUNE_LIT",   yytext().charAt(1)); }

/* ── Identificadores ── */
{Identifier}    { return token(Sym.ID, "ID", yytext()); }

/* ── Comentarios (se ignoran) ── */
"//"[^\n]*                  { /* comentario de linea */ }
"/*"([^*]|\*+[^*/])*\*+"/" { /* comentario de bloque */ }

/* ── Espacios en blanco ── */
[ \t\f\r]    { /* ignorar */ }
\n           { /* ignorar salto de linea */ }

/* ── Error lexico ── */
[^] {
    com.golite.interpreter.Interprete.getInstancia()
        .agregarError("Caracter no reconocido: '" + yytext() + "'", yyline+1, yycolumn+1, "lexico");
    System.err.println("Error lexico: '" + yytext() + "' linea " + (yyline+1));
    return new Symbol(Sym.error, yyline+1, yycolumn+1);
}