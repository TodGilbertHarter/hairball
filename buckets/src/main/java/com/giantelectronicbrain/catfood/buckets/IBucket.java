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

package com.giantelectronicbrain.catfood.buckets;

/**
 * Abstract definition of a bucket. Each bucket has a name and can be iterated to
 * expose its contents, which is a series of IBucketObject instances.
 * 
 * @author tharter
 *
 */
public interface IBucket extends Iterable<IBucketObject> {

	/**
	 * Get the name of this bucket as an IBucketName.
	 * 
	 * @return IBucketName the name of the bucket.
	 */
	public IBucketName getName();
	
	/**
	 * Convenience method to get the bucket name as a string.
	 * 
	 * @return bucket name string.
	 */
	public String getNameString();

}
