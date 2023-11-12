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
 * Literal tokens hold arbitrary literal data in the form of a Java object. This allows
 * 'static' data to be compiled into a definition. Its behavior is to push the data onto
 * the parameter stack.
 * 
 * Note: this is an immutable class.
 * 
 * @author tharter
 *
 */
public class LiteralToken implements Token {
	private final String name;
	protected Object data;

	/**
	 * Create a token which holds the object.
	 * 
	 * @param data
	 */
	public LiteralToken(String name, Object data) {
		this.data = data;
		this.name = name;
	}
	
	@Override
	public boolean execute(Interpreter interpreter) {
		interpreter.push(data);
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LiteralToken other = (LiteralToken) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LiteralToken [name=" + name + "]";
	}

	/**
	 * Get the name of the token.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the data associated with this token.
	 * 
	 * @return data as an object
	 */
	public Object getData() {
		return data;
	}
}
