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

/**
 * Variable tokens hold arbitrary data in the form of a Java object. This allows
 * Its behavior is to push itself onto the parameter stack.
 * 
 * @author tharter
 *
 */
public class VariableToken extends LiteralToken {

	/**
	 * Create a token which holds the object.
	 * 
	 * @param data
	 */
	public VariableToken(String name, Object data) {
		super(name,data);
	}
	
	@Override
	public boolean execute(Interpreter interpreter) {
		interpreter.push(this);
		return true;
	}

	@Override
	public String toString() {
		return "VariableToken [name=" + getName() + "]";
	}

	/**
	 * Change the value of the data in this object.
	 * 
	 * @param data new data
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
