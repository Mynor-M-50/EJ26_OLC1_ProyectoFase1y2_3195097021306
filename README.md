# Interprete GoLite - Fase 1 y Fase 2

## Descripcion

Interprete del lenguaje GoLite implementado en Java, desarrollado como proyecto del curso Organizacion de Lenguajes y Compiladores 1. GoLite es un subconjunto del lenguaje Go que cubre los conceptos fundamentales de compiladores.

El interprete esta construido en tres etapas:

- **Analisis Lexico** con JFlex: tokenizacion del codigo fuente
- **Analisis Sintactico** con CUP: parser LALR(1) que construye el AST
- **Analisis Semantico + Ejecucion**: recorrido del AST con manejo de scopes

---

## Caracteristicas - Fase 1

- Tipos de datos primitivos: `int`, `float64`, `string`, `bool`, `rune`
- Declaracion de variables con tipo explicito (`var x int = 10`) e inferencia de tipo (`x := 10`)
- Operadores aritmeticos, logicos y de comparacion con precedencia correcta
- Asignacion compuesta: `+=`, `-=`, `++`, `--`
- Sentencias `if`, `else if`, `else` con bloques obligatorios
- Bucles `for` estilo while y estilo clasico (init; cond; update)
- `break` y `continue` dentro de bucles
- Bloques anonimos con su propio ambito (scope anidado)
- Funciones embebidas: `fmt.Println`, `strconv.Atoi`, `strconv.ParseFloat`, `reflect.TypeOf`
- Reporte de tokens reconocidos (tabla con lexema, tipo, linea, columna)
- Reporte de errores lexicos, sintacticos y semanticos
- Modo panico: recuperacion de errores lexicos para reportar multiples errores en una pasada
- Interfaz grafica en Java Swing con editor, consola de salida y reportes

---

## Caracteristicas - Fase 2

Todas las caracteristicas de Fase 1 mas:

- Asignacion compuesta extendida: `*=`, `/=`
- Sentencia `switch` / `case` / `default`
- Bucle `for range` sobre slices
- **Funciones** con parametros tipados, retorno de valor y recursion
- **Structs** definidos por el usuario con atributos y metodos, incluyendo acceso anidado (`p.dir.calle`)
- **Slices 1D**: `append`, `len`, acceso por indice, `slices.Index`, `strings.Join`
- **Slices multidimensionales** `[][]int` con acceso y asignacion 2D (`mtx[i][j]`)
- Paso de slices y structs **por referencia** en funciones y metodos
- Valor `nil` en slices y structs; operaciones sobre `nil` generan error semantico
- Deteccion de funciones y structs duplicados con error semantico
- **Tabla de Simbolos**: variables, funciones y structs con tipo, ambito, linea y columna
- **Reporte AST visual** con Graphviz: genera `.dot` y `.png` automaticamente
- Interfaz grafica con editor multi-tab y cuatro reportes en el menu

---

## Documentacion

- [Manual de Usuario Fase 1](ManualUsuario_GoLite_Fase1.pdf)
- [Manual Tecnico Fase 1](ManualTecnico_GoLite_Fase1.pdf)
- [Manual de Usuario Fase 2](ManualUsuario_GoLite_Fase2.pdf)
- [Manual Tecnico Fase 2](ManualTecnico_GoLite_Fase2.pdf)

---

## Requisitos Previos

- **Java JDK:** version 21 o superior
- **Apache Maven:** version 3.8.x o superior (solo si se compila desde fuente)
- **Graphviz:** version 2.43.0 o superior (para generacion del PNG del AST — solo Fase 2)
- **Sistema Operativo:** Ubuntu Linux (recomendado) o cualquier sistema con soporte Java

```bash
# Instalar Graphviz en Ubuntu (requerido para reporte AST de Fase 2)
sudo apt install graphviz
```

No se requieren dependencias adicionales. El JAR ejecutable incluye todas las librerias necesarias.

