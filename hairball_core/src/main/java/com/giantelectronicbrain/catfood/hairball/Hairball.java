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
import java.util.Stack;

import com.giantelectronicbrain.catfood.IPlatform;

/**
 * This is a core base class for Hairball which provides just enough functionality
 * to allow embedding a core Hairball interpreter which can parse strings and
 * generate string output. This is enough for most testing and should be 
 * compatible with most JS transpilers.
 * 
 * @author tharter
 *
 */
public class Hairball {
	public static IPlatform PLATFORM;
	public static String VERSION;
	private final Dictionary rootDictionary = new Dictionary("root");
	private final Parser parser;
	private final Interpreter interpreter;

	public Hairball(IPlatform platform, IWordStream input, Output output) {
		this(platform);
		setIO(input, output);
	}

	public Hairball(IPlatform platform) {
		this.PLATFORM = platform;
		this.VERSION = platform.getVersion();
		IVocabulary hbVocab = HairballVocabulary.create();
		rootDictionary.add(hbVocab);
		interpreter = new Interpreter();
		parser = new Parser();
	}
	
	public void setIO(IWordStream wordStream,Output output) {
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	public void setInput(IWordStream moreWordStream) {
		Output output = this.parser.getContext().getOutput();
		ParserContext pcontext = new ParserContext(moreWordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	/**
	 * Run the Hairball engine, processing the input until eof and generating
	 * output, etc. This is the main entry point for actually running a Hairball
	 * program.
	 * 
	 * @return
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext execute() throws IOException, HairballException {
		parser.interpret();
		return parser.parse();
	}

	/**
	 * Get the whole parameter stack. This is mainly useful for testing.
	 */
	public Stack<Object> getParamStack() {
		return interpreter.getParameterStack();
	}

	/**
	 * Get the whole return stack. This is mainly useful for testing.
	 */
	public Stack<Object> getReturnStack() {
		return interpreter.getReturnStack();
	}

	/**
	 * Get the current interpreter context. Mostly useful for testing.
	 */
	public Context getInterpreterContext() {
		return interpreter.currentContext();
	}

	/**
	 * Return the parser for this Hairball instance.
	 * 
	 * @return the parser
	 */
	public Parser getParser() {
		return parser;
	}
	
}
