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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tharter
 *
 */
public class ContextTest {

	private Context uut;
	private Token firstToken = new NativeToken("one",(interp) -> { return true; });
	private Token secondToken = new NativeToken("two",(interp) -> { return true; });
	private Token thirdToken = new NativeToken("three",(interp) -> { return true; });
	private List<Token> instructions = new ArrayList<>();
	
	@Before
	public void setUp() {
		
		instructions.clear();
		instructions.add(firstToken);
		instructions.add(secondToken);
		instructions.add(thirdToken);
		
		uut = new Context(instructions);
	}
	
	@Test
	public void testInitialIpWorks() {
		uut = new Context(instructions,1);
		Token next = uut.getNextToken();
		assertEquals(secondToken,next);
	}
	
	@Test 
	public void testInitialIpMustBeInRange() {
		try {
			new Context(instructions,12);
			fail("should throw exception");
		} catch(IllegalArgumentException e) {
			// happy path
			return;
		}
	}

	@Test
	public void testBadIpFails() {
		try {
			uut.setIp(-22);
			fail("should throw exception");
		} catch(IllegalArgumentException e) {
			// happy path
			return;
		}
	}
	
	@Test
	public void testSetIpToLegalValueWorks() {
		int oldval = uut.setIp(2);
		Token next = uut.getNextToken();
		assertEquals(0,oldval);
		assertEquals(thirdToken,next);
	}
	
	@Test
	public void testGetNextTokenThereAreMore() {
		assertEquals(firstToken,uut.getNextToken());
	}

	@Test
	public void testGetNextTokenReturnsNullWhenExhausted() {
		assertEquals(firstToken,uut.getNextToken());
		assertEquals(secondToken,uut.getNextToken());
		assertEquals(thirdToken,uut.getNextToken());
		assertNull(uut.getNextToken());
	}
}
