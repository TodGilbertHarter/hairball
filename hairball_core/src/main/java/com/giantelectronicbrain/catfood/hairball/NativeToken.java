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

/**
 * A token which simply executes native Java code. Its behavior is a lambda which
 * implements the HairballBehavior functional interface. Any inputs or outputs
 * will be to the stack, or operations on the Context, etc.
 * 
 * @author tharter
 *
 */
public class NativeToken implements Token {
	private final String name;
	private final HairballBehavior behavior;

	public NativeToken(String name, HairballBehavior behavior) {
		this.behavior = behavior;
		this.name = name;
	}
	
	@Override
	public boolean execute(Interpreter interpreter) throws HairballException {
		return behavior.run(interpreter);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "NativeToken [name=" + name + "]";
	}

}
