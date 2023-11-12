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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.IPlatform;
import com.giantelectronicbrain.catfood.conf.ConfigurationException;

import io.vertx.core.Vertx;

/**
 * Hairball mainline. This gives us a stand-alone hairball parser/REPL and a class which can be instantiate to provide
 * a complete hairball 'engine'. It will accept various arguments which allow us to redirect output, read multiple
 * Hairball scripts from a set of files, dump the configuration, and run the job multiple times and
 * collect performance statistics in order to do some basic benchmarking.
 * 
 * @author tharter
 *
 */
public class StandAloneHairball {
	public static ServerPlatform PLATFORM = new ServerPlatform();
	private final Dictionary rootDictionary = new Dictionary("root");
	private final Parser parser;
	private final Interpreter interpreter;
	public static String VERSION = null; // Hairball version string, get it here

	public static void main(String[] args) throws IOException, HairballException, ConfigurationException {
		Vertx vertx = Vertx.vertx();
		int statusCode = 0;
		try {
			Object[] conf = Configurator.createConfiguration(Arrays.asList(args));
			if(conf == null) return;
			Properties configuration = (Properties) conf[0];
			List<String> argList = (List<String>) conf[1];
			VERSION = getVersion();
			String loopOption = configuration.getProperty("loopOption");
			int loopCount = loopOption == null ? 1 : Integer.parseInt(loopOption);
			long startingTime = System.currentTimeMillis(); 
			for(int i = 0; i < loopCount; i++) {
				IWordStream wordStream = makeWordStream(vertx, argList, configuration);
				Output output = makeOutput(configuration);
				StandAloneHairball hairball = new StandAloneHairball(wordStream,output);
				hairball.execute();
			}
			long endingTime = System.currentTimeMillis();
			if(loopCount > 1) printElapsed(startingTime,endingTime);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
			statusCode = -1;
		} finally {
			if(vertx != null) vertx.close();
		}
		if(statusCode != 0)
			System.exit(statusCode);
	}
	
	/**
	 * Gets the implementation version from the package. This should be the version
	 * of Hairball which is running, although it may be wrong if you are not running
	 * a packaged version of the application, I'm not sure...
	 * 
	 * @return Hairball version string
	 */
	private static String getVersion() {
		Package aPackage = StandAloneHairball.class.getPackage();
		return aPackage.getImplementationVersion();
	}

	/**
	 * Output the elapsed execution time, including setup of inputs and outputs
	 * plus the run of the parser.
	 * 
	 * @param startingTime
	 * @param endingTime
	 */
	private static void printElapsed(long startingTime, long endingTime) {
		long totalTime = endingTime - startingTime;
		long millisOver = totalTime % 1000;
		long seconds = totalTime > 0 ? totalTime / 1000 : 0;
		long secondsOver = seconds > 0 ? seconds % 60 : 0;
		long minutes = seconds > 0 ? seconds / 60 : 0;
		long minutesOver = minutes > 0 ? minutes % 60 : 0;
		long hours = minutes > 0 ? minutes / 60 : 0;
		String result = String.format("\nElapsed time was: %02d:%02d:%02d:%03d\n", hours, minutesOver, secondsOver, millisOver);
		System.out.println(result);
	}

	static class ServerPlatform implements IPlatform {

		@Override
		public boolean isClient() {
			return false;
		}

		@Override
		public Logger getLogger(String name) {
			return Logger.getLogger(name);
		}

		@Override
		public String getVersion() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * Create a console output. This ignores args.
	 * 
	 * @param args
	 * @return
	 */
	private static Output makeOutput(Properties properties) {
		//TODO: support directing output to other places besides STDOUT
		return new ConsoleOutput();
	}

	/**
	 * Create a WordStream which uses the current working directory and a list
	 * of arguments. If the argument list is empty, it will be a ConsoleWordStream,
	 * else a FileCollectionWordStream.
	 * 
	 * @param args
	 * @return
	 */
	private static IWordStream makeWordStream(Vertx vertx, List<String> args, Properties properties) {
		if(args == null || args.size() == 0)
			return new ConsoleWordStream("\n>");
		else {
			String cwd = (String) properties.get("base");
			if(cwd == null) cwd = ".";
			List<String> copyOfArgs = new ArrayList<>(args);
			return new FileCollectionWordStream(vertx,cwd,copyOfArgs);
		}
	}

	/**
	 * Create a Hairball instance with the given input and output sources.
	 * 
	 * @param wordStream IWordStream where input comes from
	 * @param output Output where we send output.
	 */
	public StandAloneHairball(IWordStream wordStream, Output output) {
		this();
		setIO(wordStream, output);
	}

	/**
	 * Create a Hairball instance. This doesn't have any associated output stream or
	 * input IWordStream. Those will have to be supplied by a call to setIO.
	 */
	public StandAloneHairball() {
		IVocabulary hbVocab = ExtendHairballVocabulary.create();
		rootDictionary.add(hbVocab);
		interpreter = new Interpreter();
		parser = new Parser();
	}
	
	/**
	 * Set the input and output of this Hairball instance.
	 * 
	 * @param wordStream
	 * @param output
	 */
	public void setIO(IWordStream wordStream,Output output) {
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	/**
	 * Set the input for this instance, the output is left unchanged.
	 * 
	 * @param wordStream
	 */
	public void setInput(IWordStream wordStream) {
		Output output = this.parser.getContext().getOutput();
		ParserContext pcontext = new ParserContext(wordStream,rootDictionary,interpreter,output,parser);
		interpreter.setParserContext(pcontext);
		parser.setParserContext(pcontext);
	}
	
	/**
	 * Run the Hairball engine, processing the input until eof and generating
	 * output, etc. This is the main entry point for actually running a Hairball
	 * program.
	 * 
	 * @return the ParserContext
	 * @throws IOException
	 * @throws HairballException 
	 */
	public ParserContext execute() throws IOException, HairballException {
		parser.interpret();
		ParserContext pctx = parser.parse();
		parser.close();
		return pctx;
	}

	/**
	 * Get the whole parameter stack. This is mainly useful for testing.
	 */
	public Stack getParamStack() {
		return interpreter.getParameterStack();
	}

	/**
	 * Get the whole return stack. This is mainly useful for testing.
	 */
	public Stack getReturnStack() {
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
