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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import com.giantelectronicbrain.catfood.hairball.tokens.Compile;
import com.giantelectronicbrain.catfood.hairball.tokens.Drop;
import com.giantelectronicbrain.catfood.hairball.tokens.Quote;

/**
 * Define the core 'native' word set. These are mostly native words implemented
 * in Java. This is the minimal word set needed to have a functional
 * hairball language.
 * 
 * @author tharter
 *
 */
public class HairballVocabulary {
	
	/**
	 * Static factory to create the one and only needed instance of this class.
	 * This is how HairballVocabulary should always be created, there is no need
	 * for more than one.
	 * 
	 * @return
	 */
	public static IVocabulary create() {
		IVocabulary hbVocab = new Vocabulary("HAIRBALL");
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
		Token compile = Compile.INSTANCE;

		/**
		 * Compile the token on the top of the stack. this simply adds the token to the
		 * current dictionary entry.
		 */
		Token compileToken = new NativeToken("compileToken",(interpreter) -> {
//			interpreter.pop();
			interpreter.getParserContext().getDictionary().addToken((Token)interpreter.pop());
			return true;
		});
		defList.add(new Definition(new Word("/HERE!"),compile,compileToken));
		defList.add(new Definition(new Word("/[COMPILE]"),compileToken,compileToken));
		
		/* Given a definition on TOS return its runtime token */
		Token getRunTime = new NativeToken("getRunTime", (interpreter) -> {
			Definition def = (Definition) interpreter.pop();
			Token rt = def.getRunTime();
			interpreter.push(rt);
			return true;
		});
		defList.add(new Definition(new Word("/RUNTIME"),compile,getRunTime));
		
		/**
		 * Store a token at an arbitrary location in the current definition. This might be useful
		 * to do something like go back and replace a placeholder with a literal to form a branch
		 * target, for example. Or to provide a very simple type of data structure.
		 */
		Token store = new NativeToken("store",(interpreter) -> {
			int pc = (Integer) interpreter.pop();
			Token token = (Token) interpreter.pop();
			interpreter.getParserContext().getDictionary().putToken(token,pc);
			return true;
		});
		defList.add(new Definition(new Word("/!"),compile,store));
		
		/**
		 * Call the inner interpreter to execute the token on TOS.
		 */
		Token execute = new NativeToken("execute",(interpreter) -> {
			Token token = (Token) interpreter.pop();
			interpreter.execute(token);
			return true;
		});
		defList.add(new Definition(new Word("/EXECUTE"),compile,execute));
		
		/**
		 * Emit TOS to the output.
		 */
		Token emit = new NativeToken("emit",(interpreter) -> {
				try {
					interpreter.getParserContext().getOutput().emit(interpreter.pop().toString());
				} catch (IOException e) {
					throw new HairballException("Failed to write output",e);
				}
				return true;
			});
		defList.add(new Definition(new Word("/."),compile,emit));
		
		/**
		 * Get a word from the input stream and push it onto TOS.
		 */
		Token word = new NativeToken("word",(interpreter) -> {
				try {
					Word nword = interpreter.getParserContext().getWordStream().getNextWord();
					interpreter.push(nword);
				} catch (IOException e) {
					throw new HairballException("Word could not read a token from input",e);
				}
				return true;
			});
		defList.add(new Definition(new Word("/W"),compile,word));
		
		/**
		 * Given a word on the stack, convert it to a literal string on the stack.
		 */
		Token wordLiteral = new NativeToken("wordLiteral", (interpreter) -> {
			Word aword = (Word) interpreter.pop();
			String wlit = aword.getValue();
			interpreter.push(wlit);
			return true;
		});
		defList.add(new Definition(new Word("/W2L"),compile,wordLiteral));
		/**
		 * Given a string, make a word.
		 */
		Token makeWord = new NativeToken("makeWord", (interpreter) -> {
			String lit = (String) interpreter.pop();
			Word aword = new Word(lit);
			interpreter.push(aword);
			return true;
		});
		defList.add(new Definition(new Word("/L2W"),compile,makeWord));
		
		/**
		 * Parse a token from input and emit it.
		 */
		Token slashToken = InterpreterToken.makeToken("slashToken",word,wordLiteral);
		defList.add(new Definition(new Word("/TOKEN"),compile,slashToken));
		
		/**
		 * Close out a new dictionary entry
		 */
		Token define = new NativeToken("define",(interpreter) -> {
				interpreter.getParserContext().getDictionary().define();
				return true;
			});
		
		/**
		 * Put the parser in compiling mode.
		 */
		Token compiling = new NativeToken("compiling",(interpreter) -> { 
				interpreter.getParserContext().getParser().compile();
				return true;
			});
		defList.add(new Definition(new Word("/COMPILING"),compiling,compiling));
		
		/**
		 * Put the parser in interpreting mode.
		 */
		Token interpreting = new NativeToken("interpreting",(interpreter) -> {
				interpreter.getParserContext().getParser().interpret();
				return true;
			});
		defList.add(new Definition(new Word("/INTERPRETING"),interpreting,interpreting));
		
		/**
		 * Put depth of stack on TOS
		 */
		Token depth = new NativeToken("depth", (interpreter) -> {
			int sdepth = interpreter.getParameterStack().size();
			interpreter.push(sdepth);
			return true;
		});
		defList.add(new Definition(new Word("/DEPTH"),compile,depth));
		/**
		 * Drop the TOS.
		 */
		Token drop = Drop.INSTANCE;
		defList.add(new Definition(new Word("/DROP"),Compile.INSTANCE,Drop.INSTANCE));
		/**
		 * Duplicate the TOS		
		 */
		Token dup = new NativeToken("dup", (interpreter) -> {
			Object tos = interpreter.pop();
			interpreter.push(tos);
			interpreter.push(tos);
			return true;
		});
		defList.add(new Definition(new Word("/DUP"),compile,dup));
		/**
		 * Swap the TOS with with the next item on the stack.
		 */
		Token swap = new NativeToken("swap", (interpreter) -> {
			Object tos = interpreter.pop();
			Object t2 = interpreter.pop();
			interpreter.push(tos);
			interpreter.push(t2);
			return true;
		});
		defList.add(new Definition(new Word("/SWAP"),compile,swap));
		/**
		 * Rotate the top 3 items on the stack.
		 */
		Token rot = new NativeToken("rot", (interpreter) -> {
			Object tos = interpreter.pop();
			Object t2 = interpreter.pop();
			Object t3 = interpreter.pop();
			interpreter.push(t2);
			interpreter.push(tos);
			interpreter.push(t3);
			return true;
		});
		defList.add(new Definition(new Word("/ROT"),compile,rot));
		Token pick = new NativeToken("pick", (interpreter) -> {
			int place = (Integer) interpreter.pop();
			Stack<Object> stack = interpreter.getParameterStack();
			int index = stack.size() - place;
			Object item = stack.remove(index);
			stack.push(item);
			return true;
		});
		defList.add(new Definition(new Word("/PICK"),compile,pick));

		/**
		 * A do-nothing word, which can be used as a placeholder, or for testing
		 */
		Token noop = new NativeToken("noop",(interpreter) -> { return true; });
		defList.add(new Definition(new Word("//"), compile, noop));

		/**
		 * Put the dictionary in DOER mode where added tokens go to the compile time behavior of the
		 * new definition, and the parser is put into interpreting mode.
		 */
		Token doer = new NativeToken("doer",(interpreter) -> {
				ParserContext pc = interpreter.getParserContext();
				pc.getDictionary().doer();
				return true;
			});
		
		/**
		 * Put the dictionary in DOES mode where added tokens go to the runtime behavior of the
		 * new definition.
		 */
		Token does = new NativeToken("does",(interpreter) -> {
				ParserContext pc = interpreter.getParserContext();
				pc.getDictionary().does();
				return true;
			});
		Token deferrCompile = new NativeToken("deferrCompile",(interpreter) -> {
			interpreter.getParserContext().getDictionary().addToCompileTime(compile);
			return true;
		});
		Token doesCT = InterpreterToken.makeToken("does", does);
		defList.add(new Definition(new Word("/DOES"),compile,doesCT));
		
		Token doerRT = InterpreterToken.makeToken("doer", drop, doer);
		defList.add(new Definition(new Word("/DOER"),doerRT,noop));
		
		/**
		 * Start a new definition with the given name, and put the parser into
		 * compiling mode. We also call /DOER, since defining a runtime behavior
		 * is the default thing to do.
		 */
		Token create = new NativeToken("create",(interpreter) -> {
				interpreter.getParserContext().getDictionary()
					.create((Word)interpreter.pop());
				return true;
			});
		Token colon = InterpreterToken.makeToken("colon",word,create,compiling,does);
		defList.add(new Definition(new Word("/:"), compile, colon));
		
		/**
		 * Create an array of objects and leave it on the stack, the size of the
		 * array is taken from the stack. The array is initially empty.
		 */
		Token allot = new NativeToken("allot",(interpreter) -> {
			int length = (Integer) interpreter.pop();
			Object[] array = new Object[length];
			interpreter.push(array);
			return true;
		});
				
		/**
		 * Quote some text and push it onto the stack as a literal.
		 */
		Token quoteRT = Quote.INSTANCE;
		
		defList.add(new Definition(new Word("/\""),compile,quoteRT));
		Token makeLiteral = new NativeToken("makeLiteral",(interpreter) -> {
			var value = interpreter.pop();
			LiteralToken lt = new LiteralToken("makeLiteral",value);
			interpreter.push(lt);
			return true;
		});
		defList.add(new Definition(new Word("/MAKELITERAL"),compile,makeLiteral));
		Token brackMakeLiteralBrack = InterpreterToken.makeToken("[MAKELITERAL]",drop,makeLiteral);
		defList.add(new Definition(new Word("/[MAKELITERAL]"),brackMakeLiteralBrack,makeLiteral));
		
		Token convert = new NativeToken("convert",(interpreter) -> {
			String lit = (String) interpreter.pop();
			Object litObj = null;
			try {
				litObj = Integer.decode(lit);
			} catch (NumberFormatException e) {
			}
			if(litObj == null) {
				try {
					litObj = Double.parseDouble(lit);
				} catch(NumberFormatException e) {
				}
			}
			if(litObj == null) litObj = lit;
			interpreter.push(litObj);
			return true;
		});
		defList.add(new Definition(new Word("/ALLOT"),compile,allot));
		Token token = new NativeToken("token",(interpreter) -> {
			ParserLocation pl = new ParserLocation(interpreter.getParserContext().getWordStream());
			try {
				Word aword = interpreter.getParserContext().getWordStream().getNextWord();
				String lit = aword.getValue();
				interpreter.push(lit);
			} catch (IOException e) {
				throw new HairballException(pl.makeErrorMessage("Token can't parse token from input"),e);
			}
			return true;
		});
		Token constantRT = InterpreterToken.makeToken("constantRT",word,create,token,convert,makeLiteral,compileToken,define);
		/**
		 * Convert the next token to a number and leave it on TOS
		 */
		Token numRT = InterpreterToken.makeToken("numRT", token,convert);
		defList.add(new Definition(new Word("/NUM"),compile,numRT));
		/**
		 * A word which takes the next value off the input stream and defines a word with
		 * that name, then takes the next token from the stack. If that token can be
		 * converted to an integer it does so, if not it is converted to a double, and
		 * if that isn't possible it is treated as a string literal. This value is then
		 * compiled into the runtime behavior of the newly defined word, and ends the
		 * definition.
		 * 
		 * This allows the easy creation of simple constants.
		 */
		defList.add(new Definition(new Word("/CONSTANT"),compile,constantRT));
		Token variableToken = new NativeToken("variableToken", (interpreter) -> {
			VariableToken vtoken = new VariableToken("",null);
			interpreter.push(vtoken);
			return true;
		});
		Token variableRT = InterpreterToken.makeToken("variableRT", word, create, variableToken, compileToken, define);
		defList.add(new Definition(new Word("/VARIABLE"),compile,variableRT));
		Token lfetchRT = new NativeToken("lfetchRT",(interpreter) -> {
			LiteralToken vt = (LiteralToken) interpreter.pop();
			interpreter.push(vt.getData());
			return true;
		});
		defList.add(new Definition(new Word("/V@"),compile,lfetchRT));
		Token vstoreRT = new NativeToken("vstoreRT", (interpreter) -> {
			VariableToken vt = (VariableToken) interpreter.pop();
			Object data = interpreter.pop();
			vt.setData(data);
			return true;
		});
		defList.add(new Definition(new Word("/V!"),compile,vstoreRT));

		/**
		 * Throw an exception, taking a string from TOS and using it as the message.
		 */
		Token abort = new NativeToken("abort",(interpreter) -> {
				throw new HairballException((String)interpreter.pop());
			});
		defList.add(new Definition(new Word("/ABORT"),compile,abort));
		
		/**
		 * End a definition, immediate word which puts us back into interpreting mode and
		 * finalizes the current definition.
		 */
		Token colonSlashRTErrorMsg = new LiteralToken("colonSlashRTErrorMsg",":/ must be matched with /: or another defining word");
		Token colonSlashCT = InterpreterToken.makeToken("colonSlash_CT",define,interpreting,drop);
		Token colonSlashRuntime = InterpreterToken.makeToken("colonSlash",colonSlashRTErrorMsg,abort);
		defList.add(new Definition(new Word(":/"), colonSlashCT, colonSlashRuntime));

		/**
		 * Output some quoted text. In Compile mode it will parse out text to a matching "/ token
		 * and then compile it as a literal, along with emit. 
		 */
		Token dotQuoteCT = new NativeToken("dotQuote_CT",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				LiteralToken lt = new LiteralToken("dotQuote_Literal",quoted);
				interpreter.getParserContext().getDictionary().addToken(lt);
				interpreter.getParserContext().getDictionary().addToken(emit);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
			return true;
		});
		Token slashBracketQuote_CT = new NativeToken("slashBracketQuote_CT", (interpreter) -> {
			try {
				interpreter.pop();
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"]/");
				LiteralToken lt = new LiteralToken("slashBraketQuote_Literal",quoted);
				interpreter.getParserContext().getDictionary().addToken(lt);				
			} catch (IOException e) {
				throw new HairballException("/[\" failed to parse input",e);
			}
			return true;
		});
		Token sbq_RT_error = InterpreterToken.makeToken("sbq_RT_error", 
				new LiteralToken("errormsg","/[\" cannot be called at runtime"),abort);
		defList.add(new Definition(new Word("/[\""),slashBracketQuote_CT,sbq_RT_error));
		Token getToMatching = new NativeToken("getToMatching",(interpreter) -> {
			String target = (String) interpreter.pop();
			try {
				String parsed = interpreter.getParserContext().getWordStream().getToDelimiter(target);
				interpreter.push(parsed);
			} catch (IOException e) {
				throw new HairballException("Failed to find "+target+" in input",e);
			}
			return true;
		});
		defList.add(new Definition(new Word("/DELIMITED"),compile,getToMatching));
		/**
		 * Runtime behavior is to parse out text to matching "/ token and emit it
		 * immediately.
		 */
		Token dotQuoteRT = new NativeToken("dotQuote",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				interpreter.push(quoted);
				emit.execute(interpreter);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
			return true;
		});
		defList.add(new Definition(new Word("/.\""),dotQuoteCT,dotQuoteRT));
		
		/**
		 * This just generates an error message complaining about unbalanced quotes, because it is
		 * not really ever called as a word, but just used by quoting operators as a delimiter. So
		 * if it is encountered outside that context, something is unbalanced.
		 */
		Token quoteSlashErrorMsg = new LiteralToken("quoteSlashErrorMsg","\"/ must match with a quoting operator");
		Token quoteSlashRT = InterpreterToken.makeToken("quoteSlash",quoteSlashErrorMsg,abort);
		defList.add(new Definition(new Word("\"/"),quoteSlashRT,quoteSlashRT));
		
		Token uuid = new NativeToken("uuid", (interpreter) -> {
			String guid = UUID.randomUUID().toString();
			interpreter.push(guid);
			return true;
		});
		defList.add(new Definition(new Word("/UUID"),compile,uuid));

		/**
		 * Emit TOS to the output.
		 */
		defList.add(new Definition(new Word("/."),compile,emit));
		
		/**
		 * Produce a parameter stack dump.
		 */
		Token dotS = new NativeToken("dotS",(interpreter) -> {
				try {
					String adepth = Integer.valueOf(interpreter.getParameterStack().size()).toString();
					Output output = interpreter.getParserContext().getOutput();
					output.emit(adepth);
					output.space();
					Object[] stack = new Object[interpreter.getParameterStack().size()];
					interpreter.getParameterStack().copyInto(stack);
					for(Object obj : stack) {
						output.emit(obj == null ? "(null)" : obj.toString());
						output.emit("\n");
					}
				} catch (IOException e) {
					throw new HairballException(e);
				}
				return true;
			});
		defList.add(new Definition(new Word("/.S"),compile,dotS));
		
		Token quit = new NativeToken("quit",(interpreter)-> {
			return false;
			});
		defList.add(new Definition(new Word("/QUIT"),compile,quit));
		
		Token commentin = new NativeToken("commentin",(interpreter) -> {
				try {
					interpreter.getParserContext().getWordStream().getToMatching("*/");
				} catch (IOException e) {
					throw new HairballException("Word could not read a token from input",e);
				}
				return true;
			});
		defList.add(new Definition(new Word("/*"),commentin,commentin));
		Token commentout = new LiteralToken("commentout","*/ must match with a quoting operator");
		defList.add(new Definition(new Word("*/"),commentout,commentout));
		
		Token spaceToken = new NativeToken("space",(interpreter) -> {
				try {
					interpreter.getParserContext().getOutput().space();
				} catch (IOException e) {
					throw new HairballException("Failed to output text",e);
				}
				return true;
			});
		defList.add(new Definition(new Word("/SPACE"),compile,spaceToken));
		
		/*
		 * Source input from another file. This will construct a new parser 
		 * context and a new parser, and then
		 * launch the parser. Output should be interpolated seamlessly into
		 * the current output stream. 
		 */
