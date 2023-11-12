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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.google.gwt.core.shared.GwtIncompatible;

/**
 * A word stream which handles non-interactive input. This is
 * good for things like files.
 * 
 * @author tharter
 *
 */
//@GwtIncompatible
public class BufferedWordStream extends WordStream {
	
	/**
	 * Create a word stream with the given input stream as its source.
	 * 
	 * @param prompt
	 */
	public BufferedWordStream(InputStream in) {
		super();
		reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public String getSource() {
		return "buffered word stream";
	}


}
