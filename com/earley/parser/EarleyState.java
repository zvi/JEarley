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

import com.earley.lexer.Symbol;


public class EarleyState<E, R> {
	public final EarleyRule<E, R> rule;
	public final int dot;
	public final int back;
	public final int left;
	public final int sub;
	public final Symbol symbol;


	public EarleyState(EarleyRule<E, R> rule, int dot, int back, int left, int sub, Symbol symbol) {
		this.rule = rule;
		this.dot = dot;
		this.back = back;
		this.left = left;
		this.sub = sub;
		this.symbol = symbol;
	}

	public String current() {
		return complete() ? null : rule.rhs(dot);
	}

	public boolean complete() {
		return dot >= rule.size();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean seperator() {
		return rule == null;
	}

	public String toString() {
		if ( rule == null ) {
			return "[ -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- ]";
		}

		int len = rule.rhs.size();
		StringBuilder sb = new StringBuilder(rule.lhs).append(":");
		for ( int i = 0; i <= len; ++i ) {
			if ( dot == i ) {
				sb.append(" .");
			} else {
				sb.append(" ");
			}

			if ( i < len ) {
				sb.append(rule.rhs.get(i));
			}
		}

		sb.append(" [B:").append(back)
			.append(", L:").append(left)
			.append(", S:").append(sub)
			.append(", SYM:").append(symbol)
			.append("]");

		return sb.toString();
	}
}
