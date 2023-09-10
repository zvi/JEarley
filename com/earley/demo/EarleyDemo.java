/****************************************************************************
 * <p>
 **   This code is an experimental implementation of an Earley Parser.
 **   Copyright (C) 2012 by Tzvi Rotshtein
 * <p>
 **   This program is free software: you can redistribute it and/or modify
 **   it under the terms of the GNU General Public License as published by
 **   the Free Software Foundation, either version 3 of the License, or
 **   (at your option) any later version.
 * <p>
 **   This program is distributed in the hope that it will be useful,
 **   but WITHOUT ANY WARRANTY; without even the implied warranty of
 **   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 **   GNU General Public License for more details.
 * <p>
 **   You should have received a copy of the GNU General Public License
 **   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **
 ***************************************************************************/

package com.earley.demo;

import com.earley.lexer.ILexer;
import com.earley.lexer.Symbol;
import com.earley.parser.EarleyParser;


public class EarleyDemo extends EarleyParser<Object, Integer> {
	protected Integer textToResult(final Symbol symbol) {
		// Because we get all terminal symbols, like "+", "*", etc (in addition to numbers).
		return symbol.text != null ? Integer.valueOf(symbol.text) : new Integer(0);
	}

	public static void main(String[] args) {
		ILexer lex = new LexerDemo("(25 + 67 - 4*9) * -(5 - 8 + 16*3) * (11 - 3)");
		EarleyParser<Object, Integer> e = new EarleyDemo();

		e.addRule("atom",    rule("num"));
		e.addRule("atom",    rule("-", "atom"),          (env, param) -> -param.get(1));
		e.addRule("atom",    rule("(", "expr", ")"),     (env, param) -> param.get(1));
		e.addRule("prod",    rule("atom"));
		e.addRule("prod",    rule("prod", "*", "atom"),  (env, param) -> param.get(0) * param.get(2));
		e.addRule("prod",    rule("prod", "/", "atom"),  (env, param) -> param.get(0) / param.get(2));
		e.addRule("sum",     rule("prod"));
		e.addRule("sum",     rule("sum", "+", "prod"),   (env, param) -> param.get(0) + param.get(2));
		e.addRule("sum",     rule("sum", "-", "prod"),   (env, param) -> param.get(0) - param.get(2));
		e.addRule("expr",    rule("sum"));
		e.addRule("start",   rule("expr"));

		e.parse("start", lex);

		Integer r = e.parse("");
		System.out.println(r);
	}
}
