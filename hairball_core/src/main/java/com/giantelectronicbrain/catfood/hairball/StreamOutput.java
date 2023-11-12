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
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tharter
 *
 */
public class StreamOutput implements Output {
	private static final Logger log = Hairball.PLATFORM.getLogger(StreamOutput.class.getName());

	private final OutputStream out;
	
	public StreamOutput(OutputStream out) {
		this.out = out;
	}
	
	public OutputStream getOutputStream() {
		return this.out;
	}

	public void space() throws IOException {
		log.log(Level.FINEST,"Emiting a space");
		
		out.write(' ');
	}
	
	@Override
	public void emit(String output) throws IOException {
		log.log(Level.FINEST,"Outputting text");
		
		out.write(output.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void close() throws IOException {
//TODO: uncomment this. We will need to make a 'ConsoleOutput' which doesn't close
//		out.close();
	}
}
