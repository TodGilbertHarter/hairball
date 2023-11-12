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
public class NativeTokenTest {
	private boolean didIt = false;

	private boolean behavior(Interpreter interp) {
		didIt = true;
		return true;
	}
	
	@Test
	public void test() {
		Token uut = new NativeToken("test",this::behavior);
		
		try {
			uut.execute(null);
		} catch (HairballException e) {
			e.printStackTrace();
			fail("threw Hairball Exception");
		}
		assertTrue(didIt);
	}

}
