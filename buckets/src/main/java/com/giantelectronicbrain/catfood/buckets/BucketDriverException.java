/**
 * This software is Copyright (C) 2016 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.buckets;

import com.giantelectronicbrain.catfood.exceptions.CatfoodApplicationException;

/**
 * 
 * Base class for exceptions thrown by bucket API.
 * 
 * @author tharter
 *
 */
public class BucketDriverException extends CatfoodApplicationException {

	private static final long serialVersionUID = -8122403196172953459L;

	public BucketDriverException(String message, Throwable cause) {
		super(null,message,null);
		this.initCause(cause);
	}


}
