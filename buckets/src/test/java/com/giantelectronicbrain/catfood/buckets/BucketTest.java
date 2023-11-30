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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

/**
 * Generic bucket test suite.
 * 
 * @author tharter
 *
 */
public abstract class BucketTest {

	protected abstract IBucketDriver createUUT() throws BucketDriverException;
	protected abstract void setUpBuckets() throws IOException;
	protected abstract void cleanUpBuckets() throws IOException;
	protected static String basePath = "./build/buckettests";
	
	private IBucket uut;
	
	@Before
	public void setUp() throws BucketDriverException, IOException {
		IBucketDriver driver  = createUUT();
		setUpBuckets();
		IBucketName bucketName = driver.makeBucketName(basePath+"/testbucket");
		uut = (IBucket) driver.getBucket(bucketName).get();//   .createBucket(bucketName);
	}
	
	@After
	public void cleanUp() throws IOException {
		cleanUpBuckets();
	}

	@Test
	public void testGetName() {
		IBucketName bName = uut.getName();
		assertNotNull(bName);
		assertEquals(basePath+"/testbucket",bName.getNameString());
	}
	
	@Test
	public void testGetObjectIterator() {
		Iterator<IBucketObject> bIter = uut.iterator();
		assertNotNull(bIter);
		assertTrue(bIter.hasNext());
		IBucketObject bObject = bIter.next();
		assertNotNull(bObject);
		String nStr = bObject.getNameString();
		assertEquals("testobject", nStr);
	}
}
