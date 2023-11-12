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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract implementation of a word stream which utilizes a BufferedReader as its input.
 * Concrete implementations should merely need to provide a constructor extension which initializes
 * the instance's reader.
 * 
 * @author tharter
 *
 */
public abstract class WordStream implements IWordStream {

	private static final Logger log = StandAloneHairball.PLATFORM.getLogger(WordStream.class.getName());

	private String input = "";
	private Scanner inputScanner;
	protected BufferedReader reader;
	private int columnNumber = 0;
	private int lineNumber = 0;

	/**
	 * Create a stream with an initially empty input. This constructor needs to be
	 * extended to provide logic to initialize the reader field.
	 * 
	 */
	public WordStream() {
		inputScanner = new Scanner(input);
	}

	/**
	 * Return the next line of input from the reader. This is exposed so that
	 * it can be overridden in certain specific cases.
	 * 
	 * @return String the next line of input
	 * @throws IOException if reading fails
	 */
	protected String getNextLine() throws IOException {
		this.columnNumber = 0;
		this.lineNumber++;
		return reader.readLine();
	}
	
	private String getNext() throws IOException {
		log.entering(this.getClass().getName(), "getNext");
		
		if(inputScanner.hasNext()) {
			log.log(Level.FINEST,"Current line has more tokens, returning next one");
			String next = inputScanner.next();
			this.columnNumber += next.length();
			return next;
		} else {
			input = this.getNextLine();
			log.log(Level.FINEST,"Line was exhausted, got a new line");
			
			if(input != null) {
				inputScanner = new Scanner(input);
				if(inputScanner.hasNext()) {
					log.log(Level.FINEST,"Returning a token from the new line");
					String next = inputScanner.next();
					this.columnNumber += next.length();
					return next;
				}
				log.log(Level.FINEST,"Line was exhausted, next line was blank, returning double newline");
				return "\n\n"; // we got double returns, which is a special token for us
			} else {
				log.log(Level.FINER,"No more input from word stream, returning null");
				return null; // input is exhausted.
			}
		}
	}
	
	@Override
	public Word getNextWord() throws IOException {
		log.entering(this.getClass().getName(), "getNextWord");
		
		String next = getNext();
		return next == null ? null : new Word(next);
	}

	@Override
	public String getToMatching(String match) throws IOException, IllegalArgumentException {
//System.out.println("Got to WordStream.getToMatching() with match of "+match);
		log.log(Level.FINEST,"Entering getToMatching, with match of "+match);
		
		if(match == null)
			throw new IllegalArgumentException("Cannot match against null");
		StringBuffer sb = new StringBuffer();
		String more =  getNext();
//System.out.println("more returned "+more);
		boolean first = true;
		while(!(match.equals(more)) && more != null) {
			if(!first) sb.append(' ');
			first = false;
			sb.append(more);
			more = getNext();
		}
		return sb.toString();
	}

	@Override
	public boolean hasMoreTokens() throws IOException {
//System.out.println("Got to wordstream hasMoreTokens");
		boolean isHasNext = inputScanner.hasNext();
		boolean rready = reader.ready();
//System.out.println("isHasNext is "+isHasNext+", rready is "+rready);		
		return isHasNext || rready;
	}

	@Override
	public String getToDelimiter(String match) throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = inputScanner.hasNextLine() ? inputScanner.nextLine() : null;
		if(line != null) {
			int midx = line.indexOf(match);
			if(midx > -1) {
				this.columnNumber += midx+1+match.length();
				inputScanner = new Scanner(line.substring(midx+match.length()));
				return line.substring(0, midx).stripLeading();
			} else {
				this.columnNumber = 0;
				sb.append(line);
			}
		}
		line = reader.readLine();
		this.lineNumber++;
		while(line != null) {
			int midx = line.indexOf(match);
			if(midx > -1) {
				this.columnNumber += midx+1+match.length();
				sb.append("\n");
				sb.append(line.substring(0, midx));
				inputScanner = new Scanner(line.substring(midx+match.length()));
				return sb.toString().stripLeading();
			}
			sb.append(line);
			this.columnNumber = 0;
			this.lineNumber++;
			line = reader.readLine();
		}
//		return sb.toString();
		return null;
	}

	@Override
	public void close() throws IOException {
		reader.close();
		inputScanner.close();
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
