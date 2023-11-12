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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author tharter
 *
 */
public class InterpreterTokenTest {

	int didIt = 0;
	
	public boolean behavior(Interpreter interp) {
		didIt++;
		return true;
	}
	
	@Test
	public void test() {
		InterpreterToken uut = new InterpreterToken("test");
		NativeToken firstToken = new NativeToken("ntest",this::behavior);
		uut.add(firstToken);
		assertEquals(1,uut.size());
		assertEquals("test",uut.getName());
	}

	public void testMake() {
		NativeToken firstToken = new NativeToken("ntest",this::behavior);
		InterpreterToken uut = (InterpreterToken) InterpreterToken.makeToken("test", firstToken);
		assertEquals(1,uut.size());
		assertEquals("test",uut.getName());
		
	}
}
