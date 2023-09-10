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

package com.earley.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.earley.lexer.ILexer;
import com.earley.lexer.Symbol;


/**
 * The parser holds an internal state and thus is not thread-safe.
 */
public abstract class EarleyParser<E, R> {
	private final Map<String, List<EarleyRule<E, R>>> rules;
	private final List<EarleyState<E, R>> states;
	private int currentSet;
	private Symbol symbol;


	public EarleyParser() {
		this.rules = new HashMap<>();
		this.states = new ArrayList<>();
	}

	//
	// Helper methods.
	//

	private boolean terminal(String name) {
		return !rules.containsKey(name);
	}

	public static String[] rule(String... args) {
		return args;
	}

	public void addRule(String lhs, String[] rhs) {
		addRule(lhs, rhs, new IEarleyAction.ReturnFirst<>());
	}

	public void addRule(String lhs, String[] rhs, IEarleyAction<E, R> action) {
		List<EarleyRule<E, R>> ruleList;
		if ( !rules.containsKey(lhs) ) {
			rules.put(lhs, ruleList = new ArrayList<>());
		} else {
			ruleList = rules.get(lhs);
		}

		ruleList.add(new EarleyRule<E, R>(lhs, rhs, action));
	}

	private void addState(EarleyRule<E, R> rule, int dot, int back, int left, int sub) {
		int size = states.size();
		for ( int i = currentSet; i < size; ++i )
		{
			EarleyState<E, R> state = states.get(i);
			if ( state.rule == rule && state.dot == dot && state.back == back ) {
				return;
			}
		}

		states.add(new EarleyState<>(rule, dot, back, left, sub, symbol));
	}

	/**
	 * The separator is a special marker that separates related sets of states in the list.
	 */
	private void addSeparator()	{
		addState(null, 0, 0, 0, 0);
	}

	//
	// The actual parser code, composed of the three phases: Predict, Complete & Scan.
	//

	private void init(String startRule) {
		symbol = null;
		currentSet = 0;
		states.clear();

		// Create a single top level rule. It's a special rule that accepts the EOF marker.
		addSeparator();
		currentSet = 1;
		addState(new EarleyRule<>("YYSTART", new String[] {startRule, "EOF"},
				new IEarleyAction.ReturnFirst<>()), 0, currentSet, 0, 0);

		closure();
	}

	private void closure() {
		int top;

		do {
			top = states.size();
			for ( int i = currentSet; i < states.size(); ++i )
			{
				EarleyState<E, R> state = states.get(i);
	
				if ( state.complete() ) {
					complete(i);
				} else if ( !terminal(state.current()) ) {
					predict(i);
				}
			}
		} while ( top != states.size() );
	}

	private void predict(int i) {
		EarleyState<E, R> state = states.get(i);
		String rhs = state.current();

		for ( EarleyRule<E, R> rule : rules.get(rhs) ) {
			addState(rule, 0, currentSet, 0, 0);
		}
	}

	private void complete(int i) {
		EarleyState<E, R> state = states.get(i);
		int j = state.back;
		String lhs = state.rule.lhs;

		for ( ; j < states.size() && !states.get(j).seperator(); ++j )
		{
			EarleyState<E, R> pstate = states.get(j);

			if ( lhs.equals(pstate.current()) ) {
				addState(pstate.rule, pstate.dot + 1, pstate.back, j, i);
			}
		}
	}

	private void scan() {
		for ( int i = currentSet; !states.get(i).seperator(); ++i )
		{
			EarleyState<E, R> state = states.get(i);

			if ( symbol.symbol.equals(state.current()) ) {
				addState(state.rule, state.dot + 1, state.back, i, 0);
			}
		}
	}

	//
	// Trigger the parser and execute the rules.
	//

	public void parse(String startState, ILexer lexer) {
		int changed;

		init(startState);

		do {
			symbol = lexer.nextSymbol();

			addSeparator();
			int nextSet = states.size();

			scan();
			currentSet = nextSet;
			closure();

			changed = states.size() - currentSet;
		} while ( changed > 0 && !symbol.symbol.equals(ILexer.EOF) );

		// Check for potential parsing problems.
		if ( changed == 0 ) {
			parseError(lexer);
		}

		if ( changed > 1 ) {
			ambiguityError(lexer);
		}
	}

	@SuppressWarnings("unused")
	protected void parseError(ILexer lexer) {
		throw new EarleyException("Parse error while reading symbol: " + symbol.text + " (" + symbol.symbol + ")");
	}

	@SuppressWarnings("unused")
	protected void ambiguityError(ILexer lexer) {
		throw new EarleyException("Input was ambiguous with this grammar");
	}

	private R parse(E env, int i) {
		EarleyState<E, R> currentState = states.get(i);
		EarleyState<E, R> state = currentState;
		List<EarleyState<E, R>> sibling = new ArrayList<>();

		while ( state.left > 0 && state.dot > 0 ) {
			sibling.add(state);
			state = states.get(state.left);
		}

		int size = sibling.size();
		List<R> data = new ArrayList<>(size);
		for ( int j = size - 1; j >= 0; --j ) {
			EarleyState<E, R> sib = sibling.get(j);
			data.add(sib.sub > 0 ? parse(env, sib.sub) : textToResult(sib.symbol));
		}

		return currentState.rule.action.action(env, data);
	}

	public R parse(E env) {
		return parse(env, states.size() - 1);
	}

	// All the objects we return are of type "R" so this method converts terminal
	// symbols into type "R" (terminal symbols can be numbers but also operators and more).
	protected abstract R textToResult(final Symbol symbol);

	@SuppressWarnings("unused")
	public void dumpStates() {
		int index = 0;
		for ( EarleyState<E, R> state : states ) {
			System.out.println("[" + index + "] " + state);
			++index;
		}
	}
}
