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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.hairball.Parser.ParserBehavior;
import com.giantelectronicbrain.catfood.hairball.tokens.Drop;
import com.giantelectronicbrain.catfood.hairball.tokens.Emit;

/**
 * Hairball outer interpreter. The outer interpreter is responsible for interpreting
 * program input in a ParserContext and invoking the appropriate behaviors of
 * encountered words using that context. 
 * 
 * The interpreter has two modes, interpreting mode and compiling mode. In the
 * interpreting mode it simply parses words off the context's input and either
 * executes their runtime behavior if they match dictionary entries, or emits
 * them to output otherwise.
 * 
 * In compiling mode the interpreter invokes the current vocabulary's compile
 * time behavior handler, which normally compiles either the runtime or compile time
 * token for the word, depending on its type.
 * 
 * The parser can handle one or two special scenarios as well. Note that a couple
 * of its behaviors can be vectored, this is still a TBD area.
 * 
 * @author tharter
 *
 */

public class Parser {
	private static final Logger log = Hairball.PLATFORM.getLogger(Parser.class.getName());

	private boolean interpreting = true;
	private ParserContext currentContext;
	private Token emit = Emit.INSTANCE;
	private ParserBehavior parserBehavior = this::executeWord;
		
	public static interface ParserBehavior {
		public abstract boolean handle(Word word) throws HairballException, IOException;
	}
	
	/**
	 * Set the version of emit which is used by the parser to emit literals in
	 * interpreting mode.
	 * 
	 * @param anEmitter a Token which, when executed, emits whatever is on the 
	 * top of the stack. All it actually has to do is consume TOS.
	 * 
	 * @return the previous emit implementation for this parser.
	 */
	public Token setEmit(Token anEmitter) {
		Token oldEmitter = this.emit;
		this.emit = anEmitter;
		return oldEmitter;
	}
	
	public Token getEmit() {
		return this.emit;
	}
	
	/**
	 * Create a parser with the given ParserContext.
	 * 
	 * @param currentContext the context to parse from.
	 */
	public Parser(ParserContext currentContext) {
		this.currentContext = currentContext;
	}
	
	/**
	 * Create an unattached parser. A context must be supplied before parsing
	 * can take place.
	 * 
	 */
	public Parser() {
		
	}
	
	/**
	 * Shutdown this parser, this should clean up open inputs and outputs, etc.
	 * @throws IOException 
	 */
	public void close() throws IOException {
		currentContext.close();
	}
	
	/**
	 * Set the current context for this parser. Also returns the old context
	 * so that it could be restored later.
	 * 
	 * @param currentContext the new parser context
	 * @return the previous context, or null
	 */
	public ParserContext setParserContext(ParserContext currentContext) {
		ParserContext temp = this.currentContext;
		this.currentContext = currentContext;
		return temp;
	}
	
	/**
	 * Parse the current input word stream, executing the behavior provided.
	 * This will continue until the word stream is exhausted, a word throws
	 * an exception, or a token returns false. In the later case we will
	 * exit without an exception, just as if the input was exhausted.
	 * 
	 * @param action action to perform on each token parsed
	 * @return the parser context, which is the whole VM state
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext parse() throws IOException, HairballException {
		IWordStream wordStream = currentContext.getWordStream();
		try {
			Word word = wordStream.getNextWord();
			while(word != null) {
				log.log(Level.FINEST,"We got a word, "+word);
				boolean rv = parserBehavior.handle(word);
				if(!rv) break; // drop out of the parsing loop if a token returns false
				word = wordStream.getNextWord();
			}
			flushLitAccum(); // make sure nothing is left behind in some edge cases
			return currentContext;
		} catch (HairballException he) {
			String msg = makeParserExceptionMessage(he,wordStream);
			throw new HairballException(msg,he);
		}
	}

	private String makeParserExceptionMessage(HairballException he, IWordStream wordStream) {
		String source = wordStream.getSource();
		int lineNumber = wordStream.getLine();
		int columnNumber = wordStream.getColumn();
		String eMsg = he.getMessage();
		return eMsg + " in "+source+" at line "+lineNumber+", column "+columnNumber;
	}
	/**
	 * Set the parser into interpreting mode. In this mode we will call the
	 * runtime behavior of definitions.
	 * 
	 */
	public ParserContext interpret() {
		this.interpreting = true;
		this.parserBehavior = this::executeWord;
		return currentContext;
	}
	
	/**
	 * Set the parser into compiling mode. In this mode we will call the 
	 * compile time behavior of definitions
	 * 
	 */
	public ParserContext compile() {
		this.interpreting = false;
		this.parserBehavior = this::compileWord;
		return currentContext;
	}
	
	/**
	 * Execute the runtime behavior of the current word.
	 * 
	 * @param word
	 * @throws HairballException 
	 */
	public boolean executeWord(Word word) throws HairballException {
		boolean rv = true;
		Definition definition = currentContext.getDictionary().lookUp(word);
		if(definition != null) {
			flushLitAccum();
			Token runTime = definition.getRunTime();
			rv = currentContext.getInterpreter().execute(runTime);
		} else {
			if(!isNumber(word))
				handleLiteralWord(word);
		}
		return rv;
	}
	
	private boolean isNumber(Word word) throws HairballException {
		String value = word.getValue();
		if(value.startsWith("#"))
			try {
				Integer v = Integer.valueOf(value.substring(1));
				currentContext.getInterpreter().push(v);
				flushLitAccum();
				return true;
			} catch(NumberFormatException e) {
				return false;
			}
		return false;
	}
	
	/**
	 * Compile words. If the word is a literal, then add a literal token to the current definition's
	 * behavior. If the word is defined, then add its token to the current definition's behavior.
	 * 
	 * @param word
	 * @throws HairballException 
	 */
	public boolean compileWord(Word word) throws HairballException {
		boolean rv = true;
		Definition definition = currentContext.getDictionary().lookUp(word);
		if(definition != null) {
			flushLitAccum();
			Token compileTime = definition.getCompileTime();
			//NOTE: putting our own definition on the stack so that compile time
			// behavior knows which word it is handling. ALL compile time behaviors
			// thus MUST include a drop or otherwise handle this definition! If
			// you don't, then it will linger on the stack and break stuff.
			currentContext.getInterpreter().push(definition);
			rv = currentContext.getInterpreter().execute(compileTime);
		} else { // this is a literal
			handleLiteralWord(word);
		}
		return rv;
	}

	private StringBuilder litAccum = new StringBuilder();
	
	private void handleLiteralWord(Word lWord) {
		if(litAccum.length() > 0) litAccum.append(' ');
		litAccum.append(lWord.getValue());
	}
	
	private void flushLitAccum() throws HairballException {
		log.log(Level.FINEST,"Calling flushLitAccum");
		
		if(litAccum.length() > 0) {
			LiteralToken token = new LiteralToken("accumLiteral",litAccum.toString());
			litAccum = new StringBuilder();
			if(interpreting) {
				token.execute(currentContext.getInterpreter());
				emit.execute(currentContext.getInterpreter());
			} else {
				currentContext.getDictionary().addToken(token);
				currentContext.getDictionary().addToken(Emit.INSTANCE);
			}
		}
	}
	
	/**
	 * Get the current parser context for this parser.
	 * 
	 * @return parser context
	 */
	public ParserContext getContext() {
		return this.currentContext;
	}
}
