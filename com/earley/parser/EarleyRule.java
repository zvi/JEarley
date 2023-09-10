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

import java.util.Arrays;
import java.util.List;


public class EarleyRule<E, R> {
	public final String lhs;
	public final List<String> rhs;
	public final IEarleyAction<E, R> action;


	public EarleyRule(String lhs, String[] rhs, IEarleyAction<E, R> action) {
		this.lhs = lhs;
		this.rhs = Arrays.asList(rhs);
		this.action = action;
	}

	public String rhs(int index) {
		return rhs.get(index);
	}

	public int size() {
		return rhs.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(lhs).append(":");
		for ( String s : rhs ) {
			sb.append(" ").append(s);
		}
		return sb.toString();
	}
}
