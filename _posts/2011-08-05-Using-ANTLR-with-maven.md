---           
layout: post
title: "Using ANTLR with maven"
date: 2011-08-05 14:02:29 UTC
updated: 2011-08-05 14:02:29 UTC
comments: false
categories: antlr maven
---

As a part of [Nectar](https://github.com/zinniasystems/Nectar), we are trying to build a custom language using ANTLR. Since our project uses maven during the build time, we have to integrate ANTLR with maven. Though ANTLR provides maven plug-in, its little tricky to use. So, in this post I am explaining the steps to integrate ANTLR with maven using ANTLR3 maven plug in.

###Step 1

You have to put all your grammar files , aka .g files in the default directory required by the plugin. Custom placing will not work because of some bug in the plugin. Hence, place the .g file in the following manner:

> src/main/antlr3/required-package/.g 

So the "required-package" is the package you specified in the .g file.

###Step 2

Add the plug-in to the pom as follows:

{% highlight xml %}
 <plugin>
 <groupId>org.antlr</groupId>
 <artifactId>antlr3-maven-plugin</artifactId>
 <version>3.1.3-1</version>
  <executions>
   <execution>
    <configuration>
   <outputDirectory>src/main/java
   </outputDirectory>
    </configuration>
    <goals>
           <goal>antlr</goal>
    </goals>
   </execution>
  </executions>
</plugin>
{% endhighlight %}

We added a configuration which generates the lexer and parser files in the src directory rather than  the default generated source in target.

For more information about the plug-in, refer [here](http://www.antlr.org/antlr3-maven-plugin/index.html) 

###Step 3 

Just run the pom and your .g will be compiled successfully. 




