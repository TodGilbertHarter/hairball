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
package com.giantelectronicbrain.catfood.hairball.pdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.giantelectronicbrain.catfood.hairball.Definition;
import com.giantelectronicbrain.catfood.hairball.HairballException;
import com.giantelectronicbrain.catfood.hairball.IVocabulary;
import com.giantelectronicbrain.catfood.hairball.InterpreterToken;
import com.giantelectronicbrain.catfood.hairball.LiteralToken;
import com.giantelectronicbrain.catfood.hairball.NativeToken;
import com.giantelectronicbrain.catfood.hairball.ParserLocation;
import com.giantelectronicbrain.catfood.hairball.Token;
import com.giantelectronicbrain.catfood.hairball.Vocabulary;
import com.giantelectronicbrain.catfood.hairball.Word;
import com.giantelectronicbrain.catfood.hairball.tokens.Compile;
import com.giantelectronicbrain.catfood.hairball.tokens.Quote;
import com.helger.pdflayout4.PDFCreationException;
import com.helger.pdflayout4.PageLayoutPDF;
import com.helger.pdflayout4.base.PLPageSet;
import com.helger.pdflayout4.element.text.PLText;
import com.helger.pdflayout4.spec.FontSpec;
import com.helger.pdflayout4.spec.PreloadFont;

/**
 * Core vocabulary set for PDF generation. This is enough logic to allow us to
 * write most of the syntax in Hairball. The idea is to be able to support all
 * the main functionality available in other outputs such as HTML or MD.
 * 
 * @author tharter
 *
 */
public class PdfVocabulary {
	
	/**
	 * Static factory to create the one and only needed instance of this class.
	 * This is how PdfVocabulary should always be created, there is no need
	 * for more than one.
	 * 
	 * @return
	 */
	public static IVocabulary create() {
		IVocabulary hbVocab = new Vocabulary("PDF");
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
		
		Token font = new NativeToken("font",(interpreter) -> {
			PreloadFont plf = (PreloadFont) interpreter.pop();
			int fontSize = (int) interpreter.pop();
			FontSpec fs = new FontSpec(plf, fontSize);
			interpreter.push(fs);
			return true;
		});
		defList.add(new Definition(new Word("/LOADFONT"),Compile.INSTANCE,font));
		
		Token saveSpec = new NativeToken("saveSpec",(interpreter) -> {
			FontSpec fs = (FontSpec) interpreter.pop();
			PdfContext ctx = (PdfContext) interpreter.pop();
			String fontName = (String) interpreter.pop();
			ctx.saveFont(fontName,fs);
			return true;
		});
		defList.add(new Definition(new Word("/FONT!"),Compile.INSTANCE,saveSpec));

		Token text = new NativeToken("text",(interpreter) -> {
			String str = (String) interpreter.pop();
			String fontName = (String) interpreter.pop();
			PdfContext ctx = (PdfContext) interpreter.pop();
			FontSpec fs = ctx.getFont(fontName);
			ctx.getPageSet().addElement(new PLText(str,fs));
			return true;
		});
		defList.add(new Definition(new Word("/TEXT"),Compile.INSTANCE,text));
		
		defList.add(new Definition(new Word("/REGULAR_FONT"),
				Compile.INSTANCE,
				new LiteralToken("RegularFont",PreloadFont.REGULAR)));
		defList.add(new Definition(new Word("/REGULAR_BOLD_FONT"),
				Compile.INSTANCE,
				new LiteralToken("RegularBoldFont",PreloadFont.REGULAR_BOLD)));
		defList.add(new Definition(new Word("/REGULAR_ITALIC_FONT"),
				Compile.INSTANCE,
				new LiteralToken("RegularItalicFont",PreloadFont.REGULAR_ITALIC)));
		
		Token document = new NativeToken("Document", (interpreter) -> {
			PLPageSet pset = new PLPageSet(PDRectangle.LETTER);
			PdfContext ctx = new PdfContext(pset);
			interpreter.push(ctx);
			return true;
		});
		defList.add(new Definition(new Word("/DOCUMENT"),Compile.INSTANCE,document));
		
		Token documentQuote = InterpreterToken.makeToken("documentQuote", Quote.INSTANCE,document);
		defList.add(new Definition(new Word("/DOCUMENT\""),Compile.INSTANCE,documentQuote));

		Token closeDocument = new NativeToken("closeDocument",(interpreter) -> {
			ParserLocation pl = new ParserLocation(interpreter.getParserContext().getWordStream());
			PdfContext ctx = (PdfContext) interpreter.pop();
			PLPageSet doc = ctx.getPageSet();
			try {
				final PageLayoutPDF pageLayout = new PageLayoutPDF();
				pageLayout.addPageSet(doc);
				pageLayout.renderTo(interpreter.getParserContext().getOutput().getOutputStream());
			} catch (UnsupportedOperationException e) {
				throw new HairballException(pl.makeErrorMessage("Cannot close PDF Document"),e);
			} catch (PDFCreationException e) {
				throw new HairballException(pl.makeErrorMessage("Cannot close PDF Document"),e);
			}
			return true;
		});
		defList.add(new Definition(new Word("DOCUMENT/"),Compile.INSTANCE,closeDocument));
	}
}
