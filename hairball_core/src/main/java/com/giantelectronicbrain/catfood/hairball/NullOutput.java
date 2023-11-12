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

import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a 'black hole' output. It is good for things like setting up an interpreter
 * by reading some preliminary startup code who's output you don't want to keep.
 * 
 * @author tharter
 *
 */
public class NullOutput implements Output {

	@Override
	public void space() throws IOException {
	}

	@Override
	public void emit(String output) throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public OutputStream getOutputStream() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("NullOutput does not support streams");
	}

}
