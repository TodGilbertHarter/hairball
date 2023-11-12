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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * A dictionary is a vocabulary of vocabularies, which will be searched in inverse
 * order of addition, newest to oldest.
 * 
 * A dictionary has the concept of the 'active' vocabulary. It will accept new definitions
 * and add them to the active vocabulary. It also has a concept of a 'current' definition, which
 * is simply a compiling state which will be used to construct a new definition when it is finalized.
 * The new definition will be added to the active vocabulary.
 * 
 * Vocabularies can be added and removed from the dictionary, which always operates in a LIFO fashion.
 * When a new vocabulary is added, it goes to the top of the 'stack', where it is always searched first
 * for definitions. The top of the stack can be discarded, or a specific vocabulary and all newer ones
 * can be removed. 
 * 
 * Removed vocabularies are still available to be re-added later. The vocabulary management words will
 * perform this task.
 * 
 * @author tharter
 *
 */
public class Dictionary implements IVocabulary {
	private final String name;
	private final Stack<IVocabulary> vocabularies = new Stack<>();
	private EmptyDefinition currentDefinition = new EmptyDefinition();
	private Consumer<Token> doerDoes = this::addToRuntime;
	private boolean doer = false;
	private IVocabulary currentVocabulary;
	private Map<String,IVocabulary> vocabularyList = new HashMap<>();

	/**
	 * This is a structure for holding the contents of the current definition
	 * before it has been finalized and added to a vocabulary.
	 * 
	 * @author tharter
	 *
	 */
	private class EmptyDefinition {
		Word name;
		List<Token> compileTime = new LinkedList<>();
		List<Token> runTime = new LinkedList<>();
		
		/**
		 * Add a token to the compiletime behavior of the Definition.
		 * 
		 * @param newToken
		 */
		public void addCompileToken(Token newToken) {
			this.compileTime.add(newToken);
		}
		
		/**
		 * Add a token to the runtime behavior of the Definition.
		 * 
		 * @param newToken
		 */
		public void addRuntimeToken(Token newToken) {
			this.runTime.add(newToken);
		}
		
	}

	/**
	 * Create a new dictionary with the given name.
	 * 
	 * @param name
	 */
	public Dictionary(String name) {
		this.name = name;
		does();
	}

	/**
	 * Get a vocabulary from the list of known vocabularies given its name.
	 * 
	 * @param name vocabulary name
	 * @return vocabulary, or null if it doesn't exist
	 */
	public IVocabulary findVocabulary(String name) {
		return vocabularyList.get(name);
	}
	
	/**
	 * Add a vocabulary to this Dictionary's list of known vocabularies.
	 * 
	 * @param vocabulary
	 */
	private void addToVocabularyList(IVocabulary vocabulary) {
		vocabularyList.put(vocabulary.getName(),vocabulary);
	}

	/**
	 * Create a new Vocabulary and add it to the ones known to this Dictionary.
	 * 
	 * @param name a name for the new Vocabulary.
	 * 
	 * @return the new Vocabulary.
	 */
	public IVocabulary createVocabulary(String name) {
		IVocabulary nVocab = new Vocabulary(name);
		addToVocabularyList(nVocab);
		return nVocab;
	}
	
	/**
	 * Get the current definition, noting that this may or may
	 * not be a complete definition, and may or may not have already
	 * been added to the current vocabulary by a define.
	 * 
	 * @return
	 */
	public Definition getCurrentDefinition() {
		Token rtToken = null;
		if(currentDefinition.runTime.size() > 1) {
			List<Token> rtList = new ArrayList<>();
			rtList.addAll(currentDefinition.runTime);
			rtToken = new InterpreterToken(currentDefinition.name.getValue(),rtList);
		} else if(currentDefinition.runTime.size() == 1) {
			rtToken = currentDefinition.runTime.get(0);
		}
		
		Token ctToken = null;
		if(currentDefinition.compileTime.size() > 1) {
			List<Token> ctList = new ArrayList<>();
			ctList.addAll(currentDefinition.compileTime);
			ctToken = new InterpreterToken(currentDefinition.name.getValue()+"_CT",ctList);
		} else if(currentDefinition.compileTime.size() == 1) {
			ctToken = currentDefinition.compileTime.get(0);
		}
		
		Definition def = new Definition(
				currentDefinition.name,
				ctToken,
				rtToken
				);
		return def;
	}
	
	/**
	 * Set the dictionary compilation state to 'doer'. This will cause addToken to add tokens to the
	 * compile time behavior of the current definition.
	 */
	public void doer() {
		doerDoes = this::addToCompileTime;
		doer = true;
	}

	/**
	 * Set the dictionary compilation state to 'does'. This will cause addToken to add tokens to the
	 * runtime behavior of the current definition.
	 */
	public void does() {
		doerDoes = this::addToRuntime;
		doer = false;
	}

	/**
	 * Probe the dictionary compiling mode. If the mode is doer, return true, otherwise false.
	 * 
	 * @return true if doer, false if does
	 */
	public boolean isDoer() {
		return doer;
	}
	
