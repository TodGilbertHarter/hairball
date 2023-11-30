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

package com.giantelectronicbrain.catfood.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;

/**
 * Perform configuration of CatFood.
 * 
 * @author tharter
 *
 */
public class Configurator {

	private static Option configOption = new Option().setLongName("config").setShortName("c");
	private static Option dumpOption = new Option().setLongName("dump").setShortName("d").setFlag(true);
	private static Option writeOption = new Option().setLongName("write").setShortName("w").setFlag(true);
	
	/**
	 * Create a set of properties derived from a Properties file and command line parameters.
	 * The command line elements will be merged with a default configuration derived either from
	 * a default properties file 'catfood.properties' read from the classpath or configuration read
	 * from a properties file pointed to by the --config=configfile directive. 
	 * 
	 * @return Properties file containing all the default and overridden property values
	 * @throws ConfigurationException on failure to parse the command line or read a properties file
	 */
	public static Properties createConfiguration(List<String> arguments) throws ConfigurationException {
		CLI cli = createCLI();
		
		CommandLine commandLine = cli.parse(arguments);

		Properties config;
		if(commandLine.isValid()) {
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
				InputStream ris = Configurator.class.getClassLoader().getResourceAsStream("catfood.properties");
				if(ris == null)
					throw new ConfigurationException("no catfood.properties in classpath","failed to read default catfood properties");
				try {
					config.load(ris);
				} catch (IOException e) {
					throw new ConfigurationException(e.getLocalizedMessage(),"failed to read default catfood properties");
				}
			}
		} else {
			throw new ConfigurationException("invalid command line",cli.getSummary());
		}
		
		Properties finalConfig = createConfiguration(config,commandLine);
		if(commandLine.isSeenInCommandLine(dumpOption)) {
			dumpConfiguration(finalConfig);
		}
		
		return finalConfig;
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
		//TODO: query each command line option and override properties where provided.
		return config;
	}

	/**
	 * Create a CLI object to parse the CatFood command line.
	 * 
	 * @return a CLI object which understands our options
	 */
	private static CLI createCLI() {
		CLI cli = CLI.create("commandline");
		cli.addOption(configOption);
		cli.addOption(dumpOption);
		cli.addOption(writeOption);
		//TODO: add options here. Might also need to add usage/help/name, not sure how that works...
		return cli;
	}
}
