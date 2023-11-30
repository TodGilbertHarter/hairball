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
package com.giantelectronicbrain.catfood.buckets.s3;

import java.util.Iterator;

import com.giantelectronicbrain.catfood.buckets.IBucket;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;

import lombok.Builder;
import lombok.Getter;

/**
 * S3 API implementation of a bucket.
 * 
 * @author tharter
 *
 */
@Getter
public class S3Bucket implements IBucket {
	private S3BucketDriverImpl bucketDriver;
	private IBucketName name;
//	private Bucket aBucket;

	/**
	 * Constructor to make a bucket which wraps an AWS
	 * bucket object.
	 * 
	 * @param abucket AWS bucket object to wrap.
	 */
	@Builder
	protected S3Bucket(S3BucketDriverImpl bucketDriver, IBucketName name) { // , bucket aBucket) {
		this.name = name;
		this.bucketDriver = bucketDriver;
	}

	@Override
	public String getNameString() {
		return getName().getNameString();
	}

	@Override
	public Iterator<IBucketObject> iterator() {
		return bucketDriver.getObjectIterator(this);
	}

}
