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

import org.junit.Before;
import org.junit.Test;

/**
 * @author tharter
 *
 */
public class DictionaryTest {

	private Dictionary uut;
	private Vocabulary testVocab;
	
	@Before
	public void setUp() {
		uut = new Dictionary("TESTDICT");
		testVocab = new Vocabulary("TEST");
		uut.add(testVocab);
	}
	
	@Test
	public void searchMultipleVocabularies() {
		Word myWord = new Word("mydef");
		Definition myDef = new Definition(myWord,null,null);
		uut.add(myDef);
		Definition actual = uut.lookUp(myWord);
		assertEquals(myDef,actual);

		Vocabulary anotherVoc = new Vocabulary("TEST2");
		uut.add(anotherVoc);
		uut.makeCurrent(anotherVoc);
		Definition anotherDef = new Definition(myWord,null,null);
		uut.add(anotherDef);
		
		Definition lookedUp = uut.lookUp(myWord);
		assertTrue(lookedUp == anotherDef);
	}
	
	@Test
	public void addAdds() {
		Word myWord = new Word("mydef");
		Definition myDef = new Definition(myWord,null,null);
		uut.add(myDef);
		Definition actual = uut.lookUp(myWord);
		assertEquals(myDef,actual);
	}

	@Test
	public void lookupFindsOnlyDefinitions() {
		Word myWord = new Word("mydef");
		Definition actual = uut.lookUp(myWord);
		assertNull(actual);
		
		Definition myDef = new Definition(myWord,null,null);
		uut.add(myDef);
		actual = uut.lookUp(myWord);
		assertEquals(myDef,actual);
		
		myWord = new Word("other");
		actual = uut.lookUp(myWord);
		assertNull(actual);
	}

	@Test
	public void doerRoutesTokenstoCompileTime() {
		uut.create(new Word("test"));
		uut.doer();
		assertTrue(uut.isDoer());
		Token aToken = new NativeToken("aToken",(interp) -> {return true; });
		uut.addToken(aToken);
		Definition def = uut.getCurrentDefinition();
		assertNotNull(def);
		Token dToken = def.getCompileTime();
		assertEquals(aToken,dToken);
	}
	
	@Test
	public void doesRoutesTokenstoRuntime() {
		uut.create(new Word("test"));
		uut.does();
		assertFalse(uut.isDoer());
		Token aToken = new NativeToken("aToken",(interp) -> {return true; });
		uut.addToken(aToken);
		Definition def = uut.getCurrentDefinition();
		assertNotNull(def);
		Token dToken = def.getRunTime();
		assertEquals(aToken,dToken);
	}
	
	@Test
	public void defineAddsToDefaultVocab() {
		Word testWord = new Word("/EM");
		uut.create(testWord);
		uut.define();
		Definition def = testVocab.lookUp(testWord);
		assertNotNull(def);
		assertEquals("/EM",def.getName().getValue());
	}
	
	@Test
	public void makeCurrentWorks() {
		IVocabulary vocab = new Vocabulary("another");
		uut.makeCurrent(vocab);

		Word testWord = new Word("test");
		uut.create(testWord);
		Definition def = uut.define();

		Definition fromDict = uut.lookUp(testWord);
		assertNull(fromDict);
		Definition fromCurrent = vocab.lookUp(testWord);
		assertNotNull(fromCurrent);
		assertEquals(def,fromCurrent);
		
	}
	
	@Test
	public void removeWorks() {
		IVocabulary vocab = new Vocabulary("another");
		uut.add(vocab);
		uut.makeCurrent(vocab);
		uut.remove(testVocab);

		Word testWord = new Word("test");
		uut.create(testWord);
		Definition def = uut.define();

		Definition fromDict = uut.lookUp(testWord);
		assertNull(fromDict);		
	}
	
}
