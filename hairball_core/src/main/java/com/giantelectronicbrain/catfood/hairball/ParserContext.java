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

/**
 * Container for the configuration state of the Parser. This lets us set up
 * parsers and much more easily pass initialization and state around.
 * 
 * @author tharter
 *
 */
public class ParserContext {
	private IWordStream wordStream;
	private Dictionary dictionary;
	private Interpreter interpreter;
	private Output output;
	private Parser parser;
	
	/**
	 * Create a new ParserContext.
	 * 
	 * @param wordStream the input wordStream for this parser
	 * @param rootDictionary the dictionary used by this parser
	 * @param interpreter the interpreter to execute words on
	 * @param output the output stream for the parser
	 * @param parser a pointer to the parser itself
	 */
	public ParserContext(IWordStream wordStream, Dictionary rootDictionary, 
			Interpreter interpreter, Output output, Parser parser) {
		this.wordStream = wordStream;
		this.dictionary = rootDictionary;
		this.interpreter = interpreter;
		this.output = output;
		this.parser = parser;
	}

	/**
	 * Shutdown the input and output associated with this context.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.output.close();
		this.wordStream.close();
	}
	
	/**
	 * Get the parser.
	 * 
	 * @return
	 */
	public Parser getParser() {
		return this.parser;
	}
	
	/**
	 * Get the output.
	 * 
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * Set the output.
	 * 
	 * @param output the output to set
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

	/**
	 * Get the wordStream.
	 * 
	 * @return the wordStream
	 */
	public IWordStream getWordStream() {
		return wordStream;
	}

	/**
	 * Set the wordStream.
	 * 
	 * @param wordStream the wordStream to set
	 */
	public void setWordStream(IWordStream wordStream) {
		this.wordStream = wordStream;
	}

	/**
	 * Get the dictionary.
	 * 
	 * @return the dictionary
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * Set the dictionary.
	 * 
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Get the interpreter.
	 * 
	 * @return the interpreter
	 */
	public Interpreter getInterpreter() {
		return interpreter;
	}

	/**
	 * Set the interpreter.
	 * 
	 * @param interpreter the interpreter to set
	 */
	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}


}
