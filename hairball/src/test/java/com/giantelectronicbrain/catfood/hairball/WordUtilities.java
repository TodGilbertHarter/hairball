/**
 * This software is Copyright (C) 2020 Tod G. Harter. All rights reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

/**
 * This is general code which is going to be used in various places to test
 * Hairball words. Since a fair amount of setup has to happen for that, we will
 * do it here in this base class instead of scattering it all over the place.
 * 
 * @author tharter
 *
 */
public class WordUtilities {

	/**
	 * Create a hairball with its input coming from the given string. This should
	 * be sufficient for many tests.
	 * 
	 * @param inputData
	 * @return
	 */
	public static StandAloneHairball setUp(String inputData, OutputStream out) {
		InputStream in = new StringBufferInputStream(inputData);
//		OutputStream out = new ByteArrayOutputStream();
		Output output = new StreamOutput(out);
		IWordStream input = new BufferedWordStream(in);
		return new StandAloneHairball(input, output);
	}

	public static BucketWordStream bucketSetUp(Vertx vertx, String input) {
//		vertx = Vertx.vertx();
		FileSystem fs = vertx.fileSystem();
		String tfname = fs.createTempFileBlocking("bucketwordstreamtest_", ".hairball");
		String fname = tfname.substring(tfname.lastIndexOf('/')+1);
		Buffer buffer = Buffer.buffer(input);
		fs.writeFileBlocking(tfname, buffer);
		return new BucketWordStream(fs, fname, "/tmp");
	}
	
	public static StandAloneHairball bucketSetUpHairball(Vertx vertx, String inputData, OutputStream out) {
		IWordStream input = bucketSetUp(vertx, inputData);
		Output output = new StreamOutput(out);
		return new StandAloneHairball(input, output);
	}

	public static String makeBucket(FileSystem fs, String input) {
		String tfname = fs.createTempFileBlocking("bucketwordstreamtest_", ".hairball");
		String fname = tfname.substring(tfname.lastIndexOf('/')+1);
		Buffer buffer = Buffer.buffer(input);
		fs.writeFileBlocking(tfname, buffer);
		return fname;
	}

	public static FileCollectionWordStream setUp(Vertx vertx, String[] inputs) {
		FileSystem fs = vertx.fileSystem();
		List<String> outputs = new ArrayList<String>();
		for(String input : inputs) {
			outputs.add(makeBucket(fs, input));
		}
		String[] arglebargle = outputs.toArray(new String[outputs.size()]);
		return new FileCollectionWordStream(vertx,"/tmp",arglebargle);
	}

	public static StandAloneHairball FileCollectionSetUpHairball(Vertx vertx, String[] inputs, OutputStream out) {
		IWordStream input = setUp(vertx,inputs);
		Output output = new StreamOutput(out);
		return new StandAloneHairball(input, output);
		
	}
}