	/**
	 * Insert a token into the current definition. The 'doer/does' state of the dictionary will determine
	 * which behavior is targeted.
	 * 
	 * @param token
	 */
	public void addToken(Token token) {
		doerDoes.accept(token);
	}
	
	/**
	 * Initiate the creation of a new definition. Note that only one definition can be
	 * in process at a time. Any existing current definition will be discarded. Call
	 * finalize first if you don't want this to happen.
	 * 
	 * @param name
	 */
	public void create(Word name) {
		currentDefinition.name = name;
	}

	/**
	 * Add a token to the compile time behavior, this is not normally called
	 * directly, but 'doer' and 'does' may need it.
	 * 
	 * @param token
	 */
	public void addToCompileTime(Token token) {
		currentDefinition.addCompileToken(token);
	}
	
	/**
	 * Add a token to the run time behavior, this is not normally called
	 * directly, but 'doer' and 'does' may need it.
	 * 
	 * @param token
	 */
	public void addToRuntime(Token token) {
		currentDefinition.addRuntimeToken(token);
	}
	
	/**
	 * Close out the current definition and add it to the active vocabulary.
	 * A reference to it is returned.
	 * 
	 * @return the new definition
	 */
	public Definition define() {
		if(currentDefinition.compileTime.size() == 0) {
			Token compile = new NativeToken("compile",(interpreter) -> {
				Definition ourDef = (Definition) interpreter.pop();
				Token rtoken = ourDef.getRunTime();
				interpreter.getParserContext().getDictionary().addToken(rtoken);
				return true;
			});
			currentDefinition.addCompileToken(compile);
		}
		Definition def = getCurrentDefinition();
		this.add(def);
		this.currentDefinition = new EmptyDefinition();
		return def;
	}
	
	/**
	 * Add a vocabulary to the dictionary search order. If there is no current
	 * vocabulary, then this will also become the current vocabulary.
	 * 
	 * @param vocabulary
	 */
	public void add(IVocabulary vocabulary) {
		addToVocabularyList(vocabulary); // make sure it is known to us
		vocabularies.add(0,vocabulary); // .push(vocabulary);
		if(currentVocabulary == null) makeCurrent(vocabulary);
	}
	
	/**
	 * Make the given vocabulary current, new definitions will be added to
	 * this vocabulary. Note that it need not be a vocabulary which is in the
	 * current dictionary search order.
	 * 
	 * @param vocabulary
	 * @return previous current vocabulary
	 */
	public IVocabulary makeCurrent(IVocabulary vocabulary) {
		addToVocabularyList(vocabulary); // make sure it is known to us
		IVocabulary old = this.currentVocabulary;
		this.currentVocabulary = vocabulary;
		return old;
	}

	/**
	 * Get the offset where the next token will be added to the current definition.
	 * This will always return the value for the runtime behavior of the definition.
	 * 
	 * @return current offset.
	 */
	public int here() {
		return this.currentDefinition.runTime.size();
	}
	
	/**
	 * Get the current vocabulary. It may be null if none is defined.
	 * 
	 * @return
	 */
	public IVocabulary getCurrent() {
		return this.currentVocabulary;
	}
	
	/**
	 * Remove the most recently added vocabulary from the dictionary search
	 * order. Note that this does not modify the current vocabulary.
	 * 
	 */
	public IVocabulary remove() {
		return vocabularies.remove(0); // .pop();
	}

	/**
	 * Pop the given vocabulary and all more recently added vocabularies from
	 * the dictionary search order. If the given vocabulary doesn't exist in
	 * the search order, do nothing.
	 * 
	 * @param vocabulary the vocabulary to remove
	 */
	public void remove(IVocabulary vocabulary) {
		if(vocabularies.contains(vocabulary))
			while(vocabularies.size() != 0) {
				IVocabulary popped = vocabularies.remove(0); //  .pop();
				if(popped.equals(vocabulary)) return;
			}
	}
	
	@Override
	public Definition lookUp(Word word) {
		for(IVocabulary vocabulary : vocabularies) {
			Definition def = vocabulary.lookUp(word);
			if(def != null) return def;
		}
		return null;
	}

	@Override
	public void add(Definition def) {
		currentVocabulary.add(def);
	}

	/**
	 * Replace a token at an arbitrary index in the runtime of the current
	 * definition.
	 * 
	 * @param token
	 * @param pc
	 */
	public void putToken(Token token, int pc) {
		currentDefinition.runTime.set(pc, token);
	}

	@Override
	public String toString() {
		return "Dictionary [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dictionary other = (Dictionary) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return
	 */
	public String getActiveVocabularies() {
		StringBuffer sb = new StringBuffer("Vocabulary Stack:\n");
		this.vocabularies.forEach(vocabulary -> sb.append(vocabulary.toString()+"\n"));
		sb.append("Current Vocabulary:\n");
		sb.append(this.currentVocabulary.toString());
		sb.append("\n");
		return sb.toString();
	}
	
	
}
