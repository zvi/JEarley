Several months ago I've encountered a parser package called "accent" (http://accent.compilertools.net/Accent.html)
by Friedrich Wilhelm Schröer.

The package is a very nice implementation of an Earley parser (first published by Jay Earley as a recognizer in 1968)
however it seems it didn't get any updates or maintenance in the last few years, so I decided to provide a similar
implementation (though not as complete and robust) in Java and make it available for all to use.

There are a few differences though. This is not a parser "generator" in the sense that you don't provide a textual
language description in a BNF form, but you actually write the rules and action directly in compilable code.
Effectively there is no pre-processing phase to generate source code files representing the parser.

The code is provided under the GPL license for your free use. As you can see, it's pretty light on code so it
should be suitable for embedding it into your code.

Things to note:
* It has to keep all the lexer's input symbols in memory (so if you're parsing very large files, watch your memory consumption).
* It generates and keeps the entire parse tree(s) in memory before it starts calling the actions on the rules.
* The default rule's action will simply propagate the result from the first RHS element. If that's not what you want, override it. 

You can try a working example. See the file: EarleyDemo.java

As always, I appreciate new ideas, suggestions and comments.

-- Tzvi
