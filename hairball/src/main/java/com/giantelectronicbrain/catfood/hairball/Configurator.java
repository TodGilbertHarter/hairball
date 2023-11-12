/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.giantelectronicbrain.catfood.conf.ConfigurationException;

import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;

/**
 * Perform configuration of Hairball. By calling Configurator.create(String[] args)
 * the command line represented by args will be parsed, if a properties file is
 * specified, it will be read too. Otherwise the default package properties file
 * will be read. The options from the command line will be used to override any
 * set in the configuration file.
 * 
 * @author tharter
 *
 */
public class Configurator {

	private static Option configOption = new Option().setLongName("config")
			.setShortName("c").setDescription("use the given configuration file");
	private static Option dumpOption = new Option().setLongName("dump")
			.setShortName("d").setFlag(true).setDescription("dump configuration to standard out");
	private static Option writeOption = new Option().setLongName("write")
			.setShortName("w").setFlag(true).setDescription("write final configuration to file");
	private static Option baseOption = new Option().setLongName("base")
			.setShortName("b").setDescription("directory all command line arguments are relative to");
	private static Option loopOption = new Option().setLongName("loop")
			.setShortName("l").setDescription("loop through the input N times, print performance stats");
	private static Option outputOption = new Option().setLongName("output")
			.setShortName("s").setDescription("direct output to a named file");
	private static Option helpOption = new Option().setLongName("help")
			.setShortName("h").setFlag(true).setHelp(true);
	private static Argument scriptFiles = new Argument()
			.setArgName("hairball script")
			.setMultiValued(true)
			.setDescription("hairball script to run")
			.setRequired(false)
			.setIndex(0);
	/**
	 * Create a set of properties derived from a Properties file and command line parameters.
	 * The command line elements will be merged with a default configuration derived either from
	 * a default properties file 'hairball.properties' read from the classpath or configuration read
	 * from a properties file pointed to by the --config=configfile directive.
	 * 
	 * Note that hairball does not require a properties file, and none is normally packaged with the
	 * application, but it can be provided if desired.
	 * 
	 * @return Properties object containing all the default and overridden property values
	 * @throws ConfigurationException on failure to parse the command line or read a properties file
	 */
	public static Object[] createConfiguration(List<String> arguments) throws ConfigurationException {
		CLI cli = createCLI();
		
		Properties config;
		CommandLine commandLine = cli.parse(arguments);
		if(commandLine.isAskingForHelp()) {
			StringBuilder builder = new StringBuilder();
			cli.usage(builder);
			System.out.println(builder.toString());
			return null;
		} else if(commandLine.isValid()) {
			if(commandLine.isOptionAssigned(configOption)) {
				String configFileName = commandLine.getOptionValue("c");
				File configFile = new File(configFileName);
				
				try {
					InputStream cfis = new FileInputStream(configFile);
					config = new Properties();
					config.load(cfis);
				} catch (IOException e) {
					throw new ConfigurationException(e.getLocalizedMessage(),"you supplied an invalid configuration file name "+configFileName);
				}
			} else {
				config = new Properties();
				InputStream ris = Configurator.class.getClassLoader().getResourceAsStream("hairball.properties");
				try {
					if(ris != null) config.load(ris);
				} catch (IOException e) {
					throw new ConfigurationException(e.getLocalizedMessage(),"failed to read default hairball properties");
				}
			}
		} else {
			throw new ConfigurationException("invalid command line",cli.getSummary());
		}
		
		Properties finalConfig = createConfiguration(config,commandLine);
		if(commandLine.isSeenInCommandLine(dumpOption)) {
			dumpConfiguration(finalConfig);
		}
		List<String> args = commandLine.allArguments();
		
		return new Object[] { finalConfig, args };
	}
	
	/**
	 * Dump a copy of the configuration properties given to the standard output.
	 * 
	 * @param config Properties to dump
	 */
	private static void dumpConfiguration(Properties config) {
		config.keySet().stream().sorted().forEachOrdered((key) -> {
			System.out.println(key+"="+config.getProperty((String) key));
		});
	}

	/**
	 * Create a Properties object containing options which need to be set due to command line flags.
	 * 
	 * @param config the Properties to be added to
	 * @param commandLine the CommandLine object to scan
	 * @return Properties containing any extra options set due to command line flags
	 */
	private static Properties createConfiguration(Properties config, CommandLine commandLine) {
		if(commandLine.isSeenInCommandLine(writeOption))
			config.setProperty("write", Boolean.TRUE.toString());
		if(commandLine.isOptionAssigned(baseOption)) {
			config.setProperty("base", commandLine.getOptionValue("b"));
		}
		if(commandLine.isOptionAssigned(dumpOption)) {
			config.setProperty("dump", "true");
		}
		if(commandLine.isOptionAssigned(loopOption)) {
			config.setProperty("loopOption", commandLine.getRawValueForOption(loopOption));
		}
		return config;
	}

	/**
	 * Create a CLI object to parse the Hairball command line.
	 * 
	 * @return a CLI object which understands our options
	 */
	private static CLI createCLI() {
		CLI cli = CLI.create("commandline");
		cli.addOption(configOption);
		cli.addOption(dumpOption);
		cli.addOption(writeOption);
		cli.addOption(baseOption);
		cli.addOption(loopOption);
		cli.addOption(outputOption);
		cli.addOption(helpOption);
		cli.addArgument(scriptFiles);
		//TODO: add options here. Might also need to add usage/help/name, not sure how that works...
		return cli;
	}
}
