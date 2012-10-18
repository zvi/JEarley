#!/bin/sh

javac -cp . `find . -name "*.java"`
java -cp . com.parser.earley.demo.EarleyDemo

