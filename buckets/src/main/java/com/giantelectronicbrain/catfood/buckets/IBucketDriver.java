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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

/**
 * Interface abstraction for accessing bucket stores. This is intended to work with things like
 * Amazon AWS S3, Swift, and various flavors of these and other similar APIs. By implementing this
 * interface you should be able to use these services interchangeably, implement your application on
 * top of different service provider libraries, etc.
 * 
 * @author tharter
 *
 */
public interface IBucketDriver {

	/**
	 * Create a new bucket with the given name.
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @return IBucket the new bucket.
	 * 
	 * @throws BucketDriverException if there is an error.
	 */
	public IBucket createBucket(IBucketName bucketName) throws BucketDriverException;
	
	/**
	 * Asynchronously create a new bucket with the given name. The handler will 
	 * be invoked when the bucket has been created.
	 * 
	 * @param bucketName IBucketName the name of the new bucket.
	 * @param handler invoked when the bucket is created.
	 * 
	 * @return the bucket driver instance
	 */
	public IBucketDriver createBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler);
	
	/**
	 * Get a bucket with the given name. If no such bucket exists then return a 
	 * void optional.
	 * 
	 * @param bucketName IBucketName the name of the bucket to get.
	 * @return Optional<IBucket> the bucket. Will be a void optional if no such bucket exists.
	 * 
	 * @throws BucketDriverException if there is an error.
	 */
	public Optional<IBucket> getBucket(IBucketName bucketName) throws BucketDriverException;

	/**
	 * Asynchronously get a bucket with the given name. Call the handler when it is available.
	 * 
	 * @param bucketName IBucketName the name of the bucket.
	 * @param handler called when the bucket has been fetched
	 */
	public IBucketDriver getBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler);
	
	/**
	 * Get a specific named object. This just returns the bucket object associated
	 * with the name, not the contents.
	 * 
	 * @param objectName name of the object
	 * @return Optional<IBucketObject> the object, or void if it doesn't exist
	 * 
	 * @throws BucketDriverException if something goes wrong.
	 */
	public Optional<IBucketObject> getBucketObject(IBucketObjectName objectName) throws BucketDriverException;

	/**
	 * Asynchronously get a bucket object and call the handler when it is ready.
	 * 
	 * @param objectName name of the object
	 * @param handler handles the returned object
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver getBucketObject(IBucketObjectName objectName, Handler<AsyncResult<IBucketObject>> handler);
	
	/**
	 * Delete a bucket.
	 * 
	 * @param bucketName bucket to delete
	 * @return true on success, false if no bucket was deleted (IE no such bucket exists)
	 * 
	 * @throws BucketDriverException if there is an error during bucket deletion
	 */
	public boolean deleteBucket(IBucketName bucketName) throws BucketDriverException;

	/**
	 * Asynchronously delete a bucket.
	 * 
	 * @param bucketName bucket to delete
	 * @param handler handle deletion
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver deleteBucket(IBucketName bucketName, Handler<AsyncResult<Void>> handler);
	
	/**
	 * Get an iterator on all buckets In the scope of the currently initialized driver.
	 * 
	 * @return a bucket iterator
	 * 
	 * @throws BucketDriverException if an iterator cannot be created
	 */
	public Iterator<IBucket> getBucketIterator() throws BucketDriverException;

	/**
	 * Delete an object from a bucket.
	 * 
	 * @param bucketObjectName the name of the object
	 * @return true if object deleted false if not
	 * 
	 * @throws BucketDriverException if there is an error during object deletion
	 */
	public boolean deleteBucketObject(IBucketObjectName bucketObjectName) throws BucketDriverException;

	/**
	 * Asynchronously delete an object from a bucket.
	 * 
	 * @param bucketObjectName the name of the object
	 * @param handler handle the results
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver deleteBucketObject(IBucketObjectName bucketObjectName, Handler<AsyncResult<Void>> hanlder);
	
	/**
	 * Create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content string to set the object's value to
	 * @return true if object was created, false otherwise
	 * 
	 * @throws BucketDriverException if there was an error during object creation
	 * @throws UnsupportedEncodingException if the string cannot be encoded
	 */
	public boolean createBucketObject(IBucketObjectName bucketObjectName, String content) throws BucketDriverException, UnsupportedEncodingException;
	
	/**
	 * Asynchronously create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content string to set the object's value to
	 * @param handler handle the results
	 * @throws BucketDriverException 
	 */
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, String content, Handler<AsyncResult<Void>> handler) throws BucketDriverException;

	/**
	 * Create a bucket object and stream the contents of the given ReadStream<Buffer> into it.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content ReadStream<Buffer> holding new bucket contents
	 * @return true if object was created, false otherwise
	 * 
	 * @throws BucketDriverException if there was an error during object creation
	 */
	public boolean createBucketObject(IBucketObjectName bucketObjectName, ReadStream<Buffer> content) throws BucketDriverException;

	/**
	 * Asynchronously create a bucket object with the given content.
	 * 
	 * @param bucketObjectName name of the object
	 * @param content ReadStream<Buffer> holding new bucket contents
	 * @param handler handle the results
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, ReadStream<Buffer> content, Handler<AsyncResult<Void>> handler);

	/**
	 * Create a bucket name object with the given string as its value. Note that implementations may put restrictions on 
	 * the values allowed for these names.
	 * 
	 * @param name the string value of the bucket's name.
	 * @return an IBucketName
	 * 
	 * @throws IllegalArgumentException if the string value is not legal in the implementation
	 */
	public IBucketName makeBucketName(String name) throws IllegalArgumentException;
	
	/**
	 * Create a bucket name object with the given string as its value. Note that implementations may put restrictions on 
	 * the values allowed for these names.
	 * 
	 * @param name the string value of the bucket's name.
	 * @param handler handle the results
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver makeBucketName(String name, Handler<AsyncResult<IBucketName>> handler);
	
	/**
	 * Create a bucket object name with the given string value and in the scope of the given IBucketName. Both the context
	 * and the implementation may restrict which values are allowed for an object.
	 * 
	 * @param bucketName The bucket name under which this object name will exist
	 * @param name the string value of the object's name
	 * @return an IBucketObjectName with the given value
	 * 
	 * @throws IllegalArgumentException if this name violates the rules of the implementation naming scheme
	 */
	public IBucketObjectName makeBucketObjectName(IBucketName bucketName, String name) throws IllegalArgumentException;

	/**
	 * Create a bucket object name with the given string value and in the scope of the given IBucketName. Both the context
	 * and the implementation may restrict which values are allowed for an object name.
	 * 
	 * @param bucketName The bucket name under which this object name will exist
	 * @param name the string value of the object's name
	 * @param handler handle the results
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver makeBucketObjectName(IBucketName bucketName, String name, Handler<AsyncResult<IBucketObjectName>> handler) throws IllegalArgumentException;

	/**
	 * Get the contents of a bucket object as a string. Note that this is only going to work for buckets which
	 * contain text, and is not an efficient way to process large objects.
	 * 
	 * @param bucketObject the object who's contents to get.
	 * @param charset the character set which encodes the texts code points
	 * @throws BucketDriverException if something goes wrong
	 * 
	 * @return contents of the bucket object as a string.
	 */
	public String getBucketObjectContentsAsString(IBucketObject bucketObject, Charset charset) throws BucketDriverException;

	/**
	 * Given a bucket object, return a ReadStream<Buffer> which will access its data.
	 * 
	 * @param fsBucketObject a bucket object.
	 * @return a ReadStream which will read the object's data.
	 */
	public ReadStream<Buffer> getReadStream(IBucketObjectName IBucketObject);

	/**
	 * Create a bucket object and then call a handler to populate it. The handler will be passed the WriteStream<Buffer>
	 * which will accept the data for this object. The handler needs to obtain that data from somewhere and write it to
	 * the object.
	 * 
	 * @param name the name of the bucket object to be created.
	 * @param handler handler which populates the WriteStream representing the contents of this object.
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver createBucketObject(IBucketObjectName name, Handler<AsyncResult<WriteStream<Buffer>>> handler);

	/**
	 * Asynchronously read data from a bucket object, calling the provided handler when
	 * a ReadStream<Buffer> is ready. 
	 * 
	 * @param bucketObjectName
	 * @param handler
	 * 
	 * @return the bucket driver
	 */
	public IBucketDriver readBucketObject(IBucketObjectName bucketObjectName, Handler<AsyncResult<ReadStream<Buffer>>> handler);

}
