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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.s3.model.S3Object;
import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import lombok.Builder;
import lombok.Getter;

@Getter
public class S3BucketObject implements IBucketObject{

	private IBucketObjectName name;
	private S3Object bucketContents;

	@Builder
	public S3BucketObject(IBucketObjectName name) {
		this.name = name;
	}
	
	public S3BucketObject(S3Object bucketContents) {
		this.bucketContents = bucketContents;
		this.name = new S3BucketObjectName(bucketContents.getKey(), S3BucketName.builder().nameString(bucketContents.getBucketName()).build());
	}

	@Override
	public String getContentsAsString() throws IOException {
/*		InputStream is = getContentsAsStream();
	    ByteArrayOutputStream into = new ByteArrayOutputStream();
	    byte[] buf = new byte[4096];
	    for (int n; 0 < (n = is.read(buf));) {
	        into.write(buf, 0, n);
	    }
	    into.close();
		is.close();
	    return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
	    */
		return null;
	}

	@Override
	public String getNameString() {
		return this.getName().getName();
	}

	@Override
	public ReadStream<Buffer> getContentsAsStream() throws IOException {
//		return bucketContents.getObjectContent();
		return null;
	}
	
	@Override
	public void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler) throws IOException {
//		bucketContents.setObjectContent(is);
	}

	@Override
	public Future<Void> setContentAsStream(ReadStream<Buffer> inputStream) throws IOException, BucketDriverException {
		// TODO Auto-generated method stub
		return null;
	}
}
