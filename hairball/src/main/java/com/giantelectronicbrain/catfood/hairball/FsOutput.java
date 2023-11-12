/**
 * This software is Copyright (C) 2021 Tod G. Harter. All rights reserved.
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

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

/**
 * Create an output based on a Vertx fileSystem.
 * 
 * @author tharter
 *
 */
public class FsOutput implements Output {
	private final Vertx vertx;
	private final AsyncFile file;
	private int position = 0;
	
	public FsOutput(String fileName) {
		vertx = Vertx.vertx();
		FileSystem fileSystem = vertx.fileSystem();
		//TODO: implement async versions of stuff
		file = fileSystem.openBlocking(fileName, new OpenOptions() {});
	}

	@Override
	public void space() throws IOException {
		emit(" ");
	}

	@Override
	public void emit(String output) throws IOException {
		Buffer buf = Buffer.buffer(output, StandardCharsets.UTF_8.name());
		file.write(buf, position);
		position += buf.length();
	}

	@Override
	public void close() throws IOException {
		file.flush();
		file.close();
		vertx.close();
	}

	@Override
	public OutputStream getOutputStream() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("FsOutput doesn't support streams");
	}

}