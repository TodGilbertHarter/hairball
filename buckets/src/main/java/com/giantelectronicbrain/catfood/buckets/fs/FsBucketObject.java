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
package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

/**
 * Implementation of a bucket object which is based on the VertX FileSystem asynchronous file handling API. This is a high
 * performance implementation which is well-suited for use in VertX-based applications.
 * 
 * @author tharter
 *
 */
public class FsBucketObject implements IBucketObject {
	private final FsBucketObjectName name;
	private final FsBucketDriverImpl driver;

	/**
	 * Create a bucket object. This is only invoked via the FsBucketDriverImpl. Note that instantiating
	 * a bucket object doesn't have any effect on the actual storage, it is simply an object which can
	 * represent an object with a given name in a bucket, who's name will be part of the object name. This
	 * method is normally just one step in a read or write operation on a bucket.
	 * 
	 * @param driver pointer to the driver which manages this object.
	 * @param name the name of this bucket object.
	 */
	protected FsBucketObject(FsBucketDriverImpl driver, FsBucketObjectName name) {
		this.driver = driver;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getName()
	 */
	@Override
	public IBucketObjectName getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getNameString()
	 */
	@Override
	public String getNameString() {
		return getName().getName();
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getContentsAsString()
	 */
	@Override
	public String getContentsAsString() throws BucketDriverException, IOException {
		return driver.getBucketObjectContentsAsString(this, StandardCharsets.UTF_8);
	}

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.IBucketObject#getContentsAsStream()
	 */
	@Override
	public ReadStream getContentsAsStream() throws BucketDriverException, IOException {
		return driver.getReadStream(this.getName());
	}

	@Override
	public void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler) throws IOException, BucketDriverException {
		driver.createBucketObject(this.getName(), is, handler);
	}

	@Override
	public Future<Void> setContentAsStream(ReadStream<Buffer> inputStream) throws IOException, BucketDriverException {
		Promise<Void> promise = Promise.promise();
		driver.createBucketObject(this.getName(), inputStream, (result) -> {
			promise.handle(result);
		});
		return promise.future();
	}

}
