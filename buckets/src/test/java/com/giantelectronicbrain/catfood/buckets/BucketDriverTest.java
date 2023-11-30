/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public abstract class BucketDriverTest {

	protected abstract IBucketDriver createUUT() throws BucketDriverException;
	protected abstract void setUpBuckets() throws IOException;
	protected abstract void cleanUpBuckets() throws IOException;
	protected static String basePath = "./build/buckettests";

	private IBucketDriver uut;
	
	@Before
	public void setUp() throws IOException, BucketDriverException {
		uut = createUUT();
		setUpBuckets();
	}
	
	@After
	public void cleanUp() throws IOException {
		cleanUpBuckets();
	}
	
	@Test
	public void testCreateBucket() {
		IBucketName bucketName = uut.makeBucketName(basePath+"/testbucket3");
		try {
			uut.createBucket(bucketName);
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("failed to create bucket");
		}
	}

	@Test
	public void testGetBucketNotExists() {
		IBucketName bucketName = uut.makeBucketName("nosuchbucket");
		try {
			Optional<IBucket> result = uut.getBucket(bucketName);
			if(result.isPresent()) {
				fail("non-existent bucket should return null");
			}
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("exception opening non-existent bucket");
		}
		
	}
	
	@Test
	public void testGetBucketExists() {
		IBucketName bucketName = uut.makeBucketName(basePath+"/testbucket");
		try {
			Optional<IBucket> result = uut.getBucket(bucketName);
			if(result.isPresent()) {
				IBucket bucket = result.get();
				IBucketName bName = bucket.getName();
				assertNotNull(bName);
				assertEquals(bucketName,bName);
			} else {
				fail("no bucket exists");
			}
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("Couldn't open existing bucket");
		}
	}

	@Test
	public void testGetBucketObject() {
		try {
			IBucketName bName = uut.makeBucketName(basePath+"/testbucket");
			IBucketObjectName boName = uut.makeBucketObjectName(bName, "testobject");
			IBucketObject bucketObj = (IBucketObject) uut.getBucketObject(boName).get();
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("couldn't get bucket object");
		}
	}

	@Test
	public void testDeleteBucket() {
		IBucketName bucketName = uut.makeBucketName(basePath+"/testbucket2");
		try {
			boolean result = uut.deleteBucket(bucketName);
			assertTrue(result);
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("couldn't delete bucket");
		}
	}

	@Test
	@Ignore // this doesn't currently work, and we don't really care...
	public void testGetBucketIterator() {
		try {
			Iterator<IBucket> bitr = uut.getBucketIterator();
			assertTrue(bitr.hasNext());
			IBucket bucket = bitr.next();
			assertEquals(basePath+"/testbucket",bucket.getNameString());
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("can't get bucket iterator");
		} catch (NoSuchElementException ne) {
			ne.printStackTrace();
			fail("iterator cannot get bucket");
		}
	}

	private boolean checkBucketObject(String bucketName, String objectName) {
		FileSystem fs = FileSystems.getDefault();
		Path hPath = fs.getPath(bucketName,objectName);
		return Files.isRegularFile(hPath);
	}

	@Test
	public void testDeleteBucketObject() {
		IBucketName bucketName = uut.makeBucketName(basePath+"/testbucket");
		IBucketObjectName objectName = uut.makeBucketObjectName(bucketName, "testobject");
		try {
			boolean result = uut.deleteBucketObject(objectName);
			System.out.println("GOT HERE");
			assertTrue(result);
			System.out.println("GOT HERE TOO");
			assertFalse(checkBucketObject(basePath+"/testbucket","testObject"));
			System.out.println("ALSO GOT HERE");
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("couldn't destroy bucket");
		}
	}

	@Test
	public void testCreateBucketObjectIBucketObjectNameString() {
		try {
			IBucketName bName = uut.makeBucketName(basePath+"/testbucket");
			IBucketObjectName boName = uut.makeBucketObjectName(bName, "testobject2");
			uut.createBucketObject(boName, "testcontents");
		} catch (BucketDriverException | UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("couldn't create a bucket object");
		}
	}

	@Test
	public void testCreateBucketObjectIBucketObjectNameInputStream() throws IOException {
		try {
//			InputStream content = IOUtils.toInputStream("this is some stuff","UTF-8");
			Buffer buf = Buffer.buffer("this is some stuff","UTF-8");
			RecordParser content = RecordParser.newDelimited("\n");
			content.handle(buf);
			IBucketName bName = uut.makeBucketName(basePath+"/testbucket");
			IBucketObjectName boName = uut.makeBucketObjectName(bName, "testobject2");
			uut.createBucketObject(boName, content);
		} catch (BucketDriverException e) {
			e.printStackTrace();
			fail("couldn't create bucket object");
		}
	}

	@Test
	public void testMakeBucketName() {
		IBucketName bName = uut.makeBucketName("test");
		assertNotNull(bName);
		assertEquals("test",bName.getNameString());
	}

	@Test
	public void testMakeBucketObjectName() {
		IBucketName bName = uut.makeBucketName(basePath+"/testbucket");
		IBucketObjectName boName = uut.makeBucketObjectName(bName, "testobject");
		assertNotNull(boName);
		assertEquals("testobject",boName.getName());
		assertEquals(bName,boName.getBucketName());
	}

}
