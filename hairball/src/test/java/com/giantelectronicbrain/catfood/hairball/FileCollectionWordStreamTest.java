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

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * @author tharter
 *
 */
public class FileCollectionWordStreamTest {

	Vertx vertx;
	FileSystem fs;
			
	public FileCollectionWordStream setUp(String[] inputs) {
		vertx = Vertx.vertx();
		fs = vertx.fileSystem();
		return WordUtilities.setUp(vertx, inputs);
	}
	
	@Test
	public void testGetToDelimiterOneLine() throws IOException {
		IWordStream uut = setUp(new String[] {"this is stuffMARK"} );
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	
	@Test
	public void testGetToDelimiterTwoLines() throws IOException {
		IWordStream uut = setUp(new String[] {"this is stuff\nmore stuffMARK"});
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff\nmore stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}
	
	@Test
	@Ignore // I assume we really do not need to span files with delimiters currently
	public void testGetToDelimiterTwoFiles() throws IOException {
		IWordStream uut = setUp(new String[] {"this is stuff", "more stuffMARK"});
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToDelimiter("MARK");
		assertNotNull(mark);
		assertEquals("is stuff\nmore stuff",mark);
		
		assertFalse(uut.hasMoreTokens() && !(uut instanceof ConsoleWordStream || uut instanceof StringWordStream));
	}

	@Test
	public void testOneSingleFile() throws IOException {
		IWordStream uut = setUp(new String[] {"foobar"});
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("foobar"),word);
		
		assertFalse(uut.hasMoreTokens());
	}

	@Test
	public void testGetMatchingToEndOneFile() throws IOException {
//System.out.println("Starting");
		IWordStream uut = setUp(new String[] {"this is stuff MARK"});
//System.out.println("Got past setup");
		Word word = uut.getNextWord();
//System.out.println("got the first word");
		assertNotNull(word);
		assertEquals(new Word("this"),word);
		String mark = uut.getToMatching("MARK");
//System.out.println("Matched");
		assertNotNull(mark);
		assertEquals("is stuff",mark);
		
		assertFalse(uut.hasMoreTokens());
	}
	
	@Test
	public void testTwoFilesNoNewLines() throws IOException {
		IWordStream uut = setUp(new String[] {"foo", "bar"});
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("foo"),word);
		
		assertTrue(uut.hasMoreTokens());
		
		word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("bar"),word);
	}
	
	@Test
	public void testTwoFilesFirstEndsWithNewline() throws IOException {
//System.out.println("STARTING");
		IWordStream uut = setUp(new String[] {"foo\n", "bar"});
		Word word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("foo"),word);

//System.out.println("testing hasMoreTokens now");
		assertTrue(uut.hasMoreTokens());
//System.out.println("Done testing hasMoreTokens now");

		word = uut.getNextWord();
		assertNotNull(word);
		assertEquals(new Word("bar"),word);
		
	}
	
	@Test 
	public void testGetMatchingAcrossFilesNoNewLines() throws IOException {
		IWordStream uut = setUp(new String[] {"foo", "bar"});
		String word = uut.getToMatching("bar");
		assertEquals("foo",word);
		
		assertTrue(uut.hasMoreTokens());
	}
	
	@Test 
	public void testGetMatchingUpToFilesNoNewLines() throws IOException {
		IWordStream uut = setUp(new String[] {"fee foo", "bar"});
		String word = uut.getToMatching("foo");
		assertEquals("fee",word);
		
		assertTrue(uut.hasMoreTokens());

		Word w = uut.getNextWord();
		assertNotNull(w);
		assertEquals(new Word("bar"),w);
	}
	
	@Test 
	public void testGetMatchingUpToFilesOneNewLines() throws IOException {
		IWordStream uut = setUp(new String[] {"fee foo\n", "bar"});
		String s = uut.getToMatching("foo");
		assertEquals("fee",s);
		
		assertTrue(uut.hasMoreTokens());
		
		Word w = uut.getNextWord();
		assertNotNull(w);
		assertEquals(new Word("bar"),w);
		
		assertFalse(uut.hasMoreTokens());
	}

	@Test 
	public void testGetMatchingUpToFilesTwoNewLines() throws IOException {
		IWordStream uut = setUp(new String[] {"fee foo\n\n", "bar"});
		String s = uut.getToMatching("foo");
		assertEquals("fee",s);
		
		assertTrue(uut.hasMoreTokens());
		
		Word w = uut.getNextWord();
		assertNotNull(w);
		assertEquals(new Word("\n\n"),w);
		
		assertTrue(uut.hasMoreTokens());

		w = uut.getNextWord();
		assertNotNull(w);
		assertEquals(new Word("bar"),w);
	}
}
