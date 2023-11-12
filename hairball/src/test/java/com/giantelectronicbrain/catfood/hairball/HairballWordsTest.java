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
import java.time.format.DateTimeFormatter;
import java.util.Stack;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests of the words in the Hairball core vocabulary.
 * 
 * @author tharter
 *
 */
@SuppressWarnings("deprecation")
public class HairballWordsTest {

	@Test
	public void testNewline() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NEWLINE",out);
		uut.execute();
		String output = out.toString();
		assertEquals("\n",output);
	}
	
	@Test
	@Ignore
	/* Not sure what the problem is here, yet. Something in refactoring broke /VERSION */
	public void testVersion() throws IOException, HairballException {
		StandAloneHairball.VERSION = "foo"; // just for testing purposes
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/VERSION",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(1,pStack.size());
		String version = (String) pStack.pop();
		assertEquals("foo",version);
		String output = out.toString();
		assertEquals("",output);
	}
	
	@Test
	public void testNewVocabulary() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NEWVOCABULARY MYVOCAB",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		Dictionary dict = uut.getParser().getContext().getDictionary();
		IVocabulary myVocab = dict.findVocabulary("MYVOCAB");
		assertEquals("MYVOCAB",myVocab.getName());
		String output = out.toString();
		assertEquals("",output);
	}

	@Test
	public void testVocabulary() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NEWVOCABULARY MYVOCAB /VOCABULARY MYVOCAB",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(1,pStack.size());
		IVocabulary myVocab = (IVocabulary) pStack.pop();
		assertEquals("MYVOCAB",myVocab.getName());
		String output = out.toString();
		assertEquals("",output);
	}
	
	@Test
	public void testMakeVocabularyActive() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NEWVOCABULARY MYVOCAB /VOCABULARY MYVOCAB /ACTIVE",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		Dictionary dict = uut.getParser().getContext().getDictionary();
		IVocabulary myVocab = dict.findVocabulary("MYVOCAB");
		assertEquals("MYVOCAB",myVocab.getName());
		myVocab = dict.remove();
		assertEquals("MYVOCAB",myVocab.getName());
		String output = out.toString();
		assertEquals("",output);
	}

	@Test
	public void testMakeVocabularyCurrent() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NEWVOCABULARY MYVOCAB "
				+ "/VOCABULARY MYVOCAB /ACTIVE "
				+ "/VOCABULARY MYVOCAB /CURRENT "
				+ "/: FOOBAR stuff :/ FOOBAR",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		Dictionary dict = uut.getParser().getContext().getDictionary();
		IVocabulary myVocab = dict.findVocabulary("MYVOCAB");
		assertEquals("MYVOCAB",myVocab.getName());
		myVocab = dict.getCurrent();
		assertNotNull(myVocab);
		assertEquals("MYVOCAB",myVocab.getName());
		Definition foobar = myVocab.lookUp(new Word("FOOBAR"));
		assertNotNull(foobar);
		String output = out.toString();
		assertEquals("stuff",output);
	}
	
	@Test
	public void testConstantWithString() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/CONSTANT MYCONST stuff MYCONST",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(1,pStack.size());
		String myConst = (String) pStack.pop();
		assertEquals("stuff",myConst);
		String output = out.toString();
		assertEquals("",output);	
	}

	@Test
	public void testConstantWithFloat() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/CONSTANT MYCONST 111.0 MYCONST",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(1,pStack.size());
		Double myConst = (Double) pStack.pop();
		assertEquals(new Double(111.0).doubleValue(),myConst.doubleValue(),0.0d);
		String output = out.toString();
		assertEquals("",output);		
	}
	
	@Test
	public void testConstantWithInteger() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/CONSTANT MYCONST 111 MYCONST",out);
		uut.execute();
		Stack<?> pStack = uut.getParamStack();
		assertEquals(1,pStack.size());
		Integer myConst = (Integer) pStack.pop();
		assertEquals(111,myConst.intValue());
		String output = out.toString();
		assertEquals("",output);		
	}
	
	@Test
	public void testToken() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/TOKEN FOOBAR /.",out);
		uut.execute();
		String output = out.toString();
		assertEquals("FOOBAR",output);		
	}
	
	@Test
	public void testNoop() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("//",out);
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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/\" this is some text \"/ /.",out);
		uut.execute();
		
		assertEquals("this is some text",out.toString());
	}
	
	@Test
	public void testSlashTick() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: FOO hello world :/ /VARIABLE VECTOR /' FOO VECTOR /V! VECTOR /V@ /EXECUTE"
				,out);
		uut.execute();
		
		assertEquals("hello world",out.toString());
	}
	
	@Test
	public void testQuoteSlashCompiled() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: QW /\" /. :/ QW this is some text \"/",out);
		uut.execute();
		
		assertEquals("this is some text",out.toString());
		assertEquals(0,uut.getParamStack().size());
	}
	
	@Test
	public void testDotNow() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/.NOW",out);
		ParserContext ctx = uut.execute();
		
