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

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.giantelectronicbrain.catfood.buckets.fs.FsBucketTestUtils;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public abstract class AsyncBucketDriverTest {

	protected abstract IBucketDriver createUUT(Vertx vertx) throws BucketDriverException;
	protected abstract void setUpBuckets() throws IOException;
	protected abstract void cleanUpBuckets() throws IOException;
	protected static String basePath = "./build/buckettests";
	
	private IBucketDriver uut;
	protected Vertx vertx;
	
	@Before
	public void setUp(TestContext context) throws IOException, BucketDriverException {
		vertx = Vertx.vertx();
		uut = createUUT(vertx);
		setUpBuckets();
	}
	
	@After
	public void cleanUp(TestContext context) throws IOException {
		cleanUpBuckets();
		vertx.close();
	}
	
	@Test
	public void testCreateBucketObjectAsync(TestContext context) {
		IBucketName bName = uut.makeBucketName(basePath+"/testbucket");
		IBucketObjectName boName = uut.makeBucketObjectName(bName, "testobject2");
		Async async = context.async();
		uut.createBucketObject(boName, result -> {
			context.assertFalse(result.failed());
			WriteStream<Buffer> ws = result.result();
			context.assertNotNull(ws);
			FileSystem fs = FsBucketTestUtils.getFileSystem();
			String tpath = fs.createTempFileBlocking("CATFOOD", null);
			Buffer tbuff = Buffer.buffer("This is some test data");
			fs.writeFileBlocking(tpath, tbuff);
			Future<AsyncFile> atfFut = fs.open(tpath, new OpenOptions());
			atfFut.onComplete(atfResult -> {
				if(atfFut.failed()) {
					atfFut.cause().printStackTrace();
					context.fail(atfFut.cause());
				}
				context.assertTrue(atfFut.succeeded());
				AsyncFile atf = atfFut.result();
				context.assertNotNull(atf);
				atf.pipeTo(ws, wsResult -> {
					context.assertTrue(wsResult.succeeded());
					async.complete();
				});
			});
		});
	}

}
