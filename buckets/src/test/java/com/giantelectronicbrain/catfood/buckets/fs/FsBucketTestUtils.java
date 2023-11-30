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

package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.streams.ReadStream;

/**
 * Put all the utility classes for testing FS buckets into one place.
 * 
 * @author tharter
 *
 */
public class FsBucketTestUtils {
	private static Vertx vertx = Vertx.vertx();
	private static FileSystem fileSystem = vertx.fileSystem();
	private static String basePath = "./build/buckettests";

	public static IBucketDriver createUUT() throws BucketDriverException {
		vertx = Vertx.vertx();
		fileSystem = vertx.fileSystem();
		return _createUUT();
	}
	
	private static IBucketDriver _createUUT() throws BucketDriverException {

		return  FsBucketDriverImpl.builder()
				.fileSystem(fileSystem)
//				.basePath(basePath)
				.build();		
	}
	
	public static FileSystem getFileSystem() {
		return fileSystem;
	}

	private static String resolvedPath(String path) {
		return basePath + "/" + path;
	}

	public static void cleanUpBuckets() throws IOException {
		String path = "testbucket";
		fileSystem.delete(resolvedPath(path));
		path = "testbucket3";
		fileSystem.delete(resolvedPath(path));
	}
	
	public static void setUpBuckets() throws IOException {
		if(!fileSystem.existsBlocking(basePath)) {
			fileSystem.mkdirsBlocking(basePath);
		}
		String path = resolvedPath("testbucket2");
		if(!fileSystem.existsBlocking(path)) {
			fileSystem.mkdirsBlocking(path);
		}
		path = resolvedPath("testbucket");
		if(!fileSystem.existsBlocking(path)) {
			fileSystem.mkdirsBlocking(path);
		}
		path = resolvedPath("testbucket/testobject");
		if(!fileSystem.existsBlocking(path)) {
			fileSystem.createFileBlocking(path);
		}
	}

	/**
	 * @param vertx2
	 * @return
	 * @throws BucketDriverException 
	 */
	public static IBucketDriver createUUT(Vertx vertx2) throws BucketDriverException {
		vertx = vertx2;
		fileSystem = vertx.fileSystem();
		return _createUUT();
	}

}
