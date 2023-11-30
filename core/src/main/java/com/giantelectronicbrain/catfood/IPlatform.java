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
package com.giantelectronicbrain.catfood;

import java.util.logging.Logger;

/**
 * Hides away all things which may depend on where we are executing, or under which
 * transpiling architecture (IE GWT vs J2CL or TeaVM, etc. or Client side vs Server side/JUnit). 
 * This is intended to help keep the rest of the code decoupled from these factors.
 * 
 * @author tharter
 *
 */
public interface IPlatform {

	/**
	 * Are we executing in a browser, or in some Java non-browser environment, such as JUnit.
	 * 
	 * @return boolean true if we are in the browser running in Javascript.
	 */
	public abstract boolean isClient();
	
	/**
	 * Get an implementation of a Logger. 
	 * 
	 * @param name name of the Logger to get
	 * @return Logger implementation
	 */
	public abstract Logger getLogger(String name);
	
	/**
	 * Get the overall Catfood platform version id string. This is established
	 * at build time for the version of Catfood. This is in SemVer format.
	 * 
	 * @return version string
	 */
	public abstract String getVersion();
}