---

## Ejecucion (JAR ejecutable)

Descargar el JAR desde la seccion Releases del repositorio y ejecutar:

```bash
java -jar GoLiteInterpreter-1.0-SNAPSHOT.jar
```

## Compilacion desde el codigo fuente

```bash
cd GoLiteInterpreter

# Compilar y empaquetar
mvn clean package

# Ejecutar
java -jar target/GoLiteInterpreter-1.0-SNAPSHOT.jar
```

> **Importante:** El comando `mvn` debe ejecutarse siempre desde dentro de la carpeta `GoLiteInterpreter/`, no desde la raiz del repositorio.

---

## Ejemplo Fase 1

```go
func main() {
    var nombre string = "GoLite"
    version := 1

    fmt.Println("Interprete:", nombre, "Version:", version)

    for i := 1; i <= 5; i++ {
        if i == 3 {
            fmt.Println("Mitad del recorrido")
            continue
        }
        fmt.Println("Iteracion:", i)
    }
}
```

## Ejemplo Fase 2

```go
struct Persona {
    string nombre;
    int edad;
}

func (p Persona) saludar() {
    fmt.Println("Hola, soy", p.nombre)
}

func factorial(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorial(n - 1)
}

func main() {
    Persona p = { nombre: "Alice", edad: 25 }
    p.saludar()

    nums := []int{5, 3, 8, 1, 9}
    nums = append(nums, 10)
    fmt.Println(nums)
    fmt.Println(len(nums))

    fmt.Println("5! =", factorial(5))

    switch len(nums) {
        case 6: fmt.Println("seis elementos")
        default: fmt.Println("otro numero")
    }
}
```

---

## Estructura del Proyecto

```
GoLiteInterpreter/
  src/main/
    jflex/
      golite.flex             -> Definicion del analizador lexico
    cup/
      golite.cup              -> Gramatica LALR(1) y construccion del AST
    java/com/golite/
      lexer/                  -> Lexer.java (generado por JFlex)
      parser/                 -> Parser.java + Sym.java (generados por CUP)
      ast/
        NodoAST.java          -> Clase base abstracta
        expresiones/          -> Nodos de expresiones (literales, operadores, slices)
        sentencias/           -> Nodos de sentencias (if, for, switch, structs, funciones)
      interpreter/
        Interprete.java       -> Singleton principal de ejecucion
        Entorno.java          -> Manejo de scopes con metadatos para tabla de simbolos
        ErrorSemantic.java    -> Modelo de error
      reports/
        Graphviz.java         -> Generador de codigo DOT
        GeneradorAST.java     -> Recorre el AST y produce el archivo DOT y PNG
        GeneradorSimbolos.java -> Recolecta simbolos post-ejecucion
      gui/
        VentanaPrincipal.java -> Interfaz grafica Swing
  pom.xml                     -> Configuracion Maven
```

---

## Herramientas utilizadas

| Herramienta   | Version        | Uso                                      |
|---------------|----------------|------------------------------------------|
| Java JDK      | 21             | Lenguaje de implementacion               |
| JFlex         | 1.9.1          | Generador del analizador lexico          |
| CUP           | 11b-20160615   | Generador del parser LALR(1)             |
| Apache Maven  | 3.8.7          | Compilacion y empaquetado                |
| Graphviz      | 2.43.0         | Renderizado del AST como imagen PNG      |
| Java Swing    | (incluido JDK) | Interfaz grafica                         |
| IntelliJ IDEA | Community Ed.  | IDE de desarrollo                        |
| Ubuntu        | 24.04          | Sistema operativo de desarrollo          |

---

## Estudiante

**Mynor Miguel Monzon Martinez**  
Carne: 3195097021306  
Curso: Organizacion de Lenguajes y Compiladores 1  
Escuela de Vacaciones - Junio 2026  
Universidad de San Carlos de Guatemala