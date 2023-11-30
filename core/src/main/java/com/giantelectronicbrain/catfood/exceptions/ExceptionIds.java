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
package com.giantelectronicbrain.catfood.exceptions;

/**
 * Ids for exceptions to be returned to REST clients, or logged.
 * 
 * @author tharter
 *
 */
public enum ExceptionIds {
	BAD_INPUTS("BAD_INPUTS"),
	SERVER_ERROR("SERVER_ERROR"),
	NO_PERMISSION("NO_PERMISSION"),
	UNKNOWN("UNKNOWN"),
	DATA_ERROR("DATA_ERROR");
	
	private String message;
	
	public String getMessage() {
		return this.message;
	}
	
	private ExceptionIds(String message) {
		this.message = message;
	}

	/**
	 * Translate some common http codes to ExceptionIds. This can
	 * be used as a fallback for deciding what errors to output.
	 * 
	 * @param statusCode
	 * @return
	 */
	public static ExceptionIds fromStatusCode(int statusCode) {
		ExceptionIds result = UNKNOWN;
		switch(statusCode) {
		case 500:
			result = SERVER_ERROR;
			break;
		case 400:
			result = BAD_INPUTS;
			break;
		case 401:
		case 403:
			result = NO_PERMISSION;
			break;
		}
		return result;
	}
}
