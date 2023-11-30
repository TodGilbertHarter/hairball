/**
 * 
 */
package com.giantelectronicbrain.catfood.initialization;

/**
 * Exception which indicates the initializer could not initialize an object.
 * 
 * @author tharter
 *
 */
public class InitializationException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InitializationException() {
	}

	/**
	 * @param message
	 */
	public InitializationException(String message) {
		super(message);
	}

}
