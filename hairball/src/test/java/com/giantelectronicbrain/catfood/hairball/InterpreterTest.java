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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the hairball inner interpreter.
 * 
 * @author tharter
 *
 */
public class InterpreterTest {

	private int lastExecuted = -1;
//	private Token rtsToken = new NativeToken("rfc",(interp) -> { interp.returnFromContext(); return true; });
	private Interpreter uut;
	private Context context;
	private Token firstToken = new NativeToken("first",(interp) -> { lastExecuted = 1; return true; });
	private Token secondToken = new NativeToken("second",(interp) -> { lastExecuted = 2; return true; });
	private Token thirdToken = new NativeToken("third",(interp) -> { lastExecuted = 3; return true; });
	private List<Token> instructions = new ArrayList<>();
	
	@Before
	public void setUp() {
		lastExecuted = -1;
		this.uut = new Interpreter();
		
		instructions.clear();
		instructions.add(firstToken);
		instructions.add(secondToken);
		instructions.add(thirdToken);
		
		context = new Context(instructions);
	}

	@Test
	public void push() {
		Integer myInt = 666;
		assertEquals(0,uut.depth());
		assertEquals(0,uut.rDepth());
		
		uut.push(myInt);
		assertEquals(1,uut.depth());
		assertEquals(0,uut.rDepth());
	}

	@Test
	public void popFromEmptyThrowsException() {
		assertEquals(0,uut.depth());
		try {
			uut.pop();
			fail("no exception was thrown");
		} catch(EmptyStackException e) {
			//happy path
		}
	}
	
	@Test
	public void popReturnsTOS() {
		uut.push(1);
		uut.push(2);
		uut.push(3);
		assertEquals(3,uut.pop());
		assertEquals(2,uut.depth());
	}
	
	@Test
	public void rPush() {
		Integer myInt = 666;
		assertEquals(0,uut.depth());
		assertEquals(0,uut.rDepth());
		
		uut.rPush(myInt);
		assertEquals(0,uut.depth());
		assertEquals(1,uut.rDepth());
	}

	@Test
	public void rPopFromEmptyThrowsException() {
		assertEquals(0,uut.depth());
		try {
			uut.rPop();
			fail("no exception was thrown");
		} catch(EmptyStackException e) {
			//happy path
		}
	}
	
	@Test
	public void rPopReturnsTOS() {
		uut.rPush(1);
		uut.rPush(2);
		uut.rPush(3);
		assertEquals(3,uut.rPop());
		assertEquals(2,uut.rDepth());
	}

	@Test
	public void testConstruct() {
		assertEquals(0,uut.depth());
		assertEquals(0,uut.rDepth());
	}
	
	@Test
	public void testJumpToContextExecutesNativeToken() throws HairballException {
		NativeToken token = new NativeToken("444",(uut) -> {uut.push(444); return true; });
		List<Token> tlist = new ArrayList<>();
		tlist.add(token);
		Context context = new Context(tlist);
		Integer myInt = 666;
		uut.rPush(myInt);
		uut.jumpToContext(context);
		uut.executeContext();
		uut.returnFromContext();
		assertEquals(1,uut.rDepth());
		assertEquals(1,uut.depth());
		assertEquals(444,uut.pop());
	}
	
	@Test
	public void testJumpToContextExecutesInterpreterToken() throws HairballException {
		InterpreterToken interpToken = new InterpreterToken("test");
		interpToken.add(firstToken);
		List<Token> interpList = new ArrayList<>();
		interpList.add(interpToken);
//		interpList.add(rtsToken);
		Context interpContext = new Context(interpList);
		
		Integer myInt = 666;
		uut.rPush(myInt);
		uut.jumpToContext(interpContext);
		uut.executeContext();
		uut.returnFromContext();
		assertEquals(1,uut.rDepth());
		assertEquals(0,uut.depth());
		assertEquals(1,lastExecuted);
		assertEquals(myInt,uut.rPop());
	}
	
	@Test
	public void testReturnFromContext() {
		Integer myInt = 666;
		uut.rPush(myInt);
		List<Token> tokenList = new ArrayList<>();
		tokenList.add(thirdToken);
		Context previous = new Context(tokenList);
		uut.branchToContext(previous);
		uut.jumpToContext(context);
		Context jumpedTo = uut.returnFromContext();
		assertEquals(context,jumpedTo);
		assertEquals(1,uut.rDepth());
		assertEquals(myInt,uut.rPop());
		assertEquals(previous,uut.currentContext());
	}
	
	
}
