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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

/**
 * @author tharter
 *
 */
public abstract class WordStreamBase {
	InputStream in;
	OutputStream out;
	
	public abstract IWordStream setUp(String input);

	@Test
	public void testGetToDelimiterOneLine() throws IOException {
		IWordStream uut = setUp("this is stuffMARK");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	
	@Test
	public void testGetToDelimiterOneLineStuffAfter() throws IOException {
		IWordStream uut = setUp("this is stuffMARKstuff after");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff",mark);
		word = uut.getNextWord();
		assertEquals(new Word("stuff"),word);
		word = uut.getNextWord();
		assertEquals(new Word("after"),word);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	
	@Test
	public void testGetToDelimiterTwoLines() throws IOException {
		IWordStream uut = setUp("this is stuff\nmore stuffMARK");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff\nmore stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}

	@Test
	public void testGetToDelimiterTwoLinesStuffAfter() throws IOException {
		IWordStream uut = setUp("this is stuff\nmore stuffMARKstuff after");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff\nmore stuff",mark);

		word = uut.getNextWord();
		assertEquals(new Word("stuff"),word);
		word = uut.getNextWord();
		assertEquals(new Word("after"),word);

		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}

	@Test
	public void testGetToDelimiterNoMatchingOneLine() throws IOException {
		IWordStream uut = setUp("this is stuffFOO");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNull(mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	
	@Test
	public void testGetToDelimiterNoMatchingTwoLines() throws IOException {
		IWordStream uut = setUp("this is stuff\nmore stuffFOO");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNull(mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	@Test
	public void testGetMatchingToEndOneFile() throws IOException {
		IWordStream uut = setUp("this is stuff MARK");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToMatching("MARK");
		assertNotNull(mark);
		assertEquals("is stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}

	@Test
	public void testHasMoreTokens() throws IOException {
		IWordStream uut = setUp("this is stuff");
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		assertTrue(uut.hasMoreTokens());

		word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("is"),word);
		assertTrue(uut.hasMoreTokens());
		
		word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("stuff"),word);
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}

	@Test
	public void handleEmptyString() throws IOException {
		IWordStream uut = setUp("");
		Word result = uut.getNextWord();
		assertEquals(null,result);
	}
	
	@Test
	public void handleNewLines() throws IOException {
		IWordStream uut = setUp("\n\n");
		Word result = uut.getNextWord();
		assertEquals("\n\n",result.getValue());
	}
	
	@Test
	public void getWorks() throws IOException {
		IWordStream uut = setUp("there are some words here");
		
		Word result = uut.getNextWord();
		assertEquals("there",result.getValue());
		result = uut.getNextWord();
		assertEquals("are",result.getValue());
		result = uut.getNextWord();
		assertEquals("some",result.getValue());
		result = uut.getNextWord();
		assertEquals("words",result.getValue());
		result = uut.getNextWord();
		assertEquals("here",result.getValue());
		result = uut.getNextWord();
		assertNull(result);
	}
	
	@Test
	public void testMultipleSpaces() throws IOException {
		 IWordStream uut = setUp("this   word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void testLeadingSpaces() throws IOException {
		 IWordStream uut = setUp("    this word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void testLeadingTabs() throws IOException {
		 IWordStream uut = setUp("	this	word");
		 Word result1 = uut.getNextWord();
		 Word result2 = uut.getNextWord();
		 assertEquals("this",result1.getValue());
		 assertEquals("word",result2.getValue());
	}
	
	@Test
	public void getToMatchingWorks() throws IOException {
		 IWordStream uut = setUp("this is going to be some fun stuff");
		 String result = uut.getToMatching("fun");
		 assertEquals("this is going to be some",result);
	}

	@Test
	public void emptyLineReturnsDoubleNewline() throws IOException {
		IWordStream uut = setUp("hi \n\nthere!");
		Word first = uut.getNextWord();
		Word second = uut.getNextWord();
		Word third = uut.getNextWord();
		
		assertEquals("hi",first.getValue());
		assertEquals("\n\n",second.getValue());
		assertEquals("there!",third.getValue());
	}
	
	@Test
	public void singleWord() throws IOException {
		IWordStream uut = setUp("foobar");
		Word first = uut.getNextWord();
		
		assertEquals("foobar",first.getValue());
	}
	
}
