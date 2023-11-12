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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

/**
 * More extensive testing of the Hairball Parser. In particular insuring that
 * the parser functions correctly in certain corner cases, like bucket parsing
 * where input structures span across or abut the break between buckets.
 * 
 * @author tharter
 *
 */
public class ParserTest {

	Vertx vertx;
	FileSystem fs;
	OutputStream out;
	IVocabulary nVocab;
	Dictionary dict;
	
	public String makeBucket(String input) {
		String tfname = fs.createTempFileBlocking("bucketwordstreamtest_", ".hairball");
		String fname = tfname.substring(tfname.lastIndexOf('/')+1);
		Buffer buffer = Buffer.buffer(input);
		fs.writeFileBlocking(tfname, buffer);
		return fname;
	}

	public Parser setUp(String[] inputs) {
		vertx = Vertx.vertx();
		fs = vertx.fileSystem();
		List<String> outputs = new ArrayList<String>();
		for(String input : inputs) {
			outputs.add(makeBucket(input));
		}
		String[] arglebargle = outputs.toArray(new String[outputs.size()]);
		IWordStream stream = new FileCollectionWordStream(vertx,"/tmp",arglebargle);
		out = new ByteArrayOutputStream();
		Output output = new StreamOutput(out);
		dict = new Dictionary("testdict");
		IVocabulary hbVocab = ExtendHairballVocabulary.create();
		dict.add(hbVocab);
		nVocab = new Vocabulary("testvocab");
		dict.add(nVocab);
		dict.makeCurrent(nVocab);
		Interpreter interp = new Interpreter();
		Parser uut = new Parser();
		ParserContext pContext = new ParserContext(stream, dict, interp, output, uut);
		interp.setParserContext(pContext);
		uut.setParserContext(pContext);
		return uut;
	}
	
	@Test
	public void testOneSingleFile() throws IOException, HairballException {
		Parser uut = setUp(new String[] {"foobar"});
		uut.parse();
		
		assertEquals("foobar",out.toString());
	}
	
	@Test
	public void testTwoFiles() throws IOException, HairballException {
		Parser uut = setUp(new String[] {"foo","bar"});
		uut.parse();
		
		assertEquals("foo bar",out.toString());
	}
	
	@Test
	public void testQuoteSlashOneFile() throws IOException, HairballException {
		Parser uut = setUp(new String[] {"/\" this is some text \"/ /."});
		uut.parse();
		
		assertEquals("this is some text",out.toString());
	}
	
	@Test
	public void testQuoteSlashTwoFiles() throws IOException, HairballException {
		Parser uut = setUp(new String[] {"/\" this is some text \"/", "/."});
		uut.parse();
		
		assertEquals("this is some text",out.toString());
	}
	
}
