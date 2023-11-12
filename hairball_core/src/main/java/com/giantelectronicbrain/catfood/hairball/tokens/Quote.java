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
package com.giantelectronicbrain.catfood.hairball.tokens;

import java.io.IOException;

import com.giantelectronicbrain.catfood.hairball.Definition;
import com.giantelectronicbrain.catfood.hairball.HairballException;
import com.giantelectronicbrain.catfood.hairball.Interpreter;
import com.giantelectronicbrain.catfood.hairball.ParserLocation;
import com.giantelectronicbrain.catfood.hairball.Token;

/**
 * Default compile time behavior for words, take the runtime
 * behavior token and insert it into the current definition's
 * token list. Which list that will be is determined by the
 * mode, DOER or DOES.
 * 
 * Note that there is no actual Hairball definition for this, it
 * is effectively how compiling mode does its job and could be thought
 * of as the compile time behavior of the parser.
 *
 * @author tharter
 *
 */
public class Quote implements Token {
	public static final Quote INSTANCE = new Quote();
	
	@Override
	public boolean execute(Interpreter interpreter) throws HairballException {
		ParserLocation pl = new ParserLocation(interpreter.getParserContext().getWordStream());
		try {
			String quoted = interpreter.getParserContext().getWordStream().getToDelimiter("\"/");
			if(quoted == null) {
				String eMsg = pl.makeErrorMessage("/\" failed to find matching \"/");
				throw new HairballException(eMsg);
			}
			quoted = quoted.stripTrailing();
			interpreter.push(quoted);
		} catch (IOException e) {
			throw new HairballException(pl.makeErrorMessage("Word could not read a token from input"),e);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Quote";
	}

}
