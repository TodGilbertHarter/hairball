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
package com.giantelectronicbrain.catfood.hairball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.buckets.IBucketDriver;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;
import com.giantelectronicbrain.catfood.buckets.fs.FsBucketDriverImpl;
//import com.giantelectronicbrain.catfood.buckets.fs.FsBucketDriverImpl.FsBucketDriverImplBuilder;
import com.giantelectronicbrain.catfood.exceptions.CatfoodApplicationException;
import com.giantelectronicbrain.catfood.exceptions.ExceptionIds;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.streams.ReadStream;

/**
 * WordStream to read from a file. This uses the Vertx asyncFile/ReadStream<Buffer> in order to
 * read from any source which can be supplied by Vertx as input without needing to pull the whole
 * input into memory first (IE large web requests and such should stream, as well as disk files).
 * 
 * <em>NOTE:</em>Further abstraction is going to be needed to make this work with arbitrary 
 * ReadStream<Buffer> implementations, but conceptually all the needed logic exists in this
 * class. Web-service based buckets for instance will require a bit more work.
 *
 * @author tharter
 * 
 */
public class BucketWordStream extends WordStream {
	private static final Logger log = StandAloneHairball.PLATFORM.getLogger(BucketWordStream.class.getName());

	private final FileSystem fileSystem;
	private final String objectName;
	private final IBucketName bucketName;
	private final IBucketDriver driver;
	private final IBucketObjectName boName;
	private ReadStream<Buffer> asyncFile = null;

	/**
	 * Construct a word stream via a filesystem bucket. Note: This is not as generic as 
	 * the final product SHOULD be, since it is polluted with FileSystem semantics. We will
	 * have to refactor getting the ReadStream<Buffer> part into the Buckets API at some point.
	 * 
	 * @param fileSystem Vertx fileSystem instance to use
	 * @param objectName name of the bucket object to read
	 * @param bucketName name of the bucket the object is in
	 */
	public BucketWordStream(FileSystem fileSystem, String objectName, String bucketName) {
		super();
		this.objectName = objectName;
		this.fileSystem = fileSystem;
//System.out.println("bucket name is: "+bucketName+", object name is: "+objectName);		
		driver = FsBucketDriverImpl.builder().fileSystem(fileSystem).build();
		this.bucketName = driver.makeBucketName(bucketName);
		this.boName = driver.makeBucketObjectName(this.bucketName, objectName);
		driver.readBucketObject(boName, result -> {
			log.fine("Got into BucketWordStream readBucketObject callback");
			if(result.succeeded()) {
				asyncFile = result.result();
				VertxBlockingInputStream is = new VertxBlockingInputStream(asyncFile);
				reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			} else {
				CatfoodApplicationException t = makeException(result.cause());
				result.cause().printStackTrace();
//				log.severe("failed to create reader "+t.getLocalizedMessage()+" details -> "+t.getDetails());
			}
		});
	}
	
	private CatfoodApplicationException makeException(Throwable cause) {
		return new CatfoodApplicationException(ExceptionIds.SERVER_ERROR,cause.getLocalizedMessage(),"Object: "+objectName);
	}

	private static final int TIME_OUT = 1000;
	
	/**
	 * Because creation of a reader is async we may need to wait for it to be
	 * instantiated here before reading data. 
	 */
	@Override
	protected String getNextLine() throws IOException {
		int times = 0;
		while(reader == null) {
			try {
				Thread.sleep(1);
				if(times++ >= TIME_OUT)
					return null;
			} catch (InterruptedException e) {
				log.severe("BucketWordStream interrupted. Returning premature end of file.");
				return null;
			}
		}
		return super.getNextLine();
	}

	@Override
	public boolean hasMoreTokens() throws IOException {
//System.out.println("CALLING hasMoreTokens in BucketWordStream");
//if(true)
//throw new IOException("FOOBY WOOBY");
		int times = 0;
		while(reader == null) {
//System.out.println("READER IS NULL");
			try {
				Thread.sleep(1);
				if(times++ >= TIME_OUT) {
//System.out.println("TIMED OUT, NO READER, RETURN FALSE");
					return false;
				}
			} catch (InterruptedException e) {
				log.severe("BucketWordStream interrupted. Returning premature end of file.");
				return false;
			}
		}
//System.out.println("CALLING SUPER HASMORETOKENS");
		return super.hasMoreTokens();
	}

	@Override
	public String getSource() {
		return this.boName.getName();
	}
	
	@Override
	public String getCurrentLocation() {
		String name = boName.getName();
		int lidx = name.lastIndexOf('/');
		if(lidx != -1)
			name = name.substring(0, lidx);
		else
			name = ".";
		return name;
//		return bucketName.getNameString();
	}

}
