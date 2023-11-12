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
 * Interface for vocabularies, which are simply collections of definitions we
 * can look up by parserToken.
 * 
 * @author tharter
 *
 */
public interface IVocabulary {
	/**
	 * Look up a parserToken in this vocabulary. If it exists
	 * then return its Definition, otherwise return null.
	 * 
	 * @param word to search for
	 * @return its definition, or null
	 */
	public abstract Definition lookUp(Word parserToken);
	
	/**
	 * Add a definition to the vocabulary.
	 * 
	 * @param newDefinition
	 */
	public abstract void add(Definition newDefinition);

	/**
	 * Get the name of the vocabulary.
	 * 
	 * @return vocabulary name
	 */
	public abstract String getName();
}
