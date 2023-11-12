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
 * A hairball parser token.
 * 
 * @author tharter
 *
 */
public class Word {
	private final String value;
	
	/**
	 * Represents a token in the input stream.
	 * 
	 * @param value
	 */
	public Word(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() == String.class)
			return obj.equals(this.value);
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	/**
	 * Return true of this Word matches the given string.
	 * 
	 * @param value String to match
	 * @return true if the two are equal
	 */
	public boolean matches(String value) {
		return this.value.equals(value);
	}

	/**
	 * Get the actual String which maches this Word.
	 * 
	 * @return String value of the Word's token.
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Word [value=" + value + "]";
	}

}
