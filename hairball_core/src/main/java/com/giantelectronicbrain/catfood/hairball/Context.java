/**
 * This software is Copyright (C) 2020 Tod G. Harter. All rights reserved.
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

import java.util.List;

/**
 * An inner interpreter execution context. This is basically like an
 * instruction pointer, but it also includes the current stream of
 * tokens being executed.
 * 
 * @author tharter
 *
 */
public class Context {
	private List<Token> instructions;
	private int instructionPointer;
	private boolean continueFlag = true;

	/**
	 * Create a new execution context with the given instructions and default
	 * instruction pointer value of zero.
	 * 
	 * @param instructions
	 */
	public Context(List<Token> instructions) {
		this(instructions,0);
	}
	
	/**
	 * Create a new context with the given instructions and pointer position. If
	 * the instructionPointer value is not legal, then throw IllegalArgumentException
	 * 
	 * @param instructions
	 * @param instructionPointer
	 */
	public Context(List<Token> instructions, int instructionPointer) {
		if(instructionPointer < 0 || instructionPointer >= instructions.size())
			throw new IllegalArgumentException("illegal value "+instructionPointer+" cannot be set");
		this.instructions = instructions;
		this.instructionPointer = instructionPointer;
	}

	/**
	 * Unset the continue flag on this Context.
	 */
	public void quit() {
		this.continueFlag = false;
	}

	/**
	 * Test the continue flag.
	 * 
	 * @return true if quit has been called on this context.
	 */
	public boolean isContinue() {
		return this.continueFlag;
	}
	
	/**
	 * Get the current ip value.
	 * 
	 * @return current value of the ip
	 */
	public int getIp() {
		return instructionPointer;
	}
	
	/**
	 * Set the instruction pointer to a new value. If the value is not legal,
	 * then throw IllegalArgumentException.
	 * 
	 * @param newIpValue the new IP value to set
	 * @return
	 */
	public int setIp(int newIpValue) {
		if(newIpValue < 0 || newIpValue >= instructions.size())
			throw new IllegalArgumentException("illegal value "+newIpValue+" cannot be set");
		int oldIp = instructionPointer;
		instructionPointer = newIpValue;
		return oldIp;
	}
	
	public Token getNextToken() {
		return instructionPointer >= instructions.size() ? null : instructions.get(instructionPointer++);
	}
}
