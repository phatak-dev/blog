---           
layout: post
title: "Getting Started With ANTLR:Basics"
date: 2011-09-25 17:20:11 UTC
updated: 2011-09-25 17:20:11 UTC
comments: false
categories: antlr java
---
Yeah! It's after a lapse of a month or so that there is a post in this blog! :)

Well, this post drives you towards the basics of ANTLR. Previously, we had learnt about setting up of ANTLR as an external tool. 

###What is ANTLR?

ANother Tool for Language Recognition, is a language tool that provides a framework for constructing recognizers, interpreters, compilers, and translators from grammatical descriptions containing actions.

###What can be the target languages?
* Ada
* Action Script
* C
* C#; C#2
* C#3
* D
* Emacs ELisp
* Objective C
* Java
* Java Script
* Python
* Ruby
* Perl6
* Perl
* PHP
* Oberon
* Scala

###What does ANTLR support?

* Tree construction
* Error recovery
* Error handling
* Tree walking
* Translation

###What environment does it support?

ANTLRWorks is the IDE for ANTLR. It is the graphical grammar editor and debugger, written by Jean Bovet using Swing.


###What for ANTLR can be used?

* ""REAL"" programming languages
* domain-specific languages [DSL]

###Who is using ANTLR?

* Programming languages :Boo, Groovy, Mantra, Nemerle, XRuby etc.
* Other Tools: HIbernate, Intellij IDEA, Jazillian, JBoss Rules, Keynote(Apple), WebLogic(Oracle) etc.

Where is that you can look for ANTLR?

You can always follow [here](http://www.antlr.org)

* to download ANTLR and ANTLRWorks, which are free and open source
* docs,articles,wiki,mailing list,examples.... You can catch everything here!


####Basic Terms

* Lexer : converts a stream of characters to a stream of tokens.
* Parser : processes of tokens, possibly creating AST
*  Abstract Syntax Tree(AST): an intermediate tree representation of the parsed input that is simpler to process than the stream of tokens. It can as well be processed multiple times.
* Tree Parser: It processes an AST
* String Template: a library that supports using templates with placeholders for outputting text

####General Steps

* Write Grammar in one or more files
* Write string templates[optional]
* Debug your grammar with ANTLRWorks
* Generate classes from grammar
* Write an application that uses generated classes
* Feed the application text that conforms to the grammar

#### A Bit Further..

Lets write a simple grammar which consists of

* Lexer
* Parser

###Lexer

Lets take the example of simple declaration type in C of the form "int a,b;" or "int a;" and same with float.

As we see we can write lexer as follows:

{% highlight octave %}
//TestLexer.g

grammar TestLexer;
ID  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.'|'a'..'z'|'A'..'Z')*;
COMMA: ',';
SEMICOLON:';';
DATATYPE: 'int' | 'float';

{% endhighlight %}

As we could see, these were the characters that were to be converted to tokens.
So, now lets write some rules which processes these tokens generated and may it create a parse tree accordingly.

{% highlight octave %}
//TestParser.g

grammar TestParser;
options {
language : Java;
}
decl:DATATYPE ID (',' ID)* ;       

{% endhighlight %}

Running ANTLR on the grammar just generates the lexer and parser,TestParser and TestLexer. To actually try the grammar on some input, we
need a test rig with a main( ) method as follows:

{% highlight java %}

// Test.java

import org.antlr.runtime.*;
public class Test {
public static void main(String[] args) throws Exception {
// Create an input character stream from standard in
ANTLRFileStream input = new ANTLRFileStream("input"); // give path to the file input
// Create an ExprLexer that feeds from that stream
TestLexer lexer = new TestLexer(input);
// Create a stream of tokens fed by the lexer
CommonTokenStream tokens = new CommonTokenStream(lexer);
// Create a parser that feeds off the token stream
TestParser parser = new TestParser(tokens);
// Begin parsing at rule decl
parser.decl();
}
}

{% endhighlight %}

We shall see how to create an AST and walk over the tree in the next blog post..
Happy learning....! :)

