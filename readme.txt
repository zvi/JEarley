This is a trivial Earley parser implementaion based on the C implementation called "accent" (http://accent.compilertools.net/Accent.html) by Friedrich Wilhelm Schröer.
Accent is a very nice implementation of an Earley parser (first published by Jay Earley as a recognizer in 1968) however I needed a similar yet tiny (though not as complete or robust) implementation in Java.

There are a few core differences though. This is not a parser "generator" in the sense that you don't provide a textual language description in a BNF form but you actually write the rules and action directly in Java.

The code is provided as-is under the GPL license. It's pretty slim and suitable for embedding into applications.
You can try a working example (see: EarleyDemo.java) by runnning the build.sh script.

As always, I'd appreciate your ideas, suggestions and comments.

-- Tzvi
