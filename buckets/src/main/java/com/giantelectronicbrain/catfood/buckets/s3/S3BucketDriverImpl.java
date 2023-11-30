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


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Optional;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucket;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;
import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObject;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class which implements an IBucketDriver which works on
 * top of the DELL ECS S3 appliance using AWS API as a REST client
 * library.
 * 
 * This includes a factory build() method to instantiate a copy of
 * the driver.
 * 
 * @author tharter
 *
 */
@Slf4j
public class S3BucketDriverImpl { //implements IBucketDriver {
	private AmazonS3Client s3Client;
	private BasicAWSCredentials awsCredentials;
//	private String accessKey;
//	private String secretKey;
//	private URL endPoint;

	@SuppressWarnings("deprecation")
	@Builder
	private S3BucketDriverImpl(String accessKey, String secretKey, URL endPoint) {
//		this.accessKey = accessKey;
//		this.secretKey = secretKey;
//		this.endPoint = endPoint;
		this.awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
		s3Client = new AmazonS3Client(this.awsCredentials);

        S3ClientOptions options = new S3ClientOptions();
        options.setPathStyleAccess(true);
        s3Client.setEndpoint(endPoint.toExternalForm());
        s3Client.setS3ClientOptions(options);
	}
	
	/**
	 * Make sure that a bucket name is an S3BucketName. If it isn't throw IllegalArgumentException.
	 * 
	 * @param bucketName IBucketName to test.
	 * @throws IllegalArgumentException if it isn't an instance of S3BucketName.
	 */
	private void isS3BucketName(IBucketName bucketName) throws IllegalArgumentException {
		if(!(bucketName instanceof S3BucketName)) {
			throw new IllegalArgumentException("You must supply an S3BucketName instance to this driver.");
		}
	}
	
	public IBucket createBucket(IBucketName bucketName) throws BucketDriverException {
		try {
			isS3BucketName(bucketName);
			Bucket abucket = this.s3Client.createBucket(bucketName.getNameString());
			return S3Bucket.builder().bucketDriver(this).name(S3BucketName.builder().nameString(abucket.getName()).build()).build();
		} catch (AmazonServiceException e) {
			throw new BucketDriverException("",e);
		}
	}
	
//	@Override
	public boolean deleteBucket(IBucketName bucketName) throws BucketDriverException{
		try {
			isS3BucketName(bucketName);	
			s3Client.deleteBucket(bucketName.getNameString());
			return true;
		} catch (AmazonServiceException e) {
			if(e.getStatusCode() == 404 && e.getErrorCode().equals("NoSuchBucket"))
				return false;
			throw new BucketDriverException("Failed to delete bucket", e);
		}
	}
	
	
//	@Override
	public Optional<IBucket> getBucket(IBucketName bucketName) throws BucketDriverException {
		isS3BucketName(bucketName);
		IBucket result = null;
		if(this.s3Client.doesBucketExist(bucketName.getNameString())) {
			result = S3Bucket.builder().bucketDriver(this).name(bucketName).build();
		}
		return Optional.ofNullable(result);
	}

//	@Override
	public Optional<IBucketObject> getBucketObject(IBucketObjectName objectName) throws BucketDriverException {
		S3Object s3obj = null;
		try {
			s3obj = s3Client.getObject(new GetObjectRequest(objectName.getBucketName().getNameString(), objectName.getName()));
		} catch (AmazonServiceException e) {
			if(e.getStatusCode() == 404 && e.getErrorCode().equals("NoSuchKey")) {
//				throw new ObjectNotFoundException("bucket object name not found " + objectName.getName(), e);
				return Optional.empty();
			} else {
				throw new BucketDriverException("failed to get object " + objectName.getName(), e);
			}
		} catch (SdkClientException e) {
			throw new BucketDriverException("AWS SDK Failure",e);
		}
		
		S3BucketObject s3bucketObj = new S3BucketObject(s3obj);
		
		return Optional.of(s3bucketObj);
	}
	
//	@Override
	public boolean createBucketObject(IBucketObjectName bucketObjectName, String content) throws BucketDriverException{
		isS3BucketName(bucketObjectName.getBucketName());

		try {
			s3Client.putObject(bucketObjectName.getBucketName().getNameString(), bucketObjectName.getName(), content);
			return true;
			
		}catch(AmazonServiceException e) {
			throw new BucketDriverException("Failed to create Bucket Object", e);
		}
	
	}
	
//	@Override
	public boolean createBucketObject(IBucketObjectName bucketObjectName, InputStream content)
			throws BucketDriverException {
		isS3BucketName(bucketObjectName.getBucketName());

		try {
			ObjectMetadata metaData = new ObjectMetadata();
			s3Client.putObject(bucketObjectName.getBucketName().getNameString(), bucketObjectName.getName(), content, metaData);
			return true;
			
		}catch(AmazonServiceException e) {
			throw new BucketDriverException("Failed to create Bucket Object", e);
		}
	
	}
	
//	@Override
	public boolean deleteBucketObject(IBucketObjectName bucketObjectName) throws BucketDriverException {
		try {
			s3Client.deleteObject(bucketObjectName.getBucketName().getNameString(), bucketObjectName.getName());
			return true;
		} catch (AmazonServiceException e) {
			throw new BucketDriverException("Failed to delete bucket object", e);
		}
	}
	