/*		Token source = new NativeToken("source", (interpreter) -> {
			Vertx vertx = Vertx.vertx();
			FileSystem fileSystem = vertx.fileSystem();
			ParserContext cContext = interpreter.getParserContext();
			String fileName = null;
			try {
				String currentBucket = interpreter.getParserContext().getWordStream().getCurrentLocation();
				fileName = (String) interpreter.pop();
				IWordStream wordStream = new BucketWordStream(fileSystem,fileName,currentBucket);
				Parser nParser = new Parser();
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
		defList.add(new Definition(new Word("/SOURCE\""),compile,sourceQuote)); */

		/**
		 * Get the current Output and leave it on the stack.
		 */
		Token getOutput = new NativeToken("getOutput",(interpreter) -> {
			Output out = interpreter.getParserContext().getOutput();
			interpreter.push(out);
			return true;
		});
		defList.add(new Definition(new Word("/@OUTPUT"),compile,getOutput));
		/**
		 * Set the parser's output to the value on the stack.
		 */
		Token setOutput = new NativeToken("setOutput",(interpreter) -> {
			Output out = (Output) interpreter.pop();
			interpreter.getParserContext().setOutput(out);
			return true;
		});
		defList.add(new Definition(new Word("/OUTPUT"),compile,setOutput));
		/**
		 * Create an output to a file. The file name is on the stack.
		 */
