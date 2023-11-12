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
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * Extend the HairballVocabulary which is found in hairball_core to include some
 * additional file handling utilities which cannot be implemented there because
 * they are impossible to transpile.
 * 
 * @author tharter
 *
 */
public class ExtendHairballVocabulary {
	
	/**
	 * Static factory to create the one and only needed instance of this class.
	 * This is how HairballVocabulary should always be created, there is no need
	 * for more than one.
	 * 
	 * @return
	 */
	public static IVocabulary create() {
		IVocabulary hbVocab = HairballVocabulary.create();
		for(Definition def : defList) {
			hbVocab.add(def);
		}
		return hbVocab;
	}
	
	/**
	 * The actual definitions which will be placed within the vocabulary.
	 */
	private static final List<Definition> defList = new ArrayList<>();
	static {

		Token compile = new NativeToken("compile",(interpreter) -> {
			Definition ourDef = (Definition) interpreter.pop();
			Token rtoken = ourDef.getRunTime();
			interpreter.getParserContext().getDictionary().addToken(rtoken);
			return true;
		});
		/**
		 * Quote some text and push it onto the stack as a literal.
		 */
		Token quoteRT = new NativeToken("quote",(interpreter) -> {
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
		});
		
		/*
		 * Source input from another file. This will construct a new parser 
		 * context and a new parser, and then
		 * launch the parser. Output should be interpolated seamlessly into
		 * the current output stream. 
		 */
		Token source = new NativeToken("source", (interpreter) -> {
			Vertx vertx = Vertx.vertx();
			FileSystem fileSystem = vertx.fileSystem();
			ParserContext cContext = interpreter.getParserContext();
			String fileName = null;
			try {
				String currentBucket = interpreter.getParserContext().getWordStream().getCurrentLocation();
				fileName = (String) interpreter.pop();
				IWordStream wordStream = new BucketWordStream(fileSystem,fileName,currentBucket);
				Parser nParser = new Parser();
				nParser.setEmit(cContext.getParser().getEmit());
				Interpreter nInterpreter = new Interpreter();
				ParserContext nContext = new ParserContext(wordStream, cContext.getDictionary(), 
						nInterpreter, cContext.getOutput(), nParser);
				nInterpreter.setParserContext(nContext);
				nParser.setParserContext(nContext);
				nParser.parse();
			} catch (IOException e) {
				throw new HairballException("Failed to parse /SOURCE file"+fileName);
			}
			vertx.close();
			return true;
		});
		Token sourceQuote = InterpreterToken.makeToken("sourceQuote", quoteRT,source);
		defList.add(new Definition(new Word("/SOURCE\""),compile,sourceQuote));

		Token version = new NativeToken("version", (interpreter) -> {
			interpreter.push(StandAloneHairball.VERSION);
			return true;
		});
		defList.add(new Definition(new Word("/VERSION"),compile,version));

	}
}
