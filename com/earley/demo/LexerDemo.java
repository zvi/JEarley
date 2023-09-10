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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.earley.lexer.ILexer;
import com.earley.lexer.Symbol;


public class LexerDemo implements ILexer {
	private final Pattern psp;
	private final Pattern pnum;
	private final Pattern pop;

	private String text;


	// Very crude and rudimentary lexical analyzer. You'd want to use another :)
	public LexerDemo(String text) {
		this.text = text;
		this.psp  = Pattern.compile("^\\s+(.*)$", Pattern.DOTALL);
		this.pnum = Pattern.compile("^(\\d+)(.*)$", Pattern.DOTALL);
		this.pop  = Pattern.compile("^([+\\-*/()])(.*)$", Pattern.DOTALL);
	}

	@Override
	public Symbol nextSymbol() {
		Matcher m;

		// Skip the whitespaces.
		m = psp.matcher(text);
		if ( m.find() ) {
			text = m.group(1);
		}

		// We consumed the entire input.
		if ( text.isEmpty() ) {
			return new Symbol(ILexer.EOF, null);
		}

		m = pnum.matcher(text);
		if ( m.find() ) {
			// Number.
			text = m.group(2);
			return new Symbol("num", m.group(1));
		}

		m = pop.matcher(text);
		if ( m.find() ) {
			// Operator.
			text = m.group(2);
			return new Symbol(m.group(1), null);
		}

		return null;
	}
}