/*		Token createOutput = new NativeToken("createOutput",(interpreter) -> {
			String currentBucket = interpreter.getParserContext().getWordStream().getCurrentLocation();
			String fileName = (String) interpreter.pop();
			String outFile = currentBucket + "/" + fileName;
			FsOutput output = new FsOutput(fileName);
			interpreter.push(output);
			return true;
		});
		defList.add(new Definition(new Word("/OPEN"),compile,createOutput)); */
		/**
		 * Close an Output which is on the stack.
		 */
		Token closeOutput = new NativeToken("closeOutput",(interpreter) -> {
			Output output = (Output) interpreter.pop();
			try {
				output.close();
			} catch (IOException e) {
				throw new HairballException("Cannot close output",e);
			}
			return true;
		});
		defList.add(new Definition(new Word("/CLOSE"),compile,closeOutput));
		/**
		 * Write a string on the stack to an Output which is on the stack
		 */
		Token writeOutput = new NativeToken("writeOutput", (interpreter) -> {
			Output output = (Output) interpreter.pop();
			String text = (String) interpreter.pop();
			try {
				output.emit(text);
			} catch (IOException e) {
				throw new HairballException("Failed to write to output",e);
			}
			return true;
		});
		defList.add(new Definition(new Word("/WRITE"),compile,writeOutput));
		
		/**
		 * Get a named vocabulary and put it on the top of the stack
		 */
		Token pushVocab = new NativeToken("pushVocab", (interpreter) -> {
			Word vocabName = (Word) interpreter.pop();
			IVocabulary vocabulary = interpreter.getParserContext().getDictionary().findVocabulary(vocabName.getValue());
			interpreter.push(vocabulary);
			return true;
		});
		/**
		 * Create a vocabulary with a given name.
		 */
		Token createVocab = new NativeToken("createVocab",(interpreter) -> {
			Word vocabName = (Word) interpreter.pop();
			IVocabulary vocabulary = interpreter.getParserContext().getDictionary().createVocabulary(vocabName.getValue());
//			interpreter.push(vocabulary);
			return true;
		});
		/**
		 * Given a vocabulary, add it to the dictionaries active vocabularies
		 */
		Token addVocabularyToStack = new NativeToken("addVocabularyToStack",(interpreter) -> {
			IVocabulary vocab = (IVocabulary) interpreter.pop();
			interpreter.getParserContext().getDictionary().add(vocab);
			return true;
		});
		defList.add(new Definition(new Word("/ACTIVE"),compile,addVocabularyToStack));
		/**
		 * Given a vocabulary the current target for definitions.
		 */
		Token makeVocabularyCurrent = new NativeToken("makeVocabularyCurrent",(interpreter) -> {
			IVocabulary vocab = (IVocabulary) interpreter.pop();
			interpreter.getParserContext().getDictionary().makeCurrent(vocab);
			return true;
		});
		defList.add(new Definition(new Word("/CURRENT"),compile,makeVocabularyCurrent));
		/**
		 * Create a new vocabulary by parsing input and adding it to the known vocabularies
		 */
		Token newVocabRT = InterpreterToken.makeToken("newVocabRT", word, createVocab);
		defList.add(new Definition(new Word("/NEWVOCABULARY"),compile,newVocabRT));
		/**
		 * The following word in the input stream is a vocabulary name, put the corresponding
		 * vocabulary on the stack.
		 */
		Token addVocabRT = InterpreterToken.makeToken("addVocabRT", word, pushVocab);
		defList.add(new Definition(new Word("/VOCABULARY"),compile,addVocabRT));

		Token vocabFetchRT = InterpreterToken.makeToken("/vocab@", makeWord,pushVocab);
		defList.add(new Definition(new Word("/VOCAB@"),compile,vocabFetchRT));
		
		Token inactivateVocabRT = new NativeToken("/INACTIVE", (interpreter) -> {
			IVocabulary vocab = (IVocabulary) interpreter.pop();
			interpreter.getParserContext().getDictionary().remove(vocab);
			return true;
		});
		defList.add(new Definition(new Word("/INACTIVE"),compile,inactivateVocabRT));
		
		Token fetchVocabs = new NativeToken("fetchVocabs", (interpreter) -> {
			String value = interpreter.getParserContext().getDictionary().getActiveVocabularies();
			interpreter.push(value);
			return true;
		});
		defList.add(new Definition(new Word("/VOCABULARIES"),compile,fetchVocabs));

		// Given a word on TOS, look it up in the Dictionary and put the definition on TOS
		Token lookup = new NativeToken("lookup", (interpreter) -> {
			Word aword = (Word) interpreter.pop();
			Definition def = interpreter.getParserContext().getDictionary().lookUp(aword);
			interpreter.push(def);
			return true;
		});
		// Given a definition on TOS get its runtime token
		Token getruntime = new NativeToken("getruntime", (interpreter) -> {
			Definition def = (Definition) interpreter.pop();
			Token rt = def.getRunTime();
			interpreter.push(rt);
			return true;
		});
		Token getToken = InterpreterToken.makeToken("getToken",word,lookup,getruntime);
		defList.add(new Definition(new Word("/'"),compile,getToken));
		
		Token now = new NativeToken("now",(interpreter) -> {
			long nowms = System.currentTimeMillis();
			interpreter.push(nowms);
			return true;
		});
		Token formatTimeFromFormatter = new NativeToken("formatTimeFromFormatter", (interpreter) -> {
			DateTimeFormatter dtf = (DateTimeFormatter) interpreter.pop();
			long time = (Long) interpreter.pop();
			ZonedDateTime zdt = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault());
			String dtstring = zdt.format(dtf);
			interpreter.push(dtstring);
			return true;
		});
		Token makeFormatter = new NativeToken("makeFormatter", (interpreter) -> {
			String tformat = (String) interpreter.pop();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(tformat);
			interpreter.push(dtf);
			return true;
		});
		Token tickDateFormat = new VariableToken("tickDateFormat",DateTimeFormatter.ISO_DATE_TIME);
		Token makeNow = InterpreterToken.makeToken("makeNow",now,tickDateFormat,lfetchRT,formatTimeFromFormatter);
		Token dotNow = InterpreterToken.makeToken("dotNow", makeNow,emit);
		/**
		 * Output a date time representing now. This will be formatted according to the
		 * DateTimeFormatter stored in the variable 'DateFormat
		 */
		defList.add(new Definition(new Word("/.NOW"),compile,dotNow));
		/**
		 * Put the current date/time on the stack as a millisecond epoch time
		 */
		defList.add(new Definition(new Word("/NOW"),compile,now));
		/**
		 * Make a display string for a given date time and leave on the stack
		 */
		defList.add(new Definition(new Word("/FORMATTIME"),compile,formatTimeFromFormatter));
		/**
		 * Make a DateTimeFormatter from a string.
		 */
		defList.add(new Definition(new Word("/MAKEFORMATTER"),compile,makeFormatter));
		defList.add(new Definition(new Word("/'DATEFORMAT"),compile,tickDateFormat));
		/**
		 * Default handling of a single newline, don't emit anything
		 */
		Token newLine = new LiteralToken("newline","\n");
		Token newLine_RT = InterpreterToken.makeToken("newLine_RT",newLine,emit);
		defList.add(new Definition(new Word("/NEWLINE"),compile,newLine_RT));
		
		Token version = new NativeToken("version", (interpreter) -> {
			interpreter.push(Hairball.VERSION);
			return true;
		});
		defList.add(new Definition(new Word("/VERSION"),compile,version));
		
		/* Create a Map<Object,Object> and put it on the stack */
		Token makeMap = new NativeToken("makemap", (interpreter) -> {
			var foo = new HashMap<Object,Object>();
			interpreter.push(foo);
			return true;
		});
		Token mapStore = new NativeToken("mapStore", (interpreter) -> {
			var map = (Map<Object,Object>) interpreter.pop();
			var key = (Object) interpreter.pop();
			var value = (Object) interpreter.pop();
			map.put(key, value);
			return true;
		});
		Token mapFetch = new NativeToken("mapFetch", (interpreter) -> {
			var map = (Map<Object,Object>) interpreter.pop();
			var key = (Object) interpreter.pop();
			var value = map.get(key);
			interpreter.push(value);
			return true;
		});
		defList.add(new Definition(new Word("/MAKEMAP"),compile,makeMap));
		defList.add(new Definition(new Word("/MAP@"),compile,mapFetch));
		defList.add(new Definition(new Word("/MAP!"),compile,mapStore));
		
		Token onePlus = new NativeToken("oneplus", (interpreter) -> {
			var value = (int) interpreter.pop();
			value = value + 1;
			interpreter.push(value);
			return true;
		});
		defList.add(new Definition(new Word("/1+"),compile,onePlus));
		
		Token setEmit = new NativeToken("setemit",(interpreter) -> {
			Parser parser = interpreter.getParserContext().getParser();
			Token newEmitter = (Token) interpreter.pop();
			parser.setEmit(newEmitter);
			return true;
		});
		defList.add(new Definition(new Word("/SETEMITTER"),compile,setEmit));
		
		Token isNull = new NativeToken("isNull", (interpreter)-> {
			Parser parser = interpreter.getParserContext().getParser();
			var value = interpreter.pop();
			interpreter.push(value == null);
			return true;
		});
		defList.add(new Definition(new Word("/ISNULL"),compile,isNull));

		/* given an IP value and a boolean on the stack, set the IP if the boolean is false,
		 * this is the runtime behavior for an IF. */
		Token branch = new NativeToken("branch", (interpreter) -> {
			int branchTarget = (Integer) interpreter.pop();
			boolean flag = (Boolean) interpreter.pop();
			if(!flag) interpreter.setIp(branchTarget);
			return true;
		});
		/* drops a dummy literal into the current definition, which will be replaced later
		 * by the branch point when we execute THEN. Leave its offset on the stack.
		 */
		Token if_compileTime = new NativeToken("if_ct", (interpreter) ->{
			Dictionary dictionary = interpreter.getParserContext().getDictionary();
			int thenTarget = dictionary.here();
			dictionary.addToken(new LiteralToken("dummy",0));
			interpreter.push(thenTarget);
			swap.execute(interpreter);
			compile.execute(interpreter);
			return true;
		});
		defList.add(new Definition(new Word("/IF"),if_compileTime,branch));
		
		Token then_compileTime = new NativeToken("then_compiletime",(interpreter) -> {
			Dictionary dictionary = interpreter.getParserContext().getDictionary();
			var foo = interpreter.pop();
			int thenTarget = (Integer) interpreter.pop();
			int thenOffset = dictionary.here();
			dictionary.putToken(new LiteralToken("thenOffset",thenOffset),thenTarget);
			dictionary.addToken(noop);
//			interpreter.push(foo);
			return true;
		});
		defList.add(new Definition(new Word("/THEN"),then_compileTime,noop));

		Token t = new LiteralToken("true",Boolean.TRUE);
		defList.add(new Definition(new Word("/TRUE"),compile,t));
		Token f = new LiteralToken("false",Boolean.FALSE);
		defList.add(new Definition(new Word("/FALSE"),compile,f));
		Token not = new NativeToken("not",(interpreter)-> {
			boolean value = (boolean) interpreter.pop();
			value = !value;
			interpreter.push(value);
			return true;
		});
		defList.add(new Definition(new Word("/NOT"),compile,not));
		
		Token trim = new NativeToken("trim", (interpreter) -> {
			String str = (String) interpreter.pop();
			str = str.strip();
			interpreter.push(str);
			return true;
		});
		defList.add(new Definition(new Word("/TRIM"),compile,trim));
	}
	
	
}
