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

package com.parser.earley;


public class EarleyException extends Exception
{
	private static final long serialVersionUID = -5505589987313694923L;


	public EarleyException()
	{
		super();
	}


	public EarleyException(String message)
	{
		super(message);
	}


	public EarleyException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public EarleyException(Throwable cause)
	{
		super(cause);
	}

}
