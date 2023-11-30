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
package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;
import java.util.Iterator;

import com.giantelectronicbrain.catfood.buckets.IBucket;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;

/**
 * @author tharter
 *
 */
public class FsBucket implements IBucket {
	private final FsBucketDriverImpl bucketDriver;
	private final FsBucketName bucketName;
	
	protected FsBucket(FsBucketDriverImpl bucketDriver, FsBucketName bucketName) {
		this.bucketDriver = bucketDriver;
		this.bucketName = bucketName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IBucketObject> iterator() {
		try {
			return bucketDriver.getBucketObjectIterator(this);
		} catch (IOException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucket#getName()
	 */
	@Override
	public IBucketName getName() {
		return this.bucketName;
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucket#getNameString()
	 */
	@Override
	public String getNameString() {
		return this.bucketName.getNameString();
	}

}
