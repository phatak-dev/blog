---           
layout: post
title: "ANTLR as an external tool in eclipse on ubuntu"
date: 2011-08-01 17:57:28 UTC
updated: 2011-08-01 17:57:28 UTC
comments: false
categories: antlr eclipse ubuntu
---

This tutorial tells how to setup the ANTLR in your eclipse.

###STEP 1

Download the jar file antlrworks-1.4.2.jar from [http://www.antlr.org/download](http://www.antlr.org/download).

Further details about ANTLRWorks: The ANTLR GUI Development Environment, follow the [link](http://www.antlr.org/works/index.html)

###STEP 2

Create a java project in eclipse as follows:

> File->New->Project

* Click on Next.
* Name the project as "TestANTLR"
* Press Finish.

######Add the antlrworks-1.4.2.jar to the project classpath.

* Right click on "TestANTLR" project .
* Select > Properties->Libraries.
* Click on "Add External jar"
* Select the complete path of the "antlrworks-1.4.2.jar" and press Ok.


###STEP 3 :  Make it as an external tool

> Run->External Tools->Configure

* Click on New.

 > Name: ANTLR Compiler

 > Tool Location: /usr/lib/jvm/java-6-sun-1.6.0.26/bin/java


> Tool Arguments: -classpath complete_path_to_antlrworks-1.4.2.jar org.antlr.Tool ${resource_name}

> Working Directory: ${container_loc}
Here, org.antlr.Tool is the main class which would take the ${resource_name} for processing.

> ${resource_name} and ${container_loc} can be selected with "Browse Variables" option too.


###Going Ahead

* Creating a grammar file

Create a grammar file with .g extension. Say, Example.g

{%highlight octave%}
grammar Example;
start : 'hello' ID ';' {System.out.println("hiii... "+$ID.text);} ;
ID: 'a'..'z' + ;
WS: (' ' |'\n' |'\r' )+
{$channel=HIDDEN;}
{% endhighlight %}


####Running the code
> Run->External Tools->ANTLR Compiler

Press F5 or right click on the project and "refresh"
all you can see is a lexer and parser files generated with the tokens.
In our example,

* ExampleLexer.java 
* ExampleParser.java 
* Example.tokens

Create Main.java program in the same project with the following code:

{%highlight java%}
import org.antlr.runtime.*;
public class Main {
public static void main(String[] args) throws Exception {
// create a CharStream that reads from standard input
ANTLRInputStream input = new ANTLRInputStream(System.in);
// create a lexer that feeds off of input CharStream
ExampleLexer lexer = new ExampleLexer(input);
// create a buffer of tokens pulled from the lexer
CommonTokenStream tokens = new CommonTokenStream(lexer);
// create a parser that feeds off the tokens buffer
ExampleParser parser = new ExampleParser(tokens);
// begin parsing at rule start
parser.start();
}
}
{% endhighlight %}


Set the arguments in the Run configurations and click on Apply and Run.

Now you have the output at console.
:)