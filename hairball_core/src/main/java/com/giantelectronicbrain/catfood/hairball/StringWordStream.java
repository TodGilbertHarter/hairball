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
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A word stream which handles non-interactive input. This stream should also be
 * transpileable to Javascript.
 * 
 * @author tharter
 *
 */
public class StringWordStream implements IWordStream {
//	private static final Logger log = Hairball.PLATFORM.getLogger(StringWordStream.class.getName());

	private String input = "";
	private TokenScanner inputScanner;
	private StringReader reader;
	private int lineNumber = 0;
	
	/**
	 * Provides functionality similar to a StringReader, since the 
	 * transpiled version lacks mark() and reset().
	 * 
	 * @author tharter
	 *
	 */
	private class TokenScanner {
		private String data;
		private int pointer;
		private int mark;
		
		public TokenScanner(String data) {
			this.data = data;
			this.pointer = 0;
		}
		
		private void mark() {
			this.mark = this.pointer;
		}
		
		private void reset() {
			this.pointer = this.mark;
		}

		/**
		 * Scan for a given string and if it exists in this scanner, appending
		 * each character to the given StringBuffer until a match is found. If
		 * there is no match when input is exhausted, then return false, else
		 * return true. Either way the StringBuffer will contain any prematch
		 * text, or the entire scanner if there was no match.
		 * 
		 * @param match string to match
		 * @param sb the string buffer to append to
		 * @return true if there was a match, false otherwise
		 */
		public boolean scanInput(String match, StringBuffer sb) {
			char ch;
			do {
				ch = inputScanner.read();
				if(ch != Character.valueOf((char)-1))
					sb.append(ch);
				int midx = sb.indexOf(match, sb.length()-match.length());
				if(midx > 0) {
					sb.replace(midx, sb.length(), "");
					return true;
				}
			} while(ch != Character.valueOf((char)-1));
			return false;
		}

		/**
		 * Return the pointer to the next character.
		 * 
		 * @return pointer to the next character.
		 */
		public int getPointer() {
			return this.pointer;
		}
		
		/**
		 * Get one character from the scanner.
		 * 
		 * @return
		 */
		public char read() {
			if(this.pointer < this.data.length())
				return this.data.charAt(this.pointer++);
			return (char)-1;
		}
		
		/**
		 * Check to see if the reader has any tokens, that is non-whitespace characters
		 * which are not newlines, in it.
		 * 
		 * @param stringReader
		 * @return true if there is a token in this reader
		 * @throws IOException 
		 */
		public boolean hasNext() throws IOException {
			mark();
			char ch = read();
			while(ch != Character.valueOf((char)-1)) {
				if(!Character.isWhitespace(ch)) {
					reset();
					return true;
				}
				ch = read();
			}
			reset();
			return false;
		}

		private String next() throws IOException {
			boolean isThereData = false;
			StringBuffer buff = new StringBuffer();
			char ch = read();
			while(ch != Character.valueOf((char)-1)) {
				if(!Character.isWhitespace(ch)) { 
					isThereData = true;
					break;
				}
				ch = read();
			}
			while(ch != Character.valueOf((char)-1)) {
				if(Character.isWhitespace(ch)) {
					break;
				}
				buff.append(ch);
				ch = read();
			}
//			log.log(Level.FINEST, "got to end of line, returning");
			return isThereData ? buff.toString() : null;
		}
		
	}
	
	/**
	 * Create a stream with the given input string as its source.
	 * 
	 * @param prompt
	 */
	public StringWordStream(String in) {
		inputScanner = new TokenScanner(input);
		reader = new StringReader(in);
	}

	@Override
	public Word getNextWord() throws IOException {
		String next = getNext();
		return next == null ? null : new Word(next);
	}
	
	private String getNext() throws IOException {
//		log.log(Level.FINEST, "Entering getNext");
		if(inputScanner.hasNext()) {
//			log.log(Level.FINEST,"input is ready, getting another word");
			return inputScanner.next();
		} else {
//			log.log(Level.FINEST, "getting another line");
			input = readLine(reader);
			if(input != null) {
//				log.log(Level.FINEST, "new line is not null, scanning");
				inputScanner = new TokenScanner(input);
				if(inputScanner.hasNext()) {
//					log.log(Level.FINEST, "getting a new word from the new line");
					return inputScanner.next();
				}
				return "\n\n"; // we got double returns, which is a special token for us
			} else {
				return null; // input is exhausted.
			}
		}
	}

	/**
	 * This is a replacement for readLine which most transpilers can handle...
	 * 
	 * @param reader the reader we are reading from.
	 * @return one line of input from the reader
	 * @throws IOException 
	 */
	private String readLine(Reader reader) throws IOException {
		boolean isThereData = false;
//		log.log(Level.FINEST,"Entering readLine");
		StringBuffer buff = new StringBuffer();
		char ch = (char) reader.read();
		while(ch != Character.valueOf((char)-1)) {
			isThereData = true;
			buff.append(ch);
			if(ch == '\n') {
//				log.log(Level.FINEST,"got a newline, returning data");
				lineNumber++;
				return buff.toString();
			}
			ch = (char) reader.read();
		}
		return isThereData ? buff.toString() : null;
	}
	
	@Override
	public boolean hasMoreTokens() throws IOException {
		boolean isHasNext = inputScanner.hasNext();
		boolean rready = reader.ready();
		return isHasNext || rready;
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
		// Create the output buffer and attempt to match against the current
		// inputBuffer.
		StringBuffer sb = new StringBuffer();
		boolean hasMatch = inputScanner.scanInput(match, sb);
		if(hasMatch) return sb.toString();
		
		// Continue to iterate through input extending the output buffer and
		// trying to match on each line.
		String line = readLine(reader);
		while(line != null) {
			inputScanner = new TokenScanner(line);
			hasMatch = inputScanner.scanInput(match, sb);
			if(hasMatch) return sb.toString();
			line = readLine(reader);
		}
		return null;
	}

	@Override
	public void close() throws IOException {
		if(reader != null) reader.close();
	}

	@Override
	public String getSource() {
		return "java string";
	}

	@Override
	public int getLine() {
		return this.lineNumber;
	}

	@Override
	public int getColumn() {
		return this.inputScanner.getPointer();
	}

	@Override
	public String getCurrentLocation() {
		return ".";
	}

}
