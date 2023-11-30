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

package com.giantelectronicbrain.catfood.conf;

/**
 * Represents a failure of CatFood to process its configuration.
 * 
 * @author tharter
 *
 */
public class ConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String helpMessage;

	/**
	 * Create a ConfigurationException.
	 * 
	 * @param errorMessage text explaining what went wrong.
	 * @param helpMessage text explaining the usage of configuration parameters.
	 */
	public ConfigurationException(final String errorMessage, final String helpMessage) {
		super(errorMessage);
		this.helpMessage = helpMessage;
	}
	
	public String getHelpMessage() {
		return this.helpMessage;
	}
}
