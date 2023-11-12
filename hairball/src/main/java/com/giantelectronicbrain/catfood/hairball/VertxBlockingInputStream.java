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
package com.giantelectronicbrain.catfood.hairball;

/**
 * @author tharter
 *
 */
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;

public class VertxBlockingInputStream extends InputStream {

    private static final Buffer END_BUFFER = Symbol.newSymbol(Buffer.class);

    private static final Buffer END_BUFFER_WITH_ERROR = Symbol.newSymbol(Buffer.class);

    private static final Logger LOG = LoggerFactory.getLogger(VertxBlockingInputStream.class);

    private int availableBytes = 0;

    private long bytesRead = 0;
    
    private boolean populateCalled = false;

    /**
     * Flag to indicate that the stream is closed.
     */
    private boolean closed = false;

    private Buffer currentBuffer;

    private IOException exceptionToThrow = null;

    private int pos;

    private final BlockingQueue<Buffer> queue = new LinkedBlockingQueue<>();

    /**
     * Constructs VertxBlockingInputStream without any associated handlers
     * configured on a ReadStream.
     */
    public VertxBlockingInputStream() {

    }

    public VertxBlockingInputStream(final ReadStream<Buffer> readStream) {
//System.out.println("creationg blocking input stream, handlers installed");  
        readStream
            .handler(this::populate)
            .endHandler(aVoid -> end());

    }

    @Override
    synchronized public int available() throws IOException {
//System.out.println("AVAILABLE CALLED, is closed "+closed);
//System.out.println("WHAT IS IN THE QUEUE "+queue.size());
//System.out.println("Has been populated? "+populateCalled);
//System.out.println("Available bytes is "+availableBytes);
//try {
//	Thread.sleep(100);
//} catch (InterruptedException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
//System.out.println("WTF DID WE GET HERE?");

    	if(closed) throw new IOException("Cannot call available() on a closed stream");
//System.out.println("WTF DID WE GET HERE TOO?");

    	if(queue.isEmpty()) {
    		int rv = 0;
//System.out.println("Has been populated? "+populateCalled);
   		if(populateCalled) rv = 0; else rv = 1;
//System.out.println("sending back answer of "+rv);    		
    		return rv;
    	}
//System.out.println("sending back answer of "+availableBytes);
        return availableBytes;
    }

    @Override
    public void close() throws IOException {
    	if(closed == false) {
    		end();
    	}
        closed = true;
    }

    public void end() {
//System.out.println("END CALLED");
        queue.add(END_BUFFER);
    }

    /**
     * End the buffer because of an error.
     *
     * @param e
     */
    public void error(final Throwable e) {
//System.out.println("ERROR CALLED");
        exceptionToThrow = new IOException(e);
        queue.add(END_BUFFER_WITH_ERROR);

    }

    @Override
    public boolean markSupported() {

        return false;
    }

    synchronized public void populate(final Buffer buffer) {
//System.out.println("POPULATE CALLED");
    	populateCalled = true;
        queue.add(buffer);
        availableBytes += buffer.length();
    }

    @Override
    public int read() throws IOException {

        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (currentBuffer == null) {
            try {
                currentBuffer = queue.take();
                pos = 0;
            } catch (final InterruptedException e) {
                LOG.error("Interrupted while waiting for next buffer", e);
                Thread.currentThread().interrupt();
            }
        }
        if (currentBuffer == null) {
            throw new IOException("Obtained a null buffer from the queue");
        } else if (currentBuffer == END_BUFFER_WITH_ERROR) {
            throw exceptionToThrow;
        } else if (currentBuffer == END_BUFFER) {
            return -1;
        } else {
            // Convert to unsigned byte
            final int b = currentBuffer.getByte(pos++) & 0xFF;
            --availableBytes;
            ++bytesRead;
            if (pos == currentBuffer.length()) {
                currentBuffer = null;
            }
            return b;
        }
    }

    @Override
    public synchronized void reset() throws IOException {

        throw new IOException("reset not supported");
    }

    /**
     * Gets a count of how much bytes have been read from this input stream.
     *
     * @return total bytes read
     */
    public long totalBytesRead() {

        return bytesRead;
    }

    /***************************************************************************************************************/
    
    /**
     * Marker we can use to tag buffers that are signaling EOF and similar.
     * 
     * @author tharter
     *
     */
    private static final class Symbol implements InvocationHandler {
	
	    /**
	     * A marker interface to indicate that an object is a symbol.
	     */
	     private static interface ISymbol {
	     }
	
	    /**
	     * Checks whether a given object is a symbol.
	     * 
	     * @param o
	     *            object to test
	     * @return true if it is a symbol.
	     */
	    public static boolean isSymbol(final Object o) {
	
	        return o instanceof ISymbol;
	    }
	
	    public static <T> T newSymbol(final Class<T> clazz) {
	
	        return newSymbol(clazz, clazz.getName());
	    }
	
	    @SuppressWarnings("unchecked")
	    public static <T> T newSymbol(final Class<T> clazz,
	        final String name) {
	
	        try {
	            return (T) Proxy.newProxyInstance(Symbol.class.getClassLoader(), new Class[] {
	                clazz,
	                ISymbol.class
	            }, new Symbol(name));
	        } catch (final IllegalArgumentException e) {
	            throw new ExceptionInInitializerError(e);
	        }
	    }
	
	    private final String symbolName;
	
	    /**
	     * Constructs Symbol.
	     *
	     * @param symbolName
	     *            symbol name
	     */
	    private Symbol(final String symbolName) {
	
	        this.symbolName = symbolName;
	    }
	
	    @Override
	    public Object invoke(final Object proxy,
	        final Method method,
	        final Object[] args) throws Throwable {
	
	        if ("equals".equals(method.getName())) {
	            return proxy == args[0];
	        } else if ("hashCode".equals(method.getName())) {
	            return symbolName.hashCode();
	        } else if ("toString".equals(method.getName())) {
	            return symbolName;
	        }
	        throw new UnsupportedOperationException();
	    }
	}
}
