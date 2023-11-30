/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.initialization;

/**
 * An initializer holds the initialized state of the application as a set of key/value
 * pairs. 
 * 
 * @author tharter
 *
 */
public interface IInitializer {

	/**
	 * Given a key return the corresponding initialized state object
	 * 
	 * @param configKey
	 * @return initialized object
	 * 
	 * @throws InitializationException if no object of the given key exists or can be initialized
	 */
	public abstract Object get(Object configKey) throws InitializationException;
	
	/**
	 * Store an initialized object on the initializer.
	 * @param configKey the key this object instance will be known by
	 * @param configValue the initialized object
	 */
	public abstract void set(Object configKey, Object configValue);
	
	/**
	 * Dump a copy of the entire initialized state to standard output.
	 */
	public abstract void print();
}