//		assertEquals("2021-07-25T14:14:54.309-07:00[America/Los_Angeles]",out.toString());
		assertEquals(0,uut.getParamStack().size());
		
	}
	
	@Test
	public void testDup() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1 /DUP",out);
		ParserContext ctx = uut.execute();
		assertEquals(2,uut.getParamStack().size());
		assertEquals(1,uut.getParamStack().pop());
		assertEquals(1,uut.getParamStack().pop());
	}

	@Test
	public void testSwap() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1 /NUM 2 /SWAP",out);
		ParserContext ctx = uut.execute();
		assertEquals(2,uut.getParamStack().size());
		assertEquals(1,uut.getParamStack().pop());
		assertEquals(2,uut.getParamStack().pop());
	}
	
	@Test
	public void testRot() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1 /NUM 2 /NUM 3 /ROT",out);
		ParserContext ctx = uut.execute();
		assertEquals(3,uut.getParamStack().size());
		
		assertEquals(1,uut.getParamStack().pop());
		assertEquals(3,uut.getParamStack().pop());
		assertEquals(2,uut.getParamStack().pop());
	}
	
	@Test
	public void testNum() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1",out);
		ParserContext ctx = uut.execute();
		assertEquals(1,uut.getParamStack().size());
		assertEquals(1,uut.getParamStack().pop());
	}
	
	@Test
	public void testPick() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1 /NUM 2 /NUM 3 /NUM 3 /PICK",out);
		ParserContext ctx = uut.execute();
		assertEquals(3,uut.getParamStack().size());
		
		assertEquals(1,uut.getParamStack().pop());
		assertEquals(3,uut.getParamStack().pop());
		assertEquals(2,uut.getParamStack().pop());
	}
	
	@Test
	public void testDrop() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/NUM 1 /DROP",out);
		ParserContext ctx = uut.execute();
		assertEquals(0,uut.getParamStack().size());
	}
	
	@Test
	public void testDefineVariable()  throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/VARIABLE MYVAR",out);
		ParserContext ctx = uut.execute();

		assertEquals("",out.toString());
		
		Dictionary dict = ctx.getDictionary();
		Definition def = dict.lookUp(new Word("MYVAR"));
		assertNotNull(def);
		
		Stack<Object> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
	}
	
	@Test
	public void testVariableCompileTime() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/VARIABLE MYVAR /\" a literal string \"/ /: MYTEST MYVAR /V! :/ MYTEST MYVAR /V@ /.",out);
		ParserContext ctx = uut.execute();
		Stack<Object> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		assertEquals("a literal string",out.toString());
	}
	
	@Test
	public void testVariableFetch() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/VARIABLE MYVAR /\" a literal string \"/ MYVAR /V! MYVAR /V@ /.",out);
		ParserContext ctx = uut.execute();
		Stack<Object> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		assertEquals("a literal string",out.toString());

	}
	
	@Test
	public void testStoreVariable()  throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/VARIABLE MYVAR /\" a literal string \"/ MYVAR /V!",out);
		ParserContext ctx = uut.execute();
		Stack<Object> pStack = uut.getParamStack();
		assertEquals(0,pStack.size());
		assertEquals("",out.toString());
		
		Dictionary dict = ctx.getDictionary();
		Definition def = dict.lookUp(new Word("MYVAR"));
		assertNotNull(def);
		
		ctx.getInterpreter().execute(def.getRunTime());
		assertEquals(1,pStack.size());
		VariableToken vToken = (VariableToken) pStack.pop();
		assertEquals("a literal string",vToken.getData());
	}

	@Test
	public void testHereStore() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/HERE!",out);

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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/HERE! /!",out);

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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/EXECUTE",out);

		Token token = new LiteralToken("42",42);
		Stack<Object> pStack = uut.getParamStack();
		pStack.push(token);
		
		uut.execute();
		assertEquals(1,pStack.size());
		assertEquals(42,pStack.pop());
		
	}
	
	@Test
	public void testDot() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/.",out);
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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/W stuff",out);
		
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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/SPACE",out);

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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: /EM /SPACE <em> :/ /: EM/ </em> :/ TEST /EM TEST EM/ TEST",out);

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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/.\" some fun stuff \"/",out);

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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: TEST some fun stuff :/\n",out);
		
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

	// This is actually a test of '/DOER /DOES' syntax
	@Test
	public void testGetMatching() throws IOException, HairballException {
//		String hb2 = "/: FOO /DOER aaaaaaaaa /DOES zzzzzzzzzzz :/ /: FOO2 FOO :/"; // FOO2 ";
		String hb = "/: /GETMATCHING /DOER /DROP /W /W2L /MAKELITERAL /HERE! :/";
//				+ " /: /EXAMPLE <code> /GETMATCHING EXAMPLE/ /. </code> :/";
//				+ " /EXAMPLE fee fie foo fum EXAMPLE/";
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp(hb,out);
		Dictionary d = uut.getParser().getContext().getDictionary();
		
		uut.execute();
		Definition getMatching = d.lookUp(new Word("/GETMATCHING"));
		
		Stack<Object> stack = uut.getReturnStack();
		assertEquals(0,stack.size());
//		String hbmore = " /: /EXAMPLE <code> /GETMATCHING EXAMPLE/ /. </code> :/";
		String hbmore = " /: /EXAMPLE <code> /GETMATCHING EXAMPLE/ /DELIMITED /. </code> :/";
		IWordStream moreWordStream = new StringWordStream(hbmore);
		
		uut.setInput(moreWordStream);
		uut.execute();
		
		Definition example = d.lookUp(new Word("/EXAMPLE"));
		
		String hbmoremore = "/EXAMPLE fee fie\n foo fum EXAMPLE/";
		IWordStream moremoreWordStream = new StringWordStream(hbmoremore);
		uut.setInput(moremoreWordStream);
		uut.execute();
		
		String output = out.toString();
		assertEquals("<code>fee fie\n foo fum </code>",output);
	}
	
	@Test
	public void testAbort() {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/\" some words \"/ /ABORT",out);
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
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: /EM <em> :/\n"
				+ "/: EM/ </em> :/",out);
		Stack<Object> pStack = uut.getParamStack();
		ParserContext ctx = uut.execute();
		assertEquals(0,pStack.size());
		Stack<?> rStack = uut.getReturnStack();
		assertEquals(0,rStack.size());

		String output = out.toString();
		assertEquals("",output);		

		String code = "/EM this is a test EM/";
		InputStream in = new StringBufferInputStream(code);
		IWordStream wordStream = new BufferedWordStream(in);
		uut.setInput(wordStream);
		uut.execute();
		output = out.toString();
		assertEquals("<em>this is a test</em>",output);
		
	}
	
	@Test
	public void stuffThatBlowsUpForSomeReason() throws IOException, HairballException {
		OutputStream out = new ByteArrayOutputStream();
		StandAloneHairball uut = WordUtilities.setUp("/: /DOCUMENT <HTML><HEAD><TITLE> /\" /. </TITLE> :/\n" +
								"/: /BODY </HEAD><BODY> :/\n" +
								"/: DOCUMENT/ </BODY></HTML> :/\n" +
								"/DOCUMENT this is a test \"/\n" +
								"/BODY\n" +
								"Some fooby wooby\n" +
								"DOCUMENT/\n",out);
		
		ParserContext ctx = uut.execute();
		String output = out.toString();
		assertEquals("<HTML><HEAD><TITLE>this is a test</TITLE></HEAD><BODY>Some fooby wooby</BODY></HTML>",output);		
	}

}
