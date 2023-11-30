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

import lombok.Builder;

/**
 * This object is used to encode the results of failed requests so they can be
 * routed back to the calling client as a standard JSON data structure. This makes
 * the client's job a lot easier and gives client-side developers a clear idea of
 * what went wrong.
 * 
 * @author tharter
 *
 */
@Builder
public class ErrorResult {
	private final ExceptionIds exceptionId = ExceptionIds.UNKNOWN;
	private final String message = "none";
	private final String details = "none";
}
