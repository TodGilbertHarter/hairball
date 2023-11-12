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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.giantelectronicbrain.catfood.buckets.fs.FsBucketName;

import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

/**
 * @author tharter
 *
 */
public class VertxBlockingInputStreamTest {

	Vertx vertx;
	FileSystem fs;
	InputStream is;
			
	public InputStream setUp(String input) throws InterruptedException {
		is = null;
		vertx = Vertx.vertx();
		fs = vertx.fileSystem();
		String fname = WordUtilities.makeBucket(fs, input);
		fname = "/tmp/"+fname;
		fs.open(fname, new OpenOptions(), result -> {
			if(result.succeeded()) {
				AsyncFile asyncFile = result.result();
				is = new VertxBlockingInputStream(asyncFile);
			} else {
				result.cause().printStackTrace();
				fail("couldn't open test file");
			}
		});
		while(is == null) {
			Thread.sleep(1);		
		}
		return is;
	}
	
	@Test
	public void testReadToEndOneLine() throws IOException, InterruptedException {
		InputStream uut = setUp("this is stuff MARK");
		assertTrue(uut.available() > 0);
		int rb = uut.read();
		assertEquals('t',(char)rb);
		StringBuffer sb = new StringBuffer();
		sb.append((char)rb);
		while(rb != -1) {
//System.out.println("available bytes reported as "+uut.available());
			rb = uut.read();
			if(rb != -1)
				sb.append((char)rb);
//			else
//				System.out.println("END OF FILE");
		}
		assertEquals("this is stuff MARK",sb.toString());
	}

	@Test
	public void testReadToEndMultipleLines() throws IOException, InterruptedException {
		InputStream uut = setUp("this is stuff MARK\nand now more stuff");
		assertTrue(uut.available() > 0);
		int rb = uut.read();
		assertEquals('t',(char)rb);
		StringBuffer sb = new StringBuffer();
		sb.append((char)rb);
		while(rb != -1) {
//System.out.println("available bytes reported as "+uut.available());
			rb = uut.read();
			if(rb != -1)
				sb.append((char)rb);
//			else
//				System.out.println("END OF FILE");
		}
		assertEquals("this is stuff MARK\nand now more stuff",sb.toString());
	}
	
}
