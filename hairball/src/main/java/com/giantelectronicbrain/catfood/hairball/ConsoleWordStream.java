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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

//import com.google.gwt.core.shared.GwtIncompatible;

/**
 * A word stream which is attached to the console.
 * 
 * @author tharter
 *
 */
//@GwtIncompatible
public class ConsoleWordStream implements IWordStream {
//	private final Console console;
	private final String prompt;
	private String input = "";
	private Scanner inputScanner;
	private BufferedReader reader;
	private OutputStream out;
	private byte[] pBytes = null;
	private int lineNumber = 0;
	private int columnNumber = 0;
	
	/**
	 * Create a ConsoleWordStream attached to STDIN/OUT.
	 * 
	 * @param prompt
	 */
	public ConsoleWordStream(String prompt) {
		this.prompt = prompt;
		pBytes = prompt.getBytes(Charset.defaultCharset());
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(System.in));
		out = System.out;
	}

	/**
	 * Create a ConsoleWordStream attached to the given input and output.
	 * 
	 * @param prompt
	 * @param out
	 * @param in
	 */
	public ConsoleWordStream(String prompt, OutputStream out, InputStream in) {
		this.prompt = prompt;
		pBytes = prompt.getBytes(Charset.defaultCharset());
		inputScanner = new Scanner(input);
		reader = new BufferedReader(new InputStreamReader(in));
		this.out = out;
	}

	@Override
	public Word getNextWord() throws IOException {
		String next = getNext();
		return next == null ? null : new Word(next);
	}
	
	private String getNext() throws IOException {
		if(inputScanner.hasNext()) {
			String next = inputScanner.next();
			this.columnNumber += next.length();
			return next;
		} else {
			out.write(pBytes);
			input = reader.readLine();
			lineNumber++;
			if(input == null) return null; // shouldn't happen with a console...
			inputScanner = new Scanner(input);
			this.columnNumber = 0;
			if(inputScanner.hasNext()) {
				String next = inputScanner.next();
				this.columnNumber += next.length();
				return next;
			}
			lineNumber++;
			return "\n\n"; // we got double returns, which is a special token for us
		}
	}

	@Override
	public boolean hasMoreTokens() {
		return true;
	}

	@Override
	public String getToMatching(String match) throws IOException {
		StringBuffer sb = new StringBuffer();
		String more =  getNext();
		boolean first = true;
		while(!(more.equals(match))) {
			if(!first) sb.append(' ');
			first = false;
			sb.append(more);
			more = getNext();
		}
		return sb.toString();
	}

	@Override
	public String getToDelimiter(String match) throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = inputScanner.nextLine();
		if(line != null) {
			this.columnNumber += line.length();
			int midx = line.indexOf(match);
			if(midx > -1) {
				inputScanner = new Scanner(line.substring(midx+match.length()));
				return line.substring(0, midx).stripLeading();
			} else {
				sb.append(line);
			}
		}
		this.columnNumber = 0;
		line = reader.readLine();
		lineNumber++;
		while(line != null) {
			int midx = line.indexOf(match);
			if(midx > -1) {
				this.columnNumber += line.length();
				sb.append("\n");
				sb.append(line.substring(0, midx));
				inputScanner = new Scanner(line.substring(midx+match.length()));
				return sb.toString().stripLeading();
			}
			sb.append(line);
			this.columnNumber = 0;
			line = reader.readLine();
			lineNumber++;
		}
		// probably won't ever get here in a console...
//		return sb.toString();
		return null;
	}

	@Override
	public void close() throws IOException {
		out.close();
		reader.close();
		inputScanner.close();
	}

	@Override
	public String getSource() {
		return "console";
	}

	@Override
	public int getLine() {
		return this.lineNumber;
	}

	@Override
	public int getColumn() {
		return this.columnNumber;
	}

	@Override
	public String getCurrentLocation() {
		return ".";
	}

}
