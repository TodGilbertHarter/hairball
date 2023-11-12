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

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hairball inner interpreter. This is the virtual machine which runs hairball 'code'.
 * It can execute lists of tokens, each of which is either a pointer to another list
 * of tokens, or a lambda.
 * 
 * In addition two stacks are implemented. The return stack is used to store contexts
 * when one context jumps to another. It can be used to store application data as a
 * temporary measure within a context, but any imbalance at the end of the context will
 * have unpredictable effects.
 * 
 * The other stack is a parameter stack. The interpreter doesn't use this itself and
 * thus it can be used to pass data between hairball words. We just clear it at start
 * and provide push and pop instructions.
 * 
 * To run a hairball program, instantiate an interpreter instance, acquire a Token
 * or tokens from somewhere, wrap them in a Context, and call start(Context). Tokens
 * in the context can call jumpToContext to 'branch' to another context, which can 
 * call returnFromContext in order to return to its calling context.
 * 
 * the branchToContext function can be used in case you have no interest in returning
 * to the previous context. 
 * 
 * A program simply consists of a series of contexts containing tokens which make
 * calls to lower level tokens, which eventually execute java lambdas to do actual
 * work. 
 * 
 * @author tharter
 *
 */
public class Interpreter {
	private static Logger log;

	private final Stack<Object> parameterStack;
	private final Stack<Object> returnStack;
	private Context currentContext;
	private ParserContext parserContext;

	/**
	 * Create a new hairball interpreter. Initially there will be
	 * no context, call start(initialContext) in order to run a
	 * hairball program.
	 */
	public Interpreter() {
		log = Hairball.PLATFORM.getLogger(Interpreter.class.getName());
		this.parameterStack = new Stack<>();
		this.returnStack = new Stack<>();
	}
	
	/**
	 * Set a parser context. Strictly speaking you can run code without this, but
	 * a lot of words will want it!
	 * 
	 * @param parserContext
	 */
	public void setParserContext(ParserContext parserContext) {
		this.parserContext = parserContext;
	}
	
	/**
	 * Native tokens can get to the outer interpreter's context this way.
	 * 
	 * @return the parser context
	 */
	public ParserContext getParserContext() {
		return this.parserContext;
	}
	
	/**
	 * Push an item to the parameter stack.
	 * 
	 * @param item the item to push
	 */
	public void push(Object item) {
		parameterStack.push(item);
	}

	/**
	 * Return the parameter stack depth.
	 * 
	 * @return number of items on the parameter stack
	 */
	public int depth() {
		return parameterStack.size();
	}

	/**
	 * Return the return stack depth.
	 * 
	 * @return number of items on the return stack
	 */
	public int rDepth() {
		return returnStack.size();
	}
	
	/**
	 * Remove the top item from the parameter stack.
	 * 
	 * @return TOS of parameter stack
	 */
	public Object pop() {
		return parameterStack.pop();
	}
	
	/**
	 * Get a copy of the TOS. Useful for native tokens to save a bit of
	 * stack wizardry.
	 * 
	 * @return TOS of parameter stack
	 */
	public Object peek() {
		return parameterStack.peek();
	}

	/**
	 * Remove the top item from the return stack.
	 * 
	 * @return TOD of the return stack
	 */
	public Object rPop() {
		return returnStack.pop();
	}

	/**
	 * Push an item onto the return stack.
	 * 
	 * @param item item to be pushed
	 */
	public void rPush(Object item) {
		returnStack.push(item);
	}

	/**
	 * Push the current context to the return stack and
	 * replace it with the given context. If hairball is
	 * running, it will continue at the ip of the new
	 * context. A later call to returnToContext will 
	 * return to the old context.
	 * 
	 * @param newContext the new context to jump to
	 */
	public void jumpToContext(Context newContext) {
		log.log(Level.FINEST, "Entering jumpToContext");
		rPush(currentContext);
		currentContext = newContext;
	}

	/**
	 * Replaces the current context with the new context.
	 * Hairball execution will continue with the new context.
	 * Note that the old context is not pushed to the return
	 * stack, there is no going back.
	 * 
	 * @param newContext a hairball execution context
	 */
	public void branchToContext(Context newContext) {
		log.log(Level.FINEST, "branchToContext");
		currentContext = newContext;
	}

	/**
	 * Set the instruction pointer of the current context to a
	 * given value. returns the old value.
	 * Throws IllegalArgumentException if the new value is illegal.
	 * 
	 * @param newIpValue a new ip value
	 * @return old ip value
	 */
	public int setIp(int newIpValue) {
		return currentContext.setIp(newIpValue);
	}

	/**
	 * gets the ip value in the current context.
	 * 
	 * @return value
	 */
	public int getIp() {
		return currentContext.getIp();
	}

	/**
	 * Gets the current context. It is probably not a good idea
	 * to modify this context, use the methods provided here to
	 * make changes.
	 * 
	 * @return the current context
	 */
	public Context currentContext() {
		return currentContext;
	}
	
	/**
	 * Restores a context from the top of the return stack.
	 * Hairball execution will continue at the instruction
	 * pointer of the restored context if a program is running.
	 * 
	 * @return the previous context
	 */
	public Context returnFromContext() {
		log.log(Level.FINEST, "Entering returnFromContext");
		Context previous = currentContext;
		currentContext = (Context) rPop();
		return previous;
	}
	
	/**
	 * Executes the next token. The value of the token is
	 * returned. If the current context is exhausted, then
	 * null is returned.
	 * 
	 * @return The executed token, or null if the current
	 * context is exhausted, or the token returns false.
	 * @throws HairballException 
	 */
	private Token executeNextToken() throws HairballException {
		log.log(Level.FINEST, "Entering executeNextToken");
		
		if(currentContext == null) return null; // detect end of program
		Token nextToken = currentContext.getNextToken();
		boolean tokenReturnValue = false;
		if(nextToken != null)
			tokenReturnValue = nextToken.execute(this);
		return tokenReturnValue ? nextToken : null;
	}

	/**
	 * Execute the current context. All tokens in the current
	 * context are executed starting at the current instruction
	 * pointer.
	 * 
	 * @return returns the context, mostly for debug purposes
	 * @throws HairballException 
	 */
	public Context executeContext() throws HairballException {
		log.log(Level.FINEST, "Entering executeContext");

		while(executeNextToken() != null) { }
		return currentContext;
//		currentContext = (Context) rPop();
	}
	
	/**
	 * Start this interpreter. This will completely wipe the
	 * interpreter's current state and then establish the
	 * initial context, and then execute it.
	 * 
	 * @param initialContext the initial context.
	 * @return returns the context, which is useful for debug
	 * @throws HairballException 
	 */
	public Context start(Context initialContext) throws HairballException {
		log.log(Level.FINEST, "Entering start");
		
		returnStack.clear();
		parameterStack.clear();
		this.currentContext = null;
		jumpToContext(initialContext); // we jump to balance the stack 
		return executeContext(); // since this line will do an rPop()
	}
	
	/**
	 * This is basically a convenience method which lets us run a bare token. The
	 * parser invokes this to get things started.
	 * 
	 * @param token
	 * @throws HairballException 
	 */
	public boolean execute(Token token) throws HairballException {
		return token.execute(this);
	}

	/**
	 * Get the parameter stack. Note that this is the live stack.
	 * 
	 * @return the parameter stack
	 */
	public Stack getParameterStack() {
		return this.parameterStack;
	}

	/**
	 * Get the return stack, this is the live stack, it isn't safe to monkey
	 * with its contents. 
	 * 
	 * @return the return stack
	 */
	public Stack getReturnStack() {
		return this.returnStack;
	}
}