	/**
	 * Internal method called back to by S3Bucket to get bucket objects.
	 * 
	 * @param s3Bucket The containing S3Bucket.
	 * @return Stream<IBucketObject> the contents of the bucket
	 */
	protected Iterator<IBucketObject> getObjectIterator(S3Bucket s3Bucket) {
		
		return new Iterator<IBucketObject>() {
			private Iterator<S3ObjectSummary> s3ObjItr = S3Objects.inBucket(s3Client, s3Bucket.getNameString()).iterator();
			
			@Override
			public boolean hasNext() {
				log.debug("calling hasnext for an object iterator, s3ObjIter is: %s",s3ObjItr);
				return s3ObjItr.hasNext();
			}

			@Override
			public IBucketObject next() {
				log.debug("calling next for an object iterator, s3ObjIter is: %s",s3ObjItr);
				S3ObjectSummary summary = s3ObjItr.next();
				S3Object obj = s3Client.getObject(s3Bucket.getNameString(), summary.getKey());
				log.debug("got an S3Object of %s",obj);
				return new S3BucketObject(obj);
			}
		};
	}
	
//	@Override
	public Iterator<IBucket> getBucketIterator() {
		S3BucketDriverImpl that = this;
		
		return new Iterator<IBucket>() {
			private Iterator<Bucket> bItr = s3Client.listBuckets().iterator();

			@Override
			public boolean hasNext() {
				return bItr.hasNext();
			}

			@Override
			public IBucket next() {
				S3Bucket bucket = S3Bucket.builder().bucketDriver(that).name(S3BucketName.builder().nameString(bItr.next().getName()).build()).build();
				return bucket;
			}
			
		};
	}

//	@Override
	public IBucketName makeBucketName(String name) throws IllegalArgumentException {
		return S3BucketName.builder().nameString(name).build();
	}

//	@Override
	public IBucketObjectName makeBucketObjectName(IBucketName bucketName, String name) throws IllegalArgumentException {
		isS3BucketName(bucketName);
		return S3BucketObjectName.builder().name(name).bucketName(bucketName).build();
	}

//	@Override
	public IBucketDriver createBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver getBucket(IBucketName bucketName, Handler<AsyncResult<IBucket>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver getBucketObject(IBucketObjectName objectName, Handler<AsyncResult<IBucketObject>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver deleteBucket(IBucketName bucketName, Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver deleteBucketObject(IBucketObjectName bucketObjectName, Handler<AsyncResult<Void>> hanlder) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, String content,
			Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver createBucketObject(IBucketObjectName bucketObjectName, InputStream content,
			Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver makeBucketName(String name, Handler<AsyncResult<IBucketName>> handler) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public IBucketDriver makeBucketObjectName(IBucketName bucketName, String name,
			Handler<AsyncResult<IBucketObjectName>> handler) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public String getBucketObjectContentsAsString(IBucketObject bucketObject, Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public ReadStream<Buffer> getReadStream(FsBucketObject IBucketObject) {
		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
	public void createBucketObject(IBucketObjectName name, ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void createBucketObject(IBucketObjectName name, Handler<AsyncResult<WriteStream<Buffer>>> handler) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void setContentsAsStream(ReadStream<Buffer> is, Handler<AsyncResult<Void>> handler)
//			throws IOException, BucketDriverException {
//		// TODO Auto-generated method stub
//		
//	}


}
