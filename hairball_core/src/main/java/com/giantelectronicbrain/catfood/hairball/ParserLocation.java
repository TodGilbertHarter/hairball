/**
 * This software is Copyright (C) 2021 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.giantelectronicbrain.catfood.hairball;

/**
 * This is used to hold information on where in the input the interpreter
 * is at a given time. It can be used to generate error messages, etc.
 * 
 * @author tharter
 *
 */
public class ParserLocation {
	final String source;
	final int column;
	final int line;
	
	/**
	 * Create a parser location.
	 * 
	 * @param source
	 * @param line
	 * @param column
	 */
	public ParserLocation(String source, int line, int column) {
		this.source = source;
		this.line = line;
		this.column = column;
	}
	
	/**
	 * Create a parser location with the values taken from the given IWordStream.
	 * 
	 * @param wordStream The IWordStream to get the location from.
	 */
	public ParserLocation(IWordStream wordStream) {
		this(wordStream.getSource(),wordStream.getLine(),wordStream.getColumn());
	}
	
	/**
	 * Make an error message indicating source and location of an error in hairball source.
	 * 
	 * @param eMsg the actual error
	 * @param wordStream stream being parsed when it happened
	 * @return complete message
	 */
	public String makeErrorMessage(String eMsg) {
		return eMsg + " in "+this.source+" at line "+this.line+", column "+this.column;
	}

}
