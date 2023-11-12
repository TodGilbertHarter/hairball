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
/**
 * This is the Hairball mini-language. Its purpose is to allow programmable templates
 * to be written in CatFood. It can also be used from the command-line as a stand-alone
 * language for template processing purposes.
 * 
 * Hairball needs to function on the client side of a web application. So it is written in
 * such a way that the GWT (or other) transpilers can easily translate the code to Javascript.
 * This means you can run the same codebase both on the server side and on the client side.
 * Note that there are some bits of Hairball which aren't relevant for, and don't translate,
 * to client side, such as Console I/O, file I/O, etc. These are marked with the GwtIncompatible
 * annotation, which most transpilers respect. It should also be possible to write support for
 * certain client-side features which are not relevant to the server side as well, such as
 * client side storage, etc. 
 * 
 * The core Hairball vocabulary is implemented in Java, which makes bootstrapping easier (you can
 * pretty much do whatever you need without loading another vocabulary written in Hairball). However
 * the actual template language vocabularies, such as the HTML vocabulary, are written in Hairball
 * and need to be loaded. There are a few strategies for that, but they are TBD...
 * 
 * @author tharter
 *
 */
package com.giantelectronicbrain.catfood.hairball;