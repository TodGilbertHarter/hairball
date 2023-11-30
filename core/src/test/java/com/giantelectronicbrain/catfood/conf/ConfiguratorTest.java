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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

/**
 * Tests of the CatFood Configurator.
 * 
 * @author tharter
 *
 */
public class ConfiguratorTest {

	/**
	 * Test method for {@link com.giantelectronicbrain.catfood.conf.Configurator#createConfiguration(java.util.List)}.
	 */
	@Test
	public void testCreateConfigurationMissingConfigfile() {
		List<String> params = new ArrayList<String>();
		params.add("--config=meow.properties");
		try {
			Configurator.createConfiguration(params);
		} catch (ConfigurationException e) {
			assertEquals("didn't get correct error on missing config file","meow.properties (No such file or directory)",e.getLocalizedMessage());
		}
	}

	@Test
	public void testCreateConfigurationDefaultConfig() {
		List<String> params = new ArrayList<String>();
		try {
			Properties props = Configurator.createConfiguration(params);
			String value = (String) props.get("foo");
			assertEquals("Value from default config should be available","bar",value);
		} catch (ConfigurationException e) {
			fail("unexpected error thrown reading default config "+e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testCreateConfigurationCustomConfigfile() {
		List<String> params = new ArrayList<String>();
		params.add("--config=src/test/resources/custom.properties");
		try {
			Properties props = Configurator.createConfiguration(params);
			String value = (String) props.get("baz");
			assertEquals("Value from default config should be available","blorb",value);
		} catch (ConfigurationException e) {
			fail("unexpected error thrown reading default config "+e.getLocalizedMessage());
		}
	}

}
