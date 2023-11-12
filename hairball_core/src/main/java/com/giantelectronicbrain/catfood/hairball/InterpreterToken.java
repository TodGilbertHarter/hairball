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

import java.util.ArrayList;
import java.util.List;

/**
 * This represents the runtime information for an interpreted word.
 * Execute here will execute a jumpToContext to a new context for this
 * token, and then calls executeContext to run it, and then returnFromContext
 * to restore the previous context.
 * 
 * @author tharter
 *
 */
public class InterpreterToken implements Token {
	private final String name;
	private final List<Token> tokens;
	
	/**
	 * Convenience function for building interpreter tokens. This makes it easier to 
	 * build them in Java, which is easier for a lot of core words.
	 * 
	 * @param tokens
	 * @return
	 */
	public static Token makeToken(String name, Token... tokens) {
		InterpreterToken token = new InterpreterToken(name);
		for(Token atoken : tokens) {
			token.add(atoken);
		}
		return token;
	}
	
	/**
	 * Create an empty interpreter token with the given name.
	 * 
	 * @param name token's name.
	 */
	public InterpreterToken(String name) {
		this(name,new ArrayList<>());
	}

	/**
	 * Create an interpreter token with the given name and behavior.
	 * 
	 * @param name token's name
	 * @param tokens list of tokens this token will execute.
	 */
	public InterpreterToken(String name, List<Token> tokens) {
		this.tokens = tokens;
		this.name = name;
	}

	/**
	 * Add a new token to the end of this token's behavior.
	 * 
	 * @param newToken a new token to append to the behavior.
	 */
	public void add(Token newToken) {
		this.tokens.add(newToken);
	}

	/**
	 * Get the number of tokens in this token.
	 * 
	 * @return number of tokens.
	 */
	public int size() { return tokens.size(); }
	
	/**
	 * Execute the behavior of this token using the given interpreter. A new
	 * InterpreterContext will be generated, the token executed on it, and the
	 * previous context restored.
	 * 
	 * @param interpreter the interpreter which is running our code
	 * @throws HairballException 
	 */
	public boolean execute(Interpreter interpreter) throws HairballException {
		Context newContext = new Context(tokens,0);
		interpreter.jumpToContext(newContext);
		Context ctx = interpreter.executeContext();
		interpreter.returnFromContext();
		return ctx.isContinue();
	}

	/**
	 * Get the name of this token. This is just a human-readable mnemonic to
	 * help identify tokens.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "InterpreterToken [name=" + name + "]";
	}

}
