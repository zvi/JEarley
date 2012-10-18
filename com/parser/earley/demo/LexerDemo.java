package com.parser.earley.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lexer.ILexer;
import com.lexer.Symbol;


public class LexerDemo implements ILexer
{
	private Pattern psp;
	private Pattern pnum;
	private Pattern pop;

	private String  text;


	// Very crude and rudimentary lexical analyzer. You'd want to use another :)
	public LexerDemo(String text)
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
