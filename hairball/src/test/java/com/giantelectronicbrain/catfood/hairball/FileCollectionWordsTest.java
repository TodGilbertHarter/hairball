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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.Stack;

import org.junit.Test;

import io.vertx.core.Vertx;

/**
 * Tests of the words in the Hairball core vocabulary, using the FsBucketWordStream
 * to help detect subtle errors.
 * 
 * @author tharter
 *
 */
public class FileCollectionWordsTest {
	
	Vertx vertx;
	OutputStream out;
	
	public StandAloneHairball setUp(String input) {
		vertx = Vertx.vertx();
		out = new ByteArrayOutputStream();
		String[] inarry = new String[] { input };
		return WordUtilities.FileCollectionSetUpHairball(vertx, inarry, out);
	}
	

	@Test
	public void testNoop() throws IOException, HairballException {
		StandAloneHairball uut = setUp("//");
		ParserContext ctx = uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		Context ictx = uut.getInterpreterContext();
		assertNull(ictx);
		assertNotNull(ctx);
		String output = out.toString();
		assertTrue(output.isEmpty());
	}

	@Test
	public void testQuoteSlashInterpreted() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/\" this is some text \"/ /.");
		uut.execute();
		
		assertEquals("this is some text",out.toString());
	}
	
	@Test
	public void testQuoteSlashCompiled() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/: QW /\" /. :/ QW this is some text \"/");
		uut.execute();
		
		assertEquals("this is some text",out.toString());
	}
	
	@Test
	public void testHereStore() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/HERE!");

		Stack<Object> pStack = uut.getParamStack();
		LiteralToken literalToken = new LiteralToken("literal","this is a literal");
		pStack.push(literalToken);

		ParserContext ctx = uut.getParser().getContext();
		ctx.getDictionary().create(new Word("TEST"));
		ctx.getDictionary().does();
				
		uut.execute();
		ctx.getDictionary().define();
		
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		
		Definition dot = ctx.getDictionary().lookUp(new Word("/."));
		dot.getRunTime().execute(ctx.getInterpreter());
		
		assertEquals("this is a literal",out.toString());
	}
	
	@Test
	public void testStore() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/HERE! /!");

		Stack<Object> pStack = uut.getParamStack();
		LiteralToken l2 = new LiteralToken("another","another literal");
		pStack.push(l2);
		pStack.push(Integer.valueOf(0));
		LiteralToken literalToken = new LiteralToken("literal","this is a literal");
		pStack.push(literalToken);

		ParserContext ctx = uut.getParser().getContext();
		ctx.getDictionary().create(new Word("TEST"));
		ctx.getDictionary().does();
				
		uut.execute();
		ctx.getDictionary().define();

		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		
		Definition dot = ctx.getDictionary().lookUp(new Word("/."));
		dot.getRunTime().execute(ctx.getInterpreter());
		
		assertEquals("another literal",out.toString());
	}
	
	@Test
	public void testExecute() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/EXECUTE");

		Token token = new LiteralToken("42",42);
		Stack<Object> pStack = uut.getParamStack();
		pStack.push(token);
		
		uut.execute();
		assertEquals(1,pStack.size());
		assertEquals(42,pStack.pop());
		
	}
	
	@Test
	public void testDot() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/.");
		Stack<Object> pStack = uut.getParamStack();
		String literal = "this is a literal";
		pStack.push(literal);

		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals(literal,output);
	}
	
	@Test
	public void testW() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/W stuff");
		
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(1,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Word tos = (Word) pStack.pop();
		assertNotNull(tos);
		assertEquals("stuff",tos.getValue());
	}
	
	@Test
	public void testSpace() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/SPACE");

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals(" ",output);
	}
	
	@Test
	public void testCompileSpace()  throws IOException, HairballException {
		StandAloneHairball uut = setUp("/: /EM /SPACE <em> :/ /: EM/ </em> :/ TEST /EM TEST EM/ TEST");

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("TEST <em>TEST</em>TEST",output);
	}
	
	@Test
	public void testDotQuote() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/.\" some fun stuff \"/");

		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("some fun stuff",output);
	}
	
	@Test
	public void testColon() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/: TEST some fun stuff :/\n");
		
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		Definition def = ctx.getDictionary().lookUp(new Word("TEST"));
		assertNotNull(def);
		def.getRunTime().execute(ctx.getInterpreter());
		assertEquals(0,pStack.size());
		rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());
		
		String output = out.toString();
		assertEquals("some fun stuff",output);
	}
	
	@Test
	public void testAbort() {
		StandAloneHairball uut = setUp("/\" some words \"/ /ABORT");
		try {
			uut.execute();
			fail("must throw error");
		} catch (IOException e) {
			e.printStackTrace();
			fail("should not throw this");
		} catch (HairballException e) {
//			String output = e.getMessage();
//			assertEquals("some words",output);
		}
	}
	
	@Test
	public void doesHairballWork() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/: /EM <em> :/\n"
				+ "/: EM/ </em> :/");
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());

		String output = out.toString();
		assertEquals("",output);		

/*		String code = "/EM this is a test EM/";
		InputStream in = new StringBufferInputStream(code);
		IWordStream wordStream = new BufferedWordStream(in);
		uut.setInput(wordStream);
		uut.execute();
		output = out.toString();
		assertEquals("<em>this is a test</em>",output); */
		
	}
	
	@Test
	public void stuffThatBlowsUpForSomeReason() throws IOException, HairballException {
		StandAloneHairball uut = setUp("/: /DOCUMENT <HTML><HEAD><TITLE> /\" /. </TITLE> :/\n" +
								"/: /BODY </HEAD><BODY> :/\n" +
								"/: DOCUMENT/ </BODY></HTML> :/\n" +
								"/DOCUMENT this is a test \"/\n" +
								"/BODY\n" +
								"Some fooby wooby\n" +
								"DOCUMENT/\n");
		
		ParserContext ctx = uut.execute();
		String output = out.toString();
		assertEquals("<HTML><HEAD><TITLE>this is a test</TITLE></HEAD><BODY>Some fooby wooby</BODY></HTML>",output);		
	}

}
