/****************************************************************************
 **
 **   This code is an experimental implementation of an Earley Parser.
 **   Copyright (C) 2012 by Tzvi Rotshtein
 **
 **   This program is free software: you can redistribute it and/or modify
 **   it under the terms of the GNU General Public License as published by
 **   the Free Software Foundation, either version 3 of the License, or
 **   (at your option) any later version.
 **
 **   This program is distributed in the hope that it will be useful,
 **   but WITHOUT ANY WARRANTY; without even the implied warranty of
 **   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 **   GNU General Public License for more details.
 **
 **   You should have received a copy of the GNU General Public License
 **   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **
 ***************************************************************************/

package com.parser.earley.demo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lexer.ILexer;
import com.lexer.Symbol;
import com.parser.earley.EarleyException;
import com.parser.earley.EarleyParser;
import com.parser.earley.IEarleyAction;


public class EarleyDemo extends EarleyParser<Object, Integer>
{

	protected Integer textToResult(final Symbol symbol)
	{
		// Because we get all terminal symbols, like "+", "*", etc (in addition to numbers).
		return symbol.text != null ? Integer.valueOf(symbol.text) : new Integer(0);
	}


	public static void main(String[] args) throws EarleyException
	{
		TestLexer                     lex = new TestLexer("25 + 67 * -(5 - 8 + 16*3) * (16 - 3)");
		EarleyParser<Object, Integer> e   = new EarleyDemo();

		e.addRule("atom",      new String[] {"num"});
		e.addRule("atom",      new String[] {"-", "atom"},              new TestAction() { public Integer action(Object env, List<Integer> param) { return -param.get(1); } });
		e.addRule("atom",      new String[] {"(", "expr", ")"},         new TestAction() { public Integer action(Object env, List<Integer> param) { return param.get(1); } });
		e.addRule("prod",      new String[] {"atom"});
		e.addRule("prod",      new String[] {"prod", "*", "atom"},      new TestAction() { public Integer action(Object env, List<Integer> param) { return param.get(0) * param.get(2); } });
		e.addRule("prod",      new String[] {"prod", "/", "atom"},      new TestAction() { public Integer action(Object env, List<Integer> param) { return param.get(0) / param.get(2); } });
		e.addRule("sum",       new String[] {"prod"});
		e.addRule("sum",       new String[] {"sum", "+", "prod"},       new TestAction() { public Integer action(Object env, List<Integer> param) { return param.get(0) + param.get(2); } });
		e.addRule("sum",       new String[] {"sum", "-", "prod"},       new TestAction() { public Integer action(Object env, List<Integer> param) { return param.get(0) - param.get(2); } });
		e.addRule("expr",      new String[] {"sum"});
		e.addRule("start",     new String[] {"expr"});

		e.parse("start", lex);
		//e.dumpStates();

		Integer r = e.exec("");
		System.out.println(r);
	}
}


// Just to save on typing the actions. (avoid copying the generic definitions)
abstract class TestAction implements IEarleyAction<Object, Integer>
{
}


// Very crude and rudimentary lexical analyzer. You'd want to use another :)
class TestLexer implements ILexer
{
	private Pattern psp;
	private Pattern pnum;
	private Pattern pop;

	private String  text;


	public TestLexer(String text)
	{
		this.text = text;

		this.psp  = Pattern.compile("^\\s+(.*)$", Pattern.DOTALL);
		this.pnum = Pattern.compile("^(\\d+)(.*)$", Pattern.DOTALL);
		this.pop  = Pattern.compile("^([\\+\\-\\*\\/\\(\\)])(.*)$", Pattern.DOTALL);
	}


	@Override
	public Symbol nextSymbol()
	{
		Matcher m;

		// Skip the whitespaces.
		m = psp.matcher(text);
		if ( m.find() )
			text = m.group(1);

		// We consumed the entire input.
		if ( text.length() == 0 )
			return new Symbol(ILexer.EOF, null);

		m = pnum.matcher(text);
		if ( m.find() )
		{
			// Number.
			text = m.group(2);
			return new Symbol("num", m.group(1));
		}

		m = pop.matcher(text);
		if ( m.find() )
		{
			// Operator.
			text = m.group(2);
			return new Symbol(m.group(1), null);
		}

		return null;
	}
	
}
