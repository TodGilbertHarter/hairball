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
package com.giantelectronicbrain.catfood.hairball.pdf;

import java.util.HashMap;
import java.util.Map;

import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.spec.FontSpec;

/**
 * Holds all the stuff we want to keep related to a given PDF while we are building
 * it.
 * 
 * @author tharter
 *
 */
public class PdfContext {
	private final PLPageSet pageSet;
	private final Map<String,FontSpec> fonts = new HashMap<>();
	
	public PdfContext(final PLPageSet pageSet) {
		this.pageSet = pageSet;
	}
	
	public PLPageSet getPageSet() {
		return this.pageSet;
	}

	/**
	 * Save a copy of a FontSpec in the context for later use.
	 * 
	 * @param fontName name of the font.
	 * @param fs spec to save.
	 */
	public void saveFont(String fontName, FontSpec fs) {
		fonts.put(fontName, fs);
	}
	
	/**
	 * Get a named font back from the context.
	 * 
	 * @param fontName name the font was saved under.
	 * @return FontSpec
	 */
	public FontSpec getFont(String fontName) {
		return fonts.get(fontName);
	}
	
}
