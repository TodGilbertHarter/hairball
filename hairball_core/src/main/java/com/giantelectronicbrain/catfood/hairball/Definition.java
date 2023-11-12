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
 * A hairball dictionary entry. This defines a hairball word.
 * 
 * @author tharter
 *
 */
public class Definition {
	private final Word name; //Name of this word
	private final Token compileTime; //compile time behavior of this word
	private final Token runTime; // runtime behavior of this word
	
	/**
	 * Create a new Definition with the given name and behaviors.
	 * 
	 * @param name Word identifying this definition.
	 * @param compileTime token representing the compile time behavior of the word.
	 * @param runTime token representing the runtime behavior of the word.
	 */
	public Definition(Word name, Token compileTime, Token runTime) {
		this.name = name;
		this.compileTime = compileTime;
		this.runTime = runTime;
	}

	/**
	 * Get the definition's name.
	 * 
	 * @return the name as a word.
	 */
	public Word getName() {
		return name;
	}

	/**
	 * Get the compile time behavior token for the Definition.
	 * 
	 * @return the compileTime
	 */
	public Token getCompileTime() {
		return compileTime;
	}

	/**
	 * Get the runtime behavior token for the Definition.
	 * 
	 * @return the runTime
	 */
	public Token getRunTime() {
		return runTime;
	}

	@Override
	public String toString() {
		return "Definition [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Definition other = (Definition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
