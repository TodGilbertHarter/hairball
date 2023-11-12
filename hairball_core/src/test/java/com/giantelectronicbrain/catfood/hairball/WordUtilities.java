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

import java.io.OutputStream;
import java.util.logging.Logger;

import com.giantelectronicbrain.catfood.IPlatform;

/**
 * This is general code which is going to be used in various places to test
 * Hairball words. Since a fair amount of setup has to happen for that, we will
 * do it here in this base class instead of scattering it all over the place.
 * 
 * @author tharter
 *
 */
public class WordUtilities {

	public static class TestPlatform implements IPlatform {

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
			return "N/A";
		}
		
	}
	/**
	 * Create a hairball with its input coming from the given string. This should
	 * be sufficient for many tests.
	 * 
	 * @param inputData
	 * @return
	 */
	public static Hairball setUp(String inputData, OutputStream out) {
		TestPlatform tp = new TestPlatform();
		Hairball.PLATFORM = tp;
		Output output = new StreamOutput(out);
		IWordStream input = new StringWordStream(inputData);
		return new Hairball(tp, input, output);
	}

}
